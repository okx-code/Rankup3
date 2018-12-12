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
  public Prestige getByPlayer(Player player) {
    Prestige prestige = super.getByPlayer(player);
    if (prestige == null) {
      return getFirst();
    } else {
      return prestige;
    }
  }
}
