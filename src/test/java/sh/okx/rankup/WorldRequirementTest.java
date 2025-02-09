package sh.okx.rankup;


import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.placeholders.RankupExpansion;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorldRequirementTest extends RankupTest {

  public WorldRequirementTest() {
    super("world");
  }

  @Test
  public void testStatusComplete() {
    server.addSimpleWorld("world");
    server.addSimpleWorld("the_nether");

    PlayerMock player = server.addPlayer();
    groupProvider.transferGroup(player.getUniqueId(), null, "a");

    RankupExpansion expansion = plugin.getPlaceholders().getExpansion();
    assertEquals("0", expansion.placeholder(player, "requirement_world_percent_done"));

    player.teleport(new Location(server.getWorld("the_nether"), 0, 0, 0));

    assertEquals("100", expansion.placeholder(player, "requirement_world_percent_done"));
  }
}
