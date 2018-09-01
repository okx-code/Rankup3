package sh.okx.rankup.requirements;

import java.util.HashSet;
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
}
