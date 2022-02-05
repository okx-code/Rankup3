package sh.okx.rankup.commands;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.RankupTest;

public class CommandInfoTest extends RankupTest {
  @Test
  public void testPlaceholders() {
    // placeholders command should never throw an exception
    PlayerMock player = server.addPlayer();
    player.addAttachment(plugin, "rankup.admin", true);
    plugin.getCommand("rankup3").execute(player, "pru", new String[] {"placeholders"});

    player.assertSaid("--- Rankup placeholders ---");
  }

  @Test
  public void testForce() {
    // forcing rankup should change group and not affect money

    PlayerMock player = server.addPlayer();
    player.addAttachment(plugin, "rankup.force", true);
    plugin.getEconomy().setPlayer(player, 11);
    groupProvider.addGroup(player.getUniqueId(), "A");

    plugin.getCommand("rankup3").execute(player, "pru", new String[] {"forcerankup", player.getName()});

    assertTrue(groupProvider.inGroup(player.getUniqueId(), "B"));
    assertEquals(11, plugin.getEconomy().getBalance(player), 0.0001);
  }

}
