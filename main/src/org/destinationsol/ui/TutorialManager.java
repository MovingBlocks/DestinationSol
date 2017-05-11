/*
 * Copyright 2015 MovingBlocks
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
        mySteps = new ArrayList<Step>();
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

        s("Hi! Shoot your main gun\n" + shootKey, shootCtrl);

        if (leftCtrl != null) {
            if (mobile) {
                s("Great! Turn left.\nDon't fly away yet!", leftCtrl);
            } else {
                s("Great! Turn left (" + gameOptions.getKeyLeftName() + " key). \nDon't fly away yet!", leftCtrl);
            }
        }

        if (mobile) {
            s("Have a look at the map", main.mapCtrl, true);
        } else {
            s("Have a look at the map\n(" + gameOptions.getKeyMapName() + " key)", main.mapCtrl, true);
        }

        if (mouseCtrl) {
            s("Zoom in the map\n(mouse wheel UP)", screens.mapScreen.zoomInCtrl);
        } else if (mobile) {
            s("Zoom in the map", screens.mapScreen.zoomInCtrl);
        } else {
            s("Zoom in the map\n(" + gameOptions.getKeyZoomInName() + " key)", screens.mapScreen.zoomInCtrl);
        }

        if (mobile) {
            s("Close the map", screens.mapScreen.closeCtrl, true);
        } else {
            s("Close the map\n(" + gameOptions.getKeyMapName() + " or " + gameOptions.getKeyCloseName() + " keys)",
                    screens.mapScreen.closeCtrl, true);
        }

        if (mouseCtrl || mobile) {
            s("Have a look\nat your inventory", main.invCtrl, true);
        } else {
            s("Have a look\nat your inventory (" + gameOptions.getKeyInventoryName() + " key)", main.invCtrl, true);
        }

        if (mouseCtrl || mobile) {
            s("In the inventory,\nselect the second row", screens.inventoryScreen.itemCtrls[1]);
        } else {
            s("In the inventory,\nselect the next item (" + gameOptions.getKeyDownName() + " key)",
                    screens.inventoryScreen.downCtrl);
        }

        if (mouseCtrl || mobile) {
            s("Go to the next page", screens.inventoryScreen.nextCtrl, true);
        } else {
            s("Go to the next page\n(" + gameOptions.getKeyRightName() + " key)", screens.inventoryScreen.nextCtrl, true);
        }

        if (mouseCtrl || mobile) {
            s("Throw away some item\nyou don't use", screens.inventoryScreen.showInventory.dropCtrl);
        } else {
            s("Throw away some item\nyou don't use (" + gameOptions.getKeyDropName() + " key)",
                    screens.inventoryScreen.showInventory.dropCtrl);
        }

        // Extra step to make sure an equipped item is selected before asking player to unequip
        if (screens.inventoryScreen.getSelectedItem() == null ||
            (screens.inventoryScreen.getSelectedItem() != null && screens.inventoryScreen.getSelectedItem().isEquipped() == 0)) {
            s(new SelectEquippedItemStep(
                    "Select an equipped item\n(note the text \"using\")", screens.inventoryScreen, game));
        }

        if (mobile) {
            s("Unequip the item\nthat is used now", screens.inventoryScreen.showInventory.eq1Ctrl);
        } else {
            s("Unequip the item\nthat is used now (" + gameOptions.getKeyEquipName() + " key)",
                    screens.inventoryScreen.showInventory.eq1Ctrl);
        }

        if (mobile) {
            s("Now equip it again", screens.inventoryScreen.showInventory.eq1Ctrl);
        } else {
            s("Now equip it again\n(" + gameOptions.getKeyEquipName() + " key)", screens.inventoryScreen.showInventory.eq1Ctrl);
        }

        if (mobile) {
            s("Close the inventory\n(Touch the screen outside inventory)", screens.inventoryScreen.closeCtrl, true);
        } else {
            s("Close the inventory (" + gameOptions.getKeyCloseName() + " key)", screens.inventoryScreen.closeCtrl, true);
        }

        if (mouseCtrl) {
            s("Move forward (" + gameOptions.getKeyUpMouseName() + " key).\nThere's no stop!", upCtrl);
        } else if (mobile) {
            s("Move forward.\nThere's no stop!", upCtrl);
        } else {
            s("Move forward (" + gameOptions.getKeyUpName() + " key).\nThere's no stop!", upCtrl);
        }

        if (mobile) {
            s("Fly closer to the station\nand talk with it", main.talkCtrl, true);
        } else {
            s("Fly closer to the station\nand talk with it (" + gameOptions.getKeyTalkName() + " key)", main.talkCtrl, true);
        }

        if (mouseCtrl || mobile) {
            s("See what there is to buy", screens.talkScreen.buyCtrl, true);
        } else {
            s("See what there is to buy\n(" + gameOptions.getKeyBuyMenuName() + " key)", screens.talkScreen.buyCtrl, true);
        }

        if (mobile) {
            s("Buy some item", screens.inventoryScreen.buyItems.buyCtrl);
        } else {
            s("Buy some item\n(" + gameOptions.getKeyBuyItemName() + " key)", screens.inventoryScreen.buyItems.buyCtrl);
        }

        if (mobile) {
            s("Close the Buy screen\n(Touch the screen outside inventory)", screens.inventoryScreen.closeCtrl, true);
        } else {
            s("Close the Buy screen\n(" + gameOptions.getKeyCloseName() + " key)", screens.inventoryScreen.closeCtrl, true);
        }

        if (mobile) {
            s("Close the Talk screen\n(Touch the screen outside inventory)", screens.talkScreen.closeCtrl, true);
        } else {
            s("Close the Talk screen\n(" + gameOptions.getKeyCloseName() + " key)", screens.talkScreen.closeCtrl, true);
        }

        if (mouseCtrl) {
            s("Use the ability of your ship\n(MIDDLE mouse button or " + gameOptions.getKeyAbilityName() + " key)",
                    abilityCtrl, true);
        } else if (mobile) {
            s("Use the ability of your ship", abilityCtrl, true);
        } else {
            s("Use the ability of your ship\n(" + gameOptions.getKeyAbilityName() + " key)", abilityCtrl, true);
        }

        s("Here's a couple of hints...\n" + shootKey2, shootCtrl);
        s("Enemies are orange icons, allies are blue\n" + shootKey2, shootCtrl);
        s("Avoid enemies with skull icon\n" + shootKey2, shootCtrl);
        s("To repair, have repair kits and just stay idle\n" + shootKey2, shootCtrl);
        s("Destroy asteroids to find money\n" + shootKey2, shootCtrl);
        s("Find or buy shields, armor, guns; equip them\n" + shootKey2, shootCtrl);
        s("Buy new ships, hire mercenaries\n" + shootKey2, shootCtrl);
        s("Tutorial is complete and will exit now!\n" + shootKey2, shootCtrl);
    }

    private void s(String text, SolUiControl ctrl) {
        s(text, ctrl, false);
    }

    private void s(String text, SolUiControl ctrl, boolean checkOn) {
        mySteps.add(new Step(text, ctrl, checkOn));
    }

    private void s(Step step) {
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
        uiDrawer.draw(myBg, SolColor.UI_BG);
        uiDrawer.drawString(step.text, uiDrawer.r / 2, myBg.y + myBg.height / 2, FontSize.TUT, true, SolColor.W);
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
