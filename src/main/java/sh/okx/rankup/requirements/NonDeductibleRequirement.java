package sh.okx.rankup.requirements;

import org.bukkit.entity.Player;

/**
 * Proxy requirement for a deductible requirement that is exactly the same but is not deductible
 */
public class NonDeductibleRequirement extends ProgressiveRequirement {
  private final DeductibleRequirement requirement;

  public NonDeductibleRequirement(DeductibleRequirement requirement, String name) {
    super(requirement.plugin, name, requirement.hasSubRequirement());
    this.requirement = requirement;
  }

  protected NonDeductibleRequirement(NonDeductibleRequirement clone) {
    super(clone);
    this.requirement = clone.requirement;
  }

  @Override
  public double getProgress(Player player) {
    return requirement.getProgress(player);
  }

  @Override
  public Requirement clone() {
    return new NonDeductibleRequirement(this);
  }
}
