package sh.okx.rankup.ranks;

import java.util.ArrayList;
import java.util.List;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.serialization.RankSerialized;

public class Rankups extends RankList<Rank> {

  public Rankups(RankupPlugin plugin, List<RankSerialized> serializedRanks) {
    super(plugin, convert(plugin, serializedRanks));
  }

  private static List<Rankup> convert(RankupPlugin plugin, List<RankSerialized> ranks) {
    List<Rankup> rankups = new ArrayList<>(ranks.size());
    for (RankSerialized rank : ranks) {
      rankups.add(Rankup.deserialize(plugin, rank));
    }
    return rankups;
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
