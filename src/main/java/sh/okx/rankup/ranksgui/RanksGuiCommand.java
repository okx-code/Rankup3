package sh.okx.rankup.ranksgui;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sh.okx.rankup.RankupPlugin;

@RequiredArgsConstructor
public class RanksGuiCommand implements CommandExecutor {
    private final RankupPlugin plugin;
    private final RanksGuiListener listener;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        listener.open(new RanksGui(plugin, player));
        return true;
    }
}