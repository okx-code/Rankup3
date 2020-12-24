package sh.okx.rankup.ranks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;

public abstract class RankList<T extends Rank> {

  protected RankupPlugin plugin;
  @Getter
  protected FileConfiguration config;
  @Getter
  private RankTree<T> tree;

  public RankList(RankupPlugin plugin, FileConfiguration config,
      Function<ConfigurationSection, T> deserializer) {
    this.plugin = plugin;
    this.config = config;
    List<RankElement<T>> rankElements = new ArrayList<>();
    for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
      ConfigurationSection rankSection = (ConfigurationSection) entry.getValue();
      validateSection(rankSection);
      T apply = deserializer.apply(rankSection);
      if (apply != null) {
        // find next
        rankElements.add(findNext(apply, rankElements));
      }
    }

    for (RankElement<T> rankElement : rankElements) {
      if (rankElement.isRootNode()) {
        if (tree == null) {
          tree = new RankTree<>(rankElement);
          addLastRank(plugin);
        } else {
          plugin.getLogger().severe("Multiple root rankup nodes detected (a root rankup nodes is a rankup that does not have anything that ranks up to it). This may lead to inconsistent behaviour.");
          plugin.getLogger().severe("Conflicting root node: " + rankElement.getRank() + ". Using root node: " + tree.getFirst().getRank());
        }
      }
    }
  }

  protected abstract void addLastRank(RankupPlugin plugin);

  private RankElement<T> findNext(T rank, List<RankElement<T>> rankElements) {
    Objects.requireNonNull(rank);

    RankElement<T> currentElement = new RankElement<>(rank, null);

    for (RankElement<T> rankElement : rankElements) {
      T rank1 = rankElement.getRank();
      if (rank1.getRank() != null
          && rank1.getRank().equalsIgnoreCase(rank.getNext())) {
        // current rank element is the next rank
        currentElement.setNext(rankElement);
      } else if (rank1.getNext() != null
          && rank1.getNext().equalsIgnoreCase(rank.getRank())) {
        rankElement.setNext(currentElement);
      }
    }
    return currentElement;
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
          "Having a final rank (for example: \"Z: rank: 'Z'\") from 3.4.2 or earlier should no longer be used.\n"
              +
              "It is safe to just delete the final rank " + name + "");
    } else if (!section.contains("requirements")) {
      throw new IllegalArgumentException("Rank " + name + " does not have any requirements.");
    }
  }

  public T getFirst() {
    return tree.getFirst().getRank();
  }

  public T getByName(String name) {
    if (name == null) {
      return null;
    }
    for (T rank : tree) {
      if (name.equalsIgnoreCase(rank.getRank())) {
        return rank;
      }
    }
    return null;
  }

  public RankElement<T> getByPlayer(Player player) {
    List<RankElement<T>> list = tree.asList();
    Collections.reverse(list);
    for (RankElement<T> t : list) {
      if (t.getRank().isIn(player)) {
        return t;
      }
    }
    return null;
  }

  public T getRankByPlayer(Player player) {
    List<RankElement<T>> list = tree.asList();
    Collections.reverse(list);
    for (RankElement<T> t : list) {
      if (t.getRank().isIn(player)) {
        return t.getRank();
      }
    }
    return null;
  }
}
