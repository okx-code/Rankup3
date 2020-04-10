package sh.okx.rankup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import sh.okx.rankup.requirements.Requirement;
import sh.okx.rankup.requirements.RequirementRegistry;

/**
 * Called immediately before rankups and prestiges are registered,
 * and immediately after the built-in requirements are registered.
 * This is used to register custom requirements.
 * This is called when the plugin is enabled, and when it is reloaded from a command.
 */
@RequiredArgsConstructor
public class RankupRegisterEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final RankupPlugin plugin;

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public RequirementRegistry getRequirementRegistry() {
    return plugin.getRequirements();
  }

  public void addRequirement(Requirement requirement) {
    plugin.getRequirements().addRequirement(requirement);
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
