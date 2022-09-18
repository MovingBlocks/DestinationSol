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

import org.joml.Vector2i;
import org.terasology.nui.AbstractWidget;
import org.terasology.nui.Canvas;
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.UIWidget;

import java.util.Collections;
import java.util.Iterator;

/**
 * A container which returns a zero size if it, or its content, is invisible. Otherwise, this element should act
 * completely transparently.
 * This can be an alternative to adding and/or removing elements dynamically.
 */
public class EmptyIfInvisibleContainer extends AbstractWidget {
    @LayoutConfig
    private UIWidget content;

    public EmptyIfInvisibleContainer() {
    }

    public EmptyIfInvisibleContainer(String id) {
        setId(id);
    }

    public EmptyIfInvisibleContainer(UIWidget content) {
        this.content = content;
    }

    public EmptyIfInvisibleContainer(String id, UIWidget content) {
        setId(id);
        this.content = content;
    }

    public void setContent(UIWidget content) {
        this.content = content;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawWidget(content);
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        if (!this.isVisible() || content == null || !content.isVisible()) {
            return new Vector2i();
        }

        return canvas.calculateRestrictedSize(content, sizeHint);
    }

    @Override
    public Vector2i getMaxContentSize(Canvas canvas) {
        if (!this.isVisible() || content == null || !content.isVisible()) {
            return new Vector2i();
        }

        return canvas.calculateMaximumSize(content);
    }

    @Override
    public Iterator<UIWidget> iterator() {
        if (content != null) {
            return Collections.singletonList(content).iterator();
        }

        return Collections.emptyIterator();
    }

    @Override
    public boolean isSkinAppliedByCanvas() {
        return false;
    }

    @Override
    public boolean canBeFocus() {
        return false;
    }
}
