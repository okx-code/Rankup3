package sh.okx.rankup.providers;

import sh.okx.rankup.economy.Economy;
import sh.okx.rankup.economy.EconomyProvider;

public class TestEconomyProvider implements EconomyProvider {
    @Override
    public Economy getEconomy() {
        return new TestEconomy();
    }
}
