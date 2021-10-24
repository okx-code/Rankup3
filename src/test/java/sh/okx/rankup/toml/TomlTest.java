package sh.okx.rankup.toml;

import static org.junit.jupiter.api.Assertions.*;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.RankupTest;
import sh.okx.rankup.ranks.Rankups;

public class TomlTest extends RankupTest {

  public TomlTest() {
    super("toml");
  }

  @Test
  public void testRequirementsNotMet() {
    PlayerMock player = server.addPlayer();

    Rankups ranks = plugin.getRankups();
    assertEquals(1500, ranks.getFirst().getRequirement(null, "money").getValueDouble());

    plugin.getPermissions().addGroup(player.getUniqueId(), "C");
    player.addAttachment(plugin, "rankup.rankup", true);
    plugin.getHelper().rankup(player);

    player.assertSaid("toml");
  }

  @Test
  public void testRankup() {
    PlayerMock player = server.addPlayer();
    plugin.getPermissions().addGroup(player.getUniqueId(), "B");
    plugin.getEconomy().setPlayer(player, 10000);
    player.addAttachment(plugin, "rankup.rankup", true);

    plugin.getHelper().rankup(player);

    assertTrue(plugin.getPermissions().inGroup(player.getUniqueId(), "C"));
  }

  @Test
  public void testRanks() {
    PlayerMock player = server.addPlayer();
    plugin.getPermissions().addGroup(player.getUniqueId(), "C");

    player.addAttachment(plugin, "rankup.ranks", true);
    plugin.getCommand("ranks").execute(player, "ranks", new String[0]);
    player.assertSaid(ChatColor.GRAY + "A " + ChatColor.DARK_GRAY + "\u00bb " + ChatColor.GRAY + "B");
    player.assertSaid(ChatColor.GRAY + "B " + ChatColor.DARK_GRAY + "\u00bb " + ChatColor.GRAY + "C");
    player.assertSaid(ChatColor.RED + "C " + ChatColor.YELLOW + "\u00bb " + ChatColor.RED + "D o");
    player.assertNoMoreSaid();
  }
}
