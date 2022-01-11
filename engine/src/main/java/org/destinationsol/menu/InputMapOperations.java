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
package org.destinationsol.menu;

import org.destinationsol.GameOptions;
import org.destinationsol.ui.SolUiBaseScreen;

import java.util.List;

public abstract class InputMapOperations extends SolUiBaseScreen {
    /**
     * Get the header title to display on the input mapping screen.
     *
     * @return String The header title
     */
    public abstract String getHeader();

    /**
     * Get the key mappings to display on the input mapping screen.
     *
     * @param gameOptions The options object that contains the key mapping
     * @return List&lt;InputConfigItem&gt List of input config items
     */
    public abstract List<InputConfigItem> getItems(GameOptions gameOptions);

    /**
     * Get the text to display in the detail are
     *
     * @return String The text to display
     */
    public abstract String getDisplayDetail();

    /**
     * Determines if the user is entering a new input key
     *
     * @return boolean True if user is entering a new key
     */
    public abstract boolean isEnterNewKey();

    /**
     * Enter a new input key
     *
     * @param newKey The value to set
     */
    public abstract void setEnterNewKey(boolean newKey);

    /**
     * States which item in the list is currently selected
     *
     * @param index The index
     */
    public abstract void setSelectedIndex(int index);

    /**
     * Returns which item in the list is currently selected
     *
     * @return The index
     */
    public abstract int getSelectedIndex();

    /**
     * Save the new configuration settings
     *
     * @param gameOptions The options object that contains the key mapping
     */
    public abstract void save(GameOptions gameOptions);

    /**
     * Reset the input mappings back to the defaults
     *
     * @param gameOptions The options object that contains the key mapping
     */
    public abstract void resetToDefaults(GameOptions gameOptions);
}
