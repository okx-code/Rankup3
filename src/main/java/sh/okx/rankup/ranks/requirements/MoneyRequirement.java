package sh.okx.rankup.ranks.requirements;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;

public class MoneyRequirement extends Requirement {
  public MoneyRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  protected MoneyRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    Economy economy = plugin.getEconomy();
    double balance = economy.getBalance(player);
    return balance >= amount;
  }

  @Override
  public void apply(Player player) {
    Economy economy = plugin.getEconomy();
    economy.withdrawPlayer(player, amount);
  }

  @Override
  public double getRemaining(Player player) {
    return Math.max(0, amount - plugin.getEconomy().getBalance(player));
  }

  @Override
  public Requirement clone() {
    return new MoneyRequirement(this);
  }
}