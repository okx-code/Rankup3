package sh.okx.rankup;

import com.electronwill.nightconfig.toml.TomlFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import sh.okx.rankup.commands.InfoCommand;
import sh.okx.rankup.commands.MaxRankupCommand;
import sh.okx.rankup.commands.PrestigeCommand;
import sh.okx.rankup.commands.PrestigesCommand;
import sh.okx.rankup.commands.RanksCommand;
import sh.okx.rankup.commands.RankupCommand;
import sh.okx.rankup.economy.Economy;
import sh.okx.rankup.economy.EconomyProvider;
import sh.okx.rankup.economy.VaultEconomyProvider;
import sh.okx.rankup.events.RankupRegisterEvent;
import sh.okx.rankup.gui.Gui;
import sh.okx.rankup.gui.GuiListener;
import sh.okx.rankup.hook.GroupProvider;
import sh.okx.rankup.hook.PermissionManager;
import sh.okx.rankup.hook.VaultPermissionManager;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.messages.pebble.PebbleMessageBuilder;
import sh.okx.rankup.placeholders.Placeholders;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankList;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.ranksgui.RanksGuiCommand;
import sh.okx.rankup.ranksgui.RanksGuiListener;
import sh.okx.rankup.requirements.Requirement;
import sh.okx.rankup.requirements.RequirementRegistry;
import sh.okx.rankup.requirements.requirement.AdvancementRequirement;
import sh.okx.rankup.requirements.requirement.BlockBreakRequirement;
import sh.okx.rankup.requirements.requirement.CraftItemRequirement;
import sh.okx.rankup.requirements.requirement.GroupRequirement;
import sh.okx.rankup.requirements.requirement.ItemDeductibleRequirement;
import sh.okx.rankup.requirements.requirement.ItemRequirement;
import sh.okx.rankup.requirements.requirement.MobKillsRequirement;
import sh.okx.rankup.requirements.requirement.MoneyDeductibleRequirement;
import sh.okx.rankup.requirements.requirement.MoneyRequirement;
import sh.okx.rankup.requirements.requirement.PermissionRequirement;
import sh.okx.rankup.requirements.requirement.PlaceholderRequirement;
import sh.okx.rankup.requirements.requirement.PlayerKillsRequirement;
import sh.okx.rankup.requirements.requirement.PlaytimeMinutesRequirement;
import sh.okx.rankup.requirements.requirement.TotalMobKillsRequirement;
import sh.okx.rankup.requirements.requirement.UseItemRequirement;
import sh.okx.rankup.requirements.requirement.WorldRequirement;
import sh.okx.rankup.requirements.requirement.XpLevelDeductibleRequirement;
import sh.okx.rankup.requirements.requirement.XpLevelRequirement;
import sh.okx.rankup.requirements.requirement.advancedachievements.AdvancedAchievementsAchievementRequirement;
import sh.okx.rankup.requirements.requirement.advancedachievements.AdvancedAchievementsTotalRequirement;
import sh.okx.rankup.requirements.requirement.mcmmo.McMMOPowerLevelRequirement;
import sh.okx.rankup.requirements.requirement.mcmmo.McMMOSkillRequirement;
import sh.okx.rankup.requirements.requirement.superbvote.SuperbVoteVotesRequirement;
import sh.okx.rankup.requirements.requirement.tokenmanager.TokensDeductibleRequirement;
import sh.okx.rankup.requirements.requirement.tokenmanager.TokensRequirement;
import sh.okx.rankup.requirements.requirement.towny.TownyKingNumberResidentsRequirement;
import sh.okx.rankup.requirements.requirement.towny.TownyKingNumberTownsRequirement;
import sh.okx.rankup.requirements.requirement.towny.TownyKingRequirement;
import sh.okx.rankup.requirements.requirement.towny.TownyMayorNumberResidentsRequirement;
import sh.okx.rankup.requirements.requirement.towny.TownyMayorRequirement;
import sh.okx.rankup.requirements.requirement.towny.TownyResidentRequirement;
import sh.okx.rankup.requirements.requirement.votingplugin.VotingPluginPointsDeductibleRequirement;
import sh.okx.rankup.requirements.requirement.votingplugin.VotingPluginPointsRequirement;
import sh.okx.rankup.requirements.requirement.votingplugin.VotingPluginVotesRequirement;
import sh.okx.rankup.serialization.RankSerialized;
import sh.okx.rankup.serialization.ShadowDeserializer;
import sh.okx.rankup.serialization.YamlDeserializer;
import sh.okx.rankup.util.UpdateNotifier;
import sh.okx.rankup.util.VersionChecker;

public class RankupPlugin extends JavaPlugin {

  public static final int CONFIG_VERSION = 10;

  @Getter
  private GroupProvider permissions;
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
  protected AutoRankup autoRankup = new AutoRankup(this);
  @Getter
  private String[] locales = {"en","pt_br","ru","zh_cn","fr","it","es","nl"};
  private String errorMessage;
  private PermissionManager permissionManager = new VaultPermissionManager(this);
  private EconomyProvider economyProvider = new VaultEconomyProvider();

  public RankupPlugin() {
    super();
  }

  protected RankupPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file, PermissionManager permissionManager, EconomyProvider economyProvider) {
    super(loader, description, dataFolder, file);
    this.permissionManager = permissionManager;
    this.economyProvider = economyProvider;
  }

  @Override
  public void onEnable() {
    UpdateNotifier notifier = new UpdateNotifier(new VersionChecker(this));

    reload(true);

    if (System.getProperty("RANKUP_TEST") == null) {
      Metrics metrics = new Metrics(this);
      metrics.addCustomChart(new Metrics.SimplePie("confirmation",
              () -> config.getString("confirmation-type", "unknown")));
      metrics.addCustomChart(new Metrics.AdvancedPie("requirements", () -> {
        Map<String, Integer> map = new HashMap<>();
        addAllRequirements(map, rankups);
        if (prestiges != null) {
          addAllRequirements(map, prestiges);
        }
        return map;
      }));
      metrics.addCustomChart(new Metrics.SimplePie("prestige",
              () -> config.getBoolean("prestige") ? "enabled" : "disabled"));
      metrics.addCustomChart(new Metrics.SimplePie("permission-rankup",
              () -> config.getBoolean("permission-rankup") ? "enabled" : "disabled"));
      metrics.addCustomChart(new Metrics.SimplePie("notify-update",
              () -> config.getBoolean("notify-update") ? "enabled" : "disabled"));
    }

    if (config.getBoolean("ranks")) {
      if (config.getBoolean("ranks-gui")) {
        RanksGuiListener listener = new RanksGuiListener();
        getCommand("ranks").setExecutor(new RanksGuiCommand(this, listener));
        getServer().getPluginManager().registerEvents(listener, this);
      } else {
        getCommand("ranks").setExecutor(new RanksCommand(this));
      }
    }
    if (config.getBoolean("prestige")) {
      getCommand("prestige").setExecutor(new PrestigeCommand(this));
      if (config.getBoolean("prestiges")) {
        getCommand("prestiges").setExecutor(new PrestigesCommand(this));
      }
    }
    if (config.getBoolean("max-rankup.enabled")) {
      getCommand("maxrankup").setExecutor(new MaxRankupCommand(this));
    }

    getCommand("rankup").setExecutor(new RankupCommand(this));
    getCommand("rankup3").setExecutor(new InfoCommand(this, notifier));
    getServer().getPluginManager().registerEvents(new GuiListener(this), this);
    getServer().getPluginManager().registerEvents(
        new JoinUpdateNotifier(notifier, () -> getConfig().getBoolean("notify-update"), "rankup.notify"), this);

    placeholders = new Placeholders(this);
    placeholders.register();
  }


  @Override
  public void onDisable() {
    closeInventories();
    if (placeholders != null) {
      placeholders.unregister();
    }
  }

  public void reload(boolean init) {
    errorMessage = null;

    config = loadConfig("config.yml");

    if (config.getBoolean("permission-rankup")) {
      permissions = permissionManager.permissionOnlyProvider();
    } else {
      permissions = permissionManager.findPermissionProvider();
      if (permissions == null) {
        errorMessage = "No permission plugin found";
      }
    }

    setupEconomy();

    closeInventories();
    loadConfigs(init);

    long time = (long) (config.getDouble("autorankup-interval") * 60 * 20);
    if (time > 0) {
      try {
        if (!autoRankup.isCancelled()) {
          autoRankup.cancel();
        }
      } catch (IllegalStateException ignored) {
      }
      autoRankup = new AutoRankup(this);
      autoRankup.runTaskTimer(this, time, time);
    }

    if (config.getInt("version") < CONFIG_VERSION) {
      getLogger().severe("You are using an outdated config!");
      getLogger().severe("This means that some things might not work!");
      getLogger().severe("To update, please rename ALL your config files (or the folder they are in),");
      getLogger().severe("and run /pru reload to generate a new config file.");
      getLogger().severe("If that does not work, restart your server.");
      getLogger().severe("You may then copy in your config values manually from the old config.");
      getLogger().severe("Check the changelog on the Rankup spigot page to see the changes.");
      getLogger().severe("https://www.spigotmc.org/resources/rankup.76964/updates");
    }

    helper = new RankupHelper(this);
  }

  public MessageBuilder newMessageBuilder(String message) {
    return new PebbleMessageBuilder(this, message);
  }

  public boolean error() {
    return error(null);
  }

  /**
   * Notify the player of an error if there is one
   *
   * @return true if there was an error and action was taken
   */
  public boolean error(CommandSender sender) {
    if (errorMessage == null) {
      return false;
    }

    if (sender instanceof Player) {
      sender.sendMessage(
          ChatColor.RED + "Could not load Rankup, check console for more information.");
    } else {
      getLogger().severe("Failed to load Rankup");
    }
    for (String line : errorMessage.split("\n")) {
      getLogger().severe(line);
    }
    getLogger().severe("More information can be found in the console log at startup");
    return true;
  }

  private void addAllRequirements(Map<String, Integer> map, RankList<? extends Rank> ranks) {
    for (Rank rank : ranks.getTree()) {
      for (Requirement requirement : rank.getRequirements().getRequirements(null)) {
        String name = requirement.getName();
        map.put(name, map.getOrDefault(name, 0) + 1);
      }
    }
  }

  /**
   * Closes all rankup inventories on disable so players cannot grab items from the inventory on a
   * plugin reload.
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

  private void loadConfigs(boolean init) {
    saveLocales();

    String locale = config.getString("locale", locales[0]);
    File localeFile = new File(new File(getDataFolder(), "locale"), locale + ".yml");
    messages = YamlConfiguration.loadConfiguration(localeFile);

    if (init) {
      Bukkit.getScheduler().runTask(this, () -> {
        refreshRanks();
        error();
      });
    } else {
      refreshRanks();
    }
  }

  public void refreshRanks() {
    try {
      registerRequirements();
      Bukkit.getPluginManager().callEvent(new RankupRegisterEvent(this));

      if (config.getBoolean("prestige")) {
        prestiges = new Prestiges(this, loadConfig("prestiges.yml"));
//        prestiges.getOrderedList();
      } else {
        prestiges = null;
      }

      rankups = new Rankups(this, loadRankupConfig("rankups"));
      // check rankups are not in an infinite loop
//      rankups.getOrderedList();



    } catch (Exception e) {
      this.errorMessage = e.getClass().getName() + ": " + e.getMessage();
      e.printStackTrace();
    }
  }

  private void saveLocales() {
    for (String locale : locales){saveLocale(locale);}
  }

  private void saveLocale(String locale) {
    String name = "locale/" + locale + ".yml";
    File file = new File(getDataFolder(), name);
    if (!file.exists()) {
      saveResource(name, false);
    }
  }

  private List<RankSerialized> loadRankupConfig(String name) {
    File ymlFile = new File(getDataFolder(), name + ".yml");
    File tomlFile = new File(getDataFolder(), name + ".toml");
    if (tomlFile.exists()) {
      try {
        return ShadowDeserializer.deserialize(TomlFormat.instance().createParser().parse(new FileReader(tomlFile)));
      } catch (FileNotFoundException ignored) {
      }
    }
    if (!ymlFile.exists()) {
      saveResource(ymlFile.getName(), false);
    }
    return YamlDeserializer.deserialize(YamlConfiguration.loadConfiguration(ymlFile));
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
    requirements.addRequirements(
        new XpLevelRequirement(this, "xp-levelh"),
        new XpLevelDeductibleRequirement(this, "xp-level"),
        new PlaytimeMinutesRequirement(this, "playtime-minutes"),
        new AdvancementRequirement(this, "advancement"),
        new GroupRequirement(this, "group"),
        new PermissionRequirement(this, "permission"),
        new PlaceholderRequirement(this, "placeholder"),
        new WorldRequirement(this, "world"),
        new BlockBreakRequirement(this, "block-break"),
        new PlayerKillsRequirement(this, "player-kills"),
        new MobKillsRequirement(this, "mob-kills"),
        new ItemRequirement(this, "itemh"),
        new ItemDeductibleRequirement(this, "item"),
        new UseItemRequirement(this, "use-item"),
        new TotalMobKillsRequirement(this, "total-mob-kills"),
        new CraftItemRequirement(this, "craft-item"));
    if (economy != null) {
      requirements.addRequirements(
        new MoneyRequirement(this, "moneyh"),
        new MoneyDeductibleRequirement(this, "money")
      );
    }

    PluginManager pluginManager = Bukkit.getPluginManager();
    if (pluginManager.isPluginEnabled("mcMMO")) {
      requirements.addRequirements(
        new McMMOSkillRequirement(this, "mcmmo"),
        new McMMOPowerLevelRequirement(this, "mcmmo-power-level")
      );
    }
    if (pluginManager.isPluginEnabled("AdvancedAchievements")) {
      requirements.addRequirements(
        new AdvancedAchievementsAchievementRequirement(this, "advancedachievements-achievement"),
        new AdvancedAchievementsTotalRequirement(this, "advancedachievements-total")
      );
    }
    if (pluginManager.isPluginEnabled("VotingPlugin")) {
      requirements.addRequirements(
        new VotingPluginVotesRequirement(this, "votingplugin-votes"),
        new VotingPluginPointsRequirement(this, "votingplugin-pointsh"),
        new VotingPluginPointsDeductibleRequirement(this, "votingplugin-points")
      );
    }
    if (Bukkit.getPluginManager().isPluginEnabled("Towny")) {
      requirements.addRequirements(
        new TownyResidentRequirement(this, "towny-resident"),
        new TownyMayorRequirement(this, "towny-mayor"),
        new TownyMayorNumberResidentsRequirement(this, "towny-mayor-residents"),
        new TownyKingRequirement(this, "towny-king"),
        new TownyKingNumberResidentsRequirement(this, "towny-king-residents"),
        new TownyKingNumberTownsRequirement(this, "towny-king-towns")
      );
    }
    if (Bukkit.getPluginManager().isPluginEnabled("TokenManager")) {
      requirements.addRequirements(
        new TokensRequirement(this, "tokenmanager-tokensh"),
        new TokensDeductibleRequirement(this, "tokenmanager-tokens")
      );
    }
    if (Bukkit.getPluginManager().isPluginEnabled("SuperbVote")) {
      requirements.addRequirements(
        new SuperbVoteVotesRequirement(this, "superbvote-votes")
      );
    }
  }
  private void setupEconomy() {
    economy = economyProvider.getEconomy();
  }

  public ConfigurationSection getSection(Rank rank, String path) {
    ConfigurationSection rankSection = rank.getSection();
    if (rankSection == null || !rankSection.isConfigurationSection(path)) {
      return this.messages.getConfigurationSection(path);
    }
    return rankSection.getConfigurationSection(path);
  }

  public MessageBuilder getMessage(Rank rank, Message message) {
    ConfigurationSection messages = rank.getSection();
    if (messages == null || !messages.isSet(message.getName())) {
      messages = this.messages;
    }
    return newMessageBuilder(messages.getString(message.getName()));
  }

  public MessageBuilder getMessage(Message message) {
    return newMessageBuilder(messages.getString(message.getName()));
  }

  public MessageBuilder getMessage(CommandSender player, Message message, Rank oldRank, Rank rank) {
    Rank actualOldRank;
    if (oldRank instanceof Prestige && oldRank.getRank() == null) {
      actualOldRank = rankups.getByName(((Prestige) oldRank).getFrom()).getRank();
    } else {
      actualOldRank = oldRank;
    }

    return getMessage(oldRank, message)
        .replacePlayer(player)
        .replaceRank(rank)
        .replaceOldRank(actualOldRank);
  }

  public void sendHeaderFooter(CommandSender sender, Rank rank, Message type) {
    MessageBuilder builder;
    if (rank == null) {
      builder = getMessage(type)
          .failIfEmpty()
          .replacePlayer(sender);
    } else {
      builder = getMessage(rank, type)
          .failIfEmpty()
          .replacePlayer(sender)
          .replaceRank(rank);
    }
    builder.send(sender);
  }
}
