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
import org.destinationsol.common.SolColor;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.ui.SolInputManager;

import java.util.Optional;

//TODO icons should be colorfull depending on item type.
public class UiItemBox extends AbstractUiElement {
    private int x;
    private int y;
    private UiImageBox icon = new UiImageBox();
    private SolItem item;
    private UiTextBox statusTextBox = new UiTextBox().setColor(SolColor.W50);
    private UiTextBox nameTextBox = new UiTextBox();
    private UiTextBox priceTextBox = new UiTextBox().setColor(SolColor.W50);
    private int amount;
    private UiTextBox amountTextBox = new UiTextBox();
    private UiHorizontalListLayout layout = new UiHorizontalListLayout().addElement(icon)
            .addElement(new UiSpacerElement().setFromElement(new UiTextBox().setText("using")).setContainedElement(statusTextBox))
            .addElement(new UiSpacerElement().setFromElement(new UiTextBox().setText("Quetzalcoatl's deadly proton accelerator")).setContainedElement(nameTextBox))
            .addElement(new UiSpacerElement().setFromElement(new UiTextBox().setText("987654321")).setContainedElement(priceTextBox))
            .addElement(new UiSpacerElement().setFromElement(new UiTextBox().setText("9999x")).setContainedElement(amountTextBox));

    @Override
    public UiItemBox recalculate() {
        icon.setImage(item.getIcon(SolApplication.getInstance().getGame()));
        statusTextBox.setText(item.isEquipped() > 0 ? "using" : "");
        nameTextBox.setText(item.getDisplayName());
        priceTextBox.setText(String.valueOf(item.getPrice()));
        amountTextBox.setText(String.valueOf(amount) + "x");
        return this;
    }

    public UiItemBox setItem(SolItem item) {
        this.item = item;
        recalculate();
        return this;
    }

    public UiItemBox setAmount(int amount) {
        this.amount = amount;
        recalculate();
        return this;
    }


    @Override
    public UiItemBox setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        layout.setPosition(x, y);
        return this;
    }

    @Override
    public UiItemBox setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
        return this;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
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
        return false;
    }

    @Override
    public boolean maybeFlashPressed(SolInputManager.InputPointer inputPointer) {
        return false;
    }

    @Override
    public boolean update(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, boolean canBePressed, SolInputManager inputMan, SolApplication cmp) {
        return false;
    }

    @Override
    public void blur() {
        layout.blur();
    }
}
