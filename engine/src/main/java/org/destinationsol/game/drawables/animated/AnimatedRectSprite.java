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
package org.destinationsol.game.drawables.animated;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.Consumed;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;

//TODO: Should this allow variable-size frames?
public class AnimatedRectSprite extends RectSprite {
    private Animation<TextureAtlas.AtlasRegion> spriteAnimation;
    private float animationTime;

    public AnimatedRectSprite(Animation<TextureAtlas.AtlasRegion> animation, float texSz, float origPercX, float origPercY, @Consumed Vector2 relativePosition,
                              DrawableLevel level, float relativeAngle, float rotationSpeed, Color tint, boolean additive) {
        super(animation.getKeyFrame(0.0f), texSz, origPercX, origPercY, relativePosition, level, relativeAngle, rotationSpeed, tint, additive);
        spriteAnimation = animation;
    }

    @Override
    public void update(SolGame game, SolObject object) {
        super.update(game, object);
        animationTime += game.getTimeStep();
    }

    @Override
    public TextureAtlas.AtlasRegion getTexture() {
        return spriteAnimation.getKeyFrame(animationTime, true);
    }

    public float getAnimationTime() {
        return animationTime;
    }

    public void setAnimationTime(float time) {
        animationTime = time;
    }

    public Animation<TextureAtlas.AtlasRegion> getAnimation() {
        return spriteAnimation;
    }

    public void setAnimation(Animation<TextureAtlas.AtlasRegion> animation) {
        spriteAnimation = animation;
    }
}
