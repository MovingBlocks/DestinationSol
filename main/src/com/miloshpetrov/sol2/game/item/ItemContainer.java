package com.miloshpetrov.sol2.game.item;

import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;

import java.util.*;

public class ItemContainer implements Iterable<SolItem> {
  private static final int CAP = 5 * Const.ITEMS_PER_PAGE;
  private List<SolItem> myItems;

  public ItemContainer() {
    myItems = new ArrayList<SolItem>();
  }

  public boolean tryConsumeItem(SolItem example) {
    for (SolItem item : myItems) {
      if (!example.isSame(item)) continue;
      myItems.remove(item);
      return true;
    }
    return false;
  }

  public int count(SolItem example) {
    int count = 0;
    for (SolItem item : myItems) {
      if (example.isSame(item)) count++;
    }
    return count;
  }

  public boolean canAdd() {
    return size() < CAP;
  }

  public void add(SolItem item) {
    if (size() >= CAP) throw new AssertionError("container is full");
    if (myItems.contains(item)) throw new AssertionError();
    myItems.add(0, item);
  }

  @Override
  public Iterator<SolItem> iterator() {
    return new Itr();
  }

  public int size() {
    return myItems.size();
  }

  public boolean contains(SolItem item) {
    return myItems.contains(item);
  }

  public SolItem get(int idx) {
    return myItems.get(idx);
  }

  public void remove(SolItem item) {
    myItems.remove(item);
  }

  public SolItem getNext(SolItem selected) {
    int idx = myItems.indexOf(selected) + 1;
    if (idx <= 0 || idx >= size()) return null;
    return myItems.get(idx);
  }

  public SolItem getRandom() {
    return SolMath.elemRnd(myItems);
  }

  private class Itr implements Iterator<SolItem> {
    int myCur;       // index of next element to return

    public boolean hasNext() {
      return myCur != myItems.size();
    }

    public SolItem next() {
      return myItems.get(myCur++);
    }

    @Override
    public void remove() {
      throw new AssertionError();
    }
  }
}
