package sh.okx.rankup.ranks;

import org.bukkit.configuration.file.FileConfiguration;
import sh.okx.rankup.RankupPlugin;

public class Rankups extends RankList<Rank> {

  public Rankups(RankupPlugin plugin, FileConfiguration config) {
    super(plugin, config, section -> Rankup.deserialize(plugin, section));
  }

  @Override
  protected void addLastRank(RankupPlugin plugin) {
    RankElement<Rank> last = getTree().last();
    String lastRankDisplayName = plugin.getConfig().getString("placeholders.last-rank-display-name");
    String lastRankName = last.getRank().getNext();
    if (lastRankDisplayName == null) {
      lastRankDisplayName = lastRankName;
    }
    last.setNext(new RankElement<>(new LastRank(plugin, lastRankName, lastRankDisplayName), null));
  }
}
