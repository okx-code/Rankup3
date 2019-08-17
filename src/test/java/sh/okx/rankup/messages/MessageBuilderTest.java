package sh.okx.rankup.messages;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;


public class MessageBuilderTest {
  @Test
  public void testFailIfEmpty() {
    assertThat(new MessageBuilder("").failIfEmpty(), instanceOf(NullMessageBuilder.class));
  }
}