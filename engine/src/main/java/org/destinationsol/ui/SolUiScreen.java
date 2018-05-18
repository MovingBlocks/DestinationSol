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

import org.destinationsol.SolApplication;

import java.util.List;

/**
 * This interface marks "Screens", that is objects designing what's going to be rendered.
 *
 * Outside of game, the screen usually takes whole area of window space, and only one screen is rendered at time. During
 * game, {@link org.destinationsol.game.screens.MainGameScreen} is usually always rendered, and other screens are
 * rendered atop of it, normally not taking up the whole window space, rather only its part.
 */
public interface SolUiScreen {

    /**
     * Returns all {@link SolUiControl SolUiControls} employed by this screen.
     * @return {@link List} of all {@code SolUiControls}.
     */
    List<SolUiControl> getControls();

    /**
     * This method is called whenever screen goes into focus.
     *
     * This method can be considered kind of constructor, as it can be used to set up some inner variables, change
     * playing music and perform all other sorts of custom initialization.
     *
     * @param solApplication {@code SolApplication} displaying this window.
     */
    default void onAdd(SolApplication solApplication) {
        // Intentionally left blank
    }

    /**
     * Updates the screen.
     *
     * This method is called on active screens on every game's frame, providing the screen with the capability to perform
     * whatever updating it finds necessary.
     *
     * @param solApplication {@code SolApplication} displaying this window.
     * @param inputPointers Input pointers employed by the input manager.
     * @param clickedOutside True if click outside of the screen area happened since the last call, false otherwise
     */
    default void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        // Intentionally left blank
    }

    /**
     * Return true if cursor is in the screen area, false otherwise.
     *
     * Should also return false if the screen takes up a whole width of window and does not cover another screen. (???)
     * TODO whats wrong with this method???
     *
     * @param inputPointer Input pointer against which to check.
     * @return True if pointer in screen area, false otherwise.
     */
    default boolean isCursorOnBackground(SolInputManager.InputPointer inputPointer) {
        return false;
    }

    /**
     * Called whenever screen is due to be closed.
     *
     * Can be used for clean-up of sorts, as well as other things (marking all items as seen when exiting inventory).
     *
     * @param solApplication {@code SolApplication} displaying this window.
     */
    default void blurCustom(SolApplication solApplication) {
        // Intentionally left blank
    }

    /**
     * Draws a background of the screen using supplied {@link UiDrawer}.
     *
     * This is called before drawing SolUiControls, images ({@link #drawImages(UiDrawer, SolApplication)}), and
     * text ( {@link #drawText(UiDrawer, SolApplication)}).
     *
     * @param uiDrawer Drawer to draw with
     * @param solApplication {@code SolApplication} displaying this window.
     */
    default void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        // Intentionally left blank
    }

    /**
     * Draws additional images screen might be employing.
     *
     * NOTE: Not to be used for drawing SolUiControls, those are drawn externally
     *
     * This is called after drawing background ({@link #drawBackground(UiDrawer, SolApplication)}) and SolUiControls,
     * but before text ({@link #drawText(UiDrawer, SolApplication)}).
     *
     * @param uiDrawer Drawer to draw with
     * @param solApplication {@code SolApplication} displaying this window.
     */
    default void drawImages(UiDrawer uiDrawer, SolApplication solApplication) {
        // Intentionally left blank
    }

    /**
     * Draws text screen might need.
     *
     * This is called after drawing background ({@link #drawBackground(UiDrawer, SolApplication)}), SolUiControls, and
     * images ({@link #drawImages(UiDrawer, SolApplication)}).
     *
     * @param uiDrawer Drawer to draw with
     * @param solApplication {@code SolApplication} displaying this window.
     */
    default void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        // Intentionally left blank
    }

    /**
     * Returns true if the screen reacts to clicking outside, false otherwise.
     *
     * @return True if reacts to clicking outside, false otherwise
     */
    default boolean reactsToClickOutside() {
        return false;
    }
}
