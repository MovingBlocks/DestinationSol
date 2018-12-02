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
import org.destinationsol.game.MapDrawer;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.responsiveUi.UiHeadlessButton;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;
import org.destinationsol.ui.responsiveUi.UiTextButton;
import org.destinationsol.ui.responsiveUi.UiVerticalListLayout;

import static org.destinationsol.ui.UiDrawer.UI_POSITION_RIGHT;


public class MapScreen extends SolUiBaseScreen {
    private MapDrawer mapDrawer;
    private UiTextButton cancelButton;
    private UiTextButton zoomInButton;
    private UiTextButton zoomOutButton;

    @Override
    public void onAdd(SolApplication solApplication) {
        mapDrawer = solApplication.getGame().getMapDrawer();
        mapDrawer.setToggled(true);
        UiVerticalListLayout verticalListLayout = new UiVerticalListLayout();
        cancelButton = new UiTextButton()
                .setDisplayName("Cancel")
                .setTriggerKey(solApplication.getOptions().getKeyClose())
                .setOnReleaseAction(uiElement -> {
                    mapDrawer.setToggled(false);
                    SolApplication.changeScreen(solApplication.getGame().getScreens().mainGameScreen);
                });
        UiHeadlessButton alternateCancelKeyButton = new UiHeadlessButton()
                .setTriggerKey(solApplication.getOptions().getKeyMap())
                .setOnReleaseAction(uiElement -> cancelButton.maybeFlashPressed(solApplication.getOptions().getKeyClose()));
        zoomOutButton = new UiTextButton()
                .setDisplayName("Zoom out")
                .setOnReleaseAction(uiElement -> mapDrawer.changeZoom(false))
                .setTriggerKey(solApplication.getOptions().getKeyZoomOut());
        zoomInButton = new UiTextButton()
                .setDisplayName("Zoom in")
                .setOnReleaseAction(uiElement -> mapDrawer.changeZoom(true))
                .setTriggerKey(solApplication.getOptions().getKeyZoomIn());
        verticalListLayout.addElement(cancelButton);
        verticalListLayout.addElement(zoomInButton);
        verticalListLayout.addElement(zoomOutButton);
        verticalListLayout.addElement(alternateCancelKeyButton);
        rootUiElement = new UiRelativeLayout().addElement(verticalListLayout, UI_POSITION_RIGHT, -UiTextButton.DEFAULT_BUTTON_WIDTH / 2, 0);
        SolApplication.getInputManager().changeScreen(this);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        Boolean scrolled = SolApplication.getInputManager().getScrolledUp();
        if (scrolled != null) {
            if (scrolled) { // If scrolling upwards
                zoomOutButton.maybeFlashPressed(solApplication.getOptions().getKeyZoomOut());
            } else { // If scrolling downwards
                zoomInButton.maybeFlashPressed(solApplication.getOptions().getKeyZoomIn());
            }
        }

        ShipUiControl shipControl = solApplication.getGame().getScreens().mainGameScreen.shipControl;
        if (shipControl instanceof ShipMouseControl) {
            shipControl.update(solApplication, true);
        }
    }
}
