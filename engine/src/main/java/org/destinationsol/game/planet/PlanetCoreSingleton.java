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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;

public class PlanetCoreSingleton {
    private final TextureAtlas.AtlasRegion texture;

    PlanetCoreSingleton() {
        texture = Assets.getAtlasRegion("engine:planetStarCommonPlanetCore");
    }

    public void draw(SolGame game, GameDrawer drawer) {
        SolCam cam = game.getCam();
        Vector2 camPosition = cam.getPosition();
        Planet nearestPlanet = game.getPlanetManager().getNearestPlanet();
        Vector2 planetPosition = nearestPlanet.getPosition();
        float distanceToCam = camPosition.dst(planetPosition);
        float viewDistance = cam.getViewDistance();
        float minGroundHeight = nearestPlanet.getMinGroundHeight();
        if (distanceToCam < minGroundHeight + viewDistance) {
            drawer.draw(texture, minGroundHeight * 2, minGroundHeight * 2, minGroundHeight, minGroundHeight, planetPosition.x, planetPosition.y, nearestPlanet.getAngle(), SolColor.WHITE);
        }
    }
}
