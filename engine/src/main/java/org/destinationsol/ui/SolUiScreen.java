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

import org.destinationsol.SolApplication;
import org.destinationsol.ui.responsiveUi.UiElement;

/**
 * This interface marks "Screens", or "UI Screens", that is objects designing what's going to be rendered.
 *
 * Outside of game, the ui screen usually takes whole area of window space, and only one ui screen is rendered at time.
 * Most notable of the "outside-of-game" ui screens, and the default ui screen presented to player when launching game,
 * is {@link org.destinationsol.menu.MainMenuScreen} During game, {@link org.destinationsol.game.screens.MainGameScreen}
 * is usually always rendered, and other ui screens are rendered atop of it, normally not taking up the whole window
 * space, rather only its part.
 */
public interface SolUiScreen {
    /**
     * @return The root {@code UiElement} used by this screen.
     */
    UiElement getRootUiElement();

    /**
     * This method is called whenever screen goes into focus.
     *
     * This method can be considered kind of constructor, as it can be used to set up some inner variables, change
     * playing music and perform all other sorts of custom initialization.
     *
     * @param solApplication {@code SolApplication} displaying this window.
     */
    //TODO maybe rename to onFocus() ? Inspect more whether name is completely fitting.
    void onAdd(SolApplication solApplication);

    /**
     * Updates the screen.
     *
     * Called on active screens for every game frame, allowing arbitrary logic to be executed.
     *
     * @param solApplication {@code SolApplication} displaying this window.
     * @param inputPointers Input pointers employed by the input manager.
     * @param clickedOutside True if click outside of the screen area happened since the last call, false otherwise
     */
    void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside);

    /**
     * Return true if cursor is in the screen area, false otherwise.
     *
     * Should also return false if the screen takes up a whole width of window and does not cover another screen. (???)
     * TODO whats wrong with this method??? Create some cleaner algorithm for deciding what this method should output.
     *
     * @param inputPointer Input pointer against which to check.
     * @return True if pointer in screen area, false otherwise.
     */
    boolean isCursorOnBackground(SolInputManager.InputPointer inputPointer);

    /**
     * Called whenever screen is due to be closed.
     *
     * Can be used for clean-up of sorts, as well as other things (marking all items as seen when exiting inventory).
     *
     * @param solApplication {@code SolApplication} displaying this window.
     */
    //TODO maybe rename to onClose()/onUnfocus() ? Inspect more whether name is completely fitting.
    void blurCustom(SolApplication solApplication);

    /**
     * Draws a background of the screen using supplied {@link UiDrawer}.
     *
     * This is called before drawing SolUiControls and other items ({@link #draw(UiDrawer, SolApplication)}).
     *
     * @param uiDrawer Drawer to draw with
     * @param solApplication {@code SolApplication} displaying this window.
     */
    void drawBackground(UiDrawer uiDrawer, SolApplication solApplication);

    /**
     * Draws text and images the screen might be employing.
     *
     * NOTE: Not to be used for drawing SolUiControls, those should be drawn higher in the stacktrace by calls to their
     * respective {@link SolUiControl#draw}.
     *
     * This is called after drawing background ({@link #drawBackground}) and SolUiControls.
     *
     * @param uiDrawer Drawer to draw with
     * @param solApplication {@code SolApplication} displaying this window.
     */
    default void draw(UiDrawer uiDrawer, SolApplication solApplication) {
        // Intentionally left blank
    }

    /**
     * Returns true if the screen reacts to clicking outside (inventory screens -> closed on outside click), false
     * otherwise (quest screens -> should not be closeable so easy).
     *
     * @return True if reacts to clicking outside, false otherwise
     */
    default boolean reactsToClickOutside() {
        return false;
    }
}
