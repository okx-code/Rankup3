package sh.okx.rankup.text;

public class ChainedTextProcessor implements TextProcessor {
  private final TextProcessor[] processors;

  public ChainedTextProcessor(TextProcessor... processors) {
    this.processors = processors;
  }

  @Override
  public String process(String string) {
    for (TextProcessor processor : processors) {
      string = processor.process(string);
    }
    return string;
  }
}
