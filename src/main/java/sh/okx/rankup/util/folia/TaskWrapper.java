package sh.okx.rankup.util.folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a wrapper around {@code BukkitTask} and Paper's {@code ScheduledTask}.
 * This class provides a unified interface for interacting with both Bukkit's task scheduler
 * and Paper's task scheduler.
 */
public class TaskWrapper {

	private BukkitTask bukkitTask;
	private ScheduledTask scheduledTask;

	/**
	 * Constructs a new TaskWrapper around a BukkitTask.
	 *
	 * @param bukkitTask the BukkitTask to wrap
	 */
	public TaskWrapper(@NotNull BukkitTask bukkitTask) {
		this.bukkitTask = bukkitTask;
	}

	/**
	 * Constructs a new TaskWrapper around Paper's ScheduledTask.
	 *
	 * @param scheduledTask the ScheduledTask to wrap
	 */
	public TaskWrapper(@NotNull ScheduledTask scheduledTask) {
		this.scheduledTask = scheduledTask;
	}

	/**
	 * Retrieves the Plugin that owns this task.
	 *
	 * @return the owning {@link Plugin}
	 */
	public Plugin getOwner() {
		return bukkitTask != null ? bukkitTask.getOwner() : scheduledTask.getOwningPlugin();
	}

	/**
	 * Checks if the task is canceled.
	 *
	 * @return true if the task is canceled, false otherwise
	 */
	public boolean isCancelled() {
		return bukkitTask != null ? bukkitTask.isCancelled() : scheduledTask.isCancelled();
	}

	/**
	 * Cancels the task. If the task is running, it will be canceled.
	 */
	public void cancel() {
		if (bukkitTask != null) {
			bukkitTask.cancel();
		} else {
			scheduledTask.cancel();
		}
	}
}
