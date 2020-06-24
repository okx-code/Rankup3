package sh.okx.rankup.ranks;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RankTree<T extends Rank> implements Iterable<T> {
  private final RankElement<T> first;

  public RankTree(RankElement<T> first) {
    this.first = first;
  }

  public RankElement<T> getFirst() {
    return first;
  }

  public int length() {
    int len = 0;
    RankElement<T> elem = first;
    while (elem != null) {
      len++;
      elem = elem.getNext();
    }
    return len;
  }

  @NotNull
  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      private RankElement<T> element = first;
      @Override
      public boolean hasNext() {
        return element.hasNext();
      }

      @Override
      public T next() {
        element = element.getNext();
        return element.getRank();
      }
    };
  }

  public List<RankElement<T>> asList() {
    List<RankElement<T>> ranks = new ArrayList<>();
    RankElement<T> elem = first;
    while (elem != null) {
      ranks.add(elem);
      elem = elem.getNext();
    }
    return ranks;
  }

  public RankElement<T> last() {
    RankElement<T> elem = first;
    while (elem.hasNext()) {
      elem = elem.getNext();
    }
    return elem;
  }
}
