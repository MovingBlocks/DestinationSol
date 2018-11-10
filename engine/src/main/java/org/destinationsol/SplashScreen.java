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
package org.destinationsol;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.ui.DisplayDimensions;

public class SplashScreen implements ApplicationListener {
    private final CommonDrawer commonDrawer;
    private final TextureAtlas.AtlasRegion loadingScreenBackgroundTex;
    private final TextureAtlas.AtlasRegion loadingScreenTextTex;
    private final DisplayDimensions displayDimensions;
    private static final String LOADING_SCREEN_BACKGROUND_TEX_NAME = "loadScreenBackground.png";
    private static final String LOADING_SCREEN_TEXT_TEX_NAME = "loadScreenText.png";
    private static final float FADE_MIN_ALPHA = 0.25f;
    private static final float FADE_MAX_ALPHA = 0.75f;
    private Color loadFadeColour;
    private boolean fadeDirection = true;

    public SplashScreen(CommonDrawer commonDrawer, DisplayDimensions displayDimensions) {
        this.commonDrawer = commonDrawer;
        this.displayDimensions = displayDimensions;

        Texture loadBackgroundTex = new Texture(Gdx.files.classpath(LOADING_SCREEN_BACKGROUND_TEX_NAME));
        Texture loadTextTex = new Texture(Gdx.files.classpath(LOADING_SCREEN_TEXT_TEX_NAME));
        loadingScreenBackgroundTex = new TextureAtlas.AtlasRegion(loadBackgroundTex, 0, 0, loadBackgroundTex.getWidth(), loadBackgroundTex.getHeight());
        loadingScreenTextTex = new TextureAtlas.AtlasRegion(loadTextTex, 0, 0, loadTextTex.getWidth(), loadTextTex.getHeight());
        loadFadeColour = Color.WHITE;
    }

    public void create() {
    }

    public void resize(int width, int height) {
    }

    public void render() {
        commonDrawer.begin();
        commonDrawer.draw(loadingScreenBackgroundTex, new Rectangle(0, 0, displayDimensions.getWidth(), displayDimensions.getHeight()), Color.WHITE);
        commonDrawer.draw(loadingScreenTextTex, new Rectangle(0, 0, displayDimensions.getWidth(), displayDimensions.getHeight()), loadFadeColour);
        commonDrawer.end();

        if (fadeDirection) {
            loadFadeColour.lerp(1, 1, 1, 0, Gdx.graphics.getDeltaTime());
            fadeDirection = (loadFadeColour.a > FADE_MIN_ALPHA);
        } else {
            loadFadeColour.lerp(1, 1, 1, 1, Gdx.graphics.getDeltaTime());
            fadeDirection = (loadFadeColour.a > FADE_MAX_ALPHA);
        }
    }

    public void pause() {
    }

    public void resume() {
    }

    public void dispose() {
    }
}
