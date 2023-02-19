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

import org.destinationsol.SolApplication;
import org.destinationsol.game.Hero;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.nui.backends.libgdx.GDXInputUtil;

import javax.inject.Inject;

/**
 * The talk screen allows the player to perform actions at a station.
 * At the moment, the player can buy and sell items from here, purchase new ships or hire mercenaries.
 */
public class TalkScreen extends NUIScreenLayer {
    public static final float MAX_TALK_DIST = 1f;
    private final SolApplication solApplication;
    private UIWarnButton sellButton;
    private UIWarnButton buyButton;
    private KeyActivatedButton changeShipButton;
    private UIWarnButton hireButton;
    private SolShip target;

    @Inject
    public TalkScreen(SolApplication solApplication) {
        this.solApplication = solApplication;
    }

    @Override
    public void initialise() {
        sellButton = find("sellButton", UIWarnButton.class);
        sellButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeySellMenu()));
        sellButton.subscribe(button -> {
            InventoryScreen inventoryScreen = solApplication.getGame().getScreens().inventoryScreen;
            inventoryScreen.setOperations(inventoryScreen.getSellItems());
            nuiManager.removeScreen(this);
            nuiManager.pushScreen(inventoryScreen);
        });

        buyButton = find("buyButton", UIWarnButton.class);
        buyButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyBuyMenu()));
        buyButton.subscribe(button -> {
            InventoryScreen inventoryScreen = solApplication.getGame().getScreens().inventoryScreen;
            inventoryScreen.setOperations(inventoryScreen.getBuyItemsScreen());
            nuiManager.removeScreen(this);
            nuiManager.pushScreen(inventoryScreen);
        });

        changeShipButton = find("changeShipButton", KeyActivatedButton.class);
        changeShipButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyChangeShipMenu()));
        changeShipButton.subscribe(button -> {
            InventoryScreen inventoryScreen = solApplication.getGame().getScreens().inventoryScreen;
            inventoryScreen.setOperations(inventoryScreen.getChangeShipScreen());
            nuiManager.removeScreen(this);
            nuiManager.pushScreen(inventoryScreen);
        });

        hireButton = find("hireButton", UIWarnButton.class);
        hireButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyHireShipMenu()));
        hireButton.subscribe(button -> {
            InventoryScreen inventoryScreen = solApplication.getGame().getScreens().inventoryScreen;
            inventoryScreen.setOperations(inventoryScreen.getHireShipsScreen());
            nuiManager.removeScreen(this);
            nuiManager.pushScreen(inventoryScreen);
        });

        KeyActivatedButton closeButton = find("closeButton", KeyActivatedButton.class);
        closeButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyClose()));
        closeButton.subscribe(button -> {
            nuiManager.removeScreen(this);
        });
    }

    @Override
    public void onAdded() {
        boolean isStation = target.getHull().config.getType() == HullConfig.Type.STATION;
        changeShipButton.setEnabled(isStation);
        hireButton.setEnabled(isStation);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (isTargetFar(solApplication.getGame().getHero())) {
            nuiManager.removeScreen(this);
        }
    }

    /**
     * Returns the button pressed to open the buy items screen.
     *
     * This is exposed directly for use in the tutorial.
     * @return the buy items button
     */
    public UIWarnButton getBuyButton() {
        return buyButton;
    }

    /**
     * Returns the button pressed to open the sell items screen.
     *
     * This is exposed directly for use in the tutorial.
     * @return the buy items button
     */
    public UIWarnButton getSellButton() {
        return sellButton;
    }

    public UIWarnButton getHireButton() {
        return hireButton;
    }

    /**
     * Returns the current ship being talked to.
     * @return the current ship being talked to
     */
    public SolShip getTarget() {
        return target;
    }

    /**
     * Assigns the ship to talk to
     * @param target the ship to talk to
     */
    public void setTarget(SolShip target) {
        this.target = target;
    }

    /**
     * Returns true if the target is within communicating range.
     * @return true, if the target is within communicating range, otherwise false
     */
    public boolean isTargetFar(Hero hero) {
        if (hero.isTranscendent() || target == null || target.getLife() <= 0) {
            return true;
        }

        float distance = target.getPosition().dst(hero.getPosition()) - hero.getHull().config.getApproxRadius() - target.getHull().config.getApproxRadius();

        return (MAX_TALK_DIST < distance);
    }
}
