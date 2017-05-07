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

package org.destinationsol.menu;

import com.badlogic.gdx.Input;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class OptionsScreen implements SolUiScreen {
    private final ArrayList<SolUiControl> myControls;
    private final SolUiControl myBackCtrl;
    private final SolUiControl myResoCtrl;
    private final SolUiControl myControlTypeCtrl;
    private final SolUiControl inputMapCtrl;
    private final SolUiControl mySoundVolCtrl;
    private final SolUiControl myMusVolCtrl;

    public OptionsScreen(MenuLayout menuLayout, GameOptions gameOptions) {

        myControls = new ArrayList<SolUiControl>();

        myResoCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true);
        myResoCtrl.setDisplayName("Resolution");
        myControls.add(myResoCtrl);

        myControlTypeCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.C);
        myControlTypeCtrl.setDisplayName("Control Type");
        myControls.add(myControlTypeCtrl);

        inputMapCtrl = new SolUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.M);
        inputMapCtrl.setDisplayName("Controls");
        myControls.add(inputMapCtrl);

        myBackCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        myBackCtrl.setDisplayName("Back");
        myControls.add(myBackCtrl);

        mySoundVolCtrl = new SolUiControl(menuLayout.buttonRect(-1, 0), true);
        mySoundVolCtrl.setDisplayName("Sound Volume");
        myControls.add(mySoundVolCtrl);

        myMusVolCtrl = new SolUiControl(menuLayout.buttonRect(-1, -1), true);
        myMusVolCtrl.setDisplayName("Music Volume");
        myControls.add(myMusVolCtrl);
    }

    @Override
    public List<SolUiControl> getControls() {
        return myControls;
    }

    @Override
    public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
        SolInputManager im = cmp.getInputMan();
        MenuScreens screens = cmp.getMenuScreens();
        GameOptions options = cmp.getOptions();
        if (myResoCtrl.isJustOff()) {
            im.setScreen(cmp, screens.resolutionScreen);
        }

        int ct = cmp.getOptions().controlType;
        String ctName = "Keyboard";
        if (ct == GameOptions.CONTROL_MIXED) ctName = "KB + Mouse";
        if (ct == GameOptions.CONTROL_MOUSE) ctName = "Mouse";
        if (ct == GameOptions.CONTROL_CONTROLLER) ctName = "Controller";
        myControlTypeCtrl.setDisplayName("Input: " + ctName);
        if (myControlTypeCtrl.isJustOff()) {
            cmp.getOptions().advanceControlType(false);
        }
        if (myBackCtrl.isJustOff()) {
            im.setScreen(cmp, screens.main);
        }


        if (inputMapCtrl.isJustOff()) {
            if (ct == GameOptions.CONTROL_MIXED) {
                screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapMixedScreen);
            } else if (ct == GameOptions.CONTROL_KB) {
                screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapKeyboardScreen);
            } else if (ct == GameOptions.CONTROL_CONTROLLER) {
                screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapControllerScreen);
            }
            im.setScreen(cmp, screens.inputMapScreen);
        }

        mySoundVolCtrl.setDisplayName("Sound Volume: " + options.getSFXVolumeAsText());
        if (mySoundVolCtrl.isJustOff()) {
            options.advanceSoundVolMul();
        }
        myMusVolCtrl.setDisplayName("Music Volume: " + options.getMusicVolumeAsText());
        if (myMusVolCtrl.isJustOff()) {
            options.advanceMusicVolMul();
            cmp.getMusicManager().resetVolume(options);
        }
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {

    }

    @Override
    public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {

    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication cmp) {
    }

    @Override
    public boolean reactsToClickOutside() {
        return false;
    }

    @Override
    public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
        return false;
    }

    @Override
    public void onAdd(SolApplication cmp) {

    }

    @Override
    public void blurCustom(SolApplication cmp) {

    }
}
