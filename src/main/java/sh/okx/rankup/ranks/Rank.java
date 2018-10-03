package sh.okx.rankup.ranks;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Operation;
import sh.okx.rankup.requirements.Requirement;
import sh.okx.rankup.requirements.operation.AllOperation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Rank {
  protected final Rankup plugin;
  @Getter
  protected final String name;
  @Getter
  protected final String next;
  @Getter
  protected final String rank;
  @Getter
  protected final Set<Requirement> requirements;
  protected final Operation operation;
  protected final List<String> commands;

  public static Rank deserialize(Rankup plugin, ConfigurationSection section) {
    Set<Requirement> requirements = new HashSet<>();
    Operation operation = null;
    ConfigurationSection requirementsSection = section.getConfigurationSection("requirements");

    if (requirementsSection != null) {
      requirements = plugin.getRequirementRegistry().getRequirements(requirementsSection);
      operation = plugin.getOperationRegistry().getOperation(section.getString("operation"));
    } else if(section.contains("next")) {
      plugin.getLogger().severe("Rank " + section.getName() + " has no requirements.");
      return null;
    }

    return new Rank(plugin,
        section.getName(),
        section.getString("next"),
        section.getString("rank"),
        requirements,
        operation,
        section.getStringList("commands"));
  }

  public boolean hasRequirements(Player player) {
    return operation.check(requirements.stream()
        .map(requirement -> requirement.check(player))
        .collect(Collectors.toList()));
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
    return next == null;
  }

  public Requirement getRequirement(String name) {
    for (Requirement requirement : requirements) {
      if (requirement.getName().equalsIgnoreCase(name)) {
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

  public void runCommands(Player player, Rank nextRank) {
    for (String command : commands) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
          new MessageBuilder(command).replaceRanks(player, this, nextRank).toString());
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Rank)) {
      return false;
    }
    return ((Rank) o).name.equals(name);
  }
}
