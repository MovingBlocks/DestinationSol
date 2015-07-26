package com.miloshpetrov.sol2.menu;


/**
 * <h1>Holds Input Mapping Information</h1>
 * The InputConfigItem class stores input mapping information including the input name and the key it is assigned to.
 */
public class InputConfigItem {
    private String displayName;
    private String inputKey;
    private boolean isAxis = true;
    private int controllerInput = -1;

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
     * Get the key mappings to display on the input mapping screen.
     * @param name The input name to display on the screen
     * @param key The key the input is mapped to
     * @param isAxis true if the input is an axis, false if is a button
     * @param key The button or axis the input is mapped to
     */
    InputConfigItem(String name, String key, boolean isAxis, int controllerInput) {
        setDisplayName(name);
        setInputKey(key);
        setIsAxis(isAxis);
        setControllerInput(controllerInput);
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

    /**
     * Get the controller input type
     * @return boolean true if the input is an axis, false if is a button
     */
    public boolean isAxis() {
        return isAxis;
    }

    /**
     * Set the controller input type
     * @param isAxis true if the input is an axis, false if is a button
     */
    public void setIsAxis(boolean isAxis) {
        this.isAxis = isAxis;
    }

    /**
     * Get the controller input
     * @return int The button or axis the input is mapped to
     */
    public int getControllerInput() {
        return controllerInput;
    }

    /**
     * Set the controller input type
     * @param controllerInput The button or axis the input is mapped to
     */
    public void setControllerInput(int controllerInput) {
        this.controllerInput = controllerInput;
    }
}
