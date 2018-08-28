package sh.okx.rankup;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import sh.okx.rankup.commands.InfoCommand;
import sh.okx.rankup.commands.RankListCommand;
import sh.okx.rankup.commands.RankupCommand;
import sh.okx.rankup.gui.Gui;
import sh.okx.rankup.gui.GuiListener;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.messages.Variable;
import sh.okx.rankup.placeholders.Placeholders;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.ranks.requirements.MoneyRequirement;
import sh.okx.rankup.ranks.requirements.PlaytimeMinutesRequirement;
import sh.okx.rankup.ranks.requirements.Requirement;
import sh.okx.rankup.ranks.requirements.RequirementRegistry;
import sh.okx.rankup.ranks.requirements.XpLevelRequirement;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public class Rankup extends JavaPlugin {
  @Getter
  private Permission permissions;
  @Getter
  private Economy economy;
  /**
   * The registry for listing the requirements to /rankup.
   */
  @Getter
  private RequirementRegistry requirementRegistry = new RequirementRegistry();
  @Getter
  private FileConfiguration messages;
  @Getter
  private FileConfiguration config;
  @Getter
  private Rankups rankups;
  @Getter
  private Placeholders placeholders;

  /**
   * Players who cannot rankup for a certain amount of time.
   */
  private Map<Player, Long> cooldowns;

  @Override
  public void onEnable() {
    registerRequirements();
    setupPermissions();
    setupEconomy();
    reload();

    Metrics metrics = new Metrics(this);
    metrics.addCustomChart(new Metrics.SimplePie("confirmation") {
      @Override
      public String getValue() {
        return getConfig().getString("confirmation.type");
      }
    });

    if (config.getBoolean("ranks")) {
      getCommand("ranks").setExecutor(new RankListCommand(this));
    }
    getCommand("rankup").setExecutor(new RankupCommand(this));
    getCommand("rankup3").setExecutor(new InfoCommand(this));
    getServer().getPluginManager().registerEvents(new GuiListener(this), this);
  }


  @Override
  public void onDisable() {
    closeInventories();
    PlaceholderAPI.unregisterExpansion(placeholders);
  }

  public void reload() {
    cooldowns = new WeakHashMap<>();
    closeInventories();
    loadConfigs();
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      placeholders = new Placeholders(this);
      placeholders.register();
    }

    if (config.getInt("version") != 0) {
      getLogger().severe("You are using an outdated config!");
      getLogger().severe("This means that some things might not work!");
      getLogger().severe("To update, please rename your config files (or the folder they are in),");
      getLogger().severe("and run /rankup3 reload to generate a new config file.");
      getLogger().severe("If that does not work, restart your server.");
      getLogger().severe("You may then copy in your config values from the old config.");
      getLogger().severe("Check the changelog on the Rankup spigot page to see the changes.");
    }
  }

  /**
   * Closes all rankup inventories on disable
   * so players cannot grab items from the inventory
   * on a plugin reload.
   */
  private void closeInventories() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      InventoryView view = player.getOpenInventory();
      if (view.getType() == InventoryType.CHEST
          && view.getTopInventory().getHolder() instanceof Gui) {
        player.closeInventory();
      }
    }
  }

  private void loadConfigs() {
    messages = loadConfig("messages.yml");
    config = loadConfig("config.yml");
    rankups = new Rankups(this, loadConfig("rankups.yml"));
  }

  private FileConfiguration loadConfig(String name) {
    File file = new File(getDataFolder(), name);
    if (!file.exists()) {
      saveResource(name, false);
    }
    return YamlConfiguration.loadConfiguration(file);
  }

  private void registerRequirements() {
    requirementRegistry.addRequirement(new MoneyRequirement(this, "money"));
    requirementRegistry.addRequirement(new XpLevelRequirement(this, "xp-level"));
    requirementRegistry.addRequirement(new PlaytimeMinutesRequirement(this, "playtime-minutes"));
  }

  private void setupPermissions() {
    RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
    permissions = rsp.getProvider();
  }

  private void setupEconomy() {
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp != null) {
      economy = rsp.getProvider();
    } else {
      getLogger().warning("No economy found.");
    }
  }

  public String formatMoney(double money) {
    List<String> shortened = config.getStringList("shorten");
    String suffix = "";

    for (int i = shortened.size(); i > 0; i--) {
      double value = Math.pow(10, 3 * i);
      if (money >= value) {
        money /= value;
        suffix = shortened.get(i - 1);
      }
    }

    String format = placeholders.getMoneyFormat().format(money);
    return format + suffix;
  }

  public MessageBuilder getMessage(Rank rank, Message message) {
    ConfigurationSection messages = rankups.getConfig()
        .getConfigurationSection(rank.getName());
    if (messages == null || !messages.isSet(message.getName())) {
      messages = this.messages;
    }
    return MessageBuilder.of(messages, message);
  }

  public MessageBuilder getMessage(Message message) {
    return MessageBuilder.of(messages, message);
  }

  public void rankup(Player player) {
    if (!checkRankup(player)) {
      return;
    }

    Rank oldRank = rankups.getRank(player);
    Rank rank = rankups.nextRank(oldRank);

    oldRank.applyRequirements(player);

    permissions.playerRemoveGroup(null, player, oldRank.getRank());
    permissions.playerAddGroup(null, player, rank.getRank());

    getMessage(oldRank, Message.SUCCESS_PUBLIC)
        .failIfEmpty()
        .replaceAll(player, oldRank, rank)
        .broadcast();
    getMessage(oldRank, Message.SUCCESS_PRIVATE)
        .failIfEmpty()
        .replaceAll(player, oldRank, rank)
        .send(player);

    oldRank.runCommands(player, rank);

    // apply cooldown last
    if(config.getInt("cooldown") > 0) {
      cooldowns.put(player, System.currentTimeMillis());
    }
  }

  /**
   * Checks if a player can rankup,
   * and if they can't, sends the player a message and returns false
   *
   * @param player the player to check if they can rankup
   * @return true if the player can rankup, false otherwise
   */
  public boolean checkRankup(Player player) {
    Rank rank = rankups.getRank(player);
    if (rank == null) { // check if in ladder
      getMessage(Message.NOT_IN_LADDER)
          .replace(Variable.PLAYER, player.getName())
          .send(player);
      return false;
    } else if (rank.isLastRank()) { // check if they are at the highest rank
      getMessage(rank, Message.NO_RANKUP)
          .replaceAll(player, rank)
          .send(player);
      return false;
    } else if (!rank.checkRequirements(player)) { // check if they can afford it
      MessageBuilder builder =
          getMessage(rank, Message.REQUIREMENTS_NOT_MET)
              .replaceAll(player, rank, rankups.nextRank(rank));
      if (economy != null) {
        double balance = economy.getBalance(player);
        double amount = rank.getRequirement("money").getAmount();
        builder = builder
            .replace(Variable.MONEY, formatMoney(amount))
            .replace(Variable.MONEY_NEEDED, formatMoney(Math.max(0, amount - balance)));
      }
      replaceRequirements(player, builder, rank);
      builder.send(player);
      return false;
    } else if (cooldowns.containsKey(player)) {
      long time = System.currentTimeMillis() - cooldowns.get(player);
      // if time passed is less than the cooldown
      long timeLeft = (config.getInt("cooldown") * 1000) - time;
      if (timeLeft > 0) {
        long secondsLeft = (long) Math.ceil(timeLeft / 1000f);
        getMessage(rank, secondsLeft > 1 ? Message.COOLDOWN_PLURAL : Message.COOLDOWN_SINGULAR)
            .failIfEmpty()
            .replaceAll(player, rank)
            .replace(Variable.SECONDS, secondsLeft)
            .send(player);
        return false;
      }
      // cooldown has expired so remove it
      cooldowns.remove(player);
    }

    return true;
  }

  public void replaceRequirements(Player player, MessageBuilder builder, Rank rank) {
    DecimalFormat simpleFormat = placeholders.getSimpleFormat();
    DecimalFormat percentFormat = placeholders.getPercentFormat();
    for (Requirement requirement : rank.getRequirements()) {
      replaceRequirements(builder, Variable.AMOUNT, requirement, () -> simpleFormat.format(requirement.getAmount()));
      replaceRequirements(builder, Variable.AMOUNT_NEEDED, requirement, () -> simpleFormat.format(requirement.getRemaining(player)));
      replaceRequirements(builder, Variable.PERCENT_LEFT, requirement, () -> percentFormat.format(Math.max(0, (requirement.getRemaining(player) / requirement.getAmount()) * 100)));
      replaceRequirements(builder, Variable.PERCENT_DONE, requirement, () -> percentFormat.format(Math.min(100, (1 - (requirement.getRemaining(player) / requirement.getAmount())) * 100)));
    }
  }

  private void replaceRequirements(MessageBuilder builder, Variable variable, Requirement requirement, Supplier<Object> value) {
    builder.replace(variable + " " + requirement.getName(), value.get());
  }
}
