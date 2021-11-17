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

import org.destinationsol.SolApplication;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.mercenary.MercenaryUtils;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.screens.TalkScreen;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.widgets.UIButton;

public class HireShipsScreen extends InventoryOperationsScreen {
    private final UIButton[] actionButtons = new UIButton[1];

    public HireShipsScreen() {
    }

    @Override
    public void initialise(SolApplication solApplication, InventoryScreen inventoryScreen) {
        KeyActivatedButton hireButton = new KeyActivatedButton();
        hireButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyHireShip()));
        hireButton.subscribe(button -> {
            SolGame game = solApplication.getGame();
            Hero hero = game.getHero();
            SolItem selectedItem = inventoryScreen.getSelectedItem();

            boolean hired = MercenaryUtils.createMerc(game, hero, (MercItem) selectedItem);
            if (hired) {
                hero.setMoney(hero.getMoney() - selectedItem.getPrice());
            }
        });
        actionButtons[0] = hireButton;
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        return game.getScreens().talkScreen.getTarget().getTradeContainer().getMercs();
    }

    @Override
    public String getHeader() {
        return "Mercenaries:";
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
            solApplication.getInputManager().setScreen(solApplication, game.getScreens().mainGameScreen);
            return;
        }

        UIButton hireButton = actionButtons[0];
        SolItem selItem = inventoryScreen.getSelectedItem();
        boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice();
        hireButton.setText(enabled ? "Hire" : "---");
        hireButton.setEnabled(enabled);
    }
}
