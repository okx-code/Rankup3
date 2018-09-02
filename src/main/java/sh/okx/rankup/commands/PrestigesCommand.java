package sh.okx.rankup.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;

@RequiredArgsConstructor
public class PrestigesCommand implements CommandExecutor {
  private final Rankup plugin;

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Prestiges prestiges = plugin.getPrestiges();
    Prestige playerRank = null;
    if (sender instanceof Player) {
      playerRank = prestiges.getByPlayer((Player) sender);
    }

    plugin.sendHeaderFooter(sender, playerRank, Message.PRESTIGES_HEADER);

    Message message = playerRank == null ? Message.PRESTIGES_INCOMPLETE : Message.PRESTIGES_COMPLETE;
    Prestige prestige = prestiges.getFirst();
    if(!prestige.isLast()) {
      prestige = prestiges.next(prestige);
      while (!prestige.isLast()) {
        Prestige next = prestiges.next(prestige);
        if (prestige.equals(playerRank)) {
          plugin.sendMessage(sender, Message.PRESTIGES_CURRENT, prestige, next);
          message = Message.PRESTIGES_INCOMPLETE;
        } else {
          plugin.sendMessage(sender, message, prestige, next);
        }
        prestige = next;
      }
    }

    plugin.sendHeaderFooter(sender, playerRank, Message.PRESTIGES_FOOTER);
    return true;
  }
}
