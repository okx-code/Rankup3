package sh.okx.rankup.requirements.requirement;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Requirement;

public class MoneyRequirement extends DeductibleRequirement {
  public MoneyRequirement(Rankup plugin) {
    super(plugin, "money");
  }

  protected MoneyRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    Economy economy = plugin.getEconomy();
    double balance = economy.getBalance(player);
    return balance >= getValueDouble();
  }

  @Override
  public void apply(Player player) {
    Economy economy = plugin.getEconomy();
    economy.withdrawPlayer(player, getValueDouble());
  }

  @Override
  public double getRemaining(Player player) {
    return Math.max(0, getValueDouble() - plugin.getEconomy().getBalance(player));
  }

  @Override
  public Requirement clone() {
    return new MoneyRequirement(this);
  }
}