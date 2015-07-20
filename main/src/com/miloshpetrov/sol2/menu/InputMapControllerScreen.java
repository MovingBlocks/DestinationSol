package com.miloshpetrov.sol2.menu;


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

    public InputMapControllerScreen(InputMapScreen inputMapScreen, GameOptions gameOptions) {
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

        // TODO: Ship Control Inputs

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
