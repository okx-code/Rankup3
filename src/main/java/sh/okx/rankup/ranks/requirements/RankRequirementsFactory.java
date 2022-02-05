package sh.okx.rankup.ranks.requirements;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.Requirement;

public class RankRequirementsFactory {
  private static final String REQUIREMENTS = "requirements";

  public static RankRequirements getRequirements(RankupPlugin plugin, ConfigurationSection section) {
    if (section.isConfigurationSection(REQUIREMENTS)) {
      return getPrestigeListRequirements(plugin, section.getConfigurationSection(REQUIREMENTS));
    } else {
      return getListRequirements(plugin, getRequirementStrings(section, REQUIREMENTS));
    }
  }

  public static RankRequirements getRequirements(RankupPlugin plugin, List<String> requirements,
      Map<String, List<String>> prestigeRequirements) {
    if (prestigeRequirements != null) {
      ConfigurationSection section = new MemoryConfiguration();
      for (Map.Entry<String, List<String>> entry : prestigeRequirements.entrySet()) {
        section.set(entry.getKey(), entry.getValue());
      }
      return getPrestigeListRequirements(plugin, section);
    } else if (requirements != null) {
      return getListRequirements(plugin, requirements);
    } else {
//      throw new IllegalArgumentException("No requirements set.");
      return null;
    }
  }

  private static Collection<String> getRequirementStrings(ConfigurationSection section, String key) {
    if (section.isList(key)) {
      return section.getStringList(key);
    } else {
      String string = section.getString(key);
      if (string == null) {
        return null;
      } else {
        return Collections.singleton(string);
      }
    }
  }

  private static List<Requirement> stringsToRequirements(RankupPlugin plugin, Iterable<String> strings) {
    return plugin.getRequirements().getRequirements(strings);
  }

  private static RankRequirements getListRequirements(RankupPlugin plugin, Iterable<String> list) {
    List<Requirement> requirements = stringsToRequirements(plugin, list);
    return new ListRankRequirements(requirements);
  }

  private static RankRequirements getPrestigeListRequirements(RankupPlugin plugin, ConfigurationSection section) {
    if (plugin.getPrestiges() == null) {
      throw new IllegalArgumentException("Prestige requirements are being used but prestiging is not enabled.");
    }

    RankRequirements defaultRequirements = null;
    Map<String, RankRequirements> requirements = new HashMap<>();

    for (String key : section.getKeys(false)) {
      Collection<String> stringRequirements = getRequirementStrings(section, key);
      if (stringRequirements != null) {
        RankRequirements rankRequirements = getListRequirements(plugin, stringRequirements);
        if ("default".equalsIgnoreCase(key)) {
          defaultRequirements = rankRequirements;
        } else {
          requirements.put(key.toLowerCase(), rankRequirements);
        }
      }
    }

    if (defaultRequirements == null) {
      throw new IllegalArgumentException("No default requirements set for rank " + section.getParent().getName() + ". See the wiki for info.");
    }

    return new PrestigeListRankRequirements(plugin, defaultRequirements, requirements);
  }
}
