package sh.okx.rankup.hook;

import java.util.UUID;

public interface GroupProvider {
  boolean inGroup(UUID uuid, String group);
  void addGroup(UUID uuid, String group);
  void removeGroup(UUID uuid, String group);
}
