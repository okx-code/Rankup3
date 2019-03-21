package sh.okx.rankup.requirements;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

  public Set<Requirement> getRequirements(List<String> list) {
    Set<Requirement> requirements = new HashSet<>();

    for (String req : list) {
      String[] parts = req.split(" ", 2);
      String name = parts[0];
      String value = parts[1];
      Requirement requirement = newRequirement(name, value);
      Objects.requireNonNull(requirement, "Unknown requirement: " + name);
      requirements.add(requirement);
    }
    return requirements;
  }
}
