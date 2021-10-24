package sh.okx.rankup.prestige;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.ranks.RankElement;
import sh.okx.rankup.ranks.RankList;

public class Prestiges extends RankList<Prestige> {
  public Prestiges(RankupPlugin plugin, FileConfiguration config) {
    super(plugin, convert(plugin, config));
  }

  private static List<Prestige> convert(RankupPlugin plugin, FileConfiguration config) {
    Map<String, Object> values = config.getValues(false);
    List<Prestige> prestiges = new ArrayList<>(values.size());
    for (Map.Entry<String, Object> entry : values.entrySet()) {
      prestiges.add(Prestige.deserialize(plugin, (ConfigurationSection) entry.getValue()));
    }
    return prestiges;
  }

  @Override
  protected void addLastRank(RankupPlugin plugin) {
    RankElement<Prestige> last = getTree().last();
    last.setNext(new RankElement<>(new LastPrestige(plugin, last.getRank().getNext()), null));
  }

}
