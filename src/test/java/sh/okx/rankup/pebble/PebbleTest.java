package sh.okx.rankup.pebble;

import static org.junit.jupiter.api.Assertions.*;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.RankupTest;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankElement;
import sh.okx.rankup.text.pebble.PebbleTextProcessor;

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
    PebbleTextProcessor processor = new PebbleTextProcessor(ctx, null);
    assertEquals("L2", processor.process("{{ list[one] }}"));
  }

  @Test
  public void testIterable() {
    PlayerMock player = server.addPlayer();

    plugin.getPermissions().addGroup(player.getUniqueId(), "C");
    RankElement<Rank> rankElement = plugin.getRankups().getByPlayer(player);

    plugin.newMessageBuilder("{{ rank.requirements is iterable }}")
        .replacePlayer(player)
        .replaceOldRank(rankElement.getRank())
        .send(player);

    player.assertSaid("true");
  }
}
