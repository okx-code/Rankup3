package sh.okx.rankup.ranks;

import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.ranks.requirements.RankRequirements;
import sh.okx.rankup.ranks.requirements.RankRequirementsFactory;

import java.util.List;
import sh.okx.rankup.serialization.RankSerialized;

public class Rankup extends Rank {
  public static Rankup deserialize(RankupPlugin plugin, RankSerialized serialized) {
    if (serialized.getNext() == null || serialized.getNext().isEmpty()) {
      plugin.getLogger().warning("Having a final rank (for example: \"Z: rank: 'Z'\") from 3.4.2 or earlier should no longer be used.");
      plugin.getLogger().warning("It is safe to just delete the final rank " + serialized.getRank() + "");
      plugin.getLogger().warning("Rankup section '" + serialized.getRank() + "' has a blank 'next' field, will be ignored.");
      return null;
    }

    return new Rankup(serialized.getMessagesAsSection(),
        plugin,
        serialized.getNext(),
        serialized.getRank(),
        serialized.getDisplayName(),
        RankRequirementsFactory.getRequirements(plugin, serialized.getRequirements(), serialized.getPrestigeRequirements()),
        Objects.requireNonNull(serialized.getCommands(), "rank commands are null"));
  }

  protected Rankup(ConfigurationSection section, RankupPlugin plugin, String next, String rank, String displayName,
      RankRequirements requirements,
      List<String> commands) {
    super(section, plugin, next, rank, displayName, requirements, commands);
  }
}
