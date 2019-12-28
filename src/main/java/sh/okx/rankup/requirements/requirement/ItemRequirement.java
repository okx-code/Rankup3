package sh.okx.rankup.requirements.requirement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

import java.util.Arrays;

public class ItemRequirement extends ProgressiveRequirement {
  public ItemRequirement(Rankup plugin, String name) {
    super(plugin, name, true);
  }

  protected ItemRequirement(ItemRequirement clone) {
    super(clone);
  }

  @Override
  public Requirement clone() {
    return new ItemRequirement(this);
  }

  @Override
  public double getProgress(Player player) {
    Material material = Material.matchMaterial(getSub());
    return Arrays.stream(player.getInventory().getContents())
        .filter(item -> item != null && item.getType() == material)
        .mapToInt(ItemStack::getAmount).sum();
  }
}
