package sh.okx.rankup.messages.pebble;


import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.prestige.Prestige;

public class PrestigeContext extends RankContext {

  private final Prestige rank;

  public PrestigeContext(RankupPlugin plugin, Player player, Prestige rank) {
    super(plugin, player, rank);
    this.rank = rank;
  }

  public String getFrom() {
    return rank.getFrom();
  }

  public String getTo() {
    return rank.getTo();
  }
}
