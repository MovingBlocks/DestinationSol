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
package org.destinationsol.nui.contextMenu;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import org.destinationsol.nui.BaseInteractionListener;
import org.destinationsol.nui.Canvas;
import org.destinationsol.nui.CoreScreenLayer;
import org.destinationsol.nui.InteractionListener;
import org.destinationsol.nui.events.NUIMouseClickEvent;
import org.destinationsol.nui.events.NUIMouseWheelEvent;
import org.destinationsol.nui.widgets.UIList;
import org.terasology.assets.ResourceUrn;

import java.util.List;

/**
 * A generic context menu, implemented as a {@link CoreScreenLayer} spanning the canvas area it should be created within.
 */
public class ContextMenuScreen extends CoreScreenLayer {

    public static final ResourceUrn ASSET_URI = new ResourceUrn("engine:contextMenuScreen");

    /**
     * A list of widgets to be used to draw the context menu.
     */
    private List<UIList<AbstractContextMenuItem>> menuWidgets = Lists.newArrayList();
    /**
     * The initial position of the menu.
     */
    private Vector2 position;

    private InteractionListener mainListener = new BaseInteractionListener() {
        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            // Close the context menu on click outside it.
            getManager().closeScreen(ASSET_URI);

            return false;
        }

        @Override
        public boolean onMouseWheel(NUIMouseWheelEvent event) {
            // Close the context menu on mouse wheel scroll outside it.
            getManager().closeScreen(ASSET_URI);

            // Consume the event to prevent awkward rendering if the menu is within a scrollable widget.
            return true;
        }
    };

    @Override
    public void initialise() {
        find("menu", UIList.class).setCanBeFocus(false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.addInteractionRegion(mainListener);
        Vector2 currentPosition = null;
        int currentWidth = 0;
        for (UIList<AbstractContextMenuItem> level : menuWidgets) {
            if (level.isVisible()) {
                if (currentPosition == null) {
                    currentPosition = new Vector2(position);
                } else {
                    currentPosition.x += currentWidth;
                }
                Vector2 size = canvas.calculatePreferredSize(level);
                Rectangle region = new Rectangle(currentPosition.x,currentPosition.y,size.x,size.y);
                double percentageThreshold = 0.9;
                if (region.y + region.height > canvas.getRegion().height * percentageThreshold) {
                    region = new Rectangle(region.x,
                        region.y
                                - (region.y + region.height
                                - canvas.getRegion().height)
                                - (int) (canvas.getRegion().height * (1 - percentageThreshold)),
                        region.x + region.width,
                        canvas.getRegion().height);
                }
                currentWidth = (int) (canvas.calculatePreferredSize(level).x - 8);
                canvas.drawWidget(level, region);
            }
        }
    }

    public void setMenuWidgets(List<UIList<AbstractContextMenuItem>> levels) {
        menuWidgets = levels;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
