package sh.okx.rankup.pebble;

import org.junit.jupiter.api.Test;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import sh.okx.rankup.RankupTest;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankElement;
import sh.okx.rankup.text.pebble.PebbleTextProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PebbleTest extends RankupTest {
  @Test
  public void testIndex() {
    Map<String, Object> ctx = new HashMap<>();
    ctx.put("one", "2");
    List<String> list = new ArrayList<>();
    list.add("L0");
    list.add("L1");
    list.add("L2");
    list.add("L3");
    ctx.put("list", list);
    PebbleTextProcessor processor = new PebbleTextProcessor(null, ctx, null);
    assertEquals("L2", processor.process("{{ list[one] }}"));
  }

  @Test
  public void testIterable() {
    PlayerMock player = server.addPlayer();

    plugin.getPermissions().transferGroup(player.getUniqueId(), null,"C");
    RankElement<Rank> rankElement = plugin.getRankups().getByPlayer(player);

    plugin.newMessageBuilder("{{ rank.requirements is iterable }}")
        .replacePlayer(player)
        .replaceOldRank(rankElement.getRank())
        .send(player);

    player.assertSaid("true");
  }

  @Test
  public void testRequirementAbsent() {
    PlayerMock player = server.addPlayer();

    plugin.getPermissions().transferGroup(player.getUniqueId(), null, "B");
    RankElement<Rank> rankElement = plugin.getRankups().getByPlayer(player);

    plugin.newMessageBuilder("{{ rank.has('xp-level') ? rank.req('xp-level').total | simple : 'none' }}")
        .replacePlayer(player)
        .replaceOldRank(rankElement.getRank())
        .send(player);

    player.assertSaid("none");
  }

  @Test
  public void testRequirementPresent() {
    PlayerMock player = server.addPlayer();

    plugin.getPermissions().transferGroup(player.getUniqueId(), null, "C");
    RankElement<Rank> rankElement = plugin.getRankups().getByPlayer(player);

    plugin.newMessageBuilder("{{ rank.has('xp-level') ? rank.req('xp-level').total | simple : 'none' }}")
        .replacePlayer(player)
        .replaceOldRank(rankElement.getRank())
        .send(player);

    player.assertSaid("2");
  }
}
