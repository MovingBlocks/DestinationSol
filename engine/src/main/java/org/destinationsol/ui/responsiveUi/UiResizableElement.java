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

/**
 * {@link UiElement UiElements} implementing this interface can be externally resized, for instance to fully fill available space.
 *
 * Depending on the specific {@code UiElement}, it may then retain the set size indefinitely, or resize itself in one or
 * both dimensions to fit its contents or to fulfill other requirements. This should be however always specified in the
 * documentation of specific {@code UiElement}.
 */
public interface UiResizableElement extends UiElement {
    /**
     * Sets width to the element.
     *
     * May silently fail or outright crash if {@code width} is lesser than what is returned by {@link #getMinWidth()}.
     * @param width Requested width
     * @return Self for method chaining
     */
    UiResizableElement setWidth(int width);

    /**
     * Sets height to the element.
     *
     * May silently fail or outright crash if {@code height} is lesser than what is returned by {@link #getMinHeight()}.
     * @param height Requested height
     * @return Self for method chaining
     */
    UiResizableElement setHeight(int height);

    /**
     * Returns the minimal width this element must have to be able to contain its contents or fill other requirements.
     *
     * @return The minimal width
     */
    default int getMinHeight() {
        return 0;
    }

    /**
     * Returns the minimal height this element must have to be able to contain its contents or fill other requirements.
     *
     * @return The minimal height
     */
    default int getMinWidth() {
        return 0;
    }
}
