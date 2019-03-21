package sh.okx.rankup.requirements.requirement.votingplugin;

import com.Ben12345rocks.VotingPlugin.UserManager.UserManager;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class VotingPluginVotesRequirement extends Requirement {
  public VotingPluginVotesRequirement(Rankup plugin) {
    super(plugin, "votingplugin-votes");
  }

  protected VotingPluginVotesRequirement(VotingPluginVotesRequirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    return getRemaining(player) < 1;
  }

  @Override
  public double getRemaining(Player player) {
    return Math.max(0, getValueDouble() - UserManager.getInstance().getVotingPluginUser(player).getPoints());
  }

  @Override
  public Requirement clone() {
    return new VotingPluginVotesRequirement(this);
  }
}
