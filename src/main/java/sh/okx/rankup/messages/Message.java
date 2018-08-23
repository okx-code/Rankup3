package sh.okx.rankup.messages;

import lombok.Getter;

public enum Message {
  NOT_IN_LADDER("not-in-ladder"),
  REQUIREMENTS_NOT_MET("rankup.requirements-not-met"),
  NO_RANKUP("rankup.no-rankup"),
  SUCCESS_PUBLIC("rankup.success-public"),
  SUCCESS_PRIVATE("rankup.success-private"),
  CONFIRMATION("rankup.confirmation"),
  TITLE("rankup.title"),
  RANKS_HEADER("rankup.ranks.header"),
  RANKS_FOOTER("rankup.ranks.footer"),
  RANKS_COMPLETE("rankup.ranks.complete"),
  RANKS_CURRENT("rankup.ranks.current"),
  RANKS_INCOMPLETE("rankup.ranks.incomplete");

  @Getter
  private final String name;

  Message(String name) {
    this.name = name;
  }
}
