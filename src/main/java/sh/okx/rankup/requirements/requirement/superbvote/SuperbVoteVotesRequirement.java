package sh.okx.rankup.requirements.requirement.superbvote;

import io.minimum.minecraft.superbvote.SuperbVote;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class SuperbVoteVotesRequirement extends ProgressiveRequirement {
  public SuperbVoteVotesRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  private SuperbVoteVotesRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    return SuperbVote.getPlugin().getVoteStorage().getVotes(player.getUniqueId()).getVotes();
  }

  @Override
  public Requirement clone() {
    return new SuperbVoteVotesRequirement(this);
  }
}
