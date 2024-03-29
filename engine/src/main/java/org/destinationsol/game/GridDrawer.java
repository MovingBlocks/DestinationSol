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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolColor;

import javax.inject.Inject;

public class GridDrawer {
    private final SolCam solCam;
    @Inject
    public GridDrawer(SolCam solCam) {
        this.solCam = solCam;
    }

    public void draw(GameDrawer drawer, SolGame game, float gridSz, TextureAtlas.AtlasRegion tex) {

        float lw = 4 * solCam.getRealLineWidth();
        Vector2 camPos = solCam.getPosition().cpy().add(game.getMapDrawer().getMapDrawPositionAdditive());
        float viewDist = solCam.getViewDistance(solCam.getRealZoom());
        float x = (int) ((camPos.x - viewDist) / gridSz) * gridSz;
        float y = (int) ((camPos.y - viewDist) / gridSz) * gridSz;
        int count = (int) (viewDist * 2 / gridSz);
        Color col = SolColor.UI_INACTIVE;
        for (int i = 0; i < count; i++) {
            drawer.draw(tex, lw, viewDist * 2, lw / 2, 0, x, y, 0, col);
            drawer.draw(tex, lw, viewDist * 2, lw / 2, 0, x, y, 90, col);
            drawer.draw(tex, lw, viewDist * 2, lw / 2, 0, x, y, 180, col);
            drawer.draw(tex, lw, viewDist * 2, lw / 2, 0, x, y, -90, col);
            x += gridSz;
            y += gridSz;
        }
    }
}
