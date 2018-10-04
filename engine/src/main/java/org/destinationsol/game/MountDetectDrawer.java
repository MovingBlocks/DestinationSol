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
package org.destinationsol.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.ship.SolShip;

public class MountDetectDrawer implements UpdateAwareSystem{
    private final Vector2 myNePos;
    private final TextureAtlas.AtlasRegion myTexture;

    private boolean myShouldDraw;
    private float myBaseRad;
    private float myAnimPercentage;
    private float myAngle;

    public MountDetectDrawer() {
        myNePos = new Vector2();
        myTexture = Assets.getAtlasRegion("engine:targetDetected");
    }

    @Override
    public void update(SolGame game, float timeStep) {
        myShouldDraw = false;
        myAnimPercentage += timeStep / 2f;
        if (myAnimPercentage > 1) {
            myAnimPercentage = 0;
        }
        myAngle += 30f * timeStep;
        if (myAngle > 180) {
            myAngle -= 360;
        }
    }

    public void setNe(SolShip ne) {
        myNePos.set(ne.getPosition());
        myBaseRad = ne.getHull().config.getApproxRadius();
        myShouldDraw = true;
    }

    public void draw(GameDrawer drawer) {
        if (!myShouldDraw) {
            return;
        }
        float radPercentage = myAnimPercentage * 2;
        if (radPercentage > 1) {
            radPercentage = 2 - radPercentage;
        }
        float rad = myBaseRad * (1 + .5f * radPercentage);
        drawer.draw(myTexture, rad * 2, rad * 2, rad, rad, myNePos.x, myNePos.y, myAngle, SolColor.WHITE);
    }
}
