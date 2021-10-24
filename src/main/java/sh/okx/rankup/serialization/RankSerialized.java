package sh.okx.rankup.serialization;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

@Data
public class RankSerialized {

  private final String rank;
  private final String next;

  private final String displayName;

  private final List<String> commands;

  private final List<String> requirements;
  private final Map<String, List<String>> prestigeRequirements;

  private final Map<String, String> messages;

  public ConfigurationSection getMessagesAsSection() {
    ConfigurationSection section = new MemoryConfiguration();
    for (Map.Entry<String, String> entry : messages.entrySet()) {
      section.set(entry.getKey(), entry.getValue());
    }
    return section;
  }
}
