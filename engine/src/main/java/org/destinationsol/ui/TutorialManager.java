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
package org.destinationsol.ui;

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.screens.InventoryScreen;
import org.destinationsol.game.screens.MainGameScreen;
import org.destinationsol.game.screens.ShipMixedControl;
import org.destinationsol.ui.nui.screens.UIShipControlsScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.nui.widgets.UIButton;

import java.util.ArrayList;
import java.util.List;

public class TutorialManager implements UpdateAwareSystem {
    private DisplayDimensions displayDimensions;
    private final Rectangle background;
    private ArrayList<Step> steps;
    private final GameScreens screens;
    private final org.destinationsol.ui.nui.screens.MainGameScreen nuiMain;
    private final boolean mobile;
    private final GameOptions gameOptions;
    private final SolGame game;

    private int stepIndex;

    public TutorialManager(GameScreens screens, org.destinationsol.ui.nui.screens.MainGameScreen nuiMain, boolean mobile, GameOptions gameOptions, SolGame game) {
        this.screens = screens;
        this.nuiMain = nuiMain;
        this.mobile = mobile;
        this.gameOptions = gameOptions;
        this.game = game;

        displayDimensions = SolApplication.displayDimensions;

        float backgroundW = displayDimensions.getRatio() * .5f;
        float backgroundH = .2f;
        background = new Rectangle(displayDimensions.getRatio() / 2 - backgroundW / 2, 1 - backgroundH, backgroundW, backgroundH);
        steps = new ArrayList<>();
        stepIndex = 0;
    }

    public void start() {
        MainGameScreen main = screens.mainGameScreen;
        boolean mouseCtrl = main.shipControl instanceof ShipMixedControl;
        SolUiControl shootCtrl = null;
        String shootKey;
        String shootKey2;
        SolUiControl upCtrl = null;
        SolUiControl abilityCtrl = null;
        UIWarnButton nuiShootCtrl = null;
        UIWarnButton nuiUpCtrl = null;
        UIWarnButton nuiLeftCtrl = null;
        UIWarnButton nuiAbilityCtrl = null;
        if (mouseCtrl) {
            ShipMixedControl mixedControl = (ShipMixedControl) main.shipControl;
            shootCtrl = mixedControl.shootCtrl;
            shootKey = "(LEFT mouse button)";
            shootKey2 = "(Click LEFT mouse button)";
            upCtrl = mixedControl.upCtrl;
            abilityCtrl = mixedControl.abilityCtrl;
        } else {
            UIShipControlsScreen kbControl = (UIShipControlsScreen) main.shipControl;
            nuiShootCtrl = kbControl.getGun1Button();
            nuiUpCtrl = kbControl.getForwardButton();
            nuiLeftCtrl = kbControl.getLeftButton();
            nuiAbilityCtrl = kbControl.getAbilityButton();
            if (mobile) {
                shootKey = "(GUN 1 button)";
                shootKey2 = "(Press GUN 1 button)";
            } else {
                shootKey = "(" + gameOptions.getKeyShootName() + " key)";
                shootKey2 = "(Press " + gameOptions.getKeyShootName() + " key)";
            }
        }

        if (mouseCtrl) {
            addStep("Hi! Shoot your main gun\n" + shootKey, shootCtrl);
        } else {
            addStep("Hi! Shoot your main gun\n" + shootKey, nuiShootCtrl);
        }

        if (nuiLeftCtrl != null) {
            if (mobile) {
                addStep("Great! Turn left.\nDon't fly away yet!", nuiLeftCtrl);
            } else {
                addStep("Great! Turn left (" + gameOptions.getKeyLeftName() + " key). \nDon't fly away yet!", nuiLeftCtrl);
            }
        }

        UIWarnButton mapButton = nuiMain.getMapButton();
        if (mobile) {
            addStep("Have a look at the map", mapButton, true);
        } else {
            addStep("Have a look at the map\n(" + gameOptions.getKeyMapName() + " key)", mapButton, true);
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

        UIWarnButton inventoryButton = nuiMain.getInventoryButton();
        if (mouseCtrl || mobile) {
            addStep("Have a look\nat your inventory", inventoryButton, true);
        } else {
            addStep("Have a look\nat your inventory (" + gameOptions.getKeyInventoryName() + " key)", inventoryButton, true);
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
            addStep("Move forward.\nThere's no stop!", nuiUpCtrl);
        } else {
            addStep("Move forward (" + gameOptions.getKeyUpName() + " key).\nThere's no stop!", nuiUpCtrl);
        }

        UIWarnButton talkButton = nuiMain.getTalkButton();
        if (mobile) {
            addStep("Fly closer to the station\nand talk with it", talkButton, true);
        } else {
            addStep("Fly closer to the station\nand talk with it (" + gameOptions.getKeyTalkName() + " key)", talkButton, true);
        }

        if (mouseCtrl || mobile) {
            addStep("See what there is to buy", screens.talkScreen.buyControl, true);
        } else {
            addStep("See what there is to buy\n(" + gameOptions.getKeyBuyMenuName() + " key)", screens.talkScreen.buyControl, true);
        }

        if (mobile) {
            addStep("Buy some item", screens.inventoryScreen.buyItemsScreen.buyControl);
        } else {
            addStep("Buy some item\n(" + gameOptions.getKeyBuyItemName() + " key)", screens.inventoryScreen.buyItemsScreen.buyControl);
        }

        if (mobile) {
            addStep("Close the Buy screen\n(Touch the screen outside inventory)", screens.inventoryScreen.closeControl, true);
        } else {
            addStep("Close the Buy screen\n(" + gameOptions.getKeyCloseName() + " key)", screens.inventoryScreen.closeControl, true);
        }

        if (mouseCtrl) {
            addStep("Use the ability of your ship\n(MIDDLE mouse button or " + gameOptions.getKeyAbilityName() + " key)",
                    abilityCtrl, true);
        } else if (mobile) {
            addStep("Use the ability of your ship", nuiAbilityCtrl, true);
        } else {
            addStep("Use the ability of your ship\n(" + gameOptions.getKeyAbilityName() + " key)", nuiAbilityCtrl, true);
        }

        if (mouseCtrl) {
            addStep("Here's a couple of hints...\n" + shootKey2, shootCtrl);
            addStep("Enemies are orange icons, allies are blue\n" + shootKey2, shootCtrl);
            addStep("Avoid enemies with skull icon\n" + shootKey2, shootCtrl);
            addStep("To repair, have repair kits and just stay idle\n" + shootKey2, shootCtrl);
            addStep("Destroy asteroids to find money\n" + shootKey2, shootCtrl);
            addStep("Find or buy shields, armor, guns; equip them\n" + shootKey2, shootCtrl);
            addStep("Buy new ships, hire mercenaries\n" + shootKey2, shootCtrl);
            addStep("Tutorial is complete and will exit now!\n" + shootKey2, shootCtrl);
        } else {
            addStep("Here's a couple of hints...\n" + shootKey2, nuiShootCtrl);
            addStep("Enemies are orange icons, allies are blue\n" + shootKey2, nuiShootCtrl);
            addStep("Avoid enemies with skull icon\n" + shootKey2, nuiShootCtrl);
            addStep("To repair, have repair kits and just stay idle\n" + shootKey2, nuiShootCtrl);
            addStep("Destroy asteroids to find money\n" + shootKey2, nuiShootCtrl);
            addStep("Find or buy shields, armor, guns; equip them\n" + shootKey2, nuiShootCtrl);
            addStep("Buy new ships, hire mercenaries\n" + shootKey2, nuiShootCtrl);
            addStep("Tutorial is complete and will exit now!\n" + shootKey2, nuiShootCtrl);
        }

        steps.get(0).start();
    }

    private void addStep(String text, SolUiControl ctrl) {
        addStep(text, ctrl, false);
    }

    private void addStep(String text, UIWarnButton ctrl) {
        addStep(text, ctrl, false);
    }

    private void addStep(String text, SolUiControl ctrl, boolean checkOn) {
        steps.add(new Step(text, ctrl, checkOn));
    }

    private void addStep(String text, UIWarnButton ctrl, boolean checkOn) {
        steps.add(new NuiStep(text, ctrl, checkOn));
    }

    private void addStep(Step step) {
        steps.add(step);
    }

    @Override
    public void update(SolGame game, float timeStep) {
        Step step = steps.get(stepIndex);
        step.highlight();
        if (step.canProgressToNextStep()) {
            stepIndex++;
            if (stepIndex < steps.size()) {
                steps.get(stepIndex).start();
            }
        }
    }

    public void draw(UiDrawer uiDrawer) {
        if (isFinished()) {
            return;
        }
        Step step = steps.get(stepIndex);
        uiDrawer.draw(background, SolColor.UI_BG_LIGHT);
        uiDrawer.drawLine(background.x, background.y, 0, background.width, SolColor.WHITE);
        uiDrawer.drawLine(background.x + background.width, background.y, 90, background.height, SolColor.WHITE);
        uiDrawer.drawLine(background.x, background.y, 90, background.height, SolColor.WHITE);
        uiDrawer.drawString(step.text, displayDimensions.getRatio() / 2, background.y + background.height / 2, FontSize.TUT, true, SolColor.WHITE);
    }

    public boolean isFinished() {
        return stepIndex == steps.size();
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

        public void start() {
            // Empty, as it is only used in NuiStep
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

    public static class NuiStep extends Step {
        public final UIWarnButton nuiCtrl;
        private boolean buttonPressed;

        public NuiStep(String text, UIWarnButton ctrl, boolean checkOn) {
            super(text, null, checkOn);
            nuiCtrl = ctrl;
        }

        @Override
        public void start() {
            nuiCtrl.subscribe(widget -> {
                buttonPressed = true;
            });
        }

        // highlight control that needs to be pressed
        @Override
        public void highlight() {
            if (nuiCtrl != null) {
                nuiCtrl.enableWarn();
            }
        }

        @Override
        public boolean canProgressToNextStep() {
            boolean pressed = buttonPressed;
            buttonPressed = false;
            if (checkOn) {
                // TODO: The following line should work but doesn't currently due a nui-libgdx issue
                //return nuiCtrl.getMode().equals(UIButton.DOWN_MODE);
                return pressed;
            } else {
                return pressed;
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
