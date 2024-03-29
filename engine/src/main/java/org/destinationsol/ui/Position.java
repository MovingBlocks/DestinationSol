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
package org.destinationsol.ui;

public class Position {
    private final DisplayDimensions displayDimensions;

    private float xNormalized;
    private float yNormalized;

    public Position(DisplayDimensions displayDimensions, float xNormalized, float yNormalized) {
        this.displayDimensions = displayDimensions;

        set(xNormalized, yNormalized);
    }

    public void set(float xNormalized, float yNormalized) {
        this.xNormalized = xNormalized;
        this.yNormalized = yNormalized;
    }

    public int getX() {
        return (int) (xNormalized * displayDimensions.getWidth());
    }

    public int getY() {
        return (int) (yNormalized * displayDimensions.getHeight());
    }
}
