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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.Consumed;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

public class RectSprite implements Drawable {
    public final Vector2 relPos;
    public final Color tint;
    private final float myOrigPercX;
    private final float myOrigPercY;
    private final TextureAtlas.AtlasRegion myTex;
    private final DrawableLevel myLevel;
    private final Vector2 myPos;
    private final float myRotSpd;
    private final boolean myAdditive;
    public float relAngle;
    public float baseAlpha;
    private float myTexSzX;
    private float myTexSzY;
    private float myOrigX;
    private float myOrigY;
    private float myRadius;
    private float myAngle;
    private boolean myEnabled;

    /**
     * consumes relPos, doesn't consume Color
     */
    public RectSprite(TextureAtlas.AtlasRegion tex, float texSz, float origPercX, float origPercY, @Consumed Vector2 relPos,
                        DrawableLevel level, float relAngle, float rotSpd, Color tint, boolean additive) {
        if (tex == null) {
            throw new AssertionError("tex is null");
        }
        myTex = tex;
        myOrigPercX = origPercX;
        myOrigPercY = origPercY;

        this.relPos = relPos;
        myPos = new Vector2();
        myLevel = level;
        this.relAngle = relAngle;
        myRotSpd = rotSpd;

        myEnabled = true;
        baseAlpha = tint.a;
        this.tint = new Color(tint);

        setTexSz(texSz);
        myAdditive = additive;
    }

    public void setTexSz(float texSz) {
        texSz /= myLevel.depth;
        int r = myTex.getRegionWidth() / myTex.getRegionHeight();
        if (r > 1) {
            myTexSzX = texSz;
            myTexSzY = texSz / r;
        } else {
            myTexSzX = texSz / r;
            myTexSzY = texSz;
        }
        myOrigX = myTexSzX / 2 + texSz * myOrigPercX;
        myOrigY = myTexSzY / 2 + texSz * myOrigPercY;

        float rx = myTexSzX / 2 + texSz * SolMath.abs(myOrigPercX);
        float ry = myTexSzY / 2 + texSz * SolMath.abs(myOrigPercY);
        myRadius = SolMath.sqrt(rx * rx + ry * ry);
    }

    public Texture getTex0() {
        return myTex.getTexture();
    }

    @Override
    public TextureAtlas.AtlasRegion getTex() {
        return myTex;
    }

    public DrawableLevel getLevel() {
        return myLevel;
    }

    public void update(SolGame game, SolObject o) {
        relAngle += myRotSpd * game.getTimeStep();
    }

    public void prepare(SolObject o) {
        float baseAngle = o.getAngle();
        Vector2 basePos = o.getPosition();
        SolMath.toWorld(myPos, relPos, baseAngle, basePos, false);
        myAngle = relAngle + baseAngle;
    }

    public Vector2 getPos() {
        return myPos;
    }

    @Override
    public Vector2 getRelPos() {
        return relPos;
    }

    public float getRadius() {
        return myRadius;
    }

    public void draw(GameDrawer drawer, SolGame game) {
        float x = myPos.x;
        float y = myPos.y;
        if (myLevel.depth != 1) {
            Vector2 camPos = game.getCam().getPos();
            x = (x - camPos.x) / myLevel.depth + camPos.x;
            y = (y - camPos.y) / myLevel.depth + camPos.y;
        }
        if (myAdditive) {
            drawer.drawAdditive(myTex, myTexSzX, myTexSzY, myOrigX, myOrigY, x, y, myAngle, tint);
        } else {
            drawer.draw(myTex, myTexSzX, myTexSzY, myOrigX, myOrigY, x, y, myAngle, tint);
        }
    }

    public boolean isEnabled() {
        return myEnabled;
    }

    public void setEnabled(boolean enabled) {
        myEnabled = enabled;
    }

    @Override
    public boolean okToRemove() {
        return true;
    }
}
