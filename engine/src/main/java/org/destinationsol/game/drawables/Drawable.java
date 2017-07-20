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

package org.destinationsol.game.drawables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

public interface Drawable {
    Texture getTex0();

    TextureAtlas.AtlasRegion getTex();

    DrawableLevel getLevel();

    // called on every update from manager
    void update(SolGame game, SolObject o);

    // called on every draw from manager. after that, this drawable should be able to return correct pos & radius
    void prepare(SolObject o);

    Vector2 getPos();

    Vector2 getRelPos();

    float getRadius();

    void draw(GameDrawer drawer, SolGame game);

    boolean isEnabled();

    boolean okToRemove();
}
