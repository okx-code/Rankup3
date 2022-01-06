package sh.okx.rankup.requirements;

import static org.junit.jupiter.api.Assertions.*;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.RankupTest;

public class PrestigeRequirementsTest extends RankupTest {

  public PrestigeRequirementsTest() {
    super("prestigerequirements");
  }

  @Test
  public void testPrestigeRequirements() {
    PlayerMock player = server.addPlayer();

    plugin.getPermissions().addGroup(player.getUniqueId(), "p1");
    plugin.getPermissions().addGroup(player.getUniqueId(), "a");

    plugin.getEconomy().setPlayer(player, 200);

    plugin.getHelper().rankup(player);

    assertTrue(plugin.getPermissions().inGroup(player.getUniqueId(), "b"), "player is not in group b");
    assertEquals(0, plugin.getEconomy().getBalance(player), "prestige requirements did not take correct amount of money");
  }
}
