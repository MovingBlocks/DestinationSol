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
import org.terasology.math.TeraMath;
import com.badlogic.gdx.math.Rectangle;
import org.terasology.math.geom.Vector2;
import org.destinationsol.nui.BaseInteractionListener;
import org.destinationsol.nui.Canvas;
import org.destinationsol.nui.CoreWidget;
import org.destinationsol.nui.InteractionListener;
import org.destinationsol.nui.LayoutConfig;
import org.destinationsol.nui.databinding.Binding;
import org.destinationsol.nui.databinding.DefaultBinding;
import org.destinationsol.nui.events.NUIMouseClickEvent;
import org.destinationsol.nui.events.NUIMouseDragEvent;
import org.destinationsol.nui.events.NUIMouseReleaseEvent;

/**
 * A simple scrollbar
 */
public class UIScrollbar extends CoreWidget {

    @LayoutConfig
    private Binding<Integer> minimum = new DefaultBinding<>(0);
    @LayoutConfig
    private Binding<Integer> range = new DefaultBinding<>(100);

    @LayoutConfig
    private Binding<Integer> value = new DefaultBinding<>(0);

    @LayoutConfig
    private boolean vertical;

    private int sliderSize;
    private int handleSize;
    private boolean dragging;
    private int mouseOffset;

    private InteractionListener handleListener = new BaseInteractionListener() {

        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            if (event.getMouseButton() == MouseInput.MOUSE_LEFT) {
                dragging = true;
                Vector2 pos = event.getRelativeMousePosition();
                if (vertical) {
                    mouseOffset = pos.y - pixelOffsetFor(getValue());
                } else {
                    mouseOffset = pos.x - pixelOffsetFor(getValue());
                }
                return true;
            }
            return false;
        }

        @Override
        public void onMouseRelease(NUIMouseReleaseEvent event) {
            dragging = false;
        }

        @Override
        public void onMouseDrag(NUIMouseDragEvent event) {
            Vector2 pos = event.getRelativeMousePosition();
            if (vertical) {
                updatePosition(pos.y - mouseOffset);
            } else {
                updatePosition(pos.x - mouseOffset);
            }
        }
    };

    private InteractionListener sliderListener = new BaseInteractionListener() {
        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            if (event.getMouseButton() == MouseInput.MOUSE_LEFT) {
                Vector2 pos = event.getRelativeMousePosition();
                mouseOffset = (sliderSize > handleSize) ? (handleSize / 2) : 0;

                int pixelPosition = vertical ? pos.y - mouseOffset : pos.x - mouseOffset;
                updatePosition(pixelPosition);

                if (sliderSize > 0) {
                    int clamped = TeraMath.clamp(pixelPosition, 0, sliderSize);
                    setValue(clamped * getRange() / sliderSize);
                } else {
                    setValue(0);
                }

                dragging = true;
                return true;
            }
            return false;
        }

        @Override
        public void onMouseDrag(NUIMouseDragEvent event) {
            Vector2 pos = event.getRelativeMousePosition();
            if (vertical) {
                updatePosition(pos.y - mouseOffset);
            } else {
                updatePosition(pos.x - mouseOffset);
            }
        }

        @Override
        public void onMouseRelease(NUIMouseReleaseEvent event) {
            dragging = false;
        }
    };

    public UIScrollbar() {
        this(true);
    }

    public UIScrollbar(boolean vertical) {
        this.vertical = vertical;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (vertical) {
            canvas.setPart("sliderVertical");
        } else {
            canvas.setPart("sliderHorizontal");
        }
        canvas.drawBackground();
        canvas.addInteractionRegion(sliderListener);

        canvas.setPart("handle");
        if (vertical) {
            sliderSize = canvas.size().y - canvas.getCurrentStyle().getFixedHeight();
        } else {
            sliderSize = canvas.size().x - canvas.getCurrentStyle().getFixedWidth();
        }

        if (sliderSize > handleSize) {
            int drawLocation = pixelOffsetFor(getValue());
            Rectangle handleRegion;
            if (vertical) {
                handleSize = canvas.getCurrentStyle().getFixedHeight();
                handleRegion = Rectangle.createFromMinAndSize(0, drawLocation, canvas.getCurrentStyle().getFixedWidth(), handleSize);
            } else {
                handleSize = canvas.getCurrentStyle().getFixedWidth();
                handleRegion = Rectangle.createFromMinAndSize(drawLocation, 0, handleSize, canvas.getCurrentStyle().getFixedHeight());
            }
            canvas.drawBackground(handleRegion);
            canvas.addInteractionRegion(handleListener, handleRegion);
        }
    }

    @Override
    public Vector2 getPreferredContentSize(Canvas canvas, Vector2 sizeHint) {
        canvas.setPart("handle");
        int x = canvas.getCurrentStyle().getFixedWidth();
        if (x == 0) {
            x = canvas.getCurrentStyle().getMinWidth();
        }
        int y = canvas.getCurrentStyle().getFixedHeight();
        if (y == 0) {
            y = canvas.getCurrentStyle().getMinHeight();
        }
        return new Vector2(x, y);
    }

    private int pixelOffsetFor(int newValue) {
        final int r = getRange();
        return (r > 0) ? (sliderSize * newValue / r) : 0;
    }

    @Override
    public String getMode() {
        if (dragging) {
            return ACTIVE_MODE;
        } else if (handleListener.isMouseOver()) {
            return HOVER_MODE;
        }
        return DEFAULT_MODE;
    }

    public void bindMinimum(Binding<Integer> binding) {
        minimum = binding;
    }

    /**
     * @return The minimum value scrollable to.
     */
    public int getMinimum() {
        return minimum.get();
    }

    /**
     * @param val The new minimum above zero.
     */
    public void setMinimum(int val) {
        minimum.set(val);
    }

    public void bindRange(Binding<Integer> binding) {
        range = binding;
    }

    /**
     * @return The max value scrollable to.
     */
    public int getRange() {
        return Math.max(0, range.get());
    }

    /**
     * @param val The new maximum scrollable.
     */
    public void setRange(int val) {
        range.set(val);
    }

    public void bindValue(Binding<Integer> binding) {
        value = binding;
    }

    /**
     * @return The current scroll value.
     */
    public int getValue() {
        return TeraMath.clamp(value.get(), getMinimum(), getMinimum() + getRange());
    }

    /**
     * @param val The new level of scrolling to set.
     */
    public void setValue(int val) {
        value.set(val);
    }

    private void updatePosition(int pixelPos) {
        int newPosition = TeraMath.clamp(pixelPos, 0, sliderSize);
        setValue((sliderSize > 0) ? (newPosition * getRange() / sliderSize) : 0);
    }

}
