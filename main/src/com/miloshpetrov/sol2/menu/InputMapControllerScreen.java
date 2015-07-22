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

public class InputMapControllerScreen implements InputMapOperations {
    private static final String HEADER_TEXT = "Controller Inputs";

    private final ArrayList<SolUiControl> controls;
    private final SolUiControl changeCtrl;
    private boolean isEnterNewKey;
    private List<InputConfigItem> itemsList = new ArrayList<InputConfigItem>();
    private int selectedIndex;

    public InputMapControllerScreen(InputMapScreen inputMapScreen, GameOptions gameOptions) {
        controls = new ArrayList<SolUiControl>();
        changeCtrl = new SolUiControl(inputMapScreen.itemCtrl(0), true);
        changeCtrl.setDisplayName("Change");
        controls.add(changeCtrl);
    }

    private InputConfigItem InitItem(int axis, int button, String displayName) {
        boolean isAxis = (axis > -1);
        int controllerInput = isAxis ? axis : button;
        String inputName = (isAxis ? "Axis: " : "Button: ") + controllerInput;
        InputConfigItem item = new InputConfigItem(displayName, inputName, isAxis, controllerInput);
        return item;
    }

    private void InitialiseList(GameOptions gameOptions) {
        itemsList.clear();

        // Ship Control Inputs
        itemsList.add(InitItem(gameOptions.getControllerAxisUpDown(), gameOptions.getControllerButtonUp(), "Up"));
        itemsList.add(InitItem(gameOptions.getControllerAxisUpDown(), gameOptions.getControllerButtonDown(), "Down"));
        itemsList.add(InitItem(gameOptions.getControllerAxisLeftRight(), gameOptions.getControllerButtonLeft(), "Left"));
        itemsList.add(InitItem(gameOptions.getControllerAxisLeftRight(), gameOptions.getControllerButtonRight(), "Right"));
        itemsList.add(InitItem(gameOptions.getControllerAxisShoot(), gameOptions.getControllerButtonShoot(), "Shoot"));
        itemsList.add(InitItem(gameOptions.getControllerAxisShoot2(), gameOptions.getControllerButtonShoot2(), "Shoot Secondary"));
        itemsList.add(InitItem(gameOptions.getControllerAxisAbility(), gameOptions.getControllerButtonAbility(), "Ability"));

        // Menu and Interface Keys
        itemsList.add(new InputConfigItem("Pause", gameOptions.getKeyPauseName()));
        itemsList.add(new InputConfigItem("Map", gameOptions.getKeyMapName()));
        itemsList.add(new InputConfigItem("Inventory", gameOptions.getKeyInventoryName()));
        itemsList.add(new InputConfigItem("Drop Item", gameOptions.getKeyDropName()));
        itemsList.add(new InputConfigItem("Talk", gameOptions.getKeyTalkName()));
        itemsList.add(new InputConfigItem("Sell", gameOptions.getKeySellMenuName()));
        itemsList.add(new InputConfigItem("Buy", gameOptions.getKeyBuyMenuName()));
        itemsList.add(new InputConfigItem("Change Ship", gameOptions.getKeyChangeShipMenuName()));
        itemsList.add(new InputConfigItem("Hire Ship", gameOptions.getKeyHireShipMenuName()));
    }

    @Override
    public void save(GameOptions gameOptions) {
        int index = 0;

        // This needs to be in the same order the list is initialised
        InputConfigItem item = itemsList.get(index++);
        gameOptions.setControllerAxisUpDown(item.isAxis() ? item.getControllerInput() : -1);
        gameOptions.setControllerButtonUp(!item.isAxis() ? item.getControllerInput() : -1);

        item = itemsList.get(index++);
        gameOptions.setControllerAxisUpDown(item.isAxis() ? item.getControllerInput() : -1);
        gameOptions.setControllerButtonDown(!item.isAxis() ? item.getControllerInput() : -1);

        item = itemsList.get(index++);
        gameOptions.setControllerAxisLeftRight(item.isAxis() ? item.getControllerInput() : -1);
        gameOptions.setControllerButtonLeft(!item.isAxis() ? item.getControllerInput() : -1);

        item = itemsList.get(index++);
        gameOptions.setControllerAxisLeftRight(item.isAxis() ? item.getControllerInput() : -1);
        gameOptions.setControllerButtonRight(!item.isAxis() ? item.getControllerInput() : -1);

        item = itemsList.get(index++);
        gameOptions.setControllerAxisShoot(item.isAxis() ? item.getControllerInput() : -1);
        gameOptions.setControllerButtonShoot(!item.isAxis() ? item.getControllerInput() : -1);

        item = itemsList.get(index++);
        gameOptions.setControllerAxisShoot2(item.isAxis() ? item.getControllerInput() : -1);
        gameOptions.setControllerButtonShoot2(!item.isAxis() ? item.getControllerInput() : -1);

        item = itemsList.get(index++);
        gameOptions.setControllerAxisAbility(item.isAxis() ? item.getControllerInput() : -1);
        gameOptions.setControllerButtonAbility(!item.isAxis() ? item.getControllerInput() : -1);

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
        itemsList.set(index++, InitItem(GameOptions.DEFAULT_AXIS_UP_DOWN, GameOptions.DEFAULT_BUTTON_UP, "Up"));
        itemsList.set(index++, InitItem(GameOptions.DEFAULT_AXIS_UP_DOWN, GameOptions.DEFAULT_BUTTON_DOWN, "Down"));
        itemsList.set(index++, InitItem(GameOptions.DEFAULT_AXIS_LEFT_RIGHT, GameOptions.DEFAULT_BUTTON_LEFT, "Left"));
        itemsList.set(index++, InitItem(GameOptions.DEFAULT_AXIS_LEFT_RIGHT, GameOptions.DEFAULT_BUTTON_RIGHT, "Right"));
        itemsList.set(index++, InitItem(GameOptions.DEFAULT_AXIS_SHOOT, GameOptions.DEFAULT_BUTTON_SHOOT, "Shoot"));
        itemsList.set(index++, InitItem(GameOptions.DEFAULT_AXIS_SHOOT2, GameOptions.DEFAULT_BUTTON_SHOOT2, "Shoot Secondary"));
        itemsList.set(index++, InitItem(GameOptions.DEFAULT_AXIS_ABILITY, GameOptions.DEFAULT_BUTTON_ABILITY, "Ability"));

        InputConfigItem item = itemsList.get(index);
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
        if (changeCtrl.isJustOff()) {
            isEnterNewKey = !isEnterNewKey;

            // TODO: Capture Controller Inputs
            
            // Can cancel the key entering by clicking this button a second time
            if (!isEnterNewKey) {
                Gdx.input.setInputProcessor(null);
                return;
            }

            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean keyUp (int keycode) {
                    if (selectedIndex >= 7) {
                        InputConfigItem item = itemsList.get(selectedIndex);
                        item.setInputKey(Input.Keys.toString(keycode));
                        itemsList.set(selectedIndex, item);
                    }
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
