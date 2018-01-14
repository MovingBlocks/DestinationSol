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
import org.destinationsol.game.SolGame;

public interface SolItem {
    String getDisplayName();

    float getPrice();

    String getDesc();

    SolItem copy();

    /**
     * Used to determine if items should be grouped together.
     * @param item The item to test equality of
     * @return A boolean indicating whether the given item is the same as the instance isSame was called on.
     */
    boolean isSame(SolItem item);

    TextureAtlas.AtlasRegion getIcon(SolGame game);

    SolItemType getItemType();

    String getCode();

    int isEquipped();

    void setEquipped(int equipped);
}
