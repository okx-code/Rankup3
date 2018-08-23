package sh.okx.rankup.ranks;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Rankups {
  @Getter
  private final FileConfiguration config;
  private final Set<Rank> ranks = new HashSet<>();

  public Rankups(Rankup plugin, FileConfiguration config) {
    this.config = config;
    for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
      ConfigurationSection rankSection = (ConfigurationSection) entry.getValue();
      ranks.add(Rank.deserialize(plugin, rankSection));
    }
  }

  public Rank getFirstRank() {
    OUTER:
    for(Rank rank : ranks) {
      // see if anything ranks up to this
      for(Rank rank0 : ranks) {
        if(!rank0.isLastRank() && rank0.getNext().equals(rank.getName())) {
          continue OUTER;
        }
      }
      // nothing ranks up to this
      return rank;
    }
    return null;
  }

  public Rank getRank(String name) {
    for(Rank rank : ranks) {
      if(rank.getName().equals(name)) {
        return rank;
      }
    }
    return null;
  }

  public Rank getRank(Player player) {
    return ranks.stream()
        .filter(rank -> rank.isInRank(player))
        .findFirst()
        .orElse(null);
  }

  public Rank nextRank(Rank rank) {
    if(rank.isLastRank()) {
      return null;
    }

    for(Rank nextRank : ranks) {
      if (rank.getNext().equalsIgnoreCase(nextRank.getName())) {
        return nextRank;
      }
    }
    // this shouldn't happen but whatever
    return null;
  }

//  public boolean hasNext(Rank start, Rank rank) {
//    while(!start.isLastRank()) {
//      start = nextRank(rank);
//      if(start.equals(rank)) {
//        return true;
//      }
//    }
//    return false;
//  }
}
