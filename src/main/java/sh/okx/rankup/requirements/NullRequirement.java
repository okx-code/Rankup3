package sh.okx.rankup.requirements;

import org.bukkit.entity.Player;

public class NullRequirement extends Requirement {
  public NullRequirement() {
    super(null, null);
  }

  @Override
  public boolean check(Player player) {
    return false;
  }

  @Override
  public Requirement clone() {
    return this;
  }

  @Override
  public double getTotal(Player player) {
    return 0;
  }
}
