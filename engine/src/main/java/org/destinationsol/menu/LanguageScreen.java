/*
 * Copyright 2019 MovingBlocks
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
package org.destinationsol.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.i18n.Translation;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.UiDrawer;

public class LanguageScreen extends SolUiBaseScreen {
    private final DisplayDimensions displayDimensions;

    private final TextureAtlas.AtlasRegion backgroundTexture;

    private final SolUiControl englishControl;
    private final SolUiControl germanControl;
    private final SolUiControl backControl;

    private String locale;

    LanguageScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        displayDimensions = SolApplication.displayDimensions;

        englishControl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
        englishControl.setDisplayName(Translation.translate("${core:languagemenu#english}"));
        controls.add(englishControl);
        germanControl = new SolUiControl(menuLayout.buttonRect(-1, 3), true);
        germanControl.setDisplayName(Translation.translate("${core:languagemenu#german}"));
        controls.add(germanControl);
        backControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true);
        backControl.setDisplayName(Translation.translate("${core:languagemenu#back}"));
        controls.add(backControl);

        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolInputManager inputManager = solApplication.getInputManager();
        MenuScreens screens = solApplication.getMenuScreens();
        if (englishControl.isJustOff()) {
            locale = "en";
            updateLocale(solApplication);
            inputManager.setScreen(solApplication, screens.languageQuery);
        }
        if (germanControl.isJustOff()) {
            locale = "de";
            updateLocale(solApplication);
            inputManager.setScreen(solApplication, screens.languageQuery);
        }
        if (backControl.isJustOff()) {
            inputManager.setScreen(solApplication, screens.options);
        }
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(backgroundTexture, displayDimensions.getRatio(), 1, displayDimensions.getRatio() / 2, 0.5f, displayDimensions.getRatio() / 2, 0.5f, 0, SolColor.WHITE);
    }

    public void updateLocale(SolApplication solApplication) {
        solApplication.getOptions().setLocale(locale);
        solApplication.getOptions().save();
    }
}
