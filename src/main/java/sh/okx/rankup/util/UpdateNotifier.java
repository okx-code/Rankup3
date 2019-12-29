package sh.okx.rankup.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import sh.okx.rankup.util.VersionChecker.VersionCheckerCallback;

public class UpdateNotifier {
  private static final String PREFIX = ChatColor.GREEN + "" + ChatColor.BOLD + "Rankup " + ChatColor.RESET;

  private final VersionChecker checker;

  public UpdateNotifier(VersionChecker checker) {
    this.checker = checker;
  }

  public void notify(CommandSender sender, boolean join) {
    if (!checker.hasChecked() && !join) {
      send(sender, false, ChatColor.YELLOW + "Checking version...");
    }
    checker.checkVersion(new VersionCheckerCallback() {
      @Override
      public void onLatestVersion(String version) {
        if (!join) {
          send(sender, false, ChatColor.GREEN + "You are on the latest version.");
        }
      }

      @Override
      public void onOutdatedVersion(String currentVersion, String latestVersion) {
        send(sender, join, ChatColor.YELLOW + "A new version is available: " + ChatColor.GOLD + latestVersion
            + ChatColor.YELLOW + ". You are on: " + ChatColor.GOLD + currentVersion
            + ChatColor.GOLD + "\nhttps://www.spigotmc.org/resources/rankup.17933/");
      }

      @Override
      public void onPreReleaseVersion(String version) {
        send(sender, join, ChatColor.RED + "You are on a pre-release version.");
      }

      @Override
      public void onFailure() {
        if (!join) {
          send(sender, false, ChatColor.RED + "Error while checking version.");
        }
      }
    });
  }

  private void send(CommandSender sender, boolean prefix, String message) {
    if (prefix) {
      sender.sendMessage(PREFIX + message);
    } else {
      sender.sendMessage(message);
    }
  }
}
