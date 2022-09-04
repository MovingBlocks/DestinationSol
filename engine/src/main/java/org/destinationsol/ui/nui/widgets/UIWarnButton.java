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

import org.destinationsol.Const;
import org.joml.Math;
import org.terasology.nui.Canvas;
import org.terasology.nui.widgets.UIButton;

/**
 * A standard {@link UIButton} that has been adapted to use the {@link UIWarnButton#WARN_MODE} style mode
 * when the user should be notified of an event e.g. a new item in their inventory.
 */
public class UIWarnButton extends KeyActivatedButton {
    public static final String WARN_MODE = "warn";

    // Warn colour code taken from SolInputManager
    private static final int WARN_COUNTER_MAX = 2;
    private static final float WARN_PERC_GROWTH_TIME = 1f;
    private static final float WARN_PERC_MIN = 0f;
    private static final float WARN_PERC_MAX = 1f;

    private int warnCounter = 0;
    private float warnPercentage;
    private boolean warnPercGrows;
    private float warnAlpha = 1f;

    public UIWarnButton() {
    }

    public UIWarnButton(String id) {
        super(id);
    }

    public UIWarnButton(String id, String text) {
        super(id, text);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (warnCounter > 0) {
            canvas.drawBackground();
            canvas.setMode(WARN_MODE);
            canvas.setAlpha(warnAlpha);
            canvas.drawBackground();
            canvas.setAlpha(1);
            canvas.setMode(getMode());
        } else {
            canvas.drawBackground();
        }
        super.onDraw(canvas);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        float dif = (warnPercGrows ? 1 : -1) * Const.REAL_TIME_STEP / WARN_PERC_GROWTH_TIME;
        warnPercentage += dif;
        if (warnPercentage < WARN_PERC_MIN || WARN_PERC_MAX < warnPercentage) {
            warnPercentage = Math.clamp(WARN_PERC_MIN, WARN_PERC_MAX, warnPercentage);
            warnPercGrows = !warnPercGrows;
        }
        warnAlpha = warnPercentage * .5f;

        if (warnCounter > 0) {
            warnCounter--;
        }
    }

    /**
     * Starts the "warn" phase of the button, causing it to pulsate briefly
     * the colour specified for the mode {@link UIWarnButton#WARN_MODE}.
     *
     * This can be used to notify the user of an event, such as a new item that is now in their inventory.
     */
    public void enableWarn() {
        warnCounter = WARN_COUNTER_MAX;
    }

    /**
     * Returns true if the button is currently in a "warn" phase.
     * @return true, if the button is currently in a "warn" phase, otherwise false.
     */
    public boolean isWarning() {
        return warnCounter > 0;
    }

    @Override
    public boolean isSkinAppliedByCanvas() {
        return false;
    }
}
