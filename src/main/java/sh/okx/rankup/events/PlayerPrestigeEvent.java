package sh.okx.rankup.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.ranks.RankElement;

/**
 * Called when a player ranks up from one prestige to another.
 */
public class PlayerPrestigeEvent extends PlayerEvent {
  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final RankupPlugin plugin;
  /**
   * The prestige someone is current at. Use <code>RankElement#getNext()</code> to get the prestige
   * a player is ranking up to.
   */
  @Getter
  private final RankElement<Prestige> prestige;

  public PlayerPrestigeEvent(RankupPlugin plugin, @NotNull Player who, RankElement<Prestige> prestige) {
    super(who);
    this.plugin = plugin;
    this.prestige = prestige;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
