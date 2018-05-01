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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.DebugCol;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.UiDrawer;

import java.util.HashSet;
import java.util.Set;

public class DrawableDebugger implements UpdateAwareSystem {
    private static final float TEX_SZ = 0.1f;
    private static final float GAP = 0.01f;
    private final Set<TextureAtlas.AtlasRegion> textures;

    public DrawableDebugger() {
        textures = new HashSet<>();
    }

    @Override
    public void update(SolGame game, float timeStep) {
        if (!DebugOptions.TEX_INFO) {
            return;
        }
        maybeCollectTextures(game);
    }

    private void maybeCollectTextures(SolGame game) {
        if (!Gdx.input.isTouched()) {
            return;
        }
        textures.clear();
        Vector2 cursorPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        game.getCam().screenToWorld(cursorPosition);
        game.getDrawableManager().collectTextures(textures, cursorPosition);
    }

    public void draw(UiDrawer uiDrawer) {
        if (!DebugOptions.TEX_INFO) {
            return;
        }
        float y = GAP;
        for (TextureAtlas.AtlasRegion texture : textures) {
            float x = GAP;
            uiDrawer.draw(uiDrawer.whiteTexture, 5 * TEX_SZ, TEX_SZ + 2 * GAP, 0, 0, x, y, 0, SolColor.DG);
            y += GAP;
            x += GAP;
            float dimensionsRatio = 1f * texture.getTexture().getWidth() / texture.getTexture().getHeight();
            float width = dimensionsRatio > 1 ? TEX_SZ : TEX_SZ / dimensionsRatio;
            float height = dimensionsRatio > 1 ? TEX_SZ / dimensionsRatio : TEX_SZ;
            uiDrawer.draw(texture, width, height, width / 2, height / 2, x + 0.5f * TEX_SZ, y + 0.5f * TEX_SZ, 0, SolColor.WHITE);
            x += TEX_SZ + GAP;
            uiDrawer.drawString(texture.name, x, y, FontSize.DEBUG, false, DebugCol.TEX_INFO);
            y += 0.5f * TEX_SZ;
        }
    }
}
