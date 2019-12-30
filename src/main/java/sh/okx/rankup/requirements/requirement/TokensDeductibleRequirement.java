package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.requirement.tokenmanager.TokensRequirement;

public class TokensDeductibleRequirement extends TokensRequirement implements DeductibleRequirement {
  public TokensDeductibleRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  protected TokensDeductibleRequirement(TokensDeductibleRequirement clone) {
    super(clone);
  }

  @Override
  public void apply(Player player, double multiplier) {
    manager.removeTokens(player, (long) (getValueInt() * multiplier));
  }

  @Override
  public TokensRequirement clone() {
    return new TokensDeductibleRequirement(this);
  }
}
