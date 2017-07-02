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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class InputMapKeyboardScreen implements InputMapOperations {
    private static final String HEADER_TEXT = "Keyboard Inputs";

    private final ArrayList<SolUiControl> controls;
    private boolean isEnterNewKey;
    private List<InputConfigItem> itemsList = new ArrayList<>();
    private int selectedIndex;

    public InputMapKeyboardScreen(InputMapScreen inputMapScreen, GameOptions gameOptions) {
        controls = new ArrayList<>();
    }

    private void InitialiseList(GameOptions gameOptions) {
        itemsList.clear();

        // Ship Control Keys
        InputConfigItem keyUp = new InputConfigItem("Up", gameOptions.getKeyUpName());
        itemsList.add(keyUp);
        InputConfigItem keyDown = new InputConfigItem("Down", gameOptions.getKeyDownName());
        itemsList.add(keyDown);
        InputConfigItem keyLeft = new InputConfigItem("Left", gameOptions.getKeyLeftName());
        itemsList.add(keyLeft);
        InputConfigItem keyRight = new InputConfigItem("Right", gameOptions.getKeyRightName());
        itemsList.add(keyRight);
        InputConfigItem keyShoot = new InputConfigItem("Shoot", gameOptions.getKeyShootName());
        itemsList.add(keyShoot);
        InputConfigItem keyShoot2 = new InputConfigItem("Shoot Secondary", gameOptions.getKeyShoot2Name());
        itemsList.add(keyShoot2);
        InputConfigItem keyAbility = new InputConfigItem("Ability", gameOptions.getKeyAbilityName());
        itemsList.add(keyAbility);

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
        gameOptions.setKeyUpName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyDownName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyLeftName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyRightName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyShootName(itemsList.get(index++).getInputKey());
        gameOptions.setKeyShoot2Name(itemsList.get(index++).getInputKey());
        gameOptions.setKeyAbilityName(itemsList.get(index++).getInputKey());

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
        item.setInputKey(GameOptions.DEFAULT_UP);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_DOWN);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_LEFT);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_RIGHT);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_SHOOT);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_SHOOT2);
        itemsList.set(index++, item);

        item = itemsList.get(index);
        item.setInputKey(GameOptions.DEFAULT_ABILITY);
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
    public void updateCustom(SolApplication cmp, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
    }

    /**
     * Remove key if it is already assigned to prevent duplicate keys
     *
     * @param keyCode The keycode to be removed
     */
    private void removeDuplicateKeys(int keyCode) {
        for (InputConfigItem item : itemsList) {
            if (Input.Keys.valueOf(item.getInputKey()) == keyCode) {
                item.setInputKey("");
            }
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
    public boolean isCursorOnBg(SolInputManager.InputPointer inputPointer) {
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
    public boolean isEnterNewKey() {
        return isEnterNewKey;
    }

    @Override
    public void setEnterNewKey(boolean newKey) {
        isEnterNewKey = newKey;

        // Cancel the key input
        if (!isEnterNewKey) {
            Gdx.input.setInputProcessor(null);
        } else {
            // Capture the new key input
            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean keyUp(int keycode) {
                    // Don't capture the escape key
                    if (keycode == Input.Keys.ESCAPE) {
                        return true;
                    }

                    removeDuplicateKeys(keycode);
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
    public String getDisplayDetail() {
        if (isEnterNewKey) {
            return "Enter New Key";
        } else {
            return "";
        }
    }

    @Override
    public List<InputConfigItem> getItems(GameOptions gameOptions) {
        return itemsList;
    }

    @Override
    public void setSelectedIndex(int index) {
        selectedIndex = index;
    }
}
