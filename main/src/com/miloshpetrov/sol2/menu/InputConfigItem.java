package com.miloshpetrov.sol2.menu;


/**
 * <h1>Holds Input Mapping Information</h1>
 * The InputConfigItem class stores input mapping information including the input name and the key it is assigned to.
 */
public class InputConfigItem {
    private String displayName;
    private String inputKey;

    /**
     * Get the key mappings to display on the input mapping screen.
     * @param name The input name to display on the screen
     * @param key The key the input is mapped to
     */
    InputConfigItem(String name, String key) {
        setDisplayName(name);
        setInputKey(key);
    }

    /**
     * Get the name to display on screen
     * @return String the name to display on screen
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the name to display on screen
     * @param name the name to display on screen
     */
    public void setDisplayName(String name) {
        displayName = name;
    }

    /**
     * Get the name of the input key
     * @return String the name to input key to display on screen
     */
    public String getInputKey() {
        return inputKey;
    }

    /**
     * Set the input key to display on screen
     * @param key the name of the input key to display on screen
     */
    public void setInputKey(String key) {
        inputKey = key;
    }
}
