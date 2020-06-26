package sh.okx.rankup.economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomyProvider implements EconomyProvider {
    @Override
    public Economy getEconomy() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp != null) {
            return new VaultEconomy(rsp.getProvider());
        } else {
            return null;
        }
    }
}
