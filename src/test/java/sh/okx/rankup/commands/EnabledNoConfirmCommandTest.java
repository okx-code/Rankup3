package sh.okx.rankup.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.RankupTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnabledNoConfirmCommandTest extends RankupTest {

  @Test
  public void testNoConfirmEnabled() {
    PlayerMock player = server.addPlayer();
    player.addAttachment(plugin, "rankup.rankup", true);
    player.addAttachment(plugin, "rankup.noconfirm", true);

    plugin.getPermissions().transferGroup(player.getUniqueId(), null, "A");
    plugin.getEconomy().setPlayer(player, 10000);

    plugin.getCommand("rankup").execute(player, "rankup", new String[] {"noconfirm"});
    assertTrue(plugin.getPermissions().inGroup(player.getUniqueId(), "B"));
  }
}
