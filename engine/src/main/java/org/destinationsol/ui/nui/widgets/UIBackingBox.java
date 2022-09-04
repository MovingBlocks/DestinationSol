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

package org.destinationsol.ui.nui.widgets;

import org.terasology.nui.BaseInteractionListener;
import org.terasology.nui.Canvas;
import org.terasology.nui.InteractionListener;
import org.terasology.nui.events.NUIMouseClickEvent;
import org.terasology.nui.events.NUIMouseDoubleClickEvent;
import org.terasology.nui.widgets.UIBox;

/**
 * A {@link UIBox} that is designed to back an existing group of widgets, acting effectively as the background
 * but absorbing all click and double-click events to prevent event propagation to the screens behind it.
 */
public class UIBackingBox extends UIBox {
    private static final InteractionListener BLOCKING_INTERACTION_LISTENER = new BaseInteractionListener() {
        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            return true;
        }

        @Override
        public boolean onMouseDoubleClick(NUIMouseDoubleClickEvent event) {
            return true;
        }
    };

    @Override
    public void onDraw(Canvas canvas) {
        canvas.addInteractionRegion(BLOCKING_INTERACTION_LISTENER);
        super.onDraw(canvas);
    }
}
