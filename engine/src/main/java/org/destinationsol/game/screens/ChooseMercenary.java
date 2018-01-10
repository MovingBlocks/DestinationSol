package org.destinationsol.game.screens;

import java.util.ArrayList;
import java.util.List;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;

public class ChooseMercenary implements InventoryOperations {
    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl giveControl;
    private final SolUiControl takeControl;
    private final ItemContainer EMPTY_ITEM_CONTAINER = new ItemContainer();

    ChooseMercenary(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        giveControl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyShoot());
        giveControl.setDisplayName("Give Items");
        controls.add(giveControl);

        takeControl = new SolUiControl(inventoryScreen.itemCtrl(1), true, gameOptions.getKeyShoot2());
        takeControl.setDisplayName("Take Items");
        controls.add(takeControl);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolGame game = solApplication.getGame();
        InventoryScreen is = game.getScreens().inventoryScreen;
        SolInputManager inputMan = solApplication.getInputMan();
        GameScreens screens = game.getScreens();
        //        SolShip hero = game.getHero();
        SolItem selItem = is.getSelectedItem();
        boolean selNull = selItem != null;

        giveControl.setEnabled(selNull);
        takeControl.setEnabled(selNull);

        if (giveControl.isJustOff() && selNull) {
            FarShip farship = ((MercItem) selItem).getFarShip();
            inputMan.setScreen(solApplication, screens.mainScreen);
            is.giveItems.setTarget(farship);
            is.setOperations(is.giveItems);
            inputMan.addScreen(solApplication, is);
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
