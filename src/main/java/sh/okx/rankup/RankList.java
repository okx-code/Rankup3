package sh.okx.rankup;

import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sh.okx.rankup.ranks.Rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class RankList<T extends Rank> {
  @Getter
  protected final FileConfiguration config;
  protected final Set<T> ranks = new HashSet<>();

  public RankList(FileConfiguration config, Function<ConfigurationSection, T> deserializer) {
    this.config = config;
    for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
      ConfigurationSection rankSection = (ConfigurationSection) entry.getValue();
      validateSection(rankSection);
      ranks.add(deserializer.apply(rankSection));
    }
  }

  protected void validateSection(ConfigurationSection section) {
    String name = "'" + section.getName() + "'";
    if (section.getConfigurationSection("requirements") != null) {
      throw new IllegalArgumentException(
          "Rankup section " + name + " is using the old requirements system.\n" +
              "Instead of a configuration section, it is now a list of strings.\n" +
              "For example, instead of \"requirements: money: 1000\" you should use \"requirements: - 'money 1000'\".");
    }
    Set<String> keys = section.getKeys(false);
    if (keys.size() == 1 && keys.iterator().next().equalsIgnoreCase("rank")) {
      throw new IllegalArgumentException(
          "Having a final rank (for example: \"Z: rank: 'Z'\") from 3.4.2 or earlier should no longer be used.\n" +
          "It is safe to just delete the final rank " + name + "");
    } else if (section.getStringList("requirements").isEmpty()) {
      throw new IllegalArgumentException("Rank " + name + " does not have any requirements.");
    }
  }

  public T getFirst() {
    OUTER:
    for (T rank : ranks) {
      // see if anything ranks up to this
      for (T rank0 : ranks) {
        if (rank0.getNext().equals(rank.getRank())) {
          continue OUTER;
        }
      }
      // nothing ranks up to this
      return rank;
    }
    return null;
  }

  public List<T> getOrderedList() {
    List<T> list = new ArrayList<>();
    T t = getFirst();
    while (t != null) {
      list.add(t);
      t = next(t);
    }
    return list;
  }

  public T getByName(String name) {
    if (name == null) {
      return null;
    }
    for (T rank : ranks) {
      if (name.equalsIgnoreCase(rank.getRank())) {
        return rank;
      }
    }
    return null;
  }

  public T getByPlayer(Player player) {
    List<T> list = getOrderedList();
    Collections.reverse(list);
    for (T t : list) {
      if (t.isIn(player)) {
        return t;
      }
    }
    return null;
  }

  public String getLast() {
    List<T> list = getOrderedList();
    return list.get(list.size() - 1).getNext();
  }

  public boolean isLast(Permission perms, Player player) {
    String last = getLast();
    String[] groups = perms.getPlayerGroups(null, player);
    for (String group : groups) {
      if (group.equalsIgnoreCase(last)) {
        return true;
      }
    }
    return false;
  }

  public T next(T rank) {
    for (T nextRank : ranks) {
      if (rank.getNext() != null && rank.getNext().equalsIgnoreCase(nextRank.getRank())) {
        return nextRank;
      }
    }
    return null;
  }
}
