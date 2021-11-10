/*
 * Copyright 2021 The Terasology Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.ui.nui.screens;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.Const;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.In;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.screens.BuyItemsScreen;
import org.destinationsol.game.screens.ChangeShipScreen;
import org.destinationsol.game.screens.ChooseMercenaryScreen;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.screens.GiveItemsScreen;
import org.destinationsol.game.screens.HireShipsScreen;
import org.destinationsol.game.screens.InventoryOperationsScreen;
import org.destinationsol.game.screens.SellItems;
import org.destinationsol.game.screens.ShowInventory;
import org.destinationsol.game.screens.TakeItems;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.nui.Color;
import org.terasology.nui.HorizontalAlign;
import org.terasology.nui.UIWidget;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.backends.libgdx.GdxColorUtil;
import org.terasology.nui.backends.libgdx.LibGDXTexture;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.layouts.ColumnLayout;
import org.terasology.nui.layouts.ScrollableArea;
import org.terasology.nui.layouts.relative.HorizontalHint;
import org.terasology.nui.layouts.relative.RelativeLayout;
import org.terasology.nui.layouts.relative.RelativeLayoutHint;
import org.terasology.nui.layouts.relative.VerticalHint;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIImage;
import org.terasology.nui.widgets.UILabel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Inventory screen displays a paginate-able list of {@link SolItem} that you can select and view information on.
 * Specialised functionality and logic for the screen is implemented via an assigned {@link InventoryOperationsScreen},
 * which can provide up to 3 additional "action buttons" placed in the bottom-right corner of the screen.
 */
public class InventoryScreen extends NUIScreenLayer {
    @In
    private SolApplication solApplication;
    private UILabel titleLabel;
    private ColumnLayout inventoryRows;
    private UIWarnButton nextButton;
    private UIWarnButton previousButton;
    private ScrollableArea descriptionScrollArea;
    private UILabel descriptionBox;
    private ColumnLayout inventoryActionButtons;
    private UIWarnButton closeButton;
    private InventoryOperationsScreen inventoryOperations;
    private int selectedIndex;
    private int page;

    private ShowInventory showInventory;
    private BuyItemsScreen buyItemsScreen;
    private SellItems sellItems;
    private ChangeShipScreen changeShipScreen;
    private HireShipsScreen hireShipsScreen;
    // The below screens deal with mercenaries
    private ChooseMercenaryScreen chooseMercenaryScreen;
    private GiveItemsScreen giveItemsScreen;
    private TakeItems takeItems;

    @Override
    public void initialise() {
        titleLabel = find("title", UILabel.class);

        inventoryRows = find("inventoryRows", ColumnLayout.class);

        nextButton = find("nextButton", UIWarnButton.class);
        nextButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyRight()));
        nextButton.subscribe(button -> {
            nextPage(button);
            selectedIndex = 0;
            updateItemRows();
        });

        previousButton = find("previousButton", UIWarnButton.class);
        previousButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyLeft()));
        previousButton.subscribe(button -> {
            previousPage(button);
            selectedIndex = 0;
            updateItemRows();
        });

        for (int rowNo = 0; rowNo < Const.ITEM_GROUPS_PER_PAGE; rowNo++) {
            inventoryRows.addWidget(createItemRow(rowNo));
        }

        descriptionScrollArea = find("itemDescriptionScrollArea", ScrollableArea.class);
        descriptionBox = find("itemDescription", UILabel.class);

        inventoryActionButtons = find("inventoryActionButtons", ColumnLayout.class);

        closeButton = new UIWarnButton("cancelButton", "Cancel");
        closeButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyEscape()));
        closeButton.subscribe(button -> {
            // Go back to the "Choose Mercenaries" screen if it was probably the last one opened.
            if (inventoryOperations == giveItemsScreen || inventoryOperations == takeItems ||
                    (inventoryOperations == showInventory && showInventory.getTarget() != solApplication.getGame().getHero().getShip())) {
                SolInputManager inputMan = solApplication.getInputManager();
                GameScreens screens = solApplication.getGame().getScreens();

                inputMan.setScreen(solApplication, screens.mainGameScreen);
                onRemoved();
                setOperations(chooseMercenaryScreen);
                onAdded();
            } else {
                nuiManager.removeScreen(this);
            }
        });

        showInventory = new ShowInventory();
        showInventory.initialise(solApplication, this);
        buyItemsScreen = new BuyItemsScreen();
        buyItemsScreen.initialise(solApplication, this);
        sellItems = new SellItems();
        sellItems.initialise(solApplication, this);
        changeShipScreen = new ChangeShipScreen();
        changeShipScreen.initialise(solApplication, this);
        hireShipsScreen = new HireShipsScreen();
        hireShipsScreen.initialise(solApplication, this);
        chooseMercenaryScreen = new ChooseMercenaryScreen();
        chooseMercenaryScreen.initialise(solApplication, this);
        giveItemsScreen = new GiveItemsScreen();
        giveItemsScreen.initialise(solApplication, this);
        takeItems = new TakeItems();
        takeItems.initialise(solApplication, this);
    }

    @Override
    public void onAdded() {
        titleLabel.setText(inventoryOperations.getHeader());
        descriptionBox.setText("");

        selectedIndex = 0;
        page = 0;
        inventoryOperations.onAdd(solApplication);

        ItemContainer items = inventoryOperations.getItems(solApplication.getGame());
        nextButton.setEnabled(Const.ITEM_GROUPS_PER_PAGE < items.groupCount());
        previousButton.setEnabled(false);

        for (UIButton actionButton : inventoryOperations.getActionButtons()) {
            inventoryActionButtons.addWidget(actionButton);
        }

        inventoryActionButtons.addWidget(closeButton);

        updateItemRows();
    }

    /**
     * Assigns the specified {@link InventoryOperationsScreen operations screen} to this screen.
     * @param operations the operations that can be performed
     */
    public void setOperations(InventoryOperationsScreen operations) {
        this.inventoryOperations = operations;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (solApplication.getGame().getHero().getShip() == showInventory.getTarget() || inventoryOperations == sellItems) {
            int itemNo = page * Const.ITEM_GROUPS_PER_PAGE;
            ItemContainer items = inventoryOperations.getItems(solApplication.getGame());
            Iterator<UIWidget> rowIterator = inventoryRows.iterator();
            rowIterator.next(); // Skip header
            while (rowIterator.hasNext()) {
                UIWidget row = rowIterator.next();
                if (itemNo >= items.groupCount()) {
                    break;
                }

                UIWarnButton itemButton = row.find("itemButton", UIWarnButton.class);
                if (itemButton != null && !itemButton.isWarning() && items.isNew(items.getGroup(itemNo))) {
                    itemButton.enableWarn();
                }
                itemNo++;
            }
        }

        inventoryOperations.update(solApplication, this);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        if (inventoryOperations != null) {
            inventoryOperations.getItems(solApplication.getGame()).markAllAsSeen();
            inventoryActionButtons.removeAllWidgets();
        }
    }

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        if (super.onKeyEvent(event)) {
            return true;
        }

        if (event.isDown()) {
            ItemContainer items = inventoryOperations.getItems(solApplication.getGame());
            if (event.getKey() == GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyUp())) {
                if (selectedIndex < 1 && previousButton.isEnabled()) {
                    selectedIndex = Const.ITEM_GROUPS_PER_PAGE - 1;
                    previousPage(previousButton);
                    previousButton.getClickSound().play(previousButton.getClickVolume());
                } else if (selectedIndex > 0) {
                    selectedIndex--;
                    previousButton.getClickSound().play(previousButton.getClickVolume());
                }

                items.seen(items.getGroup(selectedIndex + page * Const.ITEM_GROUPS_PER_PAGE));

                updateItemRows();
                return true;
            }

            if (event.getKey() == GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyDown())) {
                int itemsMaxIndex = items.groupCount() - 1;
                int maxIndex = Math.min(Const.ITEM_GROUPS_PER_PAGE - 1, itemsMaxIndex - (page * Const.ITEM_GROUPS_PER_PAGE));
                if (selectedIndex >= maxIndex && nextButton.isEnabled()) {
                    selectedIndex = 0;
                    nextPage(nextButton);
                    nextButton.getClickSound().play(nextButton.getClickVolume());
                } else if (selectedIndex < maxIndex) {
                    selectedIndex++;
                    nextButton.getClickSound().play(nextButton.getClickVolume());
                }

                items.seen(items.getGroup(selectedIndex + page * Const.ITEM_GROUPS_PER_PAGE));

                updateItemRows();
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the currently selected item group.
     * @return the selected item group
     */
    public List<SolItem> getSelected() {
        if (inventoryOperations == null || selectedIndex < 0 || selectedIndex >= Const.ITEM_GROUPS_PER_PAGE) {
            return null;
        }

        int itemGroupIndex = selectedIndex + page * Const.ITEM_GROUPS_PER_PAGE;
        ItemContainer items = inventoryOperations.getItems(solApplication.getGame());
        if (itemGroupIndex >= items.groupCount()) {
            return null;
        }

        return items.getGroup(itemGroupIndex);
    }

    /**
     * Retrieves the currently selected item.
     * @return the current selected item
     */
    public SolItem getSelectedItem() {
        List<SolItem> itemGroup = getSelected();
        if (itemGroup == null) {
            return null;
        }
        return itemGroup.isEmpty() ? null : itemGroup.get(0);
    }

    /**
     * Sets the selected item group.
     * @param itemGroup the item group to select
     */
    public void setSelected(List<SolItem> itemGroup) {
        ItemContainer items = inventoryOperations.getItems(solApplication.getGame());
        if (!items.containsGroup(itemGroup)) {
            selectedIndex = 0;
        } else {
            for (int groupNo = 0; groupNo < items.groupCount(); groupNo++) {
                if (items.getGroup(groupNo) == itemGroup) {
                    page = groupNo / Const.ITEM_GROUPS_PER_PAGE;
                    selectedIndex = groupNo % Const.ITEM_GROUPS_PER_PAGE;
                }
            }
        }

        updateItemRows();
    }

    /**
     * Returns the button representing the specified row.
     * @param index the row to retrieve
     * @return the row's item button
     */
    public UIWarnButton getRowButton(int index) {
        Iterator<UIWidget> rowIterator = inventoryRows.iterator();
        rowIterator.next(); // Skip header
        for (int rowNo = 0; rowNo < index; rowNo++) {
            rowIterator.next();
        }
        return rowIterator.next().find("itemButton", UIWarnButton.class);
    }

    /**
     * This is an internal API used by the tutorial. It just returns the buttons representing equipped items.
     * @return The buttons representing currently equipped items.
     */
    public List<UIWarnButton> getEquippedItemUIControlsForTutorial() {
        List<UIWarnButton> controls = new ArrayList<>();

        Iterator<UIWidget> rowsIterator = inventoryRows.iterator();
        rowsIterator.next(); // Skip header
        UIWidget row = rowsIterator.next();
        int startIndex = page * Const.ITEM_GROUPS_PER_PAGE;
        ItemContainer items = inventoryOperations.getItems(solApplication.getGame());
        for (int rowNo = 0; rowNo < Const.ITEM_GROUPS_PER_PAGE; rowNo++) {
            int groupNo = startIndex + rowNo;
            boolean emptyRow = groupNo >= items.groupCount();

            UIWarnButton itemButton = row.find("itemButton", UIWarnButton.class);
            if (emptyRow) {
                break;
            } else {
                List<SolItem> itemGroup = items.getGroup(groupNo);
                SolItem sample = itemGroup.get(0);

                if (inventoryOperations.isUsing(solApplication.getGame(), sample)) {
                    controls.add(itemButton);
                }
            }

            if (rowsIterator.hasNext()) {
                row = rowsIterator.next();
            }
        }

        return controls;
    }

    /**
     * @return the next button - used in the tutorial
     */
    public UIWarnButton getNextButton() {
        return nextButton;
    }

    /**
     * @return the previous button
     */
    public UIWarnButton getPreviousButton() {
        return previousButton;
    }

    /**
     * @return the close button - used in the tutorial
     */
    public UIWarnButton getCloseButton() {
        return closeButton;
    }

    /**
     * @return the {@link ShowInventory} inventory operations
     */
    public ShowInventory getShowInventory() {
        return showInventory;
    }

    /**
     * @return the {@link BuyItemsScreen} inventory operations
     */
    public BuyItemsScreen getBuyItemsScreen() {
        return buyItemsScreen;
    }

    /**
     * @return the {@link SellItems} inventory operations
     */
    public SellItems getSellItems() {
        return sellItems;
    }

    /**
     * @return the {@link ChangeShipScreen} inventory operations
     */
    public ChangeShipScreen getChangeShipScreen() {
        return changeShipScreen;
    }

    /**
     * @return the {@link HireShipsScreen} inventory operations
     */
    public HireShipsScreen getHireShipsScreen() {
        return hireShipsScreen;
    }

    /**
     * @return the {@link ChooseMercenaryScreen} inventory operations
     */
    public ChooseMercenaryScreen getChooseMercenaryScreen() {
        return chooseMercenaryScreen;
    }

    /**
     * @return the {@link GiveItemsScreen} inventory operations
     */
    public GiveItemsScreen getGiveItems() {
        return giveItemsScreen;
    }

    /**
     * @return the {@link TakeItems} inventory operations
     */
    public TakeItems getTakeItems() {
        return takeItems;
    }

    private UIWidget createItemRow(int index) {
        RelativeLayout itemRowLayout = new RelativeLayout();
        itemRowLayout.setFamily("inventoryRow");

        UIWarnButton itemButton = new UIWarnButton("itemButton", "<Item>");
        itemButton.subscribe(button -> {
            selectedIndex = index;
            updateItemRows();
        });
        itemRowLayout.addWidget(itemButton, new RelativeLayoutHint());

        UIImage itemIconBackground = new UIImage("itemIconBackground", Assets.getDSTexture("engine:whiteTex").getUiTexture());
        itemRowLayout.addWidget(itemIconBackground, new RelativeLayoutHint(
                new HorizontalHint()
                        .alignLeftRelativeTo("itemIcon", HorizontalAlign.LEFT)
                        .alignRightRelativeTo("itemIcon", HorizontalAlign.RIGHT),
                new VerticalHint()
        ));

        UIImage itemIcon = new UIImage("itemIcon");
        itemRowLayout.addWidget(itemIcon, new RelativeLayoutHint(
                new HorizontalHint().alignLeft(8),
                new VerticalHint()
        ).setUsingContentWidth(true));

        itemRowLayout.addWidget(new UILabel("itemEquippedLabel", ""), new RelativeLayoutHint(
                new HorizontalHint().alignLeftRelativeTo("itemIcon", HorizontalAlign.RIGHT, 8),
                new VerticalHint()
        ).setUsingContentWidth(true));

        itemRowLayout.addWidget(new UILabel("itemQuantityLabel", ""), new RelativeLayoutHint(
                new HorizontalHint().alignRight(8),
                new VerticalHint()
        ).setUsingContentWidth(true));

        itemRowLayout.addWidget(new UILabel("itemPriceLabel", ""), new RelativeLayoutHint(
                new HorizontalHint().alignRight(64),
                new VerticalHint()
        ).setUsingContentWidth(true));

        return itemRowLayout;
    }

    public void updateItemRows() {
        ItemContainer items = inventoryOperations.getItems(solApplication.getGame());
        Iterator<UIWidget> rowsIterator = inventoryRows.iterator();
        rowsIterator.next(); // Ignore the first row, since it's the header.
        UIWidget row = rowsIterator.next();

        int startIndex = page * Const.ITEM_GROUPS_PER_PAGE;
        if (startIndex >= items.groupCount() && page > 0) {
            // Empty page. This may have happened if the last item on a page was dropped from the inventory.
            page = (items.groupCount() - 1) / Const.ITEM_GROUPS_PER_PAGE;
            startIndex = page * Const.ITEM_GROUPS_PER_PAGE;
            selectedIndex = 0;
        }

        previousButton.setEnabled(page > 0);
        nextButton.setEnabled(((page + 1) * Const.ITEM_GROUPS_PER_PAGE) < items.groupCount());

        for (int rowNo = 0; rowNo < Const.ITEM_GROUPS_PER_PAGE; rowNo++) {
            int groupNo = startIndex + rowNo;
            boolean emptyRow = groupNo >= items.groupCount();

            UIWarnButton itemButton = row.find("itemButton", UIWarnButton.class);
            UIImage itemIconBackground = row.find("itemIconBackground", UIImage.class);
            UIImage itemIcon = row.find("itemIcon", UIImage.class);
            UILabel itemEquippedLabel = row.find("itemEquippedLabel", UILabel.class);
            UILabel itemQuantityLabel = row.find("itemQuantityLabel", UILabel.class);
            UILabel itemPriceLabel = row.find("itemPriceLabel", UILabel.class);
            if (emptyRow) {
                itemButton.setText("");
                itemIconBackground.setTint(new Color(Color.transparent));
                itemIcon.setImage(null);
                itemEquippedLabel.setText("");
                itemQuantityLabel.setText("");
                itemPriceLabel.setText("");

                itemButton.setEnabled(false);
            } else {
                List<SolItem> itemGroup = items.getGroup(groupNo);
                SolItem sample = itemGroup.get(0);

                itemButton.setText(sample.getDisplayName());
                itemButton.setActive(selectedIndex == rowNo);
                if (items.isNew(itemGroup)) {
                    itemButton.enableWarn();
                }

                itemIconBackground.setTint(GdxColorUtil.gdxToTerasologyColor(sample.getItemType().uiColor));

                TextureAtlas.AtlasRegion iconTexture = sample.getIcon(solApplication.getGame());
                itemIcon.setImage(new LibGDXTexture(iconTexture));

                itemEquippedLabel.setText(inventoryOperations.isUsing(solApplication.getGame(), sample) ? "using" : "");

                itemQuantityLabel.setText(itemGroup.size() > 1 ? "x" + itemGroup.size() : "");

                itemPriceLabel.setText(inventoryOperations.getPriceMul() > 0 ? "$" + sample.getPrice() * inventoryOperations.getPriceMul() : "");

                itemButton.setEnabled(true);
            }

            if (rowsIterator.hasNext()) {
                row = rowsIterator.next();
            }
        }

        int selectedGroup = (selectedIndex + page * Const.ITEM_GROUPS_PER_PAGE);
        if (items.groupCount() > 0 && items.groupCount() > selectedGroup) {
            List<SolItem> itemGroup = items.getGroup(selectedGroup);
            items.seen(itemGroup);
            SolItem sample = itemGroup.get(0);
            // Add an extra newline to the end to ensure that the entire area is scrollable.
            descriptionBox.setText(sample.getDisplayName() + "\n" + sample.getDescription() + "\n");
            // Scroll to top
            descriptionScrollArea.setPosition(0);
        }
    }

    private void nextPage(UIWidget nextButton) {
        int inputCount = inventoryOperations.getItems(solApplication.getGame()).groupCount();
        page++;
        nextButton.setEnabled(((page + 1) * Const.ITEM_GROUPS_PER_PAGE) < inputCount);
        previousButton.setEnabled(true);
    }

    private void previousPage(UIWidget previousButton) {
        page--;
        previousButton.setEnabled(page > 0);
        nextButton.setEnabled(true);
    }
}
