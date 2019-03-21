package sh.okx.rankup.prestige;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankList;
import sh.okx.rankup.Rankup;

public class Prestiges extends RankList<Prestige> {
  public Prestiges(Rankup plugin, FileConfiguration config) {
    super(config, section -> Prestige.deserialize(plugin, section));
  }

  @Override
  public Prestige getFirst() {
    for (Prestige prestige : ranks) {
      if (prestige.getRank() == null) {
        return prestige;
      }
    }
    throw new IllegalStateException("No prestige found for first prestige (first prestige is counted as a prestige without a rank set)");
  }
}
