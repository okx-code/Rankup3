package sh.okx.rankup.requirements.requirement.votingplugin;

import com.bencodez.votingplugin.VotingPluginMain;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class VotingPluginPointsRequirement extends ProgressiveRequirement {

  public VotingPluginPointsRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected VotingPluginPointsRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    return VotingPluginMain.getPlugin().getVotingPluginUserManager().getVotingPluginUser(player).getPoints();
  }

  @Override
  public Requirement clone() {
    return new VotingPluginPointsRequirement(this);
  }
}
