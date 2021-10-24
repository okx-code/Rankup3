package sh.okx.rankup.prestige;

import static org.junit.jupiter.api.Assertions.*;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.RankupTest;

public class BrokenPrestigeTest extends RankupTest {

  public BrokenPrestigeTest() {
    super("brokenprestige");
  }

  @Test
  public void testPrestige() {
    PlayerMock player = server.addPlayer();
    plugin.getPermissions().addGroup(player.getUniqueId(), "C");

    assertNull(plugin.getPrestiges().getByPlayer(player));
    plugin.getHelper().rankup(player);
    player.assertSaid(ChatColor.YELLOW + "You are at the highest rank.");
  }
}
