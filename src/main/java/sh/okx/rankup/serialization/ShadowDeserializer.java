package sh.okx.rankup.serialization;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShadowDeserializer {

  public static List<RankSerialized> deserialize(UnmodifiableConfig ranks) {
    List<RankSerialized> ranksList = new ArrayList<>(ranks.size());
    for (Entry entry : ranks.entrySet()) {
      UnmodifiableConfig value = entry.getValue();
      if (value == null) continue;
      String rank = value.get("rank");
      String next = value.get("next");
      String displayName = value.get("display-name");
      List<String> commands = value.getOrElse("commands", Collections.emptyList());
      List<String> requirements;
      Map<String, List<String>> prestigeRequirements;
      Object requirementsObject = value.get("requirements");
      if (requirementsObject instanceof UnmodifiableConfig) {
        requirements = null;
        UnmodifiableConfig requirementsConfig = (UnmodifiableConfig) requirementsObject;
        prestigeRequirements = new HashMap<>(requirementsConfig.size());
        for (Entry requirementEntry : requirementsConfig.entrySet()) {
          prestigeRequirements.put(requirementEntry.getKey(), requirementEntry.getValue());
        }
      } else {
        prestigeRequirements = null;
        if (requirementsObject instanceof String) {
          requirements = Collections.singletonList((String) requirementsObject);
        } else if (requirementsObject instanceof List) {
          requirements = (List<String>) requirementsObject;
        } else {
          requirements = Collections.emptyList();
        }
      }

      UnmodifiableConfig messagesConfig = value.get("rankup");
      Map<String, String> messages;
      if (messagesConfig != null) {
        messages = new HashMap<>();
        updateMap(messages, messagesConfig, "rankup.");
      } else {
        messages = Collections.emptyMap();
      }

      ranksList.add(new RankSerialized(rank, next, displayName, commands, requirements, prestigeRequirements, messages));
    }
    return ranksList;
  }

  private static void updateMap(Map<String, String> map, UnmodifiableConfig config, String prefix) {
    for (Entry message : config.entrySet()) {
      Object value = message.getValue();
      if (value != null) {
        if (value instanceof UnmodifiableConfig) {
          updateMap(map, (UnmodifiableConfig) value, prefix + message.getKey() + ".");
        } else {
          map.put(prefix + message.getKey(), String.valueOf(value));
        }
      }
    }
  }
}
