/*
 * Copyright 2017 MovingBlocks
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ItemContainer implements Iterable<List<SolItem>> {
    public static final int MAX_INVENTORY_PAGES = 4;
    public static final int MAX_GROUP_COUNT = MAX_INVENTORY_PAGES * Const.ITEM_GROUPS_PER_PAGE;
    public static final int MAX_STACK_SIZE = 30; // e.g.: ammo, repair kit

    private List<List<SolItem>> myGroups;
    private Set<List<SolItem>> myNewGroups;

    public ItemContainer() {
        myGroups = new ArrayList<>();
        myNewGroups = new HashSet<>();
    }

    public boolean tryConsumeItem(SolItem example) {
        for (List<SolItem> group : myGroups) {
            SolItem item = group.get(0);
            if (!example.isSame(item)) {
                continue;
            }
            remove(item);
            return true;
        }
        return false;
    }

    public int count(SolItem example) {
        for (List<SolItem> group : myGroups) {
            SolItem item = group.get(0);
            if (example.isSame(item)) {
                return group.size();
            }
        }
        return 0;
    }

    public boolean canAdd(SolItem example) {
        for (List<SolItem> group : myGroups) {
            SolItem item = group.get(0);
            if (item.isSame(example)) {
                return group.size() < MAX_STACK_SIZE;
            }
        }
        return myGroups.size() < MAX_GROUP_COUNT;
    }

    public void add(SolItem addedItem) {
        if (addedItem == null) {
            throw new AssertionError("adding null item");
        }
        for (List<SolItem> group : myGroups) {
            SolItem item = group.get(0);
            if (item.isSame(addedItem)) {
                if ((group.size() < MAX_STACK_SIZE)) {
                    group.add(addedItem);
                }
                return;
            }
        }
        // From now on, silently ignore if by some chance an extra inventory page is created
        //if (myGroups.size() >= MAX_GROUP_COUNT) throw new AssertionError("reached group count limit");
        ArrayList<SolItem> group = new ArrayList<>();
        group.add(addedItem);
        myGroups.add(0, group);
        myNewGroups.add(group);
    }

    @Override
    public Iterator<List<SolItem>> iterator() {
        return new Itr();
    }

    public int groupCount() {
        return myGroups.size();
    }

    public boolean contains(SolItem item) {
        for (List<SolItem> group : myGroups) {
            if (group.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public void remove(SolItem item) {
        List<SolItem> remGroup = null;
        boolean removed = false;
        for (List<SolItem> group : myGroups) {
            removed = group.remove(item);
            if (group.isEmpty()) {
                remGroup = group;
            }
            if (removed) {
                break;
            }
        }
        if (remGroup != null) {
            myGroups.remove(remGroup);
            myNewGroups.remove(remGroup);
        }
    }

    public List<SolItem> getSelectionAfterRemove(List<SolItem> selected) {
        if (selected.size() > 1) {
            return selected;
        }
        int idx = myGroups.indexOf(selected) + 1;
        if (idx <= 0 || idx >= groupCount()) {
            return null;
        }
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
