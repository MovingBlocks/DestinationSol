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

import java.util.Optional;

//TODO offer more ways to set dimensions. Optionally make resizable, using supplied dimensions as minimum
public class UiSpacerElement extends AbstractUiElement {
    private UiElement containedElement;

    /**
     * Sets this element to take as much space as {@code targetElement}.
     *
     * @param targetElement Element to set this element from
     * @return Self for method chaining
     */
    public UiSpacerElement setFromElement(UiElement targetElement) {
        width = targetElement.getWidth();
        height = targetElement.getHeight();
        return this;
    }

    public UiSpacerElement setContainedElement(UiElement containedElement) {
        this.containedElement = containedElement;
        containedElement.setPosition(x, y);
        return this;
    }

    @Override
    public UiSpacerElement setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        containedElement.setPosition(x, y);
        return this;
    }

    @Override
    public UiSpacerElement setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
        return this;
    }

    @Override
    public void draw() {
        containedElement.draw();
    }
}
