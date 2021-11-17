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
package org.destinationsol.ui.nui.screens;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.In;
import org.destinationsol.game.FactionManager;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.input.ButtonState;
import org.terasology.input.Keyboard;
import org.terasology.nui.AbstractWidget;
import org.terasology.nui.UIWidget;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.events.NUIMouseButtonEvent;

import java.util.List;

/**
 * The main HUD screen displayed when in-game. This screen is responsible for the menu buttons shown
 * on the right-hand side of the UI. Through it, the menu, map, current ship inventory, communications UI
 * and mercenaries UI can be accessed.
 */
public class MainGameScreen extends NUIScreenLayer {
    private SolShip talkTarget;
    private UIWarnButton menuButton;
    private UIWarnButton mapButton;
    private UIWarnButton inventoryButton;
    private UIWarnButton talkButton;
    private UIWarnButton mercsButton;
    private ConsoleScreen consoleScreen;

    @In
    private SolApplication solApplication;

    @Override
    public void initialise() {
        consoleScreen = (ConsoleScreen) nuiManager.createScreen("engine:console");

        GameOptions gameOptions = solApplication.getOptions();

        menuButton = find("menuButton", UIWarnButton.class);
        menuButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyMenu()));
        menuButton.subscribe(this::onMenuButtonClicked);

        mapButton = find("mapButton", UIWarnButton.class);
        mapButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyMap()));
        mapButton.subscribe(this::onMapButtonClicked);

        inventoryButton = find("itemsButton", UIWarnButton.class);
        inventoryButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyInventory()));
        inventoryButton.subscribe(this::onItemsButtonClicked);

        talkButton = find("talkButton", UIWarnButton.class);
        talkButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyTalk()));
        talkButton.subscribe(this::onTalkButtonClicked);

        mercsButton = find("mercsButton", UIWarnButton.class);
        mercsButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyMercenaryInteraction()));
        mercsButton.subscribe(this::onMercsButtonClicked);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();
        if (!nuiManager.hasScreen(gameScreens.menuScreen) &&
                !solInputManager.isScreenOn(gameScreens.mapScreen)) {
            ((AbstractWidget) contents).setVisible(true);
        } else {
            ((AbstractWidget) contents).setVisible(false);
        }

        if (solInputManager.getTopScreen() != gameScreens.mainGameScreen) {
            // User is in an original UI menu, so disable the escape key toggling the pause menu.
            menuButton.setKey(Keyboard.Key.NONE);
        } else {
            menuButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyMenu()));
        }

        // NOTE: Copied directly from the original MainGameScreen class. The logic hasn't been changed
        //       but some variables have been re-named to be more descriptive.
        SolGame game = solApplication.getGame();
        Hero hero = game.getHero();

        if (hero.isNonTranscendent() && !nuiManager.hasScreen(gameScreens.inventoryScreen)) {
            if (hero.getItemContainer().hasNew()) {
                inventoryButton.enableWarn();
            }

            if (hero.getMercs().hasNew()) {
                mercsButton.enableWarn();
            }
        }

        mercsButton.setEnabled(hero.isNonTranscendent());
        if (hero.isTranscendent()) {
            talkButton.setEnabled(false);
            return;
        }

        FactionManager factionManager = game.getFactionMan();

        // The ship can only communicate if there is a friendly target in range.
        talkTarget = null;
        float minDist = TalkScreen.MAX_TALK_DIST;
        float heroApproxRadius = hero.getHull().config.getApproxRadius();
        List<SolObject> objs = game.getObjectManager().getObjects();
        for (SolObject obj : objs) {
            if (!(obj instanceof SolShip)) {
                continue;
            }
            SolShip ship = (SolShip) obj;
            if (factionManager.areEnemies(hero.getShip(), ship)) {
                continue;
            }
            if (ship.getTradeContainer() == null) {
                continue;
            }
            float distance = ship.getPosition().dst(hero.getPosition());
            float shipApproxRadius = ship.getHull().config.getApproxRadius();
            if (minDist < distance - heroApproxRadius - shipApproxRadius) {
                continue;
            }
            talkTarget = ship;
            minDist = distance;
        }
        talkButton.setEnabled(talkTarget != null);

        if (consoleScreen.isConsoleJustClosed()) {
            game.setPaused(false);
        }
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        if (event.getState() == ButtonState.UP &&
                (event.getKey() == Keyboard.Key.GRAVE || event.getKey() == Keyboard.Key.F1) &&
                nuiManager.hasScreen(consoleScreen)) {
            nuiManager.removeScreen(consoleScreen);
            return true;
        }

        if (!contents.isVisible()) {
            return false;
        }

        if (event.getState() == ButtonState.UP &&
                (event.getKey() == Keyboard.Key.GRAVE || event.getKey() == Keyboard.Key.F1)) {
            if (!nuiManager.hasScreen(consoleScreen)) {
                nuiManager.pushScreen(consoleScreen);
                solApplication.getGame().setPaused(true);
            }
            return true;
        }

        return super.onKeyEvent(event);
    }

    @Override
    public void onMouseButtonEvent(NUIMouseButtonEvent event) {
        if (event.getState() == ButtonState.UP) {
            NUIScreenLayer topScreen = nuiManager.getTopScreen();
            if (!solApplication.getInputManager().isMouseOnUi() &&
                    topScreen != MainGameScreen.this && !(topScreen instanceof UIShipControlsScreen)) {
                nuiManager.popScreen();
                event.consume();
            }
        }
    }

    @Override
    public void onRemoved() {
        menuButton.unsubscribe(this::onMenuButtonClicked);
        mapButton.unsubscribe(this::onMapButtonClicked);
        inventoryButton.unsubscribe(this::onItemsButtonClicked);
        talkButton.unsubscribe(this::onTalkButtonClicked);
        mercsButton.unsubscribe(this::onMercsButtonClicked);
    }

    /**
     * Returns the "Menu" UI Button element.
     * @return the "Menu" UI Button
     */
    public UIWarnButton getMenuButton() {
        return menuButton;
    }

    /**
     * Returns the "Map" UI Button element.
     * @return The "Map" UI Button
     */
    public UIWarnButton getMapButton() {
        return mapButton;
    }

    /**
     * Returns the "Items" UI Button element.
     * @return The "Items" UI Button
     */
    public UIWarnButton getInventoryButton() {
        return inventoryButton;
    }

    /**
     * Returns the "Talk" UI Button element.
     * @return The "Talk" UI Button
     */
    public UIWarnButton getTalkButton() {
        return talkButton;
    }

    /**
     * Returns the "Mercs" UI Button element.
     * @return The "Mercs" UI Button
     */
    public UIWarnButton getMercsButton() {
        return mercsButton;
    }

    private void onMenuButtonClicked(UIWidget widget) {
        GameScreens gameScreens = solApplication.getGame().getScreens();

        nuiManager.pushScreen(gameScreens.menuScreen);
    }

    private void onMapButtonClicked(UIWidget widget) {
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();

        solInputManager.setScreen(solApplication, gameScreens.mapScreen);
    }

    private void onItemsButtonClicked(UIWidget widget) {
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();

        solInputManager.setScreen(solApplication, gameScreens.mainGameScreen);
        if (!nuiManager.hasScreen(gameScreens.inventoryScreen)) {
            gameScreens.inventoryScreen.getShowInventory().setTarget(solApplication.getGame().getHero().getShip());
            gameScreens.inventoryScreen.setOperations(gameScreens.inventoryScreen.getShowInventory());
            nuiManager.pushScreen(gameScreens.inventoryScreen);
        } else {
            nuiManager.removeScreen(gameScreens.inventoryScreen);
        }
    }

    private void onTalkButtonClicked(UIWidget widget) {
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();

        solInputManager.setScreen(solApplication, gameScreens.mainGameScreen);
        if (!nuiManager.hasScreen(gameScreens.talkScreen)) {
            gameScreens.talkScreen.setTarget(talkTarget);
            nuiManager.pushScreen(gameScreens.talkScreen);
        } else {
            nuiManager.removeScreen(gameScreens.talkScreen);
        }
    }

    private void onMercsButtonClicked(UIWidget widget) {
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();

        solInputManager.setScreen(solApplication, gameScreens.mainGameScreen);
        if (!nuiManager.hasScreen(gameScreens.inventoryScreen)) {
            gameScreens.inventoryScreen.setOperations(gameScreens.inventoryScreen.getChooseMercenaryScreen());
            nuiManager.pushScreen(gameScreens.inventoryScreen);
            solApplication.getGame().getHero().getMercs().markAllAsSeen();
        } else {
            nuiManager.removeScreen(gameScreens.inventoryScreen);
        }
    }
}
