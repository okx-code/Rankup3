package sh.okx.rankup.text.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class DecimalFormatFilter implements Filter {

  private final DecimalFormat format;

  public DecimalFormatFilter(DecimalFormat format) {
    this.format = format;
  }

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) throws PebbleException {
    if (input == null) {
      return null;
    }
    if (!(input instanceof Number)) {
      throw new PebbleException(null, "The input for the 'DecimalFormatFilter' filter has to be a number: " + input,
          lineNumber, self.getName());
    }

    Number number = (Number) input;
    return this.format.format(number);
  }
}
