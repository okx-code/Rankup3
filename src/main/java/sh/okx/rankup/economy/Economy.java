package sh.okx.rankup.economy;

import org.bukkit.entity.Player;

public interface Economy {
    double getBalance(Player player);
    void withdrawPlayer(Player player, double amount);
    void setPlayer(Player player, double amount);
}
