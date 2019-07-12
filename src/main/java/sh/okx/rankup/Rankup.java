package sh.okx.rankup;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import sh.okx.rankup.commands.InfoCommand;
import sh.okx.rankup.commands.PrestigeCommand;
import sh.okx.rankup.commands.PrestigesCommand;
import sh.okx.rankup.commands.RanksCommand;
import sh.okx.rankup.commands.RankupCommand;
import sh.okx.rankup.gui.Gui;
import sh.okx.rankup.gui.GuiListener;
import sh.okx.rankup.messages.NullMessageBuilder;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.messages.Variable;
import sh.okx.rankup.placeholders.Placeholders;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.requirements.Requirement;
import sh.okx.rankup.requirements.RequirementRegistry;
import sh.okx.rankup.requirements.requirement.*;
import sh.okx.rankup.requirements.requirement.XpLevelRequirement;
import sh.okx.rankup.requirements.requirement.advancedachievements.AdvancedAchievementsAchievementRequirement;
import sh.okx.rankup.requirements.requirement.advancedachievements.AdvancedAchievementsTotalRequirement;
import sh.okx.rankup.requirements.requirement.mcmmo.McMMOPowerLevelRequirement;
import sh.okx.rankup.requirements.requirement.mcmmo.McMMOSkillRequirement;
import sh.okx.rankup.requirements.requirement.votingplugin.VotingPluginVotesRequirement;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private RequirementRegistry requirements;
  @Getter
  private FileConfiguration messages;
  @Getter
  private FileConfiguration config;
  @Getter
  private Rankups rankups;
  @Getter
  private Prestiges prestiges;
  @Getter
  private Placeholders placeholders;
  @Getter
  private RankupHelper helper;
  private AutoRankup autoRankup;

  @Override
  public void onEnable() {
    setupPermissions();
    setupEconomy();
    reload();

    Metrics metrics = new Metrics(this);
    metrics.addCustomChart(new Metrics.SimplePie("confirmation",
        () -> config.getString("confirmation.type")));
    metrics.addCustomChart(new Metrics.AdvancedPie("requirements", () -> {
      Map<String, Integer> map = new HashMap<>();
      addAll(map, rankups);
      if (prestiges != null) {
        addAll(map, prestiges);
      }
      return map;
    }));

    if (config.getBoolean("ranks")) {
      getCommand("ranks").setExecutor(new RanksCommand(this));
    }
    if (config.getBoolean("prestige")) {
      getCommand("prestige").setExecutor(new PrestigeCommand(this));
      if (config.getBoolean("prestiges")) {
        getCommand("prestiges").setExecutor(new PrestigesCommand(this));
      }
    }

    getCommand("rankup").setExecutor(new RankupCommand(this));
    getCommand("rankup3").setExecutor(new InfoCommand(this));
    getServer().getPluginManager().registerEvents(new GuiListener(this), this);

    placeholders = new Placeholders(this);
    placeholders.register();
  }


  @Override
  public void onDisable() {
    closeInventories();
    placeholders.unregister();
  }

  public void reload() {
    closeInventories();
    loadConfigs();

    if (autoRankup != null) {
      autoRankup.cancel();
    }
    long time = config.getInt("autorankup-interval") * 60 * 20;
    if (time > 0) {
      autoRankup = new AutoRankup(this);
      autoRankup.runTaskTimer(this, time, time);
    }

    if (config.getInt("version") < 4) {
      getLogger().severe("You are using an outdated config!");
      getLogger().severe("This means that some things might not work!");
      getLogger().severe("To update, please rename ALL your config files (or the folder they are in),");
      getLogger().severe("and run /rankup3 reload to generate a new config file.");
      getLogger().severe("If that does not work, restart your server.");
      getLogger().severe("You may then copy in your config values from the old config.");
      getLogger().severe("Check the changelog on the Rankup spigot page to see the changes.");
    }

    helper = new RankupHelper(this);
  }

  private void addAll(Map<String, Integer> map, RankList<? extends Rank> ranks) {
    for (Rank rank : ranks.ranks) {
      for (Requirement requirement : rank.getRequirements()) {
        String name = requirement.getName();
        map.put(name, map.getOrDefault(name, 0) + 1);
      }
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
    saveLocales();

    config = loadConfig("config.yml");
    String locale = config.getString("locale", "en");
    File localeFile = new File(new File(getDataFolder(), "locale"), locale + ".yml");
    messages = YamlConfiguration.loadConfiguration(localeFile);

    Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::refreshRanks);
  }

  public void refreshRanks() {
    try {
      registerRequirements();
      Bukkit.getPluginManager().callEvent(new RankupRegisterEvent(this));

      rankups = new Rankups(this, loadConfig("rankups.yml"));
      if (config.getBoolean("prestige")) {
        prestiges = new Prestiges(this, loadConfig("prestiges.yml"));
      } else {
        prestiges = null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Bukkit.getPluginManager().disablePlugin(this);
      getLogger().severe("Could not finish enabling Rankup");
      Bukkit.broadcast(ChatColor.RED + "Could not reload rankup successfully, please check console for more information.", "rankup.reload");
    }
  }

  private void saveLocales() {
    saveLocale("en");
    saveLocale("pt-br");
    saveLocale("ru");
  }

  private void saveLocale(String locale) {
    String name = "locale/" + locale + ".yml";
    File file = new File(getDataFolder(), name);
    if (!file.exists()) {
      saveResource("locale/" + locale + ".yml", false);
    }
  }

  private FileConfiguration loadConfig(String name) {
    File file = new File(getDataFolder(), name);
    if (!file.exists()) {
      saveResource(name, false);
    }
    return YamlConfiguration.loadConfiguration(file);
  }

  private void registerRequirements() {
    requirements = new RequirementRegistry();
    requirements.addRequirement(new XpLevelRequirement(this));
    requirements.addRequirement(new PlaytimeMinutesRequirement(this));
    requirements.addRequirement(new GroupRequirement(this));
    requirements.addRequirement(new PermissionRequirement(this));
    requirements.addRequirement(new PlaceholderRequirement(this));
    requirements.addRequirement(new WorldRequirement(this));
    requirements.addRequirement(new BlockBreakRequirement(this));
    requirements.addRequirement(new PlayerKillsRequirement(this));
    requirements.addRequirement(new MobKillsRequirement(this));
    requirements.addRequirement(new ItemRequirement(this));
    requirements.addRequirement(new UseItemRequirement(this));
    requirements.addRequirement(new TotalMobKillsRequirement(this));
    requirements.addRequirement(new CraftItemRequirement(this));
    if (economy != null) {
      requirements.addRequirement(new MoneyRequirement(this));
    }

    PluginManager pluginManager = Bukkit.getPluginManager();
    if (pluginManager.isPluginEnabled("mcMMO")) {
      requirements.addRequirement(new McMMOSkillRequirement(this));
      requirements.addRequirement(new McMMOPowerLevelRequirement(this));
    }
    if (pluginManager.isPluginEnabled("AdvancedAchievements")) {
      requirements.addRequirement(new AdvancedAchievementsAchievementRequirement(this));
      requirements.addRequirement(new AdvancedAchievementsTotalRequirement(this));
    }
    if (pluginManager.isPluginEnabled("VotingPlugin")) {
      requirements.addRequirement(new VotingPluginVotesRequirement(this));
    }
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
      getLogger().warning("No economy found. The 'money' requirement will be disabled.");
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
        break;
      }
    }

    return placeholders.getMoneyFormat().format(money) + suffix;
  }

  public MessageBuilder getMessage(Rank rank, Message message) {
    ConfigurationSection messages = rank.getSection();
    if (messages == null || !messages.isSet(message.getName())) {
      messages = this.messages;
    }
    return MessageBuilder.of(messages, message);
  }

  public MessageBuilder getMessage(Message message) {
    return MessageBuilder.of(messages, message);
  }

  public MessageBuilder replaceMoneyRequirements(MessageBuilder builder, CommandSender sender, Rank rank) {
    if (builder instanceof NullMessageBuilder) {
      return builder;
    }

    Requirement money = rank.getRequirement("money");
    if (money != null) {
      Double amount = null;
      if (sender instanceof Player && rank.isIn((Player) sender)) {
        if (economy != null) {
          amount = money.getRemaining((Player) sender);
        }
      } else {
        amount = money.getValueDouble();
      }
      if (amount != null && economy != null) {
        builder.replace(Variable.MONEY_NEEDED, formatMoney(amount));
        builder.replace(Variable.MONEY, formatMoney(money.getValueDouble()));
      }
    }
    if (sender instanceof Player) {
      replaceRequirements(builder, (Player) sender, rank);
    }
    return builder;
  }

  public MessageBuilder replaceRequirements(MessageBuilder builder, Player player, Rank rank) {
    DecimalFormat simpleFormat = placeholders.getSimpleFormat();
    DecimalFormat percentFormat = placeholders.getPercentFormat();
    for (Requirement requirement : rank.getRequirements()) {
      try {
        replaceRequirements(builder, Variable.AMOUNT, requirement, () -> simpleFormat.format(requirement.getValueDouble()));
        if (rank.isIn(player)) {
          replaceRequirements(builder, Variable.AMOUNT_NEEDED, requirement, () -> simpleFormat.format(requirement.getRemaining(player)));
          replaceRequirements(builder, Variable.PERCENT_LEFT, requirement,
              () -> percentFormat.format(Math.max(0, (requirement.getRemaining(player) / requirement.getValueDouble()) * 100)));
          replaceRequirements(builder, Variable.PERCENT_DONE, requirement,
              () -> percentFormat.format(Math.min(100, (1 - (requirement.getRemaining(player) / requirement.getValueDouble())) * 100)));
          replaceRequirements(builder, Variable.AMOUNT_DONE, requirement, () -> simpleFormat.format(requirement.getValueDouble() - requirement.getRemaining(player)));
        }
      } catch (NumberFormatException ignored) {
      }
    }
    return builder;
  }

  private void replaceRequirements(MessageBuilder builder, Variable variable, Requirement requirement, Supplier<Object> value) {
    builder.replace(variable + " " + requirement.getFullName(), value.get());
  }

  public MessageBuilder getMessage(CommandSender player, Message message, Rank oldRank, String rankName) {
    String oldRankName;
    if (oldRank instanceof Prestige && oldRank.getRank() == null) {
      oldRankName = ((Prestige) oldRank).getFrom();
    } else {
      oldRankName = oldRank.getRank();
    }

    return replaceMoneyRequirements(getMessage(oldRank, message)
        .replaceRanks(player, rankName)
        .replace(Variable.OLD_RANK, oldRankName), player, oldRank)
        .replaceFromTo(oldRank);
  }

  public void sendHeaderFooter(CommandSender sender, Rank rank, Message type) {
    MessageBuilder builder;
    if (rank == null) {
      builder = getMessage(type)
          .failIfEmpty()
          .replace(Variable.PLAYER, sender.getName());
    } else {
      builder = getMessage(rank, type)
          .failIfEmpty()
          .replaceRanks(sender, rank.getRank())
          .replaceFromTo(rank);
    }
    builder.send(sender);
  }

  public boolean isLegacy() {
    String version = Bukkit.getVersion();
    return !(version.contains("1.13") || version.contains("1.14"));
  }
}
