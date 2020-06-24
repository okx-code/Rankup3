package sh.okx.rankup.prestige;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.ranks.requirements.LastRankRequirements;
import sh.okx.rankup.requirements.NullRequirement;
import sh.okx.rankup.requirements.Requirement;

import java.util.Collections;

public class LastPrestige extends Prestige {
  public LastPrestige(RankupPlugin plugin, String name) {
    super(null, plugin, null, name, new LastRankRequirements(), Collections.emptyList(), null, null);
  }

  @Override
  public boolean isIn(Player player) {
    return plugin.getPermissions().inGroup(player.getUniqueId(), rank);
  }

  @Override
  public boolean hasRequirements(Player player) {
    return false;
  }

  @Override
  public Requirement getRequirement(Player player, String name) {
    return new NullRequirement();
  }

  @Override
  public void applyRequirements(Player player) {
  }

  @Override
  public void runCommands(Player player) {
  }

  @Override
  public boolean isEligible(Player player) {
    return true;
  }
}
