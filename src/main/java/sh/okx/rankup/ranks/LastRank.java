package sh.okx.rankup.ranks;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.ranks.requirements.LastRankRequirements;
import sh.okx.rankup.requirements.NullRequirement;
import sh.okx.rankup.requirements.Requirement;

import java.util.Collections;

public class LastRank extends Rank {
  public LastRank(RankupPlugin plugin, String name) {
    super(null, plugin, null, name, new LastRankRequirements(), Collections.emptyList());
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
}
