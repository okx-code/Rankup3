package sh.okx.rankup.requirements.requirement;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

import java.util.Arrays;

public class ItemRequirement extends DeductibleRequirement {
  public ItemRequirement(Rankup plugin) {
    super(plugin, "item", true);
  }

  protected ItemRequirement(ItemRequirement clone) {
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
    return new ItemRequirement(this);
  }

  @Override
  public double getProgress(Player player) {
    Material material = Material.matchMaterial(getSub());
    return Arrays.stream(player.getInventory().getStorageContents())
        .filter(item -> item != null && item.getType() == material)
        .mapToInt(ItemStack::getAmount).sum();
  }
}
