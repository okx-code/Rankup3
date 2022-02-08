package sh.okx.rankup.requirements.requirement.votingplugin;

import com.bencodez.votingplugin.topvoter.TopVoter;
import com.bencodez.votingplugin.user.UserManager;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class VotingPluginVotesRequirement extends ProgressiveRequirement {
  public VotingPluginVotesRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected VotingPluginVotesRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    return UserManager.getInstance().getVotingPluginUser(player).getTotal(TopVoter.AllTime);
  }

  @Override
  public Requirement clone() {
    return new VotingPluginVotesRequirement(this);
  }
}
