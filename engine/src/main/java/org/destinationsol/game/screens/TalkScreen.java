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
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.responsiveUi.UiActionButton;
import org.destinationsol.ui.responsiveUi.UiElement;
import org.destinationsol.ui.responsiveUi.UiNoneElement;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;
import org.destinationsol.ui.responsiveUi.UiTextBox;
import org.destinationsol.ui.responsiveUi.UiVerticalListLayout;

//TODO quickFix to at least display some buttons. Does not actually work yet.
public class TalkScreen extends SolUiBaseScreen {
    public static final float MAX_TALK_DIST = 1f;

    private SolShip target;
    private UiElement hiddenRoot;

    @Override
    public void onAdd(SolApplication solApplication) {
        final UiVerticalListLayout verticalListLayout = new UiVerticalListLayout()
                .addElement(new UiActionButton()
                        .addElement(new UiTextBox().setText("Sell").setFontSize(FontSize.MENU))
                        .setAction(uiElement -> {
                            InventoryScreen inventoryScreen = solApplication.getGame().getScreens().inventoryScreen;
                            inventoryScreen.setOperations(new SellItems());
                            SolApplication.getInputManager().addScreen(inventoryScreen);
                        }))
                .addElement(new UiActionButton()
                        .addElement(new UiTextBox().setText("Buy").setFontSize(FontSize.MENU))
                        .setAction(uiElement -> {
                                    InventoryScreen inventoryScreen = solApplication.getGame().getScreens().inventoryScreen;
                                    inventoryScreen.setOperations(new BuyItemsScreen());
                                    SolApplication.getInputManager().addScreen(inventoryScreen);
                        }))
                .addElement(new UiActionButton()
                        .addElement(new UiTextBox().setText("Change ship").setFontSize(FontSize.MENU))
                        .setAction(uiElement -> {
                                    InventoryScreen inventoryScreen = solApplication.getGame().getScreens().inventoryScreen;
                                    inventoryScreen.setOperations(new ChangeShipScreen());
                                    SolApplication.getInputManager().addScreen(inventoryScreen);
                        }))
                .addElement(new UiActionButton()
                        .addElement(new UiTextBox().setText("Hire").setFontSize(FontSize.MENU))
                        .setAction(uiElement -> {
                            InventoryScreen inventoryScreen = solApplication.getGame().getScreens().inventoryScreen;
                            inventoryScreen.setOperations(new HireShipsScreen());
                            SolApplication.getInputManager().addScreen(inventoryScreen);
                        }));
        rootUiElement = new UiRelativeLayout()
                .addElement(verticalListLayout, UiDrawer.UI_POSITION_BOTTOM, 0, -verticalListLayout.getHeight() / 2 - 10);

        hiddenRoot = null;
    }

    public SolShip getTarget() {
        return target;
    }

    public void setTarget(SolShip target) {
        this.target = target;
    }

    //HACK: There should be a better way of doing this
    public void setHidden(boolean value) {
        if (!value && hiddenRoot == null) {
            hiddenRoot = rootUiElement;
            rootUiElement = new UiNoneElement();
        } else {
            if (hiddenRoot != null) {
                rootUiElement = hiddenRoot;
                hiddenRoot = null;
            }
        }
    }
}
