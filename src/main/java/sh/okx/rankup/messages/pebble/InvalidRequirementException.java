package sh.okx.rankup.messages.pebble;

import sh.okx.rankup.ranks.Rank;

public class InvalidRequirementException extends RuntimeException {
  private final String requirement;
  private final Rank rank;

  public InvalidRequirementException(String requirement, Rank rank) {
    super("Invalid requirement: " + requirement + " for rank " + rank.getRank());
    this.requirement = requirement;
    this.rank = rank;
  }

  public String getRequirement() {
    return requirement;
  }

  public Rank getRank() {
    return rank;
  }
}
