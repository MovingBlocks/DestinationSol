/*
 * Copyright 2021 The Terasology Foundation
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
package org.destinationsol.ui.nui.screens.mainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import org.destinationsol.SolApplication;
import org.destinationsol.common.In;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.nui.Canvas;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.layouts.ColumnLayout;
import org.terasology.nui.layouts.relative.RelativeLayout;
import org.terasology.nui.widgets.UIButton;

/**
 * The resolution screen allows the user to set the game's screen resolution, fullscreen modes and NUI scaling settings.
 * It can be accessed via the {@link OptionsScreen}.
 */
public class ResolutionScreen extends NUIScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(ResolutionScreen.class);
    @In
    private SolApplication solApplication;

    @Override
    public void initialise() {
        ColumnLayout menuButtonsLayout = find("menuButtons", ColumnLayout.class);

        UIButton resolutionButton = find("resolutionButton", UIButton.class);
        if (solApplication.isMobile()) {
            menuButtonsLayout.removeWidget(resolutionButton);
        } else {
            resolutionButton.setText(solApplication.getOptions().x + "x" + solApplication.getOptions().y);
            resolutionButton.subscribe(button -> {
                solApplication.getOptions().advanceResolution();
                resolutionButton.setText(solApplication.getOptions().x + "x" + solApplication.getOptions().y);
            });
        }

        UIButton fullScreenButton = find("fullScreenButton", UIButton.class);
        if (solApplication.isMobile()) {
            menuButtonsLayout.removeWidget(fullScreenButton);
        } else {
            fullScreenButton.setText(solApplication.getOptions().fullscreen ? "Fullscreen" : "Windowed");
            fullScreenButton.subscribe(button -> {
                solApplication.getOptions().advanceFullscreen();
                fullScreenButton.setText(solApplication.getOptions().fullscreen ? "Fullscreen" : "Windowed");
            });
        }

        UIButton nuiUIScaleButton = find("nuiUIScaleButton", UIButton.class);
        nuiUIScaleButton.setText("NUI UI Scale: " + solApplication.getOptions().getNuiUiScale());
        nuiUIScaleButton.subscribe(button -> {
            solApplication.getOptions().advanceNuiUiScale();
            nuiManager.setUiScale(solApplication.getOptions().getNuiUiScale());
            nuiUIScaleButton.setText("NUI UI Scale: " + solApplication.getOptions().getNuiUiScale());
        });

        KeyActivatedButton cancelButton = find("cancelButton", KeyActivatedButton.class);
        cancelButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyEscape()));
        cancelButton.subscribe(button -> {
            // Mobile only ever runs at one resolution: the entire screen.
            // Changing the screen resolution does not work.
            if (!solApplication.isMobile()) {
                int width = solApplication.getOptions().x;
                int height = solApplication.getOptions().y;
                if (solApplication.getOptions().fullscreen) {
                    Graphics.DisplayMode mode = null;
                    // HACK: Gdx.graphics.getDisplayMode() always returns the native desktop resolution.
                    //       See https://github.com/libgdx/libgdx/blob/5398d46aa082489052fccfaaaff7440e137ba5dc/backends/gdx-backend-lwjgl/src/com/badlogic/gdx/backends/lwjgl/LwjglGraphics.java#L555
                    //       and http://legacy.lwjgl.org/javadoc/org/lwjgl/opengl/Display.html#getDisplayMode() for more details.
                    for (Graphics.DisplayMode displayMode : Gdx.graphics.getDisplayModes()) {
                        if (displayMode.width == width && displayMode.height == height) {
                            mode = displayMode;
                        }
                    }
                    if (mode != null) {
                        Gdx.graphics.setFullscreenMode(mode);
                        nuiManager.resize(mode.width, mode.height);
                    } else {
                        logger.warn("The resolution {}x{} is not supported in fullscreen mode!", solApplication.getOptions().x, solApplication.getOptions().y);
                    }
                } else {
                    Gdx.graphics.setWindowedMode(width, height);
                    nuiManager.resize(width, height);
                }
            }

            nuiManager.setScreen(solApplication.getMenuScreens().options);
        });
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        solApplication.getMenuBackgroundManager().update();
    }

    @Override
    public void onDraw(Canvas canvas) {
        try (NUIManager.LegacyUiDrawerWrapper wrapper = nuiManager.getLegacyUiDrawer()) {
            solApplication.getMenuBackgroundManager().draw(wrapper.getUiDrawer());
        }

        super.onDraw(canvas);
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }
}
