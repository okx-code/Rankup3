package sh.okx.rankup.economy;

public class TestEconomyProvider implements EconomyProvider {
    @Override
    public Economy getEconomy() {
        return new TestEconomy();
    }
}
