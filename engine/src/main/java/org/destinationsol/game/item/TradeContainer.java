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

import org.destinationsol.game.SolGame;

import java.util.List;

public class TradeContainer {
    private static final float MAX_AWAIT = 180f;

    private final TradeConfig myConfig;
    private final ItemContainer myItems;

    private float myAwait;

    public TradeContainer(TradeConfig config) {
        myConfig = config;
        myItems = new ItemContainer();
    }

    public void update(SolGame game) {
        if (0 < myAwait) {
            myAwait -= game.getTimeStep();
            return;
        }

        myAwait = MAX_AWAIT;
        myItems.clear();
        List<ItemConfig> items = myConfig.items;
        for (ItemConfig i : items) {
            SolItem ex = i.examples.get(0);
            int amt = ex.isSame(ex) ? 16 : 1;
            for (int j = 0; j < amt; j++) {
                if (myItems.canAdd(ex)) {
                    myItems.add(ex.copy());
                }
            }
        }
    }

    public ItemContainer getItems() {
        return myItems;
    }

    public ItemContainer getShips() {
        return myConfig.hulls;
    }

    public ItemContainer getMercs() {
        return myConfig.mercs;
    }
}
