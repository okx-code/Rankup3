package sh.okx.rankup;


import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RankupBasicsTest extends RankupTest {
  @Test
  public void testAutoRankup() {
    PlayerMock player = server.addPlayer();

    // requirement of $1000
    plugin.getEconomy().setPlayer(player, 1000);
    // give them group A
    groupProvider.transferGroup(player.getUniqueId(), null, "A");
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

    player.assertSaid(plugin.getMessage(Message.NOT_IN_LADDER).replacePlayer(player).toString());
    player.assertNoMoreSaid();
  }

  @Test
  public void testLastRank() {
    PlayerMock player = server.addPlayer();

    groupProvider.transferGroup(player.getUniqueId(), null, "D");

    plugin.getHelper().rankup(player);

    player.assertSaid(plugin.getMessage(Message.NO_RANKUP).replacePlayer(player)
        .replaceRank(plugin.getRankups().getTree().last().getRank()).toString());
    player.assertNoMoreSaid();
  }

  @Test
  public void testMoneyRequirement() {
    PlayerMock player = server.addPlayer();

    plugin.getEconomy().setPlayer(player, 500);

    groupProvider.transferGroup(player.getUniqueId(), null, "A");
    plugin.getHelper().rankup(player);

    RankElement<Rank> element = plugin.getRankups().getTree().getFirst();
    Rank rank = element.getRank();

    player.assertSaid(plugin.getMessage(rank, Message.REQUIREMENTS_NOT_MET).replacePlayer(player).replaceOldRank(rank).replaceRank(element.getNext().getRank()).toString(player));
    player.assertNoMoreSaid();
  }
}
