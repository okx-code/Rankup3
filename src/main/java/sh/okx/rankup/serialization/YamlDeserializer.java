package sh.okx.rankup.serialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

public class YamlDeserializer {

  public static List<RankSerialized> deserialize(ConfigurationSection ranks) {
    Set<String> rankKeys = ranks.getKeys(false);
    List<RankSerialized> ranksList = new ArrayList<>(rankKeys.size());
    for (String rankKey : rankKeys) {
      ConfigurationSection section = ranks.getConfigurationSection(rankKey);
      if (section == null) continue;
      String rank = section.getString("rank");
      String next = section.getString("next");
      String displayName = section.getString("display-name");
      List<String> commands = section.getStringList("commands");
      List<String> requirements;
      Map<String, List<String>> prestigeRequirements;
      if (section.isConfigurationSection("requirements")) {
        requirements = null;
        ConfigurationSection requirementsSection = section.getConfigurationSection("requirements");
        Set<String> keys = requirementsSection.getKeys(false);
        prestigeRequirements = new HashMap<>(keys.size());
        for (String key : keys) {
          prestigeRequirements.put(key, requirementsSection.getStringList(key));
        }
      } else {
        prestigeRequirements = null;
        requirements = section.getStringList("requirements");
      }

      ConfigurationSection rankupSection = section.getConfigurationSection("rankup");
      Map<String, String> messages;
      if (rankupSection != null) {

        Set<String> rankup = rankupSection.getKeys(true);
        messages = new HashMap<>(rankup.size());
        for (String key : rankup) {
          if (!rankupSection.isConfigurationSection(key)) {
            messages.put(MemorySection.createPath(rankupSection, key, section), rankupSection.getString(key));
          }
        }
      } else {
        messages = Collections.emptyMap();
      }

      ranksList.add(new RankSerialized(rank, next, displayName, commands, requirements, prestigeRequirements, messages));
    }
    return ranksList;
  }

}
