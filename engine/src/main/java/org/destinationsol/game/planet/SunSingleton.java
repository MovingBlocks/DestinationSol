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
package org.destinationsol.game.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

public class SunSingleton {
    public static final float SUN_HOT_RAD = .75f * Const.SUN_RADIUS;
    public static final float GRAV_CONST = 2000;
    private static final float SUN_DMG = 4f;
    private final TextureAtlas.AtlasRegion myGradTex;
    private final TextureAtlas.AtlasRegion myWhiteTex;
    private final Color myGradTint;
    private final Color myFillTint;

    public SunSingleton() {
        myGradTex = Assets.getAtlasRegion("engine:planetStarCommonGrad");
        myWhiteTex = Assets.getAtlasRegion("engine:planetStarCommonWhiteTex");
        myGradTint = SolColor.col(1, 1);
        myFillTint = SolColor.col(1, 1);
    }

    public void draw(SolGame game, GameDrawer drawer) {
        Vector2 camPos = game.getCam().getPos();
        SolSystem sys = game.getPlanetMan().getNearestSystem(camPos);
        Vector2 toCam = SolMath.getVec(camPos);
        toCam.sub(sys.getPos());
        float toCamLen = toCam.len();
        if (toCamLen < Const.SUN_RADIUS) {
            float closeness = 1 - toCamLen / Const.SUN_RADIUS;
            myGradTint.a = SolMath.clamp(closeness * 4, 0, 1);
            myFillTint.a = SolMath.clamp((closeness - .25f) * 4, 0, 1);

            float sz = 2 * game.getCam().getViewDist();
            float gradAngle = SolMath.angle(toCam) + 90;
            drawer.draw(myWhiteTex, sz * 2, sz * 2, sz, sz, camPos.x, camPos.y, 0, myFillTint);
            drawer.draw(myGradTex, sz * 2, sz * 2, sz, sz, camPos.x, camPos.y, gradAngle, myGradTint);
        }
        SolMath.free(toCam);
    }

    public void doDmg(SolGame game, SolObject obj, float toSys) {
        float dmg = SUN_DMG * game.getTimeStep();
        if (SUN_HOT_RAD < toSys) {
            return;
        }
        obj.receiveDmg(dmg, game, null, DmgType.FIRE);
    }
}
