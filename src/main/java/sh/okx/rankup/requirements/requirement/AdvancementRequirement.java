package sh.okx.rankup.requirements.requirement;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.Requirement;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Advancement requirement
 * @author Link, with modifications from Okx
 */
public class AdvancementRequirement extends Requirement {
  public AdvancementRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected AdvancementRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    String string = getValueString();
    Iterator<Advancement> advancementIterator = Bukkit.advancementIterator();
    while (advancementIterator.hasNext()) {
      Advancement adv = advancementIterator.next();
      Pattern pattern = Pattern.compile(string.replace("*", ".*").replace("-", ""));
      if (pattern.matcher(adv.getKey().getKey()).find()) {
        boolean positive = !string.startsWith("-");
        AdvancementProgress progress = player.getAdvancementProgress(adv);
        if (progress.isDone() == positive) {
          return true;
        } else {
          return false;
        }
      }
    }
    return false;
  }

  @Override
  public Requirement clone() {
    return new AdvancementRequirement(this);
  }

  @Override
  public double getValueDouble() {
    return 1;
  }

  @Override
  public String getSub(){
    return getValueString();
  }
}
