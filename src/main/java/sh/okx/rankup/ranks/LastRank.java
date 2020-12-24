package sh.okx.rankup.ranks;

import java.util.Collections;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.ranks.requirements.LastRankRequirements;

public class LastRank extends Rank {
  public LastRank(RankupPlugin plugin, String name, String displayName) {
    super(null, plugin, null, name, displayName, new LastRankRequirements(), Collections.emptyList());
  }

  @Override
  public boolean hasRequirements(Player player) {
    return false;
  }

  @Override
  public void applyRequirements(Player player) {
  }

  @Override
  public void runCommands(Player player, Rank next) {
  }
}
