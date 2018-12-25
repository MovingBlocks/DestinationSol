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

import com.google.common.collect.Lists;
import org.destinationsol.nui.input.Keyboard;
import org.terasology.math.Border;
import com.badlogic.gdx.math.Rectangle;
import org.terasology.math.geom.Vector2;
import org.terasology.rendering.assets.font.Font;
import org.destinationsol.nui.ActivatableWidget;
import org.destinationsol.nui.BaseInteractionListener;
import org.destinationsol.nui.Canvas;
import org.destinationsol.nui.InteractionListener;
import org.destinationsol.nui.SubRegion;
import org.destinationsol.nui.TabbingManager;
import org.destinationsol.nui.databinding.Binding;
import org.destinationsol.nui.databinding.DefaultBinding;
import org.destinationsol.nui.events.NUIKeyEvent;
import org.destinationsol.nui.events.NUIMouseClickEvent;
import org.destinationsol.nui.itemRendering.ItemRenderer;
import org.destinationsol.nui.itemRendering.ToStringTextRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * A dropdown widget.
 * @param <T> the list element type
 */
public class UIDropdown<T> extends ActivatableWidget {

    private List<InteractionListener> optionListeners = Lists.newArrayList();

    protected int highlighted;

    private static final String LIST = "list";
    private static final String LIST_ITEM = "list-item";

    private Binding<List<T>> options = new DefaultBinding<>(new ArrayList<>());
    private Binding<T> selection = new DefaultBinding<>();

    private ItemRenderer<T> optionRenderer = new ToStringTextRenderer<>();
    private boolean opened;
    private InteractionListener mainListener = new BaseInteractionListener() {
        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            opened = !opened;
            optionListeners.clear();
            if (opened) {
                for (int i = 0; i < getOptions().size(); ++i) {
                    optionListeners.add(new ItemListener(i));
                }
            }
            return true;
        }
    };

    public UIDropdown() {

    }

    public UIDropdown(String id) {
        super(id);
    }

    @Override
    public boolean isSkinAppliedByCanvas() {
        return false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBackground();
        try (SubRegion ignored = canvas.subRegion(canvas.getCurrentStyle().getMargin().shrink(canvas.getRegion()), false)) {
            if (selection.get() != null) {
                optionRenderer.draw(selection.get(), canvas);
            }
        }

        if (!isEnabled()) {
            // do not open and do not add an interaction region
        } else if (opened) {
            canvas.setPart(LIST);
            canvas.setDrawOnTop(true);
            Font font = canvas.getCurrentStyle().getFont();
            Border itemMargin = canvas.getCurrentStyle().getMargin();
            int height = (font.getLineHeight() + itemMargin.getTotalHeight()) * options.get().size() + canvas.getCurrentStyle().getBackgroundBorder().getTotalHeight();
            canvas.addInteractionRegion(mainListener, Rectangle.createFromMinAndSize(0, 0, canvas.size().x, canvas.size().y + height));

            Rectangle location = Rectangle.createFromMinAndSize(0, canvas.size().y, canvas.size().x, height);
            canvas.drawBackground(location);

            int itemHeight = itemMargin.getTotalHeight() + font.getLineHeight();
            canvas.setPart(LIST_ITEM);

            for (int i = 0; i < optionListeners.size(); ++i) {
                if (optionListeners.get(i).isMouseOver()) {
                    canvas.setMode(HOVER_MODE);
                } else if (i==highlighted) {
                    canvas.setMode(HOVER_MODE);
                    setSelection(getOptions().get(highlighted));
                } else {
                    canvas.setMode(DEFAULT_MODE);
                }
                Rectangle itemRegion = Rectangle.createFromMinAndSize(0, canvas.size().y + itemHeight * i, canvas.size().x, itemHeight);
                canvas.drawBackground(itemRegion);
                optionRenderer.draw(options.get().get(i), canvas, itemMargin.shrink(itemRegion));
                canvas.addInteractionRegion(optionListeners.get(i), itemRegion);
            }
        } else {
            canvas.addInteractionRegion(mainListener);
        }
    }

    @Override
    public Vector2 getPreferredContentSize(Canvas canvas, Vector2 areaHint) {
        if (selection.get() != null) {
            return canvas.getCurrentStyle().getMargin().grow(optionRenderer.getPreferredSize(selection.get(), canvas));
        }
        return canvas.getCurrentStyle().getMargin().grow(new Vector2(1, canvas.getCurrentStyle().getFont().getLineHeight()));
    }

    @Override
    public String getMode() {
        if (!isEnabled()) {
            return DISABLED_MODE;
        } else if (opened || (TabbingManager.focusedWidget != null && TabbingManager.focusedWidget.equals(this))) {
            return ACTIVE_MODE;
        }
        return DEFAULT_MODE;
    }

    @Override
    public void onLoseFocus() {
        super.onLoseFocus();
        opened = false;
    }

    public void bindOptions(Binding<List<T>> binding) {
        options = binding;
    }

    /**
     * Get all the options from the dropdown.
     *
     * @return A List containing all the options.
     */
    public List<T> getOptions() {
        return options.get();
    }

    /**
     * Set a new set of options for the dropdown.
     *
     * @param values A List containing the new options.
     */
    public void setOptions(List<T> values) {
        this.options.set(values);
    }

    public void bindSelection(Binding<T> binding) {
        this.selection = binding;
    }

    /**
     * Get the currently selected item.
     *
     * @return The currently selected item.
     */
    public T getSelection() {
        return selection.get();
    }

    /**
     * Set the item from that should be selected.
     * Note that this item does not actually have to be contained in the List of options.
     *
     * @param value The item to set as selected.
     */
    public void setSelection(T value) {
        selection.set(value);
    }

    /**
     * Set the renderer to use for the options.
     * This is used to display the options on the dropdown list and in the selection box.
     *
     * @param itemRenderer The new item renderer.
     */
    public void setOptionRenderer(ItemRenderer<T> itemRenderer) {
        optionRenderer = itemRenderer;
    }

    public void setOpenedReverse() {
        opened = !opened;
        optionListeners.clear();
        if (opened) {
            for (int i = 0; i < getOptions().size(); ++i) {
                optionListeners.add(new ItemListener(i));
            }
        }
    }

    private class ItemListener extends BaseInteractionListener {
        private int index;

        ItemListener(int index) {
            this.index = index;
        }

        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            setSelection(getOptions().get(index));
            opened = false;
            return true;
        }
    }

    public void changeHighlighted(boolean increase) {
        if (increase) {
            highlighted++;
            if (highlighted >= getOptions().size()) {
                highlighted = 0;
            }
        } else {
            highlighted--;
            if (highlighted < 0) {
                highlighted = getOptions().size()-1;
            }
        }
        setSelection(getOptions().get(highlighted));
    }

    public boolean isOpened() { return opened; }

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        if (event.isDown()) {
            boolean dropdownOpen = false;

            if (TabbingManager.focusedWidget instanceof UIDropdown) {
                dropdownOpen = ((UIDropdown) TabbingManager.focusedWidget).isOpened();
            }
            if (TabbingManager.focusedWidget != null && dropdownOpen) {
                TabbingManager.setWidgetIsOpen(true);
            } else {
                TabbingManager.setWidgetIsOpen(false);
            }

            int keyId = event.getKey().getId();
            if (keyId == Keyboard.KeyId.UP) {
                this.changeHighlighted(false);
                return true;
            } else if (keyId == Keyboard.KeyId.DOWN) {
                this.changeHighlighted(true);
                return true;
            }
        }
        return super.onKeyEvent(event);
    }
}
