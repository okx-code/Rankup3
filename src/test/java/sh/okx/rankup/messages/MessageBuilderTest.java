package sh.okx.rankup.messages;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


public class MessageBuilderTest {
  @Test
  public void testFailIfEmpty() {
    assertThat(new MessageBuilder("").failIfEmpty(), instanceOf(EmptyMessageBuilder.class));
  }
}