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
package org.destinationsol.game.screens;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.SolApplication;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.ShipItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.ShipRepairer;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.Hull;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.screens.TalkScreen;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.widgets.UIButton;

/**
 * This screen allows you to purchase a new ship for the hero.
 * The hero's previous ship will be replaced with the purchased ship and the cost deducted from the hero's money.
 */
public class ChangeShipScreen extends InventoryOperationsScreen {
    private final UIButton[] actionButtons = new UIButton[1];

    @Override
    public void initialise(SolApplication solApplication, InventoryScreen inventoryScreen) {
        KeyActivatedButton changeButton = new KeyActivatedButton();
        changeButton.setText("Change");
        changeButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyChangeShip()));
        changeButton.subscribe(button -> {
            SolGame game = solApplication.getGame();
            Hero hero = game.getHero();
            SolItem selectedItem = inventoryScreen.getSelectedItem();

            hero.setMoney(hero.getMoney() - selectedItem.getPrice());
            changeShip(game, hero, (ShipItem) selectedItem);
        });
        actionButtons[0] = changeButton;
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        return game.getScreens().talkScreen.getTarget().getTradeContainer().getShips();
    }

    @Override
    public String getHeader() {
        return "Ships:";
    }

    @Override
    public UIButton[] getActionButtons() {
        return actionButtons;
    }

    @Override
    public void update(SolApplication solApplication, InventoryScreen inventoryScreen) {
        SolGame game = solApplication.getGame();
        Hero hero = game.getHero();
        TalkScreen talkScreen = game.getScreens().talkScreen;
        if (talkScreen.isTargetFar(hero)) {
            solApplication.getInputManager().setScreen(solApplication, game.getScreens().oldMainGameScreen);
            return;
        }

        UIButton changeButton = actionButtons[0];

        SolItem selItem = inventoryScreen.getSelectedItem();
        if (selItem == null) {
            changeButton.setText("---");
            changeButton.setEnabled(false);
            return;
        }
        boolean enabled = hasMoneyToBuyShip(hero, selItem);
        boolean sameShip = isSameShip(hero, selItem);
        if (enabled && !sameShip) {
            changeButton.setText("Change");
            changeButton.setEnabled(true);
        } else if (enabled && sameShip) {
            changeButton.setText("Have it");
            changeButton.setEnabled(false);
        } else {
            changeButton.setText("---");
            changeButton.setEnabled(false);
        }
    }

    private boolean hasMoneyToBuyShip(Hero hero, SolItem shipToBuy) {
        return hero.getMoney() >= shipToBuy.getPrice();
    }

    private boolean isSameShip(Hero hero, SolItem shipToBuy) {
        if (shipToBuy instanceof ShipItem) {
            ShipItem ship = (ShipItem) shipToBuy;
            HullConfig config1 = ship.getConfig();
            HullConfig config2 = hero.getHull().getHullConfig();
            return config1.equals(config2);
        } else {
            throw new IllegalArgumentException("ChangeShipScreen:isSameShip received " + shipToBuy.getClass() + " argument instead of ShipItem!");
        }
    }

    private void changeShip(SolGame game, Hero hero, ShipItem selected) {
        HullConfig newConfig = selected.getConfig();
        Hull hull = hero.getHull();
        Engine.Config ec = newConfig.getEngineConfig();
        Engine ei = ec == null ? null : ec.exampleEngine.copy();
        Gun g2 = hull.getGun(true);
        SolShip newHero = game.getShipBuilder().build(game, hero.getPosition(), new Vector2(), hero.getAngle(), 0, hero.getPilot(),
                hero.getItemContainer(), newConfig, newConfig.getMaxLife(), hull.getGun(false), g2, null,
                ei, new ShipRepairer(), hero.getMoney(), null, hero.getShield(), hero.getArmor());
        game.getObjectManager().removeObjDelayed(hero.getShip());
        game.getObjectManager().addObjDelayed(newHero);
        game.getHero().setSolShip(newHero, game);
    }
}
