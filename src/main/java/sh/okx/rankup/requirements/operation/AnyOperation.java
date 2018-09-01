package sh.okx.rankup.requirements.operation;

import sh.okx.rankup.requirements.ReducerOperation;

public class AnyOperation extends ReducerOperation {
  @Override
  public boolean check(boolean a, boolean b) {
    return a || b;
  }
}
