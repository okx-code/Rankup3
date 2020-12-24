package sh.okx.rankup.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankElement;

/**
 * Called when a player ranks up from one rank to another.
 */
public class PlayerRankupEvent extends PlayerEvent {
  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final RankupPlugin plugin;
  /**
   * The rank someone is current at. Use <code>RankElement#getNext()</code> to get the rank
   * a player is ranking up to.
   */
  @Getter
  private final RankElement<Rank> rank;

  public PlayerRankupEvent(RankupPlugin plugin, @NotNull Player who, RankElement<Rank> rank) {
    super(who);
    this.plugin = plugin;
    this.rank = rank;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
