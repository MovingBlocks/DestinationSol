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
package org.destinationsol.nui.widgets.browser.ui;

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.nui.widgets.browser.data.basic.flow.ContainerRenderSpace;
import org.destinationsol.nui.widgets.browser.ui.style.ParagraphRenderStyle;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class ContainerFlowContainerRenderSpace implements ContainerRenderSpace {
    private Deque<Rectangle> leftFloats = new LinkedList<>();
    private Deque<Rectangle> rightFloats = new LinkedList<>();
    private int containerWidth;

    public ContainerFlowContainerRenderSpace(int containerWidth) {
        this.containerWidth = containerWidth;
    }

    @Override
    public int getContainerWidth() {
        return containerWidth;
    }

    @Override
    public int getNextWidthChange(int y) {
        Rectangle lastLeftFloat = findLastAtYPosition(leftFloats, y);
        Rectangle lastRightFloat = findLastAtYPosition(rightFloats, y);

        if (lastLeftFloat != null && lastRightFloat != null) {
            return Math.min(lastLeftFloat.maxY(), lastRightFloat.maxY());
        } else if (lastLeftFloat != null) {
            return lastLeftFloat.maxY();
        } else if (lastRightFloat != null) {
            return lastRightFloat.maxY();
        } else {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public Rectangle addLeftFloat(int y, int width, int height) {
        int posY = y;
        while (true) {
            int availableWidth = getAvailableWidthAt(posY);
            if (availableWidth >= width) {
                int x = 0;
                Rectangle lastLeft = findLastAtYPosition(leftFloats, posY);
                if (lastLeft != null) {
                    x = lastLeft.maxX();
                }
                Rectangle floatRect = Rectangle.createFromMinAndSize(x, posY, width, height);
                leftFloats.add(floatRect);
                return floatRect;
            } else {
                Rectangle lastLeft = findLastAtYPosition(leftFloats, posY);
                Rectangle lastRight = findLastAtYPosition(rightFloats, posY);
                if (lastLeft != null && lastRight != null) {
                    posY = Math.min(lastLeft.maxY(), lastRight.maxY());
                } else if (lastLeft != null) {
                    posY = lastLeft.maxY();
                } else if (lastRight != null) {
                    posY = lastRight.maxY();
                }
            }
        }
    }

    @Override
    public Rectangle addRightFloat(int y, int width, int height) {
        int posY = y;
        while (true) {
            int availableWidth = getAvailableWidthAt(posY);
            if (availableWidth >= width) {
                int x = 0;
                Rectangle lastRight = findLastAtYPosition(rightFloats, posY);
                if (lastRight != null) {
                    x = lastRight.minX();
                }
                Rectangle floatRect = Rectangle.createFromMinAndSize(x - width, posY, width, height);
                rightFloats.add(floatRect);
                return floatRect;
            } else {
                Rectangle lastLeft = findLastAtYPosition(leftFloats, posY);
                Rectangle lastRight = findLastAtYPosition(rightFloats, posY);
                if (lastLeft != null && lastRight != null) {
                    posY = Math.min(lastLeft.maxY(), lastRight.maxY());
                } else if (lastLeft != null) {
                    posY = lastLeft.maxY();
                } else if (lastRight != null) {
                    posY = lastRight.maxY();
                }
            }
        }
    }

    @Override
    public int getNextClearYPosition(ParagraphRenderStyle.ClearStyle clearStyle) {
        int maxY = 0;
        if (clearStyle == ParagraphRenderStyle.ClearStyle.LEFT
                || clearStyle == ParagraphRenderStyle.ClearStyle.BOTH) {
            for (Rectangle leftFloat : leftFloats) {
                maxY = Math.max(maxY, leftFloat.maxY());
            }
        }
        if (clearStyle == ParagraphRenderStyle.ClearStyle.RIGHT
                || clearStyle == ParagraphRenderStyle.ClearStyle.BOTH) {
            for (Rectangle rightFloat : rightFloats) {
                maxY = Math.max(maxY, rightFloat.maxY());
            }
        }
        return maxY;
    }

    @Override
    public int getWidthForVerticalPosition(int y) {
        return getAvailableWidthAt(y);
    }

    @Override
    public int getAdvanceForVerticalPosition(int y) {
        Rectangle lastLeft = findLastAtYPosition(leftFloats, y);
        if (lastLeft != null) {
            return lastLeft.maxX();
        } else {
            return 0;
        }
    }

    private int getAvailableWidthAt(int y) {
        int width = containerWidth;
        Rectangle lastRight = findLastAtYPosition(rightFloats, y);
        if (lastRight != null) {
            width = lastRight.minX();
        }
        Rectangle lastLeft = findLastAtYPosition(leftFloats, y);
        if (lastLeft != null) {
            width -= lastLeft.maxX();
        }
        return width;
    }

    private Rectangle findLastAtYPosition(Deque<Rectangle> floats, int y) {
        Iterator<Rectangle> iterator = floats.descendingIterator();
        while (iterator.hasNext()) {
            Rectangle rect = iterator.next();
            if (rect.minY() <= y && rect.maxY() > y) {
                return rect;
            }
        }
        return null;
    }
}
