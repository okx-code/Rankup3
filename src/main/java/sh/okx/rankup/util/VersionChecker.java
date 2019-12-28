package sh.okx.rankup.util;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class VersionChecker {

  private static final int RESOURCE_ID = 17933;

  private final Plugin plugin; // used exclusively for scheduling
  private final String currentVersion;
  private String latestVersion;
  private boolean checked = false;

  public VersionChecker(Plugin plugin) {
    this.currentVersion = plugin.getDescription().getVersion();
    this.plugin = plugin;
  }

  /**
   * Checks if the version checker has already made an asynchronous call to the web server to check
   * the version, so future checks will run instantly.
   * @return true if the version checker already knows the latest version, false otherwise
   */
  public boolean hasChecked() {
    return checked;
  }

  public void checkVersion(VersionCheckerCallback callback) {
    if (latestVersion != null) {
      if (currentVersion.equals(latestVersion)) {
        callback.onLatestVersion(currentVersion);
      } else {
        callback.onOutdatedVersion(currentVersion, latestVersion);
      }
    } else {
      Bukkit.getScheduler().runTaskAsynchronously(plugin,
          () -> checkVersionSync(new SyncVersionCheckerCallback(plugin, callback)));
    }
  }

  private void checkVersionSync(VersionCheckerCallback callback) {
    if (currentVersion.contains("alpha")
        || currentVersion.contains("beta")
        || currentVersion.contains("rc")) {
      checked = true;
      callback.onPreReleaseVersion(currentVersion);
      return;
    }

    try {
      latestVersion = getLatestVersion();
      checked = true;
      if (currentVersion.equals(latestVersion)) {
        callback.onLatestVersion(currentVersion);
      } else {
        callback.onOutdatedVersion(currentVersion, latestVersion);
      }

    } catch (IOException e) {
      callback.onFailure();
    }
  }

  private String getLatestVersion() throws IOException {
    URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID);
    return CharStreams.toString(new InputStreamReader(url.openStream(), Charsets.UTF_8));
  }

  /**
   * A callback used when a version check runs
   */
  public interface VersionCheckerCallback {

    /**
     * Called when the plugin is already on the latest version
     *
     * @param version the current, and latest, version of the plugin
     */
    void onLatestVersion(String version);

    /**
     * Called when the plugin is on a version other than the latest on the SpigotMC plugin page.
     *
     * @param currentVersion the current version of the plugin specified in plugin.yml
     * @param latestVersion the latest version of the plugin specified on SpigotMC.
     */
    void onOutdatedVersion(String currentVersion, String latestVersion);

    /**
     * Called when the plugin is on a pre-release version and is exempt to the usual update system.
     * @param version the current version of the plugin
     */
    void onPreReleaseVersion(String version);

    /**
     * Called when the version checker was unable to retrieve the latest version
     */
    void onFailure();
  }

  /**
   * An implementation of {@link VersionCheckerCallback} that is called asynchronously, and then
   * forwards the calls an underlying VersionCheckerCallback synchronously on the main Bukkit thread.
   */
  class SyncVersionCheckerCallback implements VersionCheckerCallback {
    private final Plugin plugin;
    private final VersionCheckerCallback callback;

    SyncVersionCheckerCallback(Plugin plugin, VersionCheckerCallback callback) {
      this.plugin = plugin;
      this.callback = callback;
    }

    @Override
    public void onLatestVersion(String version) {
      doSync(() -> callback.onLatestVersion(version));
    }

    @Override
    public void onOutdatedVersion(String currentVersion, String latestVersion) {
      doSync(() -> callback.onOutdatedVersion(currentVersion, latestVersion));
    }

    @Override
    public void onPreReleaseVersion(String version) {
      doSync(() -> callback.onPreReleaseVersion(version));
    }

    @Override
    public void onFailure() {
      doSync(callback::onFailure);
    }

    private void doSync(Runnable r) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, r);
    }
  }
}
