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

package org.destinationsol.game.farBg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolColorUtil;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.planet.Planet;

import java.util.ArrayList;

public class FarBackgroundManagerOld {
    private final TextureAtlas.AtlasRegion nebulaTexture;
    private final ArrayList<FarBackgroundStar> stars = new ArrayList<>();
    private final float nebulaAngle;
    private final Color nebulaTint;

    public FarBackgroundManagerOld() {
        nebulaTexture = Assets.getAtlasRegion("engine:farBgNebulae");
        if (SolRandom.test(.5f)) {
            nebulaTexture.flip(nebulaTexture.isFlipX(), !nebulaTexture.isFlipY());
        }

        for (int i = 0; i < 400; i++) {
            FarBackgroundStar star = new FarBackgroundStar();
            stars.add(star);
        }

        nebulaAngle = SolRandom.randomFloat(180);
        nebulaTint = SolColor.col(.5f, 1);
    }

    public void draw(GameDrawer drawer, SolCam cam, SolGame game) {
        Planet np = game.getPlanetManager().getNearestPlanet();
        Vector2 camPos = cam.getPosition();
        float nebPercentage = (camPos.dst(np.getPosition()) - np.getGroundHeight()) / (4 * Const.ATM_HEIGHT);
        nebPercentage = SolMath.clamp(nebPercentage, 0, 1);
        nebulaTint.a = nebPercentage;

        float vd = cam.getViewDistance();
        drawer.draw(nebulaTexture, vd * 2, vd * 2, vd, vd, camPos.x, camPos.y, nebulaAngle, nebulaTint);
        for (FarBackgroundStar star : stars) {
            star.draw(drawer, vd, camPos, cam.getAngle());
        }
    }

    private static class FarBackgroundStar {
        private final Vector2 myShiftPercentage;
        private final TextureAtlas.AtlasRegion myTexture;
        private final float mySzPercentage;
        private final Color myTint;
        private final Vector2 position;

        private FarBackgroundStar() {
            myShiftPercentage = new Vector2(SolRandom.randomFloat(1), SolRandom.randomFloat(1));
            position = new Vector2();
            boolean small = SolRandom.test(.8f);
            myTexture = Assets.getAtlasRegion("engine:farBgBigStar");
            mySzPercentage = (small ? .01f : .04f) * SolRandom.randomFloat(.5f, 1);
            myTint = new Color();
            SolColorUtil.fromHSB(SolRandom.randomFloat(0, 1), .25f, 1, .7f, myTint);
        }

        public void draw(GameDrawer drawer, float vd, Vector2 camPos, float camAngle) {
            float sz = vd * mySzPercentage;
            position.set(myShiftPercentage).scl(vd).add(camPos);
            drawer.drawAdditive(myTexture, sz, sz, sz / 2, sz / 2, position.x, position.y, camAngle, myTint);
        }
    }
}
