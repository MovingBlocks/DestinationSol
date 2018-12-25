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
package org.destinationsol.nui.layers.hud;

import com.google.common.collect.Lists;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector2;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.destinationsol.nui.Canvas;
import org.destinationsol.nui.CoreWidget;
import org.destinationsol.nui.LayoutConfig;
import org.destinationsol.nui.databinding.Binding;
import org.destinationsol.nui.databinding.DefaultBinding;

import java.util.List;

/**
 */
public class UICrosshair extends CoreWidget {

    @LayoutConfig
    private List<TextureRegion> chargeStages = Lists.newArrayList();

    private Binding<Float> chargeAmount = new DefaultBinding<>(0f);

    @Override
    public void onDraw(Canvas canvas) {
        if (getChargeAmount() > 0 && !chargeStages.isEmpty()) {
            int state = TeraMath.floorToInt(getChargeAmount() * (chargeStages.size() - 1));
            canvas.drawTexture(chargeStages.get(state));
        }
    }

    @Override
    public Vector2 getPreferredContentSize(Canvas canvas, Vector2 sizeHint) {
        return Vector2.zero();
    }

    public void bindChargeAmount(Binding<Float> binding) {
        chargeAmount = binding;
    }

    public float getChargeAmount() {
        return Math.min(1.0f, chargeAmount.get());
    }

    public void setChargeAmount(float val) {
        chargeAmount.set(val);
    }
}
