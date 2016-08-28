/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.item;

import org.destinationsol.Const;
import org.destinationsol.common.SolMath;

import java.util.*;

public class ItemContainer implements Iterable<List<SolItem>> {
  public static final int MAX_GROUP_COUNT = 4 * Const.ITEM_GROUPS_PER_PAGE;
  public static final int MAX_GROUP_SZ = 30;

  private List<List<SolItem>> myGroups;
  private Set<List<SolItem>> myNewGroups;
  private int mySize;

  public ItemContainer() {
    myGroups = new ArrayList<List<SolItem>>();
    myNewGroups = new HashSet<List<SolItem>>();
  }

  public boolean tryConsumeItem(SolItem example) {
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<SolItem> group = myGroups.get(i);
      SolItem item = group.get(0);
      if (!example.isSame(item)) continue;
      remove(item);
      return true;
    }
    return false;
  }

  public int count(SolItem example) {
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<SolItem> group = myGroups.get(i);
      SolItem item = group.get(0);
      if (example.isSame(item)) return group.size();
    }
    return 0;
  }

  public boolean canAdd(SolItem example) {
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<SolItem> group = myGroups.get(i);
      SolItem item = group.get(0);
      if (item.isSame(example)) return group.size() < MAX_GROUP_SZ;
    }
    return myGroups.size() < MAX_GROUP_COUNT;
  }

  public void add(SolItem addedItem) {
    if (addedItem == null) throw new AssertionError("adding null item");
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<SolItem> group = myGroups.get(i);
      SolItem item = group.get(0);
      if (item.isSame(addedItem)) {
        if ((group.size() < MAX_GROUP_SZ))
        {
        	group.add(addedItem);
        	mySize++;
        }
        return;
        
      }
    }
    if (myGroups.size() >= MAX_GROUP_COUNT) throw new AssertionError("reached group count limit");
    ArrayList<SolItem> group = new ArrayList<SolItem>();
    group.add(addedItem);
    myGroups.add(0, group);
    mySize++;
    myNewGroups.add(group);
  }

  @Override
  public Iterator<List<SolItem>> iterator() {
    return new Itr();
  }

  public int groupCount() {
    return myGroups.size();
  }

  public int size() {
    return mySize;
  }

  public boolean contains(SolItem item) {
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<SolItem> group = myGroups.get(i);
      if (group.contains(item)) return true;
    }
    return false;
  }

  public void remove(SolItem item) {
    List<SolItem> remGroup = null;
    boolean removed = false;
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<SolItem> group = myGroups.get(i);
      removed = group.remove(item);
      if (group.isEmpty()) remGroup = group;
      if (removed) break;
    }
    if (removed) mySize--;
    if (remGroup != null) {
      myGroups.remove(remGroup);
      myNewGroups.remove(remGroup);
    }
  }

  public List<SolItem> getSelectionAfterRemove(List<SolItem> selected) {
    if (selected.size() > 1) return selected;
    int idx = myGroups.indexOf(selected) + 1;
    if (idx <= 0 || idx >= groupCount()) return null;
    return myGroups.get(idx);
  }

  public SolItem getRandom() {
    return myGroups.isEmpty() ? null : SolMath.elemRnd(SolMath.elemRnd(myGroups));
  }

  public boolean isNew(List<SolItem> group) {
    return myNewGroups.contains(group);
  }

  public void seen(List<SolItem> group) {
    myNewGroups.remove(group);
  }

  public void seenAll() {
    myNewGroups.clear();
  }

  public boolean hasNew() {
    return !myNewGroups.isEmpty();
  }

  public int getCount(int groupIdx) {
    return myGroups.get(groupIdx).size();
  }

  public boolean containsGroup(List<SolItem> group) {
    return myGroups.contains(group);
  }

  public List<SolItem> getGroup(int groupIdx) {
    return myGroups.get(groupIdx);
  }

  public void clear() {
    myGroups.clear();
    myNewGroups.clear();
    mySize = 0;
  }

  private class Itr implements Iterator<List<SolItem>> {
    int myCur;       // index of next element to return

    public boolean hasNext() {
      return myCur != myGroups.size();
    }

    public List<SolItem> next() {
      return myGroups.get(myCur++);
    }

    @Override
    public void remove() {
      throw new AssertionError("tried to remove via item iterator");
    }
  }
}
