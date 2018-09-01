package sh.okx.rankup.commands;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import sh.okx.rankup.Rankup;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@RequiredArgsConstructor
public class InfoCommand implements CommandExecutor {
  private final Rankup plugin;
  private String versionMessage;

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length > 0) {
      if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("rankup.reload")) {
        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Rankup " + ChatColor.YELLOW + "Reloaded configuration files.");
        return true;
      }
    }

    PluginDescriptionFile description = plugin.getDescription();
    sender.sendMessage(
        ChatColor.GREEN + "" + ChatColor.BOLD + description.getName() + " " + description.getVersion() +
            ChatColor.YELLOW + " by " + ChatColor.BLUE + ChatColor.BOLD + String.join(", ", description.getAuthors()));
    if (sender.hasPermission("rankup.reload")) {
      sender.sendMessage(ChatColor.GREEN + "/" + label + " reload " + ChatColor.YELLOW + "Reloads configuration files.");
    }
    if (sender.hasPermission("rankup.checkversion")) {
      if (versionMessage == null) {
        sender.sendMessage(ChatColor.YELLOW + "Checking version...");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
          String message;
          try {
            String latest = getLatestVersion();
            if (description.getVersion().equals(latest)) {
              message = ChatColor.GREEN + "You are on the latest version.";
            } else {
              message = ChatColor.YELLOW + "A new version is available: " + ChatColor.GOLD + latest
                  + "\nhttps://www.spigotmc.org/resources/rankup.17933/";
            }
          } catch (IOException e) {
            message = ChatColor.RED + "Error while checking version.";
          }
          versionMessage = message;
          Bukkit.getScheduler().runTask(plugin, () -> sender.sendMessage(versionMessage));
        });
      } else {
        sender.sendMessage(versionMessage);
      }
    }

    return true;
  }

  private String getLatestVersion() throws IOException {
    URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=17933");
    return CharStreams.toString(new InputStreamReader(url.openStream(), Charsets.UTF_8));
  }
}
