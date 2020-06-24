package sh.okx.rankup.ranks;

import lombok.Getter;

import java.util.Objects;

@Getter
public class RankElement<T extends Rank> {
  private boolean rootNode = true;
  private final T rank;
  private RankElement<T> next;

  public RankElement(T rank, RankElement<T> next) {
    Objects.requireNonNull(rank);
    this.rank = rank;
    this.next = next;
  }

  public void setRootNode(boolean rootNode) {
    this.rootNode = rootNode;
  }

  public boolean hasNext() {
    return next != null;
  }

  public void setNext(RankElement<T> next) {
    this.next = next;
    this.next.setRootNode(false);
  }
}
