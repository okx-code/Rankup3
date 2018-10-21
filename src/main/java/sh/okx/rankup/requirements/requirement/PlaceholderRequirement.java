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

  private double getValue(Player player) {
    String[] parts = getValueString().split(" ", 2);
    String parsed = PlaceholderAPI.setPlaceholders(player, parts[0]);
    if(!PlaceholderAPI.containsPlaceholders(parts[0]) || parsed.equals(parts[0])) {
      plugin.getLogger().severe(parts[0] + " is not a PlaceholderAPI placeholder!");
      return -1;
    }
    double value;
    try {
      value = Double.parseDouble(parsed);
    } catch(NumberFormatException ex) {
      plugin.getLogger().severe("Parsed placeholder '" +parsed + "' is not a valid number");
      return -1;
    }
    return value;
  }

  private double getNeeded() {
    String needed = getValueString().split(" ", 2)[1];
    try {
      return Double.parseDouble(needed);
    } catch(NumberFormatException e) {
      plugin.getLogger().severe("Needed '" + needed + "' is not a valid number!");
      return -1;
    }
  }

  @Override
  public boolean check(Player player) {
    String[] parts = getValueString().split(" ");
    String parsed = PlaceholderAPI.setPlaceholders(player, parts[0]);
    if(!PlaceholderAPI.containsPlaceholders(parts[0]) || parsed.equals(parts[0])) {
      throw new IllegalArgumentException(parts[0] + " is not a PlaceholderAPI placeholder!");
    }
    String value = parts[2];

    // string operations
    switch(parts[1]) {
      case "=":
        return parsed.equals(value);
    }

    // numeric operations
    double p = Double.parseDouble(parsed);
    double v = Double.parseDouble(value);
    switch(parts[1]) {
      case ">":
        return p > v;
      case ">=":
        return p >= v;
      case "<":
        return p < v;
      case "<=":
        return p <= v;
    }
    throw new IllegalArgumentException("Invalid operation: " + parts[1]);
  }

  @Override
  public double getRemaining(Player player) {
    return check(player) ? 0 : 1;
  }

  @Override
  public Requirement clone() {
    return new PlaceholderRequirement(this);
  }
}
