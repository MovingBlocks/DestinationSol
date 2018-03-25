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
import org.destinationsol.game.ship.SolShip;

public class MercItem implements SolItem {
    private final ShipConfig config;
    private final String myDesc;
    private SolShip solship;

    public MercItem(ShipConfig config) {
        this.config = config;
        myDesc = "Has a shield and repairers\n" + ShipItem.makeDesc(config.hull);
    }

    @Override
    public String getDisplayName() {
        return config.hull.getDisplayName();
    }

    @Override
    public float getPrice() {
        return config.hull.getHirePrice();
    }

    @Override
    public String getDescription() {
        return myDesc;
    }

    @Override
    public SolItem copy() {
        return new MercItem(config);
    }

    @Override
    // Always returns false so that MercItems don't group together.
    // That way they can be selected separate of each other.
    public boolean isSame(SolItem item) {
        return false;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(SolGame game) {
        return config.hull.getIcon();
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
        return config;
    }
    
    public void setSolShip(SolShip solship) {
        this.solship = solship;
    }
    
    public SolShip getSolShip() {
        return this.solship;
    }
}
