package com.miloshpetrov.sol2.menu;


import com.miloshpetrov.sol2.GameOptions;
import com.miloshpetrov.sol2.ui.SolUiScreen;

import java.util.List;

public interface InputMapOperations extends SolUiScreen {
    /**
     * Get the header title to display on the input mapping screen.
     * @return String The header title
     */
    String getHeader();

    /**
     * Get the key mappings to display on the input mapping screen.
     * @param gameOptions The options object that contains the key mapping
     * @return List&lt;InputConfigItem&gt List of input config items
     */
    List<InputConfigItem> getItems(GameOptions gameOptions);

    /**
     * Get the text to display in the detail are
     * @return String The text to display
     */
    String getDisplayDetail();

    /**
     * Determines if the user is entering a new input key
     * @return boolean True if user is entering a new key
     */
    boolean isEnterNewKey();

    /**
     * States which item in the list is currently selected
     * @param index The index
     */
    void setSelectedIndex(int index);
}
