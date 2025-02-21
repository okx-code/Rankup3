package sh.okx.rankup.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.RankupTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DisabledNoConfirmCommandTest extends RankupTest {

  public DisabledNoConfirmCommandTest() {
    super("noconfirm");
  }

  @Test
  public void testNoConfirmDisabled() {
    PlayerMock player = server.addPlayer();
    player.addAttachment(plugin, "rankup.rankup", true);
    player.addAttachment(plugin, "rankup.noconfirm", true);

    plugin.getPermissions().transferGroup(player.getUniqueId(), null, "A");
    plugin.getEconomy().setPlayer(player, 10000);

    plugin.getCommand("rankup").execute(player, "rankup", new String[] {"noconfirm"});
    assertFalse(plugin.getPermissions().inGroup(player.getUniqueId(), "B"));
  }
}
