package sh.okx.rankup.hook;

import java.util.UUID;

public interface GroupProvider {
  boolean inGroup(UUID uuid, String group);
  void transferGroup(UUID uuid, String oldGroup, String group);
}
