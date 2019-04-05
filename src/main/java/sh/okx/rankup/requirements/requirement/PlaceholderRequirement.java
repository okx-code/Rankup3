package sh.okx.rankup.requirements.requirement;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class PlaceholderRequirement extends Requirement {
  public PlaceholderRequirement(Rankup plugin) {
    super(plugin, "placeholder");
  }

  public PlaceholderRequirement(PlaceholderRequirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    String[] parts = getValueString().split(" ");
    String parsed = PlaceholderAPI.setPlaceholders(player, parts[0]);
    if (!PlaceholderAPI.containsPlaceholders(parts[0]) || parsed.equals(parts[0])) {
      throw new IllegalArgumentException(parts[0] + " is not a PlaceholderAPI placeholder!");
    } else if (parts.length < 3) {
      throw new IllegalArgumentException("Placeholder requirements must be in the form %placeholder% <operation> string");
    }
    String value = parts[2];

    // string operations
    switch (parts[1]) {
      case "=":
        return parsed.equals(value);
    }

    // numeric operations
    double p = Double.parseDouble(parsed);
    double v = Double.parseDouble(value);
    switch (parts[1]) {
      case ">":
        return p > v;
      case ">=":
        return p >= v;
      case "<":
        return p < v;
      case "<=":
        return p <= v;
      case "==":
        return p == v;
    }
    throw new IllegalArgumentException("Invalid operation: " + parts[1]);
  }

  @Override
  public Requirement clone() {
    return new PlaceholderRequirement(this);
  }
}
