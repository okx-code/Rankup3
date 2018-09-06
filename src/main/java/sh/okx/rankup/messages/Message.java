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
  RANKS_HEADER("rankup.list.header"),
  RANKS_FOOTER("rankup.list.footer"),
  RANKS_COMPLETE("rankup.list.complete"),
  RANKS_CURRENT("rankup.list.current"),
  RANKS_INCOMPLETE("rankup.list.incomplete"),
  PRESTIGES_HEADER("prestige.list.header"),
  PRESTIGES_FOOTER("prestige.list.footer"),
  PRESTIGES_COMPLETE("prestige.list.complete"),
  PRESTIGES_CURRENT("prestige.list.current"),
  PRESTIGES_INCOMPLETE("prestige.list.incomplete"),
  PRESTIGE_TITLE("prestige.title"),
  COOLDOWN_SINGULAR("rankup.cooldown.singular"),
  COOLDOWN_PLURAL("rankup.cooldown.plural"),
  MUST_PRESTIGE("rankup.must-prestige"),
  NOT_HIGH_ENOUGH("prestige.not-high-enough");

  @Getter
  private final String name;

  Message(String name) {
    this.name = name;
  }
}