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
package org.destinationsol.menu;

import org.destinationsol.GameOptions;
import org.destinationsol.ui.SolUiScreen;

import java.util.List;

public interface InputMapOperations extends SolUiScreen {
    /**
     * Get the header title to display on the input mapping screen.
     *
     * @return String The header title
     */
    String getHeader();

    /**
     * Get the key mappings to display on the input mapping screen.
     *
     * @param gameOptions The options object that contains the key mapping
     * @return List&lt;InputConfigItem&gt List of input config items
     */
    List<InputConfigItem> getItems(GameOptions gameOptions);

    /**
     * Get the text to display in the detail are
     *
     * @return String The text to display
     */
    String getDisplayDetail();

    /**
     * Determines if the user is entering a new input key
     *
     * @return boolean True if user is entering a new key
     */
    boolean isEnterNewKey();

    /**
     * Enter a new input key
     *
     * @param newKey The value to set
     */
    void setEnterNewKey(boolean newKey);

    /**
     * States which item in the list is currently selected
     *
     * @param index The index
     */
    void setSelectedIndex(int index);

    /**
     * Save the new configuration settings
     *
     * @param gameOptions The options object that contains the key mapping
     */
    void save(GameOptions gameOptions);

    /**
     * Reset the input mappings back to the defaults
     *
     * @param gameOptions The options object that contains the key mapping
     */
    void resetToDefaults(GameOptions gameOptions);
}
