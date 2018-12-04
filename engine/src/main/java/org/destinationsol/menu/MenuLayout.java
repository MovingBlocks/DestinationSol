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

package org.destinationsol.menu;

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.SolApplication;
import org.destinationsol.ui.DisplayDimensions;

public class MenuLayout {
    public static final float BG_BORDER = .03f;
    private static final int numberOfRowsTotal = 5;
    private final float btnW;
    private final float btnH;
    private final float colCenter;
    private final float row0;
    private final float rowH;
    private final float myPad;

    public MenuLayout() {
        DisplayDimensions displayDimensions = SolApplication.displayDimensions;

        btnW = .40f * displayDimensions.getRatio();
        btnH = .1f;
        myPad = .1f * btnH;
        rowH = btnH + myPad;
        colCenter = .5f * displayDimensions.getRatio() - btnW / 2;
        row0 = 1 - myPad - numberOfRowsTotal * rowH;
    }

    static Rectangle bottomRightFloatingButton(DisplayDimensions aspectRatio) {
        final float BUTTON_WIDTH = .15f;
        final float BUTTON_HEIGHT = .07f;

        return new Rectangle(aspectRatio.getRatio() - BUTTON_WIDTH, 1 - BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    public Rectangle buttonRect(int col, int row) {
        float x = col == -1 ? colCenter : .5f; //unfinished
        float y = row0 + rowH * row;
        return new Rectangle(x, y, btnW, btnH);
    }

    public Rectangle background(int colCount, int startRow, int rowCount) {
        float x = colCount == -1 ? colCenter : .5f; //unfinished
        float y = row0 + rowH * startRow;
        return new Rectangle(x - BG_BORDER, y - BG_BORDER, btnW + 2 * BG_BORDER, rowH * rowCount - myPad + 2 * BG_BORDER);
    }
}
