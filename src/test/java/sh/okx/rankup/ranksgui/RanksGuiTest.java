package sh.okx.rankup.ranksgui;

import static org.junit.jupiter.api.Assertions.*;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.inventory.Inventory;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.RankupTest;

public class RanksGuiTest extends RankupTest {

  public RanksGuiTest() {
    super("ranksgui");
  }

  @Test
  public void testRowsWithGroup() {
    PlayerMock player = server.addPlayer();
    plugin.getPermissions().addGroup(player.getUniqueId(), "a");

    player.addAttachment(plugin, "rankup.ranks", true);
    server.dispatchCommand(player, "ranks");

    Inventory top = player.getOpenInventory().getTopInventory();
    assertNotNull(top, "ranks gui has not opened");
    assertEquals(36, top.getSize(), "ranks gui is configured to have 4 rows");
  }

  @Test
  public void testRowsWithoutGroup() {
    PlayerMock player = server.addPlayer();

    player.addAttachment(plugin, "rankup.ranks", true);
    server.dispatchCommand(player, "ranks");

    Inventory top = player.getOpenInventory().getTopInventory();
    assertNotNull(top, "ranks gui has not opened");
    assertEquals(27, top.getSize(), "ranks gui is configured to have 3 rows");
  }
}
