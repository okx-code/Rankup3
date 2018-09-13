package sh.okx.rankup.ranks;

import org.bukkit.configuration.file.FileConfiguration;
import sh.okx.rankup.RankList;
import sh.okx.rankup.Rankup;

public class Rankups extends RankList<Rank> {
  public Rankups(Rankup plugin, FileConfiguration config) {
    super(config, section -> Rank.deserialize(plugin, section));
  }
}
