package sh.okx.rankup.requirements.requirement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Requirement;

public class ItemDeductibleRequirement extends ItemRequirement implements DeductibleRequirement {

  public ItemDeductibleRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  public ItemDeductibleRequirement(ItemDeductibleRequirement clone) {
    super(clone);
  }

  @Override
  public void apply(Player player, double multiplier) {
    Material type = Material.matchMaterial(getSub());
    if (type == null) {
      throw new IllegalArgumentException("Invalid item " + getSub());
    }
    player.getInventory().removeItem(new ItemStack(type, (int) (getValueInt() * multiplier)));
  }

  @Override
  public Requirement clone() {
    return new ItemDeductibleRequirement(this);
  }
}
