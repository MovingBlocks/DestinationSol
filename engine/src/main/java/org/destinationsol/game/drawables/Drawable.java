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

package org.destinationsol.game.drawables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import org.destinationsol.CommonDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

/**
 * Wrapper for {@link TextureAtlas.AtlasRegion}.
 *
 * {@code Drawable}s, in addition to the texture itself, offer additional capabilities, like depth level. Every drawable
 * is drawn against some {@link SolObject}, and it can have its position and rotation relative to this object.
 */
public interface Drawable {

    /**
     * Returns the texture this drawable wraps.
     *
     * @return {@code AtlasRegion} with the respective texture
     */
    TextureAtlas.AtlasRegion getTexture();

    /**
     * Returns the drawing level.
     *
     * {@code DrawableLevel} represents the depth at which this drawable renders, as well as its "logical group".
     *
     * @return Level of this drawable
     */
    DrawableLevel getLevel();

    /**
     * Called on every game's frame, allows for handling of object-specific runtime needs.
     *
     * @param game Game this drawable belongs to.
     * @param o Object against which it is drawn
     */
    void update(SolGame game, SolObject o);

    /**
     * Called before every draw of this drawable.
     *
     * After call to this method, the drawable must be able to return correct position and radius, and must be able to
     * correctly draw itself.
     *
     * @param o Object against which this drawable is drawn.
     */
    void prepare(SolObject o);

    /**
     * Returns position of the origin of this drawable.
     *
     * @return Origin of this drawable
     */
    Vector2 getPosition();

    /**
     * Returns position of the origin of this drawable, relative to the {@link SolObject} this is drawn against.
     *
     * @return Origin relative to parent {@code SolObject}
     */
    Vector2 getRelativePosition();

    /**
     * Returns radius of this drawable, that is the longest distance from origin where there is still the texture.
     *
     * @return Radius of this drawable.
     */
    float getRadius();

    /**
     * Draws this drawable to the supplied CommonDrawer.
     *
     * @param drawer CommonDrawer to which to draw
     * @param game Game this drawable belongs in
     */
    void draw(CommonDrawer drawer, SolGame game);

    /**
     * @return True if this drawable should be drawn, false otherwise
     */
    boolean isEnabled();

    /**
     * @return True if this drawable is not to be drawn in game more and can thus be removed.
     */
    boolean okToRemove();
}
