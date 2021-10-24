package sh.okx.rankup.ranks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;

public abstract class RankList<T extends Rank> {

  protected RankupPlugin plugin;
  @Getter
  private RankTree<T> tree;

  public RankList(RankupPlugin plugin, Collection<? extends T> ranks) {
    this.plugin = plugin;
    List<RankElement<T>> rankElements = new ArrayList<>();
    for (T rank : ranks) {
      if (rank != null && validateSection(rank)) {
        // find next
        rankElements.add(findNext(rank, rankElements));
      } else {
        plugin.getLogger().warning("Ignoring rank: " + rank);
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

  protected boolean validateSection(T rank) {
    String name = rank.getRank() == null ? "rank" : rank.getRank();
    String nextField = rank.getNext();
    if (nextField == null || nextField.isEmpty()) {
      plugin.getLogger().warning("Rankup section " + name + " does not have a 'next' field.");
      plugin.getLogger().warning("Having a final rank (for example: \"Z: rank: 'Z'\") from 3.4.2 or earlier should no longer be used.");
      plugin.getLogger().warning("If this is intended as a final rank, you should delete " + name);
      return false;
    } else if (rank.getRequirements() == null) {
      plugin.getLogger().warning("Rank " + name + " does not have any requirements.");
      return false;
    }
    return true;
  }

  public T getFirst() {
    return tree.getFirst().getRank();
  }

  public T getRankByName(String name) {
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

  public RankElement<T> getByName(String name) {
    if (name == null) {
      return null;
    }
    List<RankElement<T>> rankElements = tree.asList();
    for (RankElement<T> rank : rankElements) {
      if (name.equalsIgnoreCase(rank.getRank().getRank())) {
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
