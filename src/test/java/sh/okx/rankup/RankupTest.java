package sh.okx.rankup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sh.okx.rankup.economy.TestEconomyProvider;
import sh.okx.rankup.hook.GroupProvider;
import sh.okx.rankup.hook.TestGroupProvider;
import sh.okx.rankup.hook.TestPermissionManager;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.messages.Variable;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankElement;

public class RankupTest {
    protected GroupProvider groupProvider;
    protected ServerMock server;
    protected RankupPlugin plugin;

    @Before
    public void setup() {
        System.setProperty("TEST", "true");

        try {
            groupProvider = new TestGroupProvider();

            server = MockBukkit.mock();
            plugin = MockBukkit.load(RankupPlugin.class, new TestPermissionManager(groupProvider), new TestEconomyProvider());
            // let rankup finish setting up
            server.getScheduler().performTicks(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAutoRankup() {
        PlayerMock player = server.addPlayer();

        // requirement of $1000
        plugin.getEconomy().setPlayer(player, 1000);
        // give them group A
        groupProvider.addGroup(player.getUniqueId(), "A");
        // give the permission to auto rankup
        player.addAttachment(plugin, "rankup.auto", true);

        plugin.autoRankup.run();
        assertTrue(groupProvider.inGroup(player.getUniqueId(), "B"));
        assertEquals(0, plugin.getEconomy().getBalance(player), 0);
    }

    @Test
    public void testNotInLadder() {
        PlayerMock player = server.addPlayer();

        plugin.getHelper().rankup(player);

        player.assertSaid(plugin.getMessage(Message.NOT_IN_LADDER).replace(Variable.PLAYER, player.getName()).toString());
        player.assertNoMoreSaid();
    }

    @Test
    public void testLastRank() {
        PlayerMock player = server.addPlayer();

        groupProvider.addGroup(player.getUniqueId(), "D");

        plugin.getHelper().rankup(player);

        player.assertSaid(plugin.getMessage(Message.NO_RANKUP).replaceRanks(player, plugin.getRankups().getTree().last().getRank()).toString());
        player.assertNoMoreSaid();
    }

    @Test
    public void testMoneyRequirement() {
        PlayerMock player = server.addPlayer();

        plugin.getEconomy().setPlayer(player, 500);

        groupProvider.addGroup(player.getUniqueId(), "A");
        plugin.getHelper().rankup(player);

        RankElement<Rank> element = plugin.getRankups().getTree().getFirst();
        Rank rank = element.getRank();

        player.assertSaid(plugin.replaceMoneyRequirements(plugin.getMessage(rank, Message.REQUIREMENTS_NOT_MET).replaceRanks(player, rank, element.getNext().getRank()), player, rank).toString());
        player.assertNoMoreSaid();
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
        System.clearProperty("TEST");
    }
}
