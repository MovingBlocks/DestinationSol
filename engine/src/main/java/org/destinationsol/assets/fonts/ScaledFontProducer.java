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
package org.destinationsol.assets.fonts;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.destinationsol.assets.AssetHelper;
import org.terasology.gestalt.assets.AssetDataProducer;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.module.annotations.RegisterAssetDataProducer;
import org.terasology.gestalt.naming.Name;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * An {@link AssetDataProducer} that returns the specified font scaled to the size provided in the base ResourceUrn's
 * fragment part.
 *
 * For example, a font of "engine:main#2" would return the font data from "engine:main" scaled by a factor
 * of 2x. The scale can be fractional, for example "engine:main#0.5" would produce a font that is half the size.
 */
@RegisterAssetDataProducer
public class ScaledFontProducer implements AssetDataProducer<FontData> {
    /**
     * The asset manager used to obtain the base font data.
     */
    private final AssetHelper assetHelper;

    /**
     * Creates a new {@link ScaledFontProducer} to produce scaled fonts assets depending on the ResourceUrn provided.
     * @param assetHelper the asset manager to use when obtaining base font data
     */
    @Inject
    public ScaledFontProducer(AssetHelper assetHelper) {
        this.assetHelper = assetHelper; // TODO provide AssetManager
    }

    @Override
    public Set<ResourceUrn> getAvailableAssetUrns() {
        return Collections.emptySet();
    }

    @Override
    public Set<Name> getModulesProviding(Name resourceName) {
        return Collections.emptySet();
    }

    @Override
    public ResourceUrn redirect(ResourceUrn urn) {
        return urn;
    }

    @Override
    public Optional<FontData> getAssetData(ResourceUrn urn) throws IOException {
        if (urn.getFragmentName().isEmpty()) {
            return Optional.empty();
        }

        float scale = Float.parseFloat(urn.getFragmentName().toString());
        Optional<Font> fontAsset = assetHelper.get(urn.getRootUrn(), Font.class);
        if (!fontAsset.isPresent()) {
            return Optional.empty();
        }

        BitmapFont bitmapFont = fontAsset.get().getBitmapFont();
        // Invert the flip value because the original font may be already flipped.
        BitmapFont.BitmapFontData fontData = new BitmapFont.BitmapFontData(bitmapFont.getData().getFontFile(), !bitmapFont.getData().flipped);
        fontData.setScale(scale);

        return Optional.of(new FontData(new BitmapFont(fontData, bitmapFont.getRegions(), bitmapFont.isFlipped())));
    }
}
