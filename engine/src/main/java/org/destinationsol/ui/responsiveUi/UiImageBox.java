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

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.textures.DSTexture;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.DisplayDimensions;

import java.util.Optional;

/**
 * Thic class is user to display images as part of UI. Please note, that due to scaling issues, this class does not
 * implement {@link UiResizableElement}, although it implements its methods. This allows you to resize the image
 * manually to suit your needs, while preventing unexpected rescaling from parent elements. If you want this element to be automatically resizable, please use {@link UiResizableImageBox}
 */
public class UiImageBox extends AbstractUiElement {
    private TextureRegion image;

    @Override
    public UiImageBox setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public UiImageBox setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
        return this;
    }

    public UiImageBox setImage(TextureRegion image) {
        this.image = image;
        recalculate();
        return this;
    }

    public UiImageBox setImage(DSTexture image) {
        this.image = new TextureRegion(image.getTexture());
        recalculate();
        return this;
    }

    @Override
    public void draw() {
        final DisplayDimensions displayDimensions = SolApplication.displayDimensions;
//        SolApplication.getUiDrawer().draw(getScreenArea(), Color.WHITE);
        SolApplication.getUiDrawer().draw(
                image,
                displayDimensions.getFloatWidthForPixelWidth(width),
                displayDimensions.getFloatHeightForPixelHeight(height),
                0, 0,
                displayDimensions.getFloatWidthForPixelWidth(x - width / 2),
                displayDimensions.getFloatHeightForPixelHeight(y - height / 2),
                0, SolColor.WHITE);
    }

    public UiImageBox setWidth_(int width) {
        this.width = width;
        return this;
    }

    public UiImageBox setHeight_(int height) {
        this.height = height;
        return this;
    }

    public int getDefaultHeight() {
        return image.getRegionHeight();
    }

    public int getDefaultWidth() {
        return image.getRegionWidth();
    }

    @Override
    public UiImageBox recalculate() {
        width = image.getRegionWidth();
        height = image.getRegionHeight();
        getParent().ifPresent(UiContainerElement::recalculate);
        return this;
    }
}
