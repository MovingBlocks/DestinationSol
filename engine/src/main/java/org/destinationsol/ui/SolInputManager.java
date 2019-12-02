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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.ui.responsiveUi.UiElement;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;

public class SolInputManager {
    private static final float CURSOR_SZ = .07f;
    private static final float WARN_PERC_GROWTH_TIME = 1f;
    private static final int POINTER_COUNT = 4;
    private static final float CURSOR_SHOW_TIME = 3;

    private static Cursor hiddenCursor;

    // TODO: I'm not sure why screens are part of the InputManager. Refactor this out maybe?
    private final List<SolUiScreen> screens = new ArrayList<>();
    private final List<SolUiScreen> screensToRemove = new ArrayList<>();
    private final List<SolUiScreen> screensToAdd = new ArrayList<>();
    private final InputPointer[] inputPointers;
    private final InputPointer flashInputPointer;
    private final Vector2 mousePos;
    private final Vector2 mousePrevPos;
    private final PlayableSound hoverSound;
    private final TextureAtlas.AtlasRegion uiCursor;
    private final Color warnColor;
    private float mouseIdleTime;
    //HACK: Mouse locking is currently broken on Linux
    private final boolean osIsLinux;
    private TextureAtlas.AtlasRegion currCursor;
    private boolean mouseOnUi;
    private float warnPercentage;
    private boolean warnPercGrows;
    private Boolean scrolledUp;

    public SolInputManager(OggSoundManager soundManager) {
        inputPointers = new InputPointer[POINTER_COUNT];
        for (int i = 0; i < POINTER_COUNT; i++) {
            inputPointers[i] = new InputPointer();
        }
        SolInputProcessor sip = new SolInputProcessor(this);
        Gdx.input.setInputProcessor(sip);
        flashInputPointer = new InputPointer();
        mousePos = new Vector2();
        mousePrevPos = new Vector2();

        // Create an empty 1x1 pixmap to use as hidden cursor
        Pixmap pixmap = new Pixmap(1, 1, RGBA8888);
        hiddenCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        pixmap.dispose();

        // We want the original mouse cursor to be hidden as we draw our own mouse cursor.
        Gdx.input.setCursorCatched(false);
        setMouseCursorHidden();
        uiCursor = Assets.getAtlasRegion("engine:uiCursor");
        warnColor = new Color(SolColor.UI_WARN);

        hoverSound = soundManager.getSound("engine:uiHover");
        osIsLinux = System.getProperty("os.name").equals("Linux");
    }

    private static void setPointerPosition(InputPointer inputPointer, int screenX, int screenY) {
        int h = Gdx.graphics.getHeight();

        inputPointer.x = 1f * screenX / h;
        inputPointer.y = 1f * screenY / h;
    }

    /**
     * Hides the mouse cursor by setting it to a transparent image.
     */
    private void setMouseCursorHidden() {
        Gdx.graphics.setCursor(hiddenCursor);
    }

    void maybeFlashPressed(int keyCode) {
        for (SolUiScreen screen : screens) {
            if (screen.getRootUiElement().maybeFlashPressed(keyCode)) {
                return;
            }
        }

    }

    void maybeFlashPressed(int x, int y) {
        setPointerPosition(flashInputPointer, x, y);

        for (SolUiScreen screen : screens) {
            if (screen.getRootUiElement().maybeFlashPressed(flashInputPointer) || screen.isCursorOnBackground(flashInputPointer)) {
                return;
            }
        }
    }

    public void changeScreen(SolUiScreen screen) {
        for (SolUiScreen oldScreen : screens) {
            removeScreen(oldScreen);
        }
        addScreen(screen);
    }

    public void addScreen(SolUiScreen screen) {
        screensToAdd.add(screen);
    }

    private void removeScreen(SolUiScreen screen) {
        screensToRemove.add(screen);
    }

    public boolean isScreenOn(SolUiScreen screen) {
        return screens.contains(screen);
    }

    public void update(SolApplication solApplication) {
        boolean mobile = solApplication.isMobile();
        SolGame game = solApplication.getGame();

        // This keeps the mouse within the window, but only when playing the game with the mouse.
        // All other times the mouse can freely leave and return.
        if (!mobile && solApplication.getOptions().controlType == GameOptions.ControlType.MIXED && game != null && getTopScreen() != game.getScreens().menuScreen) {
            if (!Gdx.input.isCursorCatched() && !osIsLinux) {
                Gdx.input.setCursorCatched(true);
            }
            maybeFixMousePos();
        } else {
            if (Gdx.input.isCursorCatched()) {
                Gdx.input.setCursorCatched(false);
            }
        }

        updatePointers();

        boolean consumed = false;
        mouseOnUi = false;
        boolean clickOutsideReacted = false;
        for (SolUiScreen screen : screens) {
            UiElement rootUiElement = screen.getRootUiElement();

            boolean consumedNow = rootUiElement.update(inputPointers, currCursor != null, !consumed, this, solApplication);

            Rectangle area = screen.getRootUiElement().getScreenArea();
            if (area != null && area.contains(mousePos)) {
                mouseOnUi = true;
            }

            if (consumedNow) {
                consumed = true;
            }

            boolean clickedOutside = false;
            if (!consumed) {
                for (InputPointer inputPointer : inputPointers) {
                    boolean onBackground = screen.isCursorOnBackground(inputPointer);
                    if (inputPointer.pressed && onBackground) {
                        clickedOutside = false;
                        consumed = true;
                        break;
                    }
                    if (!onBackground && inputPointer.isJustUnPressed() && !clickOutsideReacted) {
                        clickedOutside = true;
                    }
                }
            }

            if (clickedOutside && screen.reactsToClickOutside()) {
                clickOutsideReacted = true;
            }

            // TODO: This does not really make sense. Look into it.
            if (screen.isCursorOnBackground(inputPointers[0])) {
                mouseOnUi = true;
            }
            screen.updateCustom(solApplication, inputPointers, clickedOutside);
        }

        TutorialManager tutorialManager = game == null ? null : game.getTutMan();
        if (tutorialManager != null) {
            solApplication.finishGame();
        }

        updateCursor(solApplication);
        addRemoveScreens();
        updateWarnPerc();
        scrolledUp = null;
    }

    private void updateWarnPerc() {
        float dif = SolMath.toInt(warnPercGrows) * Const.REAL_TIME_STEP / WARN_PERC_GROWTH_TIME;
        warnPercentage += dif;
        if (warnPercentage < 0 || 1 < warnPercentage) {
            warnPercentage = SolMath.clamp(warnPercentage);
            warnPercGrows = !warnPercGrows;
        }
        warnColor.a = warnPercentage * .5f;
    }

    private void addRemoveScreens() {
        SolApplication solApplication = SolApplication.getInstance();

        while (!screensToRemove.isEmpty()) {
            SolUiScreen screen = screensToRemove.remove(0);

            if (!isScreenOn(screen)) {
                continue;
            }

            screen.getRootUiElement().blur();
            screen.blurCustom(solApplication);

            screens.remove(screen);
        }

        while (!screensToAdd.isEmpty()) {
            SolUiScreen screen = screensToAdd.remove(0);

            if (isScreenOn(screen)) {
                continue;
            }

            screen.onAdd(solApplication);

            screens.add(screen);
        }
    }

    private void updateCursor(SolApplication solApplication) {
        if (solApplication.isMobile()) {
            return;
        }
        SolGame game = solApplication.getGame();

        mousePos.set(inputPointers[0].x, inputPointers[0].y);
        if (solApplication.getOptions().controlType == GameOptions.ControlType.MIXED || solApplication.getOptions().controlType == GameOptions.ControlType.MOUSE) {
            if (game == null || mouseOnUi) {
                currCursor = uiCursor;
            } else {
                // currCursor = game.getScreens().mainGameScreen.shipControl.getInGameTex();
//                if (currCursor == null) {
//                    currCursor = uiCursor;
//                }
            }
            return;
        }
        if (mousePrevPos.epsilonEquals(mousePos, 0) && game != null && getTopScreen() != game.getScreens().menuScreen) {
            mouseIdleTime += Const.REAL_TIME_STEP;
            currCursor = mouseIdleTime < CURSOR_SHOW_TIME ? uiCursor : null;
        } else {
            currCursor = uiCursor;
            mouseIdleTime = 0;
            mousePrevPos.set(mousePos);
        }
    }

    private void maybeFixMousePos() {
        if (osIsLinux) {
            //Mouse locking is broken on Linux
            return;
        }

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        // TODO: look into the usefulness of this, and replace with Gdx.graphics.* with displayDimensions if nothing else
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        mouseX = (int) MathUtils.clamp((float) mouseX, (float) 0, (float) w);
        mouseY = (int) MathUtils.clamp((float) mouseY, (float) 0, (float) h);
        Gdx.input.setCursorPosition(mouseX, mouseY);
    }

    private void updatePointers() {
        for (int i = 0; i < POINTER_COUNT; i++) {
            InputPointer inputPointer = inputPointers[i];
            int screenX = Gdx.input.getX(i);
            int screenY = Gdx.input.getY(i);
            setPointerPosition(inputPointer, screenX, screenY);
            inputPointer.prevPressed = inputPointer.pressed;
            inputPointer.pressed = Gdx.input.isTouched(i);
        }
    }

    public void draw(UiDrawer uiDrawer, SolApplication solApplication) {
        for (int i = screens.size() - 1; i >= 0; i--) {
            SolUiScreen screen = screens.get(i);

            screen.drawBackground(uiDrawer, solApplication);

            screen.draw(uiDrawer, solApplication);

            try {
                screen.getRootUiElement().draw();
            } catch (NullPointerException e) {
                throw new RuntimeException(screen.getClass().getSimpleName());
            }
        }

        SolGame game = solApplication.getGame();
        TutorialManager tutorialManager = game == null ? null : game.getTutMan();

        if (currCursor != null) {
            uiDrawer.draw(currCursor, CURSOR_SZ, CURSOR_SZ, CURSOR_SZ / 2, CURSOR_SZ / 2, mousePos.x, mousePos.y, 0, SolColor.WHITE);
        }
    }

    public Vector2 getMousePos() {
        return mousePos;
    }

    public InputPointer[] getPtrs() {
        return inputPointers;
    }

    public boolean isMouseOnUi() {
        return mouseOnUi;
    }

    public void playHover(SolApplication solApplication) {
        hoverSound.getOggSound().getSound().play(.7f * solApplication.getOptions().sfxVolume.getVolume(), .7f, 0);
    }

    public void playClick(SolApplication solApplication) {
        hoverSound.getOggSound().getSound().play(.7f * solApplication.getOptions().sfxVolume.getVolume(), .9f, 0);
    }

    public SolUiScreen getTopScreen() {
        return screens.isEmpty() ? null : screens.get(0);
    }

    public void scrolled(boolean up) {
        scrolledUp = up;
    }

    public Boolean getScrolledUp() {
        return scrolledUp;
    }

    public void dispose() {
        hoverSound.getOggSound().getSound().dispose();
    }

    public static class InputPointer {
        public float x;
        public float y;
        public boolean pressed;
        public boolean prevPressed;

        public boolean isJustPressed() {
            return pressed && !prevPressed;
        }

        public boolean isJustUnPressed() {
            return !pressed && prevPressed;
        }
    }

}
