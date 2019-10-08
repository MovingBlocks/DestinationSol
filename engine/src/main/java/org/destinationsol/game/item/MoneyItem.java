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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.game.SolGame;

public class MoneyItem implements SolItem {
    public static final int SMALL_AMOUNT = 10;
    public static final int MEDIUM_AMOUNT = 3 * SMALL_AMOUNT;
    public static final int BIG_AMOUNT = 10 * SMALL_AMOUNT;
    public static final int HUGE_AMOUNT = 100 * SMALL_AMOUNT;

    private final float amount;
    private final SolItemType itemType;

    MoneyItem(float amt, SolItemType itemType) {
        amount = amt;
        this.itemType = itemType;
    }

    @Override
    public String getDisplayName() {
        return "money";
    }

    @Override
    public float getPrice() {
        return amount;
    }

    @Override
    public String getDescription() {
        return "money";
    }

    @Override
    public MoneyItem copy() {
        return new MoneyItem(amount, itemType);
    }

    @Override
    public boolean isSame(SolItem item) {
        return item instanceof MoneyItem && ((MoneyItem) item).amount == amount;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(SolGame game) {
        ItemManager im = game.getItemMan();
        if(amount == HUGE_AMOUNT) {
            return im.hugeMoneyIcon;
        } else if (amount == BIG_AMOUNT) {
            return im.bigMoneyIcon;
        }else if (amount == MEDIUM_AMOUNT) {
            return im.medMoneyIcon;
        }
        return im.moneyIcon;
    }

    @Override
    public SolItemType getItemType() {
        return itemType;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public int isEquipped() {
        return 0;
    }

    @Override
    public void setEquipped(int equipped) {

    }
}
