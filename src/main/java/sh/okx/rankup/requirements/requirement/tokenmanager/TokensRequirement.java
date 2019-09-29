package sh.okx.rankup.requirements.requirement.tokenmanager;

import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.ProgressiveRequirement;

public class TokensRequirement extends ProgressiveRequirement implements DeductibleRequirement {
  private final TokenManager manager = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");

  public TokensRequirement(Rankup plugin) {
    super(plugin, "tokenmanager-tokens");
  }

  private TokensRequirement(TokensRequirement clone) {
    super(clone);
  }

  @Override
  public void apply(Player player, double multiplier) {
    manager.removeTokens(player, (long) (getValueInt() * multiplier));
  }

  @Override
  public double getProgress(Player player) {
    return manager.getTokens(player).orElse(0);
  }

  @Override
  public TokensRequirement clone() {
    return new TokensRequirement(this);
  }
}
