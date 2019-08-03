package sh.okx.rankup.requirements.requirement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

import java.util.Arrays;

public class ItemhRequirement extends ProgressiveRequirement {
  public ItemhRequirement(Rankup plugin) {
    super(plugin, "itemh", true);
  }

  protected ItemhRequirement(ItemRequirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    Material material = Material.matchMaterial(getSub());
    return Arrays.stream(player.getInventory().getStorageContents())
        .filter(item -> item != null && item.getType() == material)
        .mapToInt(ItemStack::getAmount).sum();
  }

  @Override
  public Requirement clone() {
    return new ItemhRequirement(plugin);
  }
}
