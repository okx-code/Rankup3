package sh.okx.rankup.ranks.requirements;

import java.util.HashSet;
import java.util.Set;

public class RequirementRegistry {
  private Set<Requirement> requirements = new HashSet<>();

  public void addRequirement(Requirement requirement) {
    requirements.add(requirement);
  }

  public Requirement newRequirement(String name, double amount) {
    for(Requirement requirement : requirements) {
      if(requirement.getName().equalsIgnoreCase(name)) {
        Requirement newRequirement = requirement.clone();
        newRequirement.setAmount(amount);
        return newRequirement;
      }
    }
    return null;
  }
}
