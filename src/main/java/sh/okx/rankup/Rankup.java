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
import sh.okx.rankup.ranks.requirements.PlaytimeHoursRequirement;
import sh.okx.rankup.ranks.requirements.XpLevelRequirement;
import sh.okx.rankup.ranks.requirements.MoneyRequirement;
import sh.okx.rankup.ranks.requirements.RequirementRegistry;

import java.io.File;

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

    getCommand("rankup").setExecutor(new RankupCommand(this));
    getCommand("rankup3").setExecutor(new InfoCommand(this));
    getCommand("ranks").setExecutor(new RankListCommand(this));
    getServer().getPluginManager().registerEvents(new GuiListener(this), this);
  }


  @Override
  public void onDisable() {
    closeInventories();
    PlaceholderAPI.unregisterExpansion(placeholders);
  }

  public void reload() {
    closeInventories();
    loadConfigs();
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      placeholders = new Placeholders(this);
      placeholders.register();
    }

    if(config.getInt("version") != YamlConfiguration.loadConfiguration(getTextResource("config.yml")).getInt("version")) {
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
    for(Player player : Bukkit.getOnlinePlayers()) {
      InventoryView view = player.getOpenInventory();
      if(view.getType() == InventoryType.CHEST
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
    requirementRegistry.addRequirement(new PlaytimeHoursRequirement(this, "playtime-hours"));
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

  public MessageBuilder getMessage(Rank rank, Message message) {
    ConfigurationSection messages = rankups.getConfig()
        .getConfigurationSection(rank.getName());
    if(messages == null || !messages.isSet(message.getName())) {
      messages = this.messages;
    }
    return MessageBuilder.of(messages, message);
  }

  public MessageBuilder getMessage(Message message) {
    return MessageBuilder.of(messages, message);
  }

  public void rankup(Player player) {
    if(!checkRankup(player)) {
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
  }

  /**
   * Checks if a player can rankup,
   * and if they can't, sends the player a message and returns false
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
              .replaceAll(player, rank);
      if (economy != null) {
        double balance = economy.getBalance(player);
        builder = builder
            .replace(Variable.MONEY, balance)
            .replace(Variable.MONEY_NEEDED, rank.getRequirement("money").getAmount() - balance);
      }
      builder.send(player);
      return false;
    }
    return true;
  }
}
