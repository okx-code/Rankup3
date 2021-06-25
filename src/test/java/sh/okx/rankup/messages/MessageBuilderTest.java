package sh.okx.rankup.messages;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MessageBuilderTest {
  @Test
  public void testFailIfEmpty() {
    assertTrue(new StringMessageBuilder("").failIfEmpty() instanceof NullMessageBuilder);
  }
}