package sh.okx.rankup;


import static org.junit.jupiter.api.Assertions.assertEquals;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.placeholders.RankupExpansion;

public class RankupPlaceholderTest extends RankupTest {
    @Test
    public void testCurrentRank() {
        PlayerMock player = server.addPlayer();
        groupProvider.addGroup(player.getUniqueId(), "C");

        RankupExpansion expansion = plugin.getPlaceholders().getExpansion();

        assertEquals("C", expansion.placeholder(player, "current_rank"));
    }

    @Test
    public void testNextRank() {
        PlayerMock player = server.addPlayer();
        groupProvider.addGroup(player.getUniqueId(), "C");

        RankupExpansion expansion = plugin.getPlaceholders().getExpansion();
        assertEquals("D", expansion.placeholder(player, "next_rank"));
    }

    @Test
    public void testStatusCurrent() {
        PlayerMock player = server.addPlayer();
        groupProvider.addGroup(player.getUniqueId(), "B");

        RankupExpansion expansion = plugin.getPlaceholders().getExpansion();
        assertEquals("Current", expansion.placeholder(player, "status_b"));
    }

    @Test
    public void testStatusComplete() {
        PlayerMock player = server.addPlayer();
        groupProvider.addGroup(player.getUniqueId(), "D");

        RankupExpansion expansion = plugin.getPlaceholders().getExpansion();
        assertEquals("Complete", expansion.placeholder(player, "status_b"));
    }

    @Test
    public void testStatusIncomplete() {
        PlayerMock player = server.addPlayer();
        groupProvider.addGroup(player.getUniqueId(), "A");

        RankupExpansion expansion = plugin.getPlaceholders().getExpansion();
        assertEquals("Incomplete", expansion.placeholder(player, "status_b"));
    }

    @Test
    public void testMoneyLeft() {
        PlayerMock player = server.addPlayer();
        groupProvider.addGroup(player.getUniqueId(), "A");
        plugin.getEconomy().setPlayer(player, 900);

        RankupExpansion expansion = plugin.getPlaceholders().getExpansion();
        assertEquals("100.0", expansion.placeholder(player, "money_left"));
    }

    @Test
    public void testMoney() {
        PlayerMock player = server.addPlayer();
        groupProvider.addGroup(player.getUniqueId(), "A");
        plugin.getEconomy().setPlayer(player, 900);

        RankupExpansion expansion = plugin.getPlaceholders().getExpansion();
        assertEquals("1000", expansion.placeholder(player, "money"));
    }
}
