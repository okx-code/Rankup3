package sh.okx.rankup.messages;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.RankupTest;

import java.text.DecimalFormat;

public class RankupPlaceholderTest extends RankupTest {
  public RankupPlaceholderTest() {
    super("placeholder");
  }

  @Test
  public void testReceivesSuccessMessages() {
    PlayerMock player = server.addPlayer();
    PlayerMock receiver = server.addPlayer();

    plugin.getEconomy().setPlayer(player, 1000);

    groupProvider.addGroup(player.getUniqueId(), "A");
    plugin.getHelper().rankup(player);

    // success-public message must be the same for both players
    player.assertSaid(receiver.nextMessage());

    // receiver does not receive success-private
    receiver.assertNoMoreSaid();

    // player receives success-private and nothing else
    assertNotNull(player.nextMessage());
    player.assertNoMoreSaid();
  }

  @Test
  public void testQuotientAndPercent() {
    PlayerMock player = server.addPlayer();

    plugin.getEconomy().setPlayer(player, 100);

    groupProvider.addGroup(player.getUniqueId(), "A");
    plugin.getHelper().rankup(player);

    DecimalFormat decimal = new DecimalFormat("#.#");
    player.assertSaid(decimal.format(0.1) + " 10");
  }
}
