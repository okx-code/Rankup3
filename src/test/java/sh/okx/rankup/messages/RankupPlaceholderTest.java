package sh.okx.rankup.messages;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.Test;
import sh.okx.rankup.RankupTest;

public class RankupPlaceholderTest extends RankupTest {
  @Test
  public void testSuccessPublicIsSame() {
    PlayerMock player = server.addPlayer();
    PlayerMock receiver = server.addPlayer();

    plugin.getEconomy().setPlayer(player, 1000);

    groupProvider.addGroup(player.getUniqueId(), "A");
    plugin.getHelper().rankup(player);

    // success-public message must be the same for both players
    player.assertSaid(receiver.nextMessage());

    // receiver does not receive success-private
    receiver.assertNoMoreSaid();
  }
}
