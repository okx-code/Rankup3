package sh.okx.rankup.requirements;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequirementRegistry {
  private Set<Requirement> requirements = new HashSet<>();

  public void addRequirement(Requirement requirement) {
    requirements.add(requirement);
  }

  public Requirement newRequirement(String name, String value) {
    for (Requirement requirement : requirements) {
      if (requirement.getName().equalsIgnoreCase(name)) {
        Requirement newRequirement = requirement.clone();
        newRequirement.setValue(value);
        return newRequirement;
      }
    }
    return null;
  }

  public Set<Requirement> getRequirements(ConfigurationSection section) {
    Set<Requirement> requirements = new HashSet<>();

    for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
      String name = entry.getKey();
      String value = String.valueOf(entry.getValue());
      Requirement requirement = newRequirement(name, value);
      if (requirement == null) {
        System.err.println("Unknown requirement: " + name);
      } else {
        requirements.add(requirement);
      }
    }
    return requirements;
  }

  public void apply(Player player, Set<Requirement> requirements) {
    for (Requirement requirement : requirements) {
      if (requirement instanceof DeductibleRequirement) {
        ((DeductibleRequirement) requirement).apply(player);
      }
    }
  }
}
