package sh.okx.rankup.ranks;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Requirement;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Rank {
  @Getter
  protected final ConfigurationSection section;
  protected final Rankup plugin;
  @Getter
  protected final String next;
  @Getter
  protected final String rank;
  @Getter
  protected final Set<Requirement> requirements;
  protected final List<String> commands;

  public static Rank deserialize(Rankup plugin, ConfigurationSection section) {
    List<String> requirementsList = section.getStringList("requirements");
    Set<Requirement> requirements = plugin.getRequirements().getRequirements(requirementsList);

    return new Rank(section, plugin,
        section.getString("next"),
        section.getString("rank"),
        requirements,
        section.getStringList("commands"));
  }

  public boolean hasRequirements(Player player) {
    for (Requirement requirement : requirements) {
      if (!requirement.check(player)) {
        return false;
      }
    }
    return true;
  }

  public boolean isIn(Player player) {
    String[] groups = plugin.getPermissions().getPlayerGroups(null, player);
    for (String group : groups) {
      if (group.equalsIgnoreCase(rank)) {
        return true;
      }
    }
    return false;
  }

  public boolean isLast() {
    return plugin.getRankups().getByName(next) == null;
  }

  public Requirement getRequirement(String name) {
    for (Requirement requirement : requirements) {
      String reqName = requirement.getName();
      if (requirement.hasSubRequirement()) {
        reqName += "#" + requirement.getSub();
      }
      if (reqName.equalsIgnoreCase(name)) {
        return requirement;
      }
    }
    return null;
  }

  public void applyRequirements(Player player) {
    for (Requirement requirement : requirements) {
      if (requirement instanceof DeductibleRequirement) {
        ((DeductibleRequirement) requirement).apply(player);
      }
    }
  }

  public void runCommands(Player player) {
    for (String command : commands) {
      String string = new MessageBuilder(command).replaceRanks(player, this, next).toString();
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        string = PlaceholderAPI.setPlaceholders(player, string);
      }
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string);
    }
  }
}
