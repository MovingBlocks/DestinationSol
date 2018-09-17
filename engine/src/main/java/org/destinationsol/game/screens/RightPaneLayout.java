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
package org.destinationsol.game.screens;

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.SolApplication;
import org.destinationsol.ui.DisplayDimensions;

public class RightPaneLayout {
    private final float btnH;
    private final float btnW;
    private final float row0;
    private final float rowH;
    private final float col0;

    public RightPaneLayout() {
        DisplayDimensions displayDimensions = SolApplication.displayDimensions;

        btnH = .07f;
        rowH = 1.1f * btnH;
        row0 = .1f;
        btnW = 3 * btnH;
        col0 = displayDimensions.getRatio() - btnW;
    }

    public Rectangle buttonRect(int row) {
        float x = col0;
        float y = row0 + rowH * row;
        return new Rectangle(x, y, btnW, btnH);
    }
}
