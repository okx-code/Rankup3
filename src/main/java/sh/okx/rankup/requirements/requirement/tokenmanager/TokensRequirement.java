package sh.okx.rankup.requirements.requirement.tokenmanager;

import java.util.Objects;
import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;

public class TokensRequirement extends ProgressiveRequirement {
  protected final TokenManager manager = (TokenManager) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("TokenManager"));

  public TokensRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected TokensRequirement(TokensRequirement clone) {
    super(clone);
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
