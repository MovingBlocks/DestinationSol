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
package org.destinationsol.nui.widgets;

import org.terasology.input.MouseInput;
import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.nui.BaseInteractionListener;
import org.destinationsol.nui.Canvas;
import org.destinationsol.nui.InteractionListener;
import org.destinationsol.nui.events.NUIMouseClickEvent;

/**
 * A text widget with a button to clear the text.
 */
public class ResettableUIText extends UIText {

    private InteractionListener clearInteractionListener = new BaseInteractionListener() {
        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            if (event.getMouseButton() == MouseInput.MOUSE_LEFT) {
                setText("");
                return true;
            }
            return false;
        }
    };

    @Override
    public void onDraw(Canvas canvas) {
        Rectangle clearButtonRegion = Rectangle.createFromMinAndSize(0, 0, 30, canvas.size().y);
        lastWidth = canvas.size().x - clearButtonRegion.size().x;
        if (isEnabled()) {
            canvas.addInteractionRegion(interactionListener, Rectangle.createFromMinAndMax(0, 0, canvas.size().x, canvas.size().y));
            canvas.addInteractionRegion(clearInteractionListener, Rectangle.createFromMinAndMax(canvas.size().x, 0, canvas.size().x +
                    clearButtonRegion.size().x, canvas.size().y));
        }
        drawAll(canvas, canvas.size().x - clearButtonRegion.size().x);
    }
}
