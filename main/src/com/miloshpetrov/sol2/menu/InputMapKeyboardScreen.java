package com.miloshpetrov.sol2.menu;

import com.miloshpetrov.sol2.GameOptions;
import com.miloshpetrov.sol2.SolApplication;
import com.miloshpetrov.sol2.ui.SolInputManager;
import com.miloshpetrov.sol2.ui.SolUiControl;
import com.miloshpetrov.sol2.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class InputMapKeyboardScreen implements InputMapOperations {
    private static final String HEADER_TEXT = "Keyboard Inputs";

    private final ArrayList<SolUiControl> controls;

    public InputMapKeyboardScreen(InputMapScreen inputMapScreen, GameOptions gameOptions) {
        controls = new ArrayList<SolUiControl>();
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
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

    }

    @Override
    public void blurCustom(SolApplication cmp) {

    }

    @Override
    public String getHeader() {
        return HEADER_TEXT;
    }

    @Override
    public List<InputConfigItem> getItems(GameOptions gameOptions) {
        List<InputConfigItem> items = new ArrayList<InputConfigItem>();

        // Ship Control Keys
        InputConfigItem keyUp = new InputConfigItem("Up", gameOptions.getKeyUpName());
        items.add(keyUp);
        InputConfigItem keyDown = new InputConfigItem("Down", gameOptions.getKeyDownName());
        items.add(keyDown);
        InputConfigItem keyLeft = new InputConfigItem("Left", gameOptions.getKeyLeftName());
        items.add(keyLeft);
        InputConfigItem keyRight = new InputConfigItem("Right", gameOptions.getKeyRightName());
        items.add(keyRight);
        InputConfigItem keyShoot = new InputConfigItem("Shoot", gameOptions.getKeyShootName());
        items.add(keyShoot);
        InputConfigItem keyShoot2 = new InputConfigItem("Shoot Secondary", gameOptions.getKeyShoot2Name());
        items.add(keyShoot2);
        InputConfigItem keyAbility = new InputConfigItem("Ability", gameOptions.getKeyAbilityName());
        items.add(keyAbility);

        // Menu and Interface Keys
        InputConfigItem pause = new InputConfigItem("Pause", gameOptions.getKeyPauseName());
        items.add(pause);
        InputConfigItem map = new InputConfigItem("Map", gameOptions.getKeyMapName());
        items.add(map);
        InputConfigItem inventory = new InputConfigItem("Inventory", gameOptions.getKeyInventoryName());
        items.add(inventory);
        InputConfigItem drop = new InputConfigItem("Drop Item", gameOptions.getKeyDropName());
        items.add(drop);
        InputConfigItem talk = new InputConfigItem("Talk", gameOptions.getKeyTalkName());
        items.add(talk);
        InputConfigItem sell = new InputConfigItem("Sell", gameOptions.getKeySellMenuName());
        items.add(sell);
        InputConfigItem buy = new InputConfigItem("Buy", gameOptions.getKeyBuyMenuName());
        items.add(buy);
        InputConfigItem changeShip = new InputConfigItem("Change Ship", gameOptions.getKeyChangeShipMenuName());
        items.add(changeShip);
        InputConfigItem hireShip = new InputConfigItem("Hire Ship", gameOptions.getKeyHireShipMenuName());
        items.add(hireShip);
        return items;
    }
}