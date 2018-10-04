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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.Consumed;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

public class RectSprite implements Drawable {
    public final Vector2 relativePosition;
    public final Color tint;
    private final float originalPercentageX;
    private final float originalPercentageY;
    private final TextureAtlas.AtlasRegion texture;
    private final DrawableLevel level;
    private final Vector2 position;
    private final float rotationSpeed;
    private final boolean isAdditive;
    public float relativeAngle;
    public float baseAlpha;
    private float textureSizeX;
    private float textureSizeY;
    private float originalX;
    private float originalY;
    private float radius;
    private float angle;
    private boolean isEnabled;

    /**
     * consumes relPos, doesn't consume Color
     */
    public RectSprite(TextureAtlas.AtlasRegion tex, float texSz, float origPercX, float origPercY, @Consumed Vector2 relativePosition,
                      DrawableLevel level, float relativeAngle, float rotationSpeed, Color tint, boolean additive) {
        if (tex == null) {
            throw new AssertionError("tex is null");
        }
        texture = tex;
        originalPercentageX = origPercX;
        originalPercentageY = origPercY;

        this.relativePosition = relativePosition;
        position = new Vector2();
        this.level = level;
        this.relativeAngle = relativeAngle;
        this.rotationSpeed = rotationSpeed;

        isEnabled = true;
        baseAlpha = tint.a;
        this.tint = new Color(tint);

        setTextureSize(texSz);
        isAdditive = additive;
    }

    public void setTextureSize(float textureSize) {
        textureSize /= level.depth;
        int dimensionsRatio = texture.getRegionWidth() / texture.getRegionHeight();
        if (dimensionsRatio > 1) {
            textureSizeX = textureSize;
            textureSizeY = textureSize / dimensionsRatio;
        } else {
            textureSizeX = textureSize / dimensionsRatio;
            textureSizeY = textureSize;
        }
        originalX = textureSizeX / 2 + textureSize * originalPercentageX;
        originalY = textureSizeY / 2 + textureSize * originalPercentageY;

        float relativeX = textureSizeX / 2 + textureSize * SolMath.abs(originalPercentageX);
        float relativeY = textureSizeY / 2 + textureSize * SolMath.abs(originalPercentageY);
        radius = SolMath.sqrt(relativeX * relativeX + relativeY * relativeY);
    }

    @Override
    public TextureAtlas.AtlasRegion getTexture() {
        return texture;
    }

    @Override
    public DrawableLevel getLevel() {
        return level;
    }

    @Override
    public void update(SolGame game, SolObject o) {
        relativeAngle += rotationSpeed * game.getTimeStep();
    }

    @Override
    public void prepare(SolObject object) {
        float baseAngle = object.getAngle();
        Vector2 basePosition = object.getPosition();
        SolMath.toWorld(position, relativePosition, baseAngle, basePosition);
        angle = relativeAngle + baseAngle;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public Vector2 getRelativePosition() {
        return relativePosition;
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public void draw(GameDrawer drawer, SolGame game) {
        float x = position.x;
        float y = position.y;
        if (level.depth != 1) {
            Vector2 camPosition = game.getCam().getPosition();
            x = (x - camPosition.x) / level.depth + camPosition.x;
            y = (y - camPosition.y) / level.depth + camPosition.y;
        }
        if (isAdditive) {
            drawer.drawAdditive(texture, textureSizeX, textureSizeY, originalX, originalY, x, y, angle, tint);
        } else {
            drawer.draw(texture, textureSizeX, textureSizeY, originalX, originalY, x, y, angle, tint);
        }
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean okToRemove() {
        return true;
    }
}
