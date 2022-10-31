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

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.context.Context;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.screens.ConsoleScreen;
import org.destinationsol.ui.nui.screens.UIShipControlsScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated This class only exists for compatibility purposes whilst the rest of the code is
 * transitions to the new NUI-based MainGameScreen. Almost all functionality is now implemented by the new screen.
 * @see org.destinationsol.ui.nui.screens.MainGameScreen the new NUI-based MainGameScreen
 */
@Deprecated
public class MainGameScreen extends SolUiBaseScreen {
    static final float CELL_SZ = .17f;
    static final float HELPER_ROW_1 = 1 - 3f * CELL_SZ;

    private final ShipUiControl shipControl;
    private final SolUiControl pauseControl;
    private final CameraKeyboardControl cameraControl;
    private final SolApplication solApplication;

    private List<SolUiScreen> gameOverlayScreens = new ArrayList<>();
    private List<WarnDrawer> warnDrawers = new ArrayList<>();

    MainGameScreen(RightPaneLayout rightPaneLayout, Context context) {
        solApplication = context.get(SolApplication.class);
        GameOptions gameOptions = solApplication.getOptions();

        switch (gameOptions.controlType) {
            case KEYBOARD:
                UIShipControlsScreen shipControlsScreen =
                        (UIShipControlsScreen) solApplication.getNuiManager().createScreen("engine:uiShipControlsScreen");
                shipControl = shipControlsScreen;
                break;
            case MOUSE:
                shipControl = new ShipMouseControl();
                break;
            case CONTROLLER:
                shipControl = new ShipControllerControl(solApplication);
                break;
            case MIXED:
            default:
                shipControl = new ShipMixedControl(solApplication, controls);
                break;
        }

        pauseControl = new SolUiControl(null, true, gameOptions.getKeyPause());
        controls.add(pauseControl);
        cameraControl = new CameraKeyboardControl(gameOptions, controls);
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        super.onAdd(solApplication);
        if (solApplication.getOptions().controlType == GameOptions.ControlType.KEYBOARD) {
            UIShipControlsScreen uiControls = (UIShipControlsScreen) shipControl;
            if (!solApplication.getNuiManager().hasScreen(uiControls)) {
                solApplication.getNuiManager().pushScreen(uiControls);
            }
        }
    }

    public static Rectangle btn(float x, float y, boolean halfHeight) {
        float gap = .01f;
        float cellH = CELL_SZ;
        if (halfHeight) {
            cellH /= 2;
        }
        return new Rectangle(x + gap, y + gap, CELL_SZ - gap * 2, cellH - gap * 2);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (DebugOptions.PRINT_BALANCE) {
            solApplication.finishGame();
            return;
        }
        SolGame game = solApplication.getGame();
        SolInputManager inputMan = solApplication.getInputManager();
        NUIManager nuiManager = solApplication.getNuiManager();
        GameScreens screens = game.getScreens();

        for (WarnDrawer warnDrawer : warnDrawers) {
            warnDrawer.update(game);
        }

        NUIScreenLayer topScreen = nuiManager.getTopScreen();
        boolean controlsEnabled = inputMan.getTopScreen() == this &&
                (topScreen instanceof org.destinationsol.ui.nui.screens.MainGameScreen ||
                        topScreen instanceof UIShipControlsScreen);
        shipControl.update(solApplication, controlsEnabled);

        if (solApplication.getNuiManager().hasScreenOfType(ConsoleScreen.class)) {
            controls.forEach(x -> x.setEnabled(false));
        } else if (!nuiManager.hasScreen(screens.menuScreen)) {
            game.setPaused(false);
            controls.forEach(x -> x.setEnabled(true));
        }

        if (pauseControl.isJustOff()) {
            game.setPaused(!game.isPaused());
        }

        for (SolUiScreen screen : gameOverlayScreens) {
            screen.updateCustom(solApplication, inputPointers, clickedOutside);
        }
    }

    @Override
    public void drawImages(UiDrawer uiDrawer, SolApplication solApplication) {
        int drawPlace = 0;
        for (WarnDrawer wd : warnDrawers) {
            if (wd.drawPercentage > 0) {
                wd.draw(uiDrawer, drawPlace++);
            }
        }

        for (SolUiScreen screen : gameOverlayScreens) {
            screen.drawImages(uiDrawer, solApplication);
        }
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        int drawPlace = 0;
        for (WarnDrawer warnDrawer : warnDrawers) {
            if (warnDrawer.drawPercentage > 0) {
                warnDrawer.drawText(uiDrawer, drawPlace++);
            }
        }

        for (SolUiScreen screen : gameOverlayScreens) {
            screen.drawText(uiDrawer, solApplication);
        }
    }

    @Override
    public void blurCustom(SolApplication solApplication) {
        shipControl.blur();

        for (SolUiScreen screen : gameOverlayScreens) {
            screen.blurCustom(solApplication);
        }
    }

    public ShipUiControl getShipControl() {
        return shipControl;
    }

    public boolean isCameraUp() {
        return cameraControl.isUp();
    }

    public boolean isCameraDown() {
        return cameraControl.isDown();
    }

    public boolean isCameraLeft() {
        return cameraControl.isLeft();
    }

    public boolean isCameraRight() {
        return cameraControl.isRight();
    }

    /**
     * @deprecated Use NUI screens instead. All NUI screens are overlays.
     * @see NUIScreenLayer
     * @see NUIManager#createScreen(String)
     * @see NUIManager#pushScreen(NUIScreenLayer)
     */
    @Deprecated
    public void addOverlayScreen(SolUiScreen screen) {
        gameOverlayScreens.add(screen);
        screen.onAdd(solApplication);
        controls.addAll(screen.getControls());
    }

    /**
     * @deprecated Use NUI screens instead. All NUI screens are overlays.
     * @see NUIManager#removeScreen(NUIScreenLayer)
     */
    @Deprecated
    public void removeOverlayScreen(SolUiScreen screen) {
        gameOverlayScreens.remove(screen);
        controls.removeAll(screen.getControls());
    }

    /**
     * @deprecated Use NUI screens instead. All NUI screens are overlays.
     * @see NUIManager#hasScreen(NUIScreenLayer)
     */
    @Deprecated
    public boolean hasOverlay(SolUiScreen screen) {
        return gameOverlayScreens.contains(screen);
    }

    /**
     * @deprecated Use the new MainGameScreen and UIWarnDrawer instead.
     * @see org.destinationsol.ui.nui.widgets.UIWarnDrawer
     * @see org.destinationsol.ui.nui.screens.MainGameScreen#addWarnDrawer(String, org.terasology.nui.Color, String, org.terasology.nui.databinding.Binding)
     * @see org.destinationsol.ui.nui.screens.MainGameScreen#addWarnDrawer(org.destinationsol.ui.nui.widgets.UIWarnDrawer)
     */
    @Deprecated
    public void addWarnDrawer(WarnDrawer drawer) {
        if (!warnDrawers.contains(drawer)) {
            warnDrawers.add(drawer);
        }
    }

    /**
     * @deprecated Use the new MainGameScreen and UIWarnDrawer instead.
     * @see org.destinationsol.ui.nui.widgets.UIWarnDrawer
     * @see org.destinationsol.ui.nui.screens.MainGameScreen#removeWarnDrawer(String)
     * @see org.destinationsol.ui.nui.screens.MainGameScreen#removeWarnDrawer(org.destinationsol.ui.nui.widgets.UIWarnDrawer)
     */
    @Deprecated
    public void removeWarnDrawer(WarnDrawer drawer) {
        warnDrawers.remove(drawer);
    }

    /**
     * @deprecated Use the new MainGameScreen and UIWarnDrawer instead.
     * @see org.destinationsol.ui.nui.widgets.UIWarnDrawer
     * @see org.destinationsol.ui.nui.screens.MainGameScreen#hasWarnDrawer(String)
     * @see org.destinationsol.ui.nui.screens.MainGameScreen#hasWarnDrawer(org.destinationsol.ui.nui.widgets.UIWarnDrawer)
     */
    @Deprecated
    public boolean hasWarnDrawer(WarnDrawer drawer) {
        return warnDrawers.contains(drawer);
    }
}

