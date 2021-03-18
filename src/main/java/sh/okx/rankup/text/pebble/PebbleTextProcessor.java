package sh.okx.rankup.text.pebble;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.loader.StringLoader;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import sh.okx.rankup.text.TextProcessor;

public class PebbleTextProcessor implements TextProcessor {

  private final Map<String, Object> context;
  private final PebbleOptions options;

  public PebbleTextProcessor(Map<String, Object> context, PebbleOptions options) {
    this.context = context;
    this.options = options;
  }

  @Override
  public String process(String string) {
    PebbleEngine engine = new PebbleEngine.Builder().autoEscaping(false).extension(
        new AbstractExtension() {
          @Override
          public Map<String, Filter> getFilters() {
            Map<String, Filter> filters = new HashMap<>();
            if (options != null) {
              DecimalFormat moneyFormat = options.getMoneyFormat();
              if (moneyFormat != null) filters.put("money", new DecimalFormatFilter(moneyFormat));

              DecimalFormat percentFormat = options.getPercentFormat();
              if (percentFormat != null) filters.put("percent", new DecimalFormatFilter(percentFormat));

              DecimalFormat simpleFormat = options.getSimpleFormat();
              if (simpleFormat != null) filters.put("simple", new DecimalFormatFilter(simpleFormat));
            }
            return filters;
          }
        })
        .loader(new StringLoader()).build();
    StringWriter writer = new StringWriter();
    try {
      engine.getTemplate(string).evaluate(writer, context);
      return writer.toString();
    } catch (IOException e) {
      e.printStackTrace();
      return string;
    }
  }
}
