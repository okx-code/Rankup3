package sh.okx.rankup;


import be.seeseemelk.mockbukkit.entity.PlayerMock;
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
    PlayerMock player = server.addPlayer();
    groupProvider.addGroup(player.getUniqueId(), "a");

    RankupExpansion expansion = plugin.getPlaceholders().getExpansion();
    assertEquals("0", expansion.placeholder(player, "requirement_world_percent_done"));

    player.teleport(new Location(server.getWorld("the_nether"), 0, 0, 0));

    assertEquals("100", expansion.placeholder(player, "requirement_world_percent_done"));
  }
}