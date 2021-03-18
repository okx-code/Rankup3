package sh.okx.rankup.text.pebble;

import java.text.DecimalFormat;
import lombok.Data;

@Data
public class PebbleOptions {
  private final DecimalFormat moneyFormat;
  private final DecimalFormat percentFormat;
  private final DecimalFormat simpleFormat;
}
