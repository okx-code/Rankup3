package sh.okx.rankup.prestige;

import org.bukkit.configuration.file.FileConfiguration;
import sh.okx.rankup.ranks.RankElement;
import sh.okx.rankup.ranks.RankList;
import sh.okx.rankup.RankupPlugin;

public class Prestiges extends RankList<Prestige> {
  public Prestiges(RankupPlugin plugin, FileConfiguration config) {
    super(plugin, config, section -> Prestige.deserialize(plugin, section));
  }

  @Override
  public void addLastRank(RankupPlugin plugin) {
    RankElement<Prestige> last = getTree().last();
    last.setNext(new RankElement<>(new LastPrestige(plugin, last.getRank().getNext()), null));
  }

}
