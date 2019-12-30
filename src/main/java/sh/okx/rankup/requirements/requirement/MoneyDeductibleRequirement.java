package sh.okx.rankup.requirements.requirement;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Requirement;

public class MoneyDeductibleRequirement extends MoneyRequirement implements DeductibleRequirement {

  public MoneyDeductibleRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  protected MoneyDeductibleRequirement(MoneyDeductibleRequirement clone) {
    super(clone);
  }

  @Override
  public void apply(Player player, double multiplier) {
    Economy economy = plugin.getEconomy();
    economy.withdrawPlayer(player, getValueDouble() * multiplier);
  }

  @Override
  public Requirement clone() {
    return new MoneyDeductibleRequirement(this);
  }
}
