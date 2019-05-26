package sh.okx.rankup.requirements.requirement;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class ItemRequirement extends DeductibleRequirement {
  public ItemRequirement(Rankup plugin) {
    super(plugin, "item", true);
  }

  protected ItemRequirement(ItemRequirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    Material material = Material.matchMaterial(getSub());
    int count = 0;
    for (ItemStack item : player.getInventory().getStorageContents()) {
      if (item != null && item.getType() == material) {
        count += item.getAmount();
      }
    }
    return count;
  }

  @Override
  public void apply(Player player, double multiplier) {
    player.getInventory().removeItem(new ItemStack(Material.matchMaterial(getSub()), (int) (getValueInt() * multiplier)));
  }

  @Override
  public Requirement clone() {
    return new ItemRequirement(this);
  }
}
