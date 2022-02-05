package sh.okx.rankup.text;

import sh.okx.rankup.util.Colour;

public class ColourTextProcessor implements TextProcessor {

  @Override
  public String process(String string) {
    return Colour.translate(string);
  }
}
