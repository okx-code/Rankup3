package sh.okx.rankup.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import sh.okx.rankup.placeholders.Placeholders;
import sh.okx.rankup.text.pebble.PebbleTextProcessor;

public class TextProcessorBuilder {

  private final List<TextProcessor> processors = new ArrayList<>();

  public TextProcessorBuilder colour() {
    processors.add(new ColourTextProcessor());
    return this;
  }

  public TextProcessorBuilder pebble(Map<String, Object> context, Placeholders options) {
    processors.add(new PebbleTextProcessor(context, options));
    return this;
  }

  public TextProcessorBuilder papi(@Nullable Player player) {
    processors.add(new PlaceholderApiTextProcessor(player));
    return this;
  }

  public TextProcessorBuilder legacy(Map<String, Object> context, Placeholders options) {
    processors.add(new LegacyTextProcessor(context, options));
    return this;
  }

  public TextProcessor create() {
    return new ChainedTextProcessor(processors.toArray(new TextProcessor[0]));
  }
}
