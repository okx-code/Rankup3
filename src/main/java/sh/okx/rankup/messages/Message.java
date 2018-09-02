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
  RANKS_INCOMPLETE("rankup.ranks.incomplete"),
  PRESTIGES_HEADER("rankup.prestiges.header"),
  PRESTIGES_FOOTER("rankup.prestiges.footer"),
  PRESTIGES_COMPLETE("rankup.prestiges.complete"),
  PRESTIGES_CURRENT("rankup.prestiges.current"),
  PRESTIGES_INCOMPLETE("rankup.prestiges.incomplete"),
  COOLDOWN_SINGULAR("rankup.cooldown.singular"),
  COOLDOWN_PLURAL("rankup.cooldown.plural"),
  NOT_HIGH_ENOUGH("rankup.not-high-enough"),
  MUST_PRESTIGE("rankup.must-prestige");

  @Getter
  private final String name;

  Message(String name) {
    this.name = name;
  }
}
