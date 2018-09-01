package sh.okx.rankup.ranks;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.messages.Variable;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Operation;
import sh.okx.rankup.requirements.Requirement;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Rank {
  private final Rankup plugin;
  @Getter
  private final String name;
  @Getter
  private final String next;
  @Getter
  private final String rank;
  @Getter
  private final Set<Requirement> requirements;
  private final Operation operation;
  private final List<String> commands;

  public static Rank deserialize(Rankup plugin, ConfigurationSection section) {
    String rank = section.getString("rank");

    Set<Requirement> requirements = new HashSet<>();
    Operation operation = null;
    ConfigurationSection requirementsSection = section.getConfigurationSection("requirements");
    if (requirementsSection != null) {
      for (Map.Entry<String, Object> entry : requirementsSection.getValues(false).entrySet()) {
        String name = entry.getKey();
        String value = String.valueOf(entry.getValue());
        Requirement requirement = plugin.getRequirementRegistry().newRequirement(name, value);
        if (requirement == null) {
          plugin.getLogger().warning("Unknown requirement " + name);
        } else {
          requirements.add(requirement);
        }
      }

      String operationName = Optional.ofNullable(section.getString("operation")).orElse("all");
      operation = plugin.getOperationRegistry().getOperation(operationName);
    }

    return new Rank(plugin,
        section.getName(),
        section.getString("next"),
        rank,
        requirements,
        operation,
        section.getStringList("commands"));
  }

  public boolean checkRequirements(Player player) {
    return operation.check(requirements.stream()
        .map(requirement -> requirement.check(player))
        .collect(Collectors.toList()));
  }

  public boolean isInRank(Player player) {
    String[] groups = plugin.getPermissions().getPlayerGroups(player);
    for (String group : groups) {
      if (group.equalsIgnoreCase(rank)) {
        return true;
      }
    }
    return false;
  }

  public boolean isLastRank() {
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
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), new MessageBuilder(command)
          .replace(Variable.PLAYER, player.getName())
          .replace(Variable.OLD_RANK, rank)
          .replace(Variable.OLD_RANK_NAME, name)
          .replace(Variable.RANK, nextRank.rank)
          .replace(Variable.RANK_NAME, nextRank.name)
          .toString());
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
