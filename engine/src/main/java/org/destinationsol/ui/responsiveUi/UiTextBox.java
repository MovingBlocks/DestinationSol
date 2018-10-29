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

import com.badlogic.gdx.graphics.Color;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Single as well multi-line textBox for displaying all kinds of text.
 *
 * Doesn't add any padding around the text, but inserts spacers between its lines. Aligns the text to the left.
 */
public class UiTextBox extends AbstractUiElement {
    private int x;
    private int y;
    private float fontSize = FontSize.HINT;

    private List<String> lines = new ArrayList<>();
    private int width;
    private int height;
    private int lineSpacer;
    private Color color = SolColor.WHITE;

    public UiTextBox setFontSize(float fontSize) {
        this.fontSize = fontSize;
        recalculate();
        return this;
    }

    @Override
    public UiTextBox setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public UiTextBox setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
        return this;
    }

    @Override
    public UiTextBox recalculate() {
        width = lines.stream().map(line -> SolApplication.getUiDrawer().getStringLength(line, fontSize)).max(Integer::compareTo).orElse(0);
        height = lines.stream().map(line -> SolApplication.getUiDrawer().getStringHeight(line, fontSize)).reduce(Integer::sum).orElse(0);
        lineSpacer = (height / lines.size()) / 2;
        height += lineSpacer * lines.size() - 1;
        return this;
    }

    /**
     * Sets the contents of this element to the given {@code text}.
     * <p>
     * Supports newlines through {@code '\n'} character. No other special characters are supported.
     *
     * @param text Text to set
     * @return Self for method chaining
     */
    public UiTextBox setText(String text) {
        lines.clear();
        lines.addAll(Arrays.asList(text.split("\n")));
        recalculate();
        return this;
    }

    @Override
    public int getX() {
        return x;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void draw() {
        final UiDrawer uiDrawer = SolApplication.getUiDrawer();
        int textY = y - (height / 2);
        final int textX = x - (width / 2);
        final int lineHeight = height / lines.size();
        final DisplayDimensions displayDimensions = SolApplication.displayDimensions;
        for (String line : lines) {
            uiDrawer.drawString(line, displayDimensions.getFloatWidthForPixelWidth(textX), displayDimensions.getFloatHeightForPixelHeight(textY), fontSize, UiDrawer.TextAlignment.LEFT,false, color);
            textY += lineSpacer + lineHeight;
        }
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

    }
}
