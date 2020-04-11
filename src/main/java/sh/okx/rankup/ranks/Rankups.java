package sh.okx.rankup.ranks;

import org.bukkit.configuration.file.FileConfiguration;
import sh.okx.rankup.RankList;
import sh.okx.rankup.RankupPlugin;

public class Rankups extends RankList<Rank> {
  public Rankups(RankupPlugin plugin, FileConfiguration config) {
    super(config, section -> Rankup.deserialize(plugin, section));
  }
}
