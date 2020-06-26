package sh.okx.rankup.economy;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestEconomy implements Economy {
    private final Map<UUID, Double> balances = new HashMap<>();

    @Override
    public double getBalance(Player player) {
        return balances.get(player.getUniqueId());
    }

    @Override
    public void withdrawPlayer(Player player, double amount) {
        balances.put(player.getUniqueId(), balances.getOrDefault(player.getUniqueId(), 0D) - amount);
    }

    public void setPlayer(Player player, double amount) {
        balances.put(player.getUniqueId(), amount);
    }
}
