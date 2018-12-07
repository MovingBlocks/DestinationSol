/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.item;

import org.destinationsol.Const;
import org.destinationsol.common.SolRandom;
import org.terasology.module.sandbox.API;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@API
public class ItemContainer implements Iterable<List<SolItem>> {
    private static final int MAX_INVENTORY_PAGES = 4;
    private static final int MAX_GROUP_COUNT = MAX_INVENTORY_PAGES * Const.ITEM_GROUPS_PER_PAGE;
    private static final int MAX_STACK_SIZE = 30; // e.g.: ammo, repair kit

    private List<List<SolItem>> groups;
    private Set<List<SolItem>> newGroups;

    public ItemContainer() {
        groups = new ArrayList<>();
        newGroups = new HashSet<>();
    }

    public boolean tryConsumeItem(SolItem example) {
        for (List<SolItem> group : groups) {
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
        for (List<SolItem> group : groups) {
            SolItem item = group.get(0);
            if (example.isSame(item)) {
                return group.size();
            }
        }
        return 0;
    }

    public boolean canAdd(SolItem example) {
        for (List<SolItem> group : groups) {
            SolItem item = group.get(0);
            if (item.isSame(example)) {
                return group.size() < MAX_STACK_SIZE;
            }
        }
        return groups.size() < MAX_GROUP_COUNT;
    }

    public void add(SolItem addedItem) {
        if (addedItem == null) {
            throw new AssertionError("adding null item");
        }
        for (List<SolItem> group : groups) {
            SolItem item = group.get(0);
            if (item.isSame(addedItem)) {
                if ((group.size() < MAX_STACK_SIZE)) {
                    group.add(addedItem);
                }
                return;
            }
        }
        // From now on, silently ignore if by some chance an extra inventory page is created
        //if (groups.size() >= MAX_GROUP_COUNT) throw new AssertionError("reached group count limit");
        ArrayList<SolItem> group = new ArrayList<>();
        group.add(addedItem);
        groups.add(0, group);
        newGroups.add(group);
    }

    @Override
    public Iterator<List<SolItem>> iterator() {
        return new ItemContainerIterator();
    }

    public int groupCount() {
        return groups.size();
    }

    public boolean contains(SolItem item) {
        for (List<SolItem> group : groups) {
            if (group.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public void remove(SolItem item) {
        List<SolItem> remGroup = null;
        for (List<SolItem> group : groups) {
            boolean removed = group.remove(item);
            if (group.isEmpty()) {
                remGroup = group;
            }
            if (removed) {
                break;
            }
        }
        if (remGroup != null) {
            groups.remove(remGroup);
            newGroups.remove(remGroup);
        }
    }

    public List<SolItem> getSelectionAfterRemove(List<SolItem> selected) {
        if (selected.size() > 1) {
            return selected;
        }
        int idx = groups.indexOf(selected) + 1;
        if (idx <= 0 || idx >= groupCount()) {
            return null;
        }
        return groups.get(idx);
    }

    public SolItem getRandom() {
        return groups.isEmpty() ? null : SolRandom.randomElement(SolRandom.randomElement(groups));
    }

    public boolean isNew(List<SolItem> group) {
        return newGroups.contains(group);
    }

    public void seen(List<SolItem> group) {
        newGroups.remove(group);
    }

    public void markAllAsSeen() {
        newGroups.clear();
    }

    public boolean hasNew() {
        return !newGroups.isEmpty();
    }

    public int getCount(int groupIdx) {
        return groups.get(groupIdx).size();
    }

    public boolean containsGroup(List<SolItem> group) {
        return groups.contains(group);
    }

    public List<SolItem> getGroup(int groupIdx) {
        return groups.get(groupIdx);
    }

    public void clear() {
        groups.clear();
        newGroups.clear();
    }

    private class ItemContainerIterator implements Iterator<List<SolItem>> {
        int myCur;       // index of next element to return

        public boolean hasNext() {
            return myCur != groups.size();
        }

        public List<SolItem> next() {
            return groups.get(myCur++);
        }

        @Override
        public void remove() {
            throw new AssertionError("tried to remove via item iterator");
        }
    }
}
