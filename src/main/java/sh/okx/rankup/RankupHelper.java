package sh.okx.rankup;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.hook.PermissionProvider;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.messages.Variable;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.Rankups;

/**
 * Actually performs the ranking up and prestiging for the plugin and also manages the cooldowns
 * between ranking up.
 */
public class RankupHelper {

  private final RankupPlugin plugin;
  private final ConfigurationSection config;
  private final PermissionProvider permissions;
  /**
   * Players who cannot rankup/prestige for a certain amount of time.
   */
  private Map<Player, Long> cooldowns = new HashMap<>();

  public RankupHelper(RankupPlugin plugin) {
    this.plugin = plugin;
    this.config = plugin.getConfig();
    this.permissions = plugin.getPermissions();
  }

  public void doRankup(Player player, Rank rank) {
    rank.runCommands(player);

    if (rank.getRank() != null) {
      permissions.removeGroup(player.getUniqueId(), rank.getRank());
    }
    permissions.addGroup(player.getUniqueId(), rank.getNext());
  }

  public void sendRankupMessages(Player player, Rank rank) {
    plugin.getMessage(rank, Message.SUCCESS_PUBLIC)
        .failIfEmpty()
        .replaceRanks(player, rank, rank.getNext())
        .broadcast();
    plugin.getMessage(rank, Message.SUCCESS_PRIVATE)
        .failIfEmpty()
        .replaceRanks(player, rank, rank.getNext())
        .send(player);
  }

  public void doPrestige(Player player, Prestige prestige) {
    prestige.runCommands(player);

    permissions.removeGroup(player.getUniqueId(), prestige.getFrom());
    permissions.addGroup(player.getUniqueId(), prestige.getTo());

    if (prestige.getRank() != null) {
      permissions.removeGroup(player.getUniqueId(), prestige.getRank());
    }
    permissions.addGroup(player.getUniqueId(), prestige.getNext());
  }

  public void sendPrestigeMessages(Player player, Prestige prestige) {
    plugin.getMessage(prestige, Message.PRESTIGE_SUCCESS_PUBLIC)
        .failIfEmpty()
        .replaceRanks(player, prestige, prestige.getNext())
        .replaceFromTo(prestige)
        .broadcast();
    plugin.getMessage(prestige, Message.PRESTIGE_SUCCESS_PRIVATE)
        .failIfEmpty()
        .replaceRanks(player, prestige, prestige.getNext())
        .replaceFromTo(prestige)
        .send(player);
  }

  private boolean checkCooldown(Player player, Rank rank) {
    if (cooldowns.containsKey(player)) {
      long time = System.currentTimeMillis() - cooldowns.get(player);
      // if time passed is less than the cooldown
      long cooldownSeconds = config.getInt("cooldown");
      long timeLeft = (cooldownSeconds * 1000) - time;
      if (timeLeft > 0) {
        long secondsLeft = (long) Math.ceil(timeLeft / 1000f);
        plugin
            .getMessage(rank, secondsLeft > 1 ? Message.COOLDOWN_PLURAL : Message.COOLDOWN_SINGULAR)
            .failIfEmpty()
            .replaceRanks(player, rank.getRank())
            .replaceFromTo(rank)
            .replace(Variable.SECONDS, cooldownSeconds)
            .replace(Variable.SECONDS_LEFT, secondsLeft)
            .send(player);
        return true;
      }
      // cooldown has expired so remove it
      cooldowns.remove(player);
    }
    return false;
  }

  private void applyCooldown(Player player) {
    if (config.getInt("cooldown") > 0) {
      cooldowns.put(player, System.currentTimeMillis());
    }
  }

  public void rankup(Player player) {
    if (!checkRankup(player)) {
      return;
    }

    Rank rank = plugin.getRankups().getByPlayer(player);
    rank.applyRequirements(player);
    applyCooldown(player);

    doRankup(player, rank);
    sendRankupMessages(player, rank);
  }

  public boolean checkRankup(Player player) {
    return checkRankup(player, true);
  }

  /**
   * Checks if a player can rankup, and if they can't, sends the player a message and returns false
   *
   * @param player the player to check if they can rankup
   * @return true if the player can rankup, false otherwise
   */
  public boolean checkRankup(Player player, boolean message) {
    Rankups rankups = plugin.getRankups();
    Rank rank = rankups.getByPlayer(player);
    if (rankups.isLast(permissions, player)) {
      Prestiges prestiges = plugin.getPrestiges();
      plugin.getMessage(prestiges == null || prestiges.isLast(permissions, player) ? Message.NO_RANKUP : Message.MUST_PRESTIGE)
          .failIf(!message)
          .replaceRanks(player, rankups.getLast())
          .send(player);
      return false;
    } else if (rank == null) { // check if in ladder
      plugin.getMessage(Message.NOT_IN_LADDER)
          .failIf(!message)
          .replace(Variable.PLAYER, player.getName())
          .send(player);
      return false;
    } else if (!rank.hasRequirements(player)) { // check if they can afford it
      if (message) {
        plugin.replaceMoneyRequirements(plugin.getMessage(rank, Message.REQUIREMENTS_NOT_MET)
            .replaceRanks(player, rank, rank.getNext()), player, rank)
            .send(player);
      }
      return false;
    } else if (message && checkCooldown(player, rank)) {
      return false;
    }

    return true;
  }

  public void prestige(Player player) {
    if (!checkPrestige(player)) {
      return;
    }

    Prestige prestige = plugin.getPrestiges().getByPlayer(player);
    prestige.applyRequirements(player);

    applyCooldown(player);
    doPrestige(player, prestige);
    sendPrestigeMessages(player, prestige);
  }

  public boolean checkPrestige(Player player) {
    return checkPrestige(player, true);
  }

  public boolean checkPrestige(Player player, boolean message) {
    Prestiges prestiges = plugin.getPrestiges();
    Prestige prestige = prestiges.getByPlayer(player);
    if (prestige == null || !prestige.isEligable(player)) { // check if in ladder
      plugin.getMessage(Message.NOT_HIGH_ENOUGH)
          .failIf(!message)
          .replace(Variable.PLAYER, player.getName())
          .send(player);
      return false;
    } else if (prestiges
        .isLast(plugin.getPermissions(), player)) { // check if they are at the highest rank
      plugin.getMessage(prestige, Message.PRESTIGE_NO_PRESTIGE)
          .failIf(!message)
          .replaceRanks(player, prestige.getRank())
          .replaceFromTo(prestige)
          .send(player);
      return false;
    } else if (!prestige.hasRequirements(player)) { // check if they can afford it
      plugin.replaceMoneyRequirements(
          plugin.getMessage(prestige, Message.PRESTIGE_REQUIREMENTS_NOT_MET)
              .failIf(!message)
              .replaceRanks(player, prestige, prestiges.next(prestige).getRank()), player, prestige)
          .replaceFromTo(prestige)
          .send(player);
      return false;
    } else if (checkCooldown(player, prestige)) {
      return false;
    }

    return true;
  }
}
