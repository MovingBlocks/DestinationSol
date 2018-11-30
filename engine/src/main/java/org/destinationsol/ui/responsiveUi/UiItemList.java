/*
 * Copyright 2018 MovingBlocks
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
package org.destinationsol.ui.responsiveUi;

import org.destinationsol.SolApplication;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.ui.SolInputManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//TODO movepage buttons should not be that huge as they are. Also, does not generally work well
public class UiItemList extends AbstractUiElement {
    private ItemContainer container;
    private UiVerticalListLayout layout = new UiVerticalListLayout();
    private int page;
    private static final int ITEMS_PER_PAGE = 6;
    private ArrayList<UiStateButton<UiBooleanEnum>> buttons = new ArrayList<>();
    private SolItem selectedItem;


    @Override
    public UiItemList setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        layout.setPosition(x, y);
        return this;
    }

    @Override
    public UiItemList setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
        return this;
    }

    @Override
    public UiItemList recalculate() {
        layout = new UiVerticalListLayout().setPosition(x, y);
        resetPage();
        int skipElements = page * ITEMS_PER_PAGE;
        buttons = new ArrayList<>();
        int addedItems = 0;
        for (List<SolItem> itemList : container) {
            if (skipElements-- > 0) {
                continue;
            }
            if (addedItems++ == ITEMS_PER_PAGE) {
                break;
            }
            final UiStateButton<UiBooleanEnum> button = new UiStateButton<>(UiBooleanEnum.FALSE)
                    .addElement(new UiItemBox()
                            .setItem(itemList.get(0))
                            .setAmount(itemList.size()))
                    .setCallback(uiElement -> setSelectedItem(uiElement, itemList.get(0)));
            button.setHeight(button.getMinHeight());
            buttons.add(button);
            layout.addElement(button);
        }
        if (selectedItem == null && buttons.size() != 0) {
            buttons.get(0).triggerCallback();
        }
        final UiActionButton moveLeftButton = new UiActionButton().addElement(new UiSpacerElement()
                .setFromElement(new UiTextBox().setText("justsomespace"))
                .setContainedElement(new UiTextBox().setText("<")))
                .setAction(uiElement -> {
                    page--;
                    resetPage();
                    recalculate();
                });
        final UiActionButton moveRightButton = new UiActionButton().addElement(new UiSpacerElement()
                .setFromElement(new UiTextBox().setText("justsomespace"))
                .setContainedElement(new UiTextBox().setText(">")))
                .setAction(uiElement -> {
                    page++;
                    resetPage();
                    recalculate();
                });
        final UiHorizontalListLayout horizontalListLayout = new UiHorizontalListLayout().addElement(moveLeftButton)
                .addElement(moveRightButton);
        layout.addElement(horizontalListLayout);
        return this;
    }

    private void resetPage() {
        if (page > container.groupCount() / ITEMS_PER_PAGE) {
            page = container.groupCount() / ITEMS_PER_PAGE;
        } else if (container.groupCount() % ITEMS_PER_PAGE == 0 && page == container.groupCount() / ITEMS_PER_PAGE) {
            page--;
        }
        if (page < 0) {
            page = 0;
        }
    }

    public SolItem getSelectedItem() {
        return selectedItem;
    }

    public UiItemList setItemContainer(ItemContainer container) {
        this.container = container;
        recalculate();
        return this;
    }

    private void setSelectedItem(UiElement uiElement, SolItem item) {
        for (UiStateButton<UiBooleanEnum> button : buttons) {
            if (button != uiElement) {
                button.setState(UiBooleanEnum.FALSE);
            } else {
                button.setState(UiBooleanEnum.TRUE);
            }
        }
        selectedItem = item;
    }

    @Override
    public int getWidth() {
        return layout.getWidth();
    }

    @Override
    public int getHeight() {
        return layout.getHeight();
    }

    @Override
    public void draw() {
        layout.draw();
    }

    @Override
    public boolean maybeFlashPressed(int keyCode) {
        return layout.maybeFlashPressed(keyCode);
    }

    @Override
    public boolean maybeFlashPressed(SolInputManager.InputPointer inputPointer) {
        return layout.maybeFlashPressed(inputPointer);
    }

    @Override
    public boolean update(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, boolean canBePressed, SolInputManager inputMan, SolApplication cmp) {
        return layout.update(inputPointers, cursorShown, canBePressed, inputMan, cmp);
    }

    @Override
    public void blur() {
        layout.blur();
    }
}
