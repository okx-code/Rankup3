package sh.okx.rankup.requirements.requirement;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class MoneyRequirement extends ProgressiveRequirement implements DeductibleRequirement {
  public MoneyRequirement(Rankup plugin) {
    super(plugin, "money");
  }

  protected MoneyRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public void apply(Player player, double multiplier) {
    Economy economy = plugin.getEconomy();
    economy.withdrawPlayer(player, getValueDouble() * multiplier);
  }

  @Override
  public double getProgress(Player player) {
    return plugin.getEconomy().getBalance(player);
  }

  @Override
  public Requirement clone() {
    return new MoneyRequirement(this);
  }
}