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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.SolGame;

public class MercItem implements SolItem {
    private final ShipConfig myConfig;
    private final String myDesc;

    public MercItem(ShipConfig config) {
        myConfig = config;
        myDesc = "Has a shield and repairers\n" + ShipItem.makeDesc(myConfig.hull);
    }

    @Override
    public String getDisplayName() {
        return myConfig.hull.getDisplayName();
    }

    @Override
    public float getPrice() {
        return myConfig.hull.getHirePrice();
    }

    @Override
    public String getDesc() {
        return myDesc;
    }

    @Override
    public SolItem copy() {
        return new MercItem(myConfig);
    }

    @Override
    public boolean isSame(SolItem item) {
        return item instanceof MercItem && ((MercItem) item).myConfig == myConfig;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(SolGame game) {
        return myConfig.hull.getIcon();
    }

    @Override
    public SolItemType getItemType() {
        return ShipItem.EMPTY;
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

    public ShipConfig getConfig() {
        return myConfig;
    }
}
