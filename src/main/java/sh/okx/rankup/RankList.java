package sh.okx.rankup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sh.okx.rankup.ranks.Rank;

public class RankList<T extends Rank> {
  protected final RankupPlugin plugin;
  @Getter
  protected final FileConfiguration config;
  protected final Collection<T> ranks = new ArrayList<>();

  public RankList(RankupPlugin plugin, FileConfiguration config, Function<ConfigurationSection, T> deserializer) {
    this.plugin = plugin;
    this.config = config;
    for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
      ConfigurationSection rankSection = (ConfigurationSection) entry.getValue();
      validateSection(rankSection);
      T apply = deserializer.apply(rankSection);
      if (apply != null) {
        ranks.add(apply);
      }
    }
    List<T> ordered = getOrderedList();
    Set<T> provisionalRanks = new HashSet<>(ordered);
    this.ranks.clear();
    this.ranks.addAll(provisionalRanks);
  }

  protected void validateSection(ConfigurationSection section) {
    String name = "'" + section.getName() + "'";
    /*if (section.getConfigurationSection("requirements") != null) {
      throw new IllegalArgumentException(
          "Rankup/prestige section " + name + " is using the old requirements system.\n" +
              "Instead of a configuration section, it is now a list of strings.\n" +
              "For example, instead of \"requirements: money: 1000\" you should use \"requirements: - 'money 1000'\".");
    }*/
    Set<String> keys = section.getKeys(false);
    if (keys.size() == 1 && keys.iterator().next().equalsIgnoreCase("rank")) {
      throw new IllegalArgumentException(
          "Having a final rank (for example: \"Z: rank: 'Z'\") from 3.4.2 or earlier should no longer be used.\n" +
              "It is safe to just delete the final rank " + name + "");
    } else if (!section.contains("requirements")) {
      throw new IllegalArgumentException("Rank " + name + " does not have any requirements.");
    }
  }

  public T getFirst() {
    OUTER:
    for (T rank : ranks) {
      // see if anything ranks up to this
      for (T rank0 : ranks) {
        if (rank0.getNext().equalsIgnoreCase(rank.getRank())) {
          continue OUTER;
        }
      }
      // nothing ranks up to this
      return rank;
    }
    throw new IllegalArgumentException("Could not find a first rank. First ranks must not have anything that ranks up to them.");
  }

  public List<T> getOrderedList() {
    List<T> list = new ArrayList<>();
    T t = getFirst();
    while (t != null) {
      for (T existing : list) {
        if (existing.equals(t)) {
          throw new IllegalArgumentException("Infinite rankup loop detected at rank " + t.getRank() + " to " + t.getNext()
              + "\nMake sure no there are no rankups to previous ranks or to the same rank");
        }
      }
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

  public boolean isLast(Player player) {
    String last = getLast();
    return plugin.getPermissions().inGroup(player.getUniqueId(), last);
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
