package sh.okx.rankup.messages.pebble;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankTree;
import sh.okx.rankup.requirements.Requirement;

public class RankContext {
  private final RankupPlugin plugin;
  private final Player player;
  private final Rank rank;

  public RankContext(RankupPlugin plugin, Player player, Rank rank) {
    this.plugin = plugin;
    this.player = player;
    this.rank = rank;
  }

  public String getRank() {
    return rank.getRank();
  }

  public String getName() {
    return rank.getDisplayName();
  }

  public RequirementContext getRequirement(String requirement) {
    Requirement context = rank.getRequirement(player, requirement);
    if (context == null) {
      throw new InvalidRequirementException(requirement, rank);
    }
    return new RequirementContext(player, context);
  }

  public RequirementContext getRequirement(String requirement, String sub) {
    return getRequirement(requirement + "#" + sub);
  }

  public RequirementContext getReq(String requirement) {
    return getRequirement(requirement);
  }

  public RequirementContext getReq(String requirement, String sub) {
    return getRequirement(requirement, sub);
  }

  public List<RequirementContext> getRequirements() {
    List<RequirementContext> list = new ArrayList<>();
    for (Requirement requirement : rank.getRequirements().getRequirements(player)) {
      list.add(new RequirementContext(player, requirement));
    }
    return list;
  }

  public boolean getDone() {
    for (RequirementContext context : getRequirements()) {
      if (!context.getDone()) {
        return false;
      }
    }
    return true;
  }

  public int getIndex() {
    RankTree<Rank> tree = plugin.getRankups().getTree();
    int index = 0;
    for (Rank rank : tree) {
      if (rank == this.rank) {
        return index;
      }
      index++;
    }
    return -1;
  }

  public String toString() {
    return rank.getRank();
  }
}
