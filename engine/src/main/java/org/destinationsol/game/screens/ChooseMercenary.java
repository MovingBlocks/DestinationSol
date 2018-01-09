package org.destinationsol.game.screens;

import java.util.ArrayList;
import java.util.List;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;

public class ChooseMercenary implements InventoryOperations {
    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl selectControl;
    private final ItemContainer EMPTY_ITEM_CONTAINER = new ItemContainer();

    ChooseMercenary(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        selectControl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyShoot());
        selectControl.setDisplayName("Select");
        controls.add(selectControl);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolGame game = solApplication.getGame();
        InventoryScreen is = game.getScreens().inventoryScreen;
        SolShip hero = game.getHero();
        SolItem selItem = is.getSelectedItem();
        boolean selNull = selItem != null;

        selectControl.setDisplayName("Select");
        selectControl.setEnabled(selNull);

        if (selectControl.isJustOff() && selNull) {
            MercItem mercItem = (MercItem) selItem;
        }
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        ItemContainer mercs = game.getHero().getTradeContainer().getMercs();
        return mercs != null ? mercs : EMPTY_ITEM_CONTAINER;
    }

    @Override
    public String getHeader() {
        return "Mercenaries:";
    }

}
