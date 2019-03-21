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
      ranks.add(deserializer.apply(rankSection));
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
      System.out.println("n");
      return null;
    }
    for (T rank : ranks) {
      System.out.println(name + " <> " + rank.getNext());
      if (name.equalsIgnoreCase(rank.getRank())) {
        System.out.println("y");
        return rank;
      }
    }
    System.out.println("l");
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
