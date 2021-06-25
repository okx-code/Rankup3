package sh.okx.rankup.pebble;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.text.pebble.PebbleTextProcessor;

public class PebbleTest {
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
}
