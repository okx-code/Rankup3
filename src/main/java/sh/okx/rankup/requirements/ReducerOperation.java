package sh.okx.rankup.requirements;

import java.util.List;

public abstract class ReducerOperation extends Operation {
  public abstract boolean check(boolean a, boolean b);

  @Override
  public boolean check(List<Boolean> booleans) {
    return booleans.stream().reduce(this::check).orElse(true);
  }
}
