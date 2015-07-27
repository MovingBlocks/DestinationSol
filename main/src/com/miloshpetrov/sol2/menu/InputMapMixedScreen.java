package com.miloshpetrov.sol2.menu;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.miloshpetrov.sol2.GameOptions;
import com.miloshpetrov.sol2.SolApplication;
import com.miloshpetrov.sol2.ui.SolInputManager;
import com.miloshpetrov.sol2.ui.SolUiControl;
import com.miloshpetrov.sol2.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class InputMapMixedScreen implements InputMapOperations {
    private static final String HEADER_TEXT = "Keyboard and Mouse Inputs";

    private final ArrayList<SolUiControl> controls;
    private boolean isEnterNewKey;
    private List<InputConfigItem> itemsList = new ArrayList<InputConfigItem>();
    private int selectedIndex;

    public InputMapMixedScreen(InputMapScreen inputMapScreen, GameOptions gameOptions) {
        controls = new ArrayList<SolUiControl>();
    }

    private void InitialiseList(GameOptions gameOptions) {
        itemsList.clear();

        // Ship Control Keys
        InputConfigItem mouseUp = new InputConfigItem("Up", gameOptions.getKeyUpMouseName());
        itemsList.add(mouseUp);
        InputConfigItem mouseDown = new InputConfigItem("Down", gameOptions.getKeyDownMouseName());
        itemsList.add(mouseDown);

        // Menu and Interface Keys
        InputConfigItem pause = new InputConfigItem("Pause", gameOptions.getKeyPauseName());
        itemsList.add(pause);
        InputConfigItem map = new InputConfigItem("Map", gameOptions.getKeyMapName());
        itemsList.add(map);
        InputConfigItem inventory = new InputConfigItem("Inventory", gameOptions.getKeyInventoryName());
        itemsList.add(inventory);
        InputConfigItem drop = new InputConfigItem("Drop Item", gameOptions.getKeyDropName());
        itemsList.add(drop);
        InputConfigItem talk = new InputConfigItem("Talk", gameOptions.getKeyTalkName());
        itemsList.add(talk);
        InputConfigItem sell = new InputConfigItem("Sell", gameOptions.getKeySellMenuName());
        itemsList.add(sell);
        InputConfigItem buy = new InputConfigItem("Buy", gameOptions.getKeyBuyMenuName());
        itemsList.add(buy);
        InputConfigItem changeShip = new InputConfigItem("Change Ship", gameOptions.getKeyChangeShipMenuName());
        itemsList.add(changeShip);
        InputConfigItem hireShip = new InputConfigItem("Hire Ship", gameOptions.getKeyHireShipMenuName());
        itemsList.add(hireShip);
    }

    @Override
    public void save(GameOptions gameOptions) {
        int index = 0;

        // This needs to be in the same order the list is initialised
        gameOptions.setKeyUpMouseName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyDownMouseName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyPauseName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyMapName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyInventoryName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyDropName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyTalkName(itemsList.get(index++).getInputKey());
        gameOptions.setKeySellMenuName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyBuyMenuName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyChangeShipMenuName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyHireShipMenuName(itemsList.get(index++).getInputKey());
        gameOptions.save();
    }

    @Override
    public void resetToDefaults(GameOptions gameOptions) {
        int index = 0;

        // This needs to be in the same order the list is initialised
        InputConfigItem item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_MOUSE_UP);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_MOUSE_DOWN);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_PAUSE);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_MAP);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_INVENTORY);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_DROP);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_TALK);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_SELL);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_BUY);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_CHANGE_SHIP);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_HIRE_SHIP);
        itemsList.set(index++, item);
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    }

    @Override
    public void setEnterNewKey(boolean newKey){
        isEnterNewKey = newKey;

        // Cancel the key input
        if (!isEnterNewKey) {
            Gdx.input.setInputProcessor(null);
        } else {
            // Capture the new key input
            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean keyUp (int keycode) {
                    // Don't capture the escape key
                    if (keycode == Input.Keys.ESCAPE) return true;

                    InputConfigItem item = itemsList.get(selectedIndex);
                    item.setInputKey(Input.Keys.toString(keycode));
                    itemsList.set(selectedIndex, item);
                    Gdx.input.setInputProcessor(null);

                    isEnterNewKey = false;
                    return true; // return true to indicate the event was handled
                }
            });
        }
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {

    }

    @Override
    public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {

    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication cmp) {
    }

    @Override
    public boolean reactsToClickOutside() {
        return false;
    }

    @Override
    public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
        return false;
    }

    @Override
    public void onAdd(SolApplication cmp) {
        InitialiseList(cmp.getOptions());
        isEnterNewKey = false;
        selectedIndex = 0;
    }

    @Override
    public void blurCustom(SolApplication cmp) {

    }

    @Override
    public String getHeader() {
        return HEADER_TEXT;
    }

    @Override
    public String getDisplayDetail() {
        if (isEnterNewKey) {
            return "Enter New Key";
        } else {
            return "";
        }
    }

    @Override
    public boolean isEnterNewKey(){
        return isEnterNewKey;
    }

    @Override
    public List<InputConfigItem> getItems(GameOptions gameOptions) {
        return itemsList;
    }

    @Override
    public void setSelectedIndex(int index){
        selectedIndex = index;
    }
}
