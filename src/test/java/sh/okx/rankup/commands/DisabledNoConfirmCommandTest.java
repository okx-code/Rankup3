package sh.okx.rankup.commands;

import static org.junit.jupiter.api.Assertions.*;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.RankupTest;

public class DisabledNoConfirmCommandTest extends RankupTest {

  public DisabledNoConfirmCommandTest() {
    super("noconfirm");
  }

  @Test
  public void testNoConfirmDisabled() {
    PlayerMock player = server.addPlayer();
    player.addAttachment(plugin, "rankup.rankup", true);
    player.addAttachment(plugin, "rankup.noconfirm", true);

    plugin.getPermissions().addGroup(player.getUniqueId(), "A");
    plugin.getEconomy().setPlayer(player, 10000);

    plugin.getCommand("rankup").execute(player, "rankup", new String[] {"noconfirm"});
    assertFalse(plugin.getPermissions().inGroup(player.getUniqueId(), "B"));
  }
}
