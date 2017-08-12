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
package org.destinationsol.ui;

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.GameOptions;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.screens.InventoryScreen;
import org.destinationsol.game.screens.MainScreen;
import org.destinationsol.game.screens.ShipKbControl;
import org.destinationsol.game.screens.ShipMixedControl;

import java.util.ArrayList;
import java.util.List;

public class TutorialManager {
    private final Rectangle myBg;
    private final ArrayList<Step> mySteps;

    private int myStepIdx;

    public TutorialManager(float r, GameScreens screens, boolean mobile, GameOptions gameOptions, SolGame game) {
        float bgW = r * .5f;
        float bgH = .2f;
        myBg = new Rectangle(r / 2 - bgW / 2, 1 - bgH, bgW, bgH);
        mySteps = new ArrayList<>();
        myStepIdx = 0;

        MainScreen main = screens.mainScreen;
        boolean mouseCtrl = main.shipControl instanceof ShipMixedControl;
        SolUiControl shootCtrl;
        String shootKey;
        String shootKey2;
        SolUiControl upCtrl;
        SolUiControl leftCtrl;
        SolUiControl abilityCtrl;
        if (mouseCtrl) {
            ShipMixedControl mixedControl = (ShipMixedControl) main.shipControl;
            shootCtrl = mixedControl.shootCtrl;
            shootKey = "(LEFT mouse button)";
            shootKey2 = "(Click LEFT mouse button)";
            upCtrl = mixedControl.upCtrl;
            leftCtrl = null;
            abilityCtrl = mixedControl.abilityCtrl;
        } else {
            ShipKbControl kbControl = (ShipKbControl) main.shipControl;
            shootCtrl = kbControl.shootCtrl;
            upCtrl = kbControl.upCtrl;
            leftCtrl = kbControl.leftCtrl;
            abilityCtrl = kbControl.abilityCtrl;
            if (mobile) {
                shootKey = "(GUN 1 button)";
                shootKey2 = "(Press GUN 1 button)";
            } else {
                shootKey = "(" + gameOptions.getKeyShootName() + " key)";
                shootKey2 = "(Press " + gameOptions.getKeyShootName() + " key)";
            }
        }

        addStep("Hi! Shoot your main gun\n" + shootKey, shootCtrl);

        if (leftCtrl != null) {
            if (mobile) {
                addStep("Great! Turn left.\nDon't fly away yet!", leftCtrl);
            } else {
                addStep("Great! Turn left (" + gameOptions.getKeyLeftName() + " key). \nDon't fly away yet!", leftCtrl);
            }
        }

        if (mobile) {
            addStep("Have a look at the map", main.mapControl, true);
        } else {
            addStep("Have a look at the map\n(" + gameOptions.getKeyMapName() + " key)", main.mapControl, true);
        }

        if (mouseCtrl) {
            addStep("Zoom in the map\n(mouse wheel UP)", screens.mapScreen.zoomInControl);
        } else if (mobile) {
            addStep("Zoom in the map", screens.mapScreen.zoomInControl);
        } else {
            addStep("Zoom in the map\n(" + gameOptions.getKeyZoomInName() + " key)", screens.mapScreen.zoomInControl);
        }

        if (mobile) {
            addStep("Close the map", screens.mapScreen.closeControl, true);
        } else {
            addStep("Close the map\n(" + gameOptions.getKeyMapName() + " or " + gameOptions.getKeyCloseName() + " keys)",
                    screens.mapScreen.closeControl, true);
        }

        if (mouseCtrl || mobile) {
            addStep("Have a look\nat your inventory", main.inventoryControl, true);
        } else {
            addStep("Have a look\nat your inventory (" + gameOptions.getKeyInventoryName() + " key)", main.inventoryControl, true);
        }

        if (mouseCtrl || mobile) {
            addStep("In the inventory,\nselect the second row", screens.inventoryScreen.itemControls[1]);
        } else {
            addStep("In the inventory,\nselect the next item (" + gameOptions.getKeyDownName() + " key)",
                    screens.inventoryScreen.downControl);
        }

        if (mouseCtrl || mobile) {
            addStep("Go to the next page", screens.inventoryScreen.nextControl, true);
        } else {
            addStep("Go to the next page\n(" + gameOptions.getKeyRightName() + " key)", screens.inventoryScreen.nextControl, true);
        }

        if (mouseCtrl || mobile) {
            addStep("Throw away some item\nyou don't use", screens.inventoryScreen.showInventory.dropControl);
        } else {
            addStep("Throw away some item\nyou don't use (" + gameOptions.getKeyDropName() + " key)",
                    screens.inventoryScreen.showInventory.dropControl);
        }

        // Extra step to make sure an equipped item is selected before asking player to unequip
        if (screens.inventoryScreen.getSelectedItem() == null ||
            (screens.inventoryScreen.getSelectedItem() != null && screens.inventoryScreen.getSelectedItem().isEquipped() == 0)) {
            addStep(new SelectEquippedItemStep(
                    "Select an equipped item\n(note the text \"using\")", screens.inventoryScreen, game));
        }

        if (mobile) {
            addStep("Unequip the item\nthat is used now", screens.inventoryScreen.showInventory.eq1Control);
        } else {
            addStep("Unequip the item\nthat is used now (" + gameOptions.getKeyEquipName() + " key)",
                    screens.inventoryScreen.showInventory.eq1Control);
        }

        if (mobile) {
            addStep("Now equip it again", screens.inventoryScreen.showInventory.eq1Control);
        } else {
            addStep("Now equip it again\n(" + gameOptions.getKeyEquipName() + " key)", screens.inventoryScreen.showInventory.eq1Control);
        }

        if (mobile) {
            addStep("Close the inventory\n(Touch the screen outside inventory)", screens.inventoryScreen.closeControl, true);
        } else {
            addStep("Close the inventory (" + gameOptions.getKeyCloseName() + " key)", screens.inventoryScreen.closeControl, true);
        }

        if (mouseCtrl) {
            addStep("Move forward (" + gameOptions.getKeyUpMouseName() + " key).\nThere's no stop!", upCtrl);
        } else if (mobile) {
            addStep("Move forward.\nThere's no stop!", upCtrl);
        } else {
            addStep("Move forward (" + gameOptions.getKeyUpName() + " key).\nThere's no stop!", upCtrl);
        }

        if (mobile) {
            addStep("Fly closer to the station\nand talk with it", main.talkControl, true);
        } else {
            addStep("Fly closer to the station\nand talk with it (" + gameOptions.getKeyTalkName() + " key)", main.talkControl, true);
        }

        if (mouseCtrl || mobile) {
            addStep("See what there is to buy", screens.talkScreen.buyControl, true);
        } else {
            addStep("See what there is to buy\n(" + gameOptions.getKeyBuyMenuName() + " key)", screens.talkScreen.buyControl, true);
        }

        if (mobile) {
            addStep("Buy some item", screens.inventoryScreen.buyItems.buyControl);
        } else {
            addStep("Buy some item\n(" + gameOptions.getKeyBuyItemName() + " key)", screens.inventoryScreen.buyItems.buyControl);
        }

        if (mobile) {
            addStep("Close the Buy screen\n(Touch the screen outside inventory)", screens.inventoryScreen.closeControl, true);
        } else {
            addStep("Close the Buy screen\n(" + gameOptions.getKeyCloseName() + " key)", screens.inventoryScreen.closeControl, true);
        }

        if (mobile) {
            addStep("Close the Talk screen\n(Touch the screen outside inventory)", screens.talkScreen.closeControl, true);
        } else {
            addStep("Close the Talk screen\n(" + gameOptions.getKeyCloseName() + " key)", screens.talkScreen.closeControl, true);
        }

        if (mouseCtrl) {
            addStep("Use the ability of your ship\n(MIDDLE mouse button or " + gameOptions.getKeyAbilityName() + " key)",
                    abilityCtrl, true);
        } else if (mobile) {
            addStep("Use the ability of your ship", abilityCtrl, true);
        } else {
            addStep("Use the ability of your ship\n(" + gameOptions.getKeyAbilityName() + " key)", abilityCtrl, true);
        }

        addStep("Here's a couple of hints...\n" + shootKey2, shootCtrl);
        addStep("Enemies are orange icons, allies are blue\n" + shootKey2, shootCtrl);
        addStep("Avoid enemies with skull icon\n" + shootKey2, shootCtrl);
        addStep("To repair, have repair kits and just stay idle\n" + shootKey2, shootCtrl);
        addStep("Destroy asteroids to find money\n" + shootKey2, shootCtrl);
        addStep("Find or buy shields, armor, guns; equip them\n" + shootKey2, shootCtrl);
        addStep("Buy new ships, hire mercenaries\n" + shootKey2, shootCtrl);
        addStep("Tutorial is complete and will exit now!\n" + shootKey2, shootCtrl);
    }

    private void addStep(String text, SolUiControl ctrl) {
        addStep(text, ctrl, false);
    }

    private void addStep(String text, SolUiControl ctrl, boolean checkOn) {
        mySteps.add(new Step(text, ctrl, checkOn));
    }

    private void addStep(Step step) {
        mySteps.add(step);
    }

    public void update() {
        Step step = mySteps.get(myStepIdx);
        step.highlight();
        if (step.canProgressToNextStep()) {
            myStepIdx++;
        }
    }

    public void draw(UiDrawer uiDrawer) {
        if (isFinished()) {
            return;
        }
        Step step = mySteps.get(myStepIdx);
        uiDrawer.draw(myBg, SolColor.UI_BG_LIGHT);
        uiDrawer.drawLine(myBg.x, myBg.y, 0, myBg.width, SolColor.WHITE);
        uiDrawer.drawLine(myBg.x + myBg.width, myBg.y, 90, myBg.height, SolColor.WHITE);
        uiDrawer.drawLine(myBg.x, myBg.y, 90, myBg.height, SolColor.WHITE);
        uiDrawer.drawString(step.text, uiDrawer.r / 2, myBg.y + myBg.height / 2, FontSize.TUT, true, SolColor.WHITE);
    }

    public boolean isFinished() {
        return myStepIdx == mySteps.size();
    }

    public static class Step {
        public final String text;
        public final SolUiControl ctrl;
        public final boolean checkOn;

        public Step(String text, SolUiControl ctrl, boolean checkOn) {
            this.text = text;
            this.ctrl = ctrl;
            this.checkOn = checkOn;
        }

        // highlight control that needs to be pressed
        public void highlight() {
            if (ctrl != null) {
                ctrl.enableWarn();
            }
        }

        public boolean canProgressToNextStep() {
            if (checkOn) {
                return ctrl.isOn();
            } else {
                return ctrl.isJustOff();
            }
        }
    }

    public static class SelectEquippedItemStep extends Step {
        InventoryScreen inventoryScreen;
        SolGame game;

        public SelectEquippedItemStep(String text, InventoryScreen inventoryScreen, SolGame game) {
            super(text, null, true);
            this.inventoryScreen = inventoryScreen;
            this.game = game;
        }

        @Override
        public boolean canProgressToNextStep() {
            SolItem selected = inventoryScreen.getSelectedItem();
            if (selected != null && selected.isEquipped() != 0) {
                return true;
            }
            return false;
        }

        // Highlight all equipped items on opened inventory page
        @Override
        public void highlight() {
            List<SolUiControl> equippedItemControls = inventoryScreen.getEquippedItemUIControlsForTutorial(game);
            for (SolUiControl control : equippedItemControls) {
                control.enableWarn();
            }
        }
    }
}
