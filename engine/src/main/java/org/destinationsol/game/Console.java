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
package org.destinationsol.game;

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.Collections;
import java.util.List;

public class Console implements SolUiScreen {
    @Override
    public List<SolUiControl> getControls() {
        return Collections.emptyList();
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(new Rectangle(0.05f, 0.05f, 0.5f * uiDrawer.r, 0.5f), SolColor.UI_LIGHT);
    }
}
