package sh.okx.rankup.text.pebble;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.loader.StringLoader;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import sh.okx.rankup.messages.pebble.InvalidRequirementException;
import sh.okx.rankup.placeholders.Placeholders;
import sh.okx.rankup.text.TextProcessor;

public class PebbleTextProcessor implements TextProcessor {

  private final Logger logger;
  private final Map<String, Object> context;
  private final Placeholders options;

  public PebbleTextProcessor(Logger logger, Map<String, Object> context, Placeholders options) {
    this.logger = logger;
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
              if (moneyFormat != null) {
                filters.put("money", new DecimalFormatFilter(moneyFormat));
                filters.put("shortmoney", new MoneyShortFilter(options));
              }

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
      try {
        engine.getTemplate(string).evaluate(writer, context);
        return writer.toString();
      } catch (RuntimeException ex) {
        if (ex.getCause() instanceof InvocationTargetException) {
          if (ex.getCause().getCause() instanceof InvalidRequirementException) {
            InvalidRequirementException cause = (InvalidRequirementException) ex.getCause().getCause();
            logger.severe("Unknown requirement \"" + cause.getRequirement() + "\" on rank \"" + cause.getRank().getRank() + "\" in message:");
            for (String line : string.split("\n")) {
              logger.severe(line);
            }
            logger.severe("Change the message to not use that requirement, or add the requirement to the rank in the config.");
          } else {
            ex.printStackTrace();
          }
        } else {
          ex.printStackTrace();
        }
        return "Unable to parse message, please check console";
      }
    } catch (IOException e) {
      e.printStackTrace();
      return string;
    }
  }
}
