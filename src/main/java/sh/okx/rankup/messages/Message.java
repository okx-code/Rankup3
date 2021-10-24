package sh.okx.rankup.messages;

import lombok.Getter;

public enum Message {
  NOT_IN_LADDER("not-in-ladder"),
  NOT_HIGH_ENOUGH("not-high-enough"),
  REQUIREMENTS_NOT_MET("rankup.requirements-not-met"),
  NO_RANKUP("rankup.no-rankup"),
  SUCCESS_PUBLIC("rankup.success-public"),
  SUCCESS_PRIVATE("rankup.success-private"),
  CONFIRMATION("rankup.confirmation"),
  TITLE("rankup.gui.title"),
  RANKS_HEADER("rankup.list.header"),
  RANKS_FOOTER("rankup.list.footer"),
  RANKS_COMPLETE("rankup.list.complete"),
  RANKS_CURRENT("rankup.list.current"),
  RANKS_INCOMPLETE("rankup.list.incomplete"),
  PRESTIGE_REQUIREMENTS_NOT_MET("prestige.requirements-not-met"),
  PRESTIGE_NO_PRESTIGE("prestige.no-prestige"),
  PRESTIGES_HEADER("prestige.list.header"),
  PRESTIGES_FOOTER("prestige.list.footer"),
  PRESTIGES_COMPLETE("prestige.list.complete"),
  PRESTIGES_CURRENT("prestige.list.current"),
  PRESTIGES_INCOMPLETE("prestige.list.incomplete"),
  PRESTIGE_TITLE("prestige.gui.title"),
  COOLDOWN_SINGULAR("rankup.cooldown.singular"),
  COOLDOWN_PLURAL("rankup.cooldown.plural"),
  MUST_PRESTIGE("rankup.must-prestige"),
  PRESTIGE_SUCCESS_PUBLIC("prestige.success-public"),
  PRESTIGE_SUCCESS_PRIVATE("prestige.success-private"),
  PRESTIGE_CONFIRMATION("prestige.confirmation"),
  ;

  @Getter
  private final String name;

  Message(String name) {
    this.name = name;
  }
}