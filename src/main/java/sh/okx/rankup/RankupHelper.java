package sh.okx.rankup;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.hook.GroupProvider;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.messages.Variable;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankElement;
import sh.okx.rankup.ranks.Rankups;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Actually performs the ranking up and prestiging for the plugin and also manages the cooldowns
 * between ranking up.
 */
public class RankupHelper {

  private final RankupPlugin plugin;
  private final ConfigurationSection config;
  private final GroupProvider permissions;
  /**
   * Players who cannot rankup/prestige for a certain amount of time.
   */
  private Map<Player, Long> cooldowns = new HashMap<>();

  public RankupHelper(RankupPlugin plugin) {
    this.plugin = plugin;
    this.config = plugin.getConfig();
    this.permissions = plugin.getPermissions();
  }

  public void doRankup(Player player, RankElement<Rank> rank) {
    rank.getRank().runCommands(player);

    if (rank.getRank() != null) {
      permissions.removeGroup(player.getUniqueId(), rank.getRank().getRank());
    }
    permissions.addGroup(player.getUniqueId(), rank.getNext().getRank().getRank());
  }

  public void sendRankupMessages(Player player, RankElement<Rank> rank) {
    plugin.getMessage(rank.getRank(), Message.SUCCESS_PUBLIC)
        .failIfEmpty()
        .replaceRanks(player, rank.getRank(), rank.getNext().getRank())
        .broadcast();
    plugin.getMessage(rank.getRank(), Message.SUCCESS_PRIVATE)
        .failIfEmpty()
        .replaceRanks(player, rank.getRank(), rank.getNext().getRank())
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

  public void sendPrestigeMessages(Player player, RankElement<Prestige> prestige) {
    Objects.requireNonNull(prestige);
    Objects.requireNonNull(prestige.getNext());

    plugin.getMessage(prestige.getRank(), Message.PRESTIGE_SUCCESS_PUBLIC)
        .failIfEmpty()
        .replaceRanks(player, prestige.getRank(), prestige.getNext().getRank())
        .replaceFromTo(prestige.getRank())
        .broadcast();
    plugin.getMessage(prestige.getRank(), Message.PRESTIGE_SUCCESS_PRIVATE)
        .failIfEmpty()
        .replaceRanks(player, prestige.getRank(), prestige.getNext().getRank())
        .replaceFromTo(prestige.getRank())
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

    RankElement<Rank> rankElement = plugin.getRankups().getByPlayer(player);
    Rank rank = rankElement.getRank();
    rank.applyRequirements(player);
    applyCooldown(player);

    doRankup(player, rankElement);
    sendRankupMessages(player, rankElement);
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
    RankElement<Rank> rankElement = rankups.getByPlayer(player);
    if (rankElement == null) { // check if in ladder
      plugin.getMessage(Message.NOT_IN_LADDER)
          .failIf(!message)
          .replace(Variable.PLAYER, player.getName())
          .send(player);
      return false;
    }
    Rank rank = rankElement.getRank();
    if (!rankElement.hasNext()) {
      Prestiges prestiges = plugin.getPrestiges();
      plugin.getMessage(prestiges == null || !prestiges.getByPlayer(player).hasNext() ? Message.NO_RANKUP : Message.MUST_PRESTIGE)
          .failIf(!message)
          .replaceRanks(player, rankups.getTree().last().getRank().getRank())
          .send(player);
      return false;
    } else if (!rank.hasRequirements(player)) { // check if they can afford it
      if (message) {
        plugin.replaceMoneyRequirements(plugin.getMessage(rank, Message.REQUIREMENTS_NOT_MET)
            .replaceRanks(player, rank, rankElement.getNext().getRank()), player, rank)
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

    RankElement<Prestige> rankElement = plugin.getPrestiges().getByPlayer(player);
    Prestige prestige = rankElement.getRank();
    prestige.applyRequirements(player);

    applyCooldown(player);
    doPrestige(player, prestige);
    sendPrestigeMessages(player, rankElement);
  }

  public boolean checkPrestige(Player player) {
    return checkPrestige(player, true);
  }

  public boolean checkPrestige(Player player, boolean message) {
    Prestiges prestiges = plugin.getPrestiges();
    RankElement<Prestige> prestigeElement = prestiges.getByPlayer(player);
    if (prestigeElement == null || !prestigeElement.getRank().isEligible(player)) { // check if in ladder
      plugin.getMessage(Message.NOT_HIGH_ENOUGH)
          .failIf(!message)
          .replace(Variable.PLAYER, player.getName())
          .send(player);
      return false;
    } else if (!prestigeElement.hasNext()) { // check if they are at the highest rank
      plugin.getMessage(prestigeElement.getRank(), Message.PRESTIGE_NO_PRESTIGE)
          .failIf(!message)
          .replaceRanks(player, prestigeElement.getRank().getRank())
          .replaceFromTo(prestigeElement.getRank())
          .send(player);
      return false;
    } else if (!prestigeElement.getRank().hasRequirements(player)) { // check if they can afford it
      plugin.replaceMoneyRequirements(
          plugin.getMessage(prestigeElement.getRank(), Message.PRESTIGE_REQUIREMENTS_NOT_MET)
              .failIf(!message)
              .replaceRanks(player, prestigeElement.getRank(), prestigeElement.getNext().getRank().getRank()), player, prestigeElement.getRank())
          .replaceFromTo(prestigeElement.getRank())
          .send(player);
      return false;
    } else if (checkCooldown(player, prestigeElement.getRank())) {
      return false;
    }

    return true;
  }
}
