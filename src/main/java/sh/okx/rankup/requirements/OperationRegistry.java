package sh.okx.rankup.requirements;

import java.util.HashMap;
import java.util.Map;

public class OperationRegistry {
  private Map<String, Operation> operations = new HashMap<>();

  public void addOperation(String name, Operation operation) {
    operations.put(name.toLowerCase(), operation);
  }

  public Operation getOperation(String name) {
    return operations.get(name == null ? "all" : name.toLowerCase());
  }
}
