package sh.okx.rankup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import sh.okx.rankup.requirements.Operation;
import sh.okx.rankup.requirements.OperationRegistry;
import sh.okx.rankup.requirements.Requirement;
import sh.okx.rankup.requirements.RequirementRegistry;

/**
 * Called immediately before rankups and prestiges are registered,
 * and immediately after the built-in requirements and operations are registered.
 * This is used to register custom requirements.
 * This is called when the plugin is enabled, and when it is reloaded from a command.
 */
@RequiredArgsConstructor
public class RankupRegisterEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final Rankup plugin;

  public RequirementRegistry getRequirementRegistry() {
    return plugin.getRequirementRegistry();
  }

  public OperationRegistry getOperationRegistry() {
    return plugin.getOperationRegistry();
  }

  public void addRequirement(Requirement requirement) {
    plugin.getRequirementRegistry().addRequirement(requirement);
  }

  public void addOperation(String name, Operation operation) {
    plugin.getOperationRegistry().addOperation(name, operation);
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
