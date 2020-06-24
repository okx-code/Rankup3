package sh.okx.rankup.ranks;

import org.bukkit.configuration.file.FileConfiguration;
import sh.okx.rankup.RankupPlugin;

public class Rankups extends RankList<Rank> {
  public Rankups(RankupPlugin plugin, FileConfiguration config) {
    super(plugin, config, section -> Rankup.deserialize(plugin, section));
  }

  @Override
  public void addLastRank(RankupPlugin plugin) {
    RankElement<Rank> last = getTree().last();
    last.setNext(new RankElement<>(new LastRank(plugin, last.getRank().getNext()), null));
  }
}
