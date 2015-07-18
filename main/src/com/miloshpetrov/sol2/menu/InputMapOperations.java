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
}
