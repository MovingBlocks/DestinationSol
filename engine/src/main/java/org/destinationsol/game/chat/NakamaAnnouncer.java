// Copyright 2026 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.destinationsol.game.chat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.Const;
import org.destinationsol.SolApplication;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.UiDrawer;

/**
 * Displays incoming Nakama cross-game chat messages as a fading banner
 * on the game HUD, similar to zone name announcements.
 */
public class NakamaAnnouncer {
    private static final float FADE_TIME = 5f;
    private static final float DISPLAY_Y = 0.06f;
    private static final float BG_PADDING_X = 0.08f;
    private static final float BG_PADDING_Y = 0.008f;
    private static final float BG_HEIGHT = 0.03f;

    private final DisplayDimensions displayDimensions;
    private final Color textColor = new Color(0.6f, 0.9f, 1f, 0f);
    private final Color bgColor = new Color(0f, 0f, 0f, 0f);
    private String text = "";

    public NakamaAnnouncer() {
        displayDimensions = SolApplication.displayDimensions;
    }

    /**
     * Show a new message. Resets the fade timer.
     */
    public void announce(String message) {
        text = message;
        textColor.a = 1f;
        bgColor.a = 0.8f;
    }

    public void update() {
        if (textColor.a > 0) {
            textColor.a -= Const.REAL_TIME_STEP / FADE_TIME;
            bgColor.a = Math.min(0.8f, textColor.a) * 0.8f;
        }
    }

    public void drawText(UiDrawer uiDrawer) {
        if (textColor.a <= 0) {
            return;
        }
        float centerX = displayDimensions.getRatio() / 2;
        Rectangle bg = new Rectangle(
                centerX - BG_PADDING_X - displayDimensions.getRatio() * 0.15f,
                DISPLAY_Y - BG_PADDING_Y,
                displayDimensions.getRatio() * 0.3f + BG_PADDING_X * 2,
                BG_HEIGHT + BG_PADDING_Y * 2
        );
        uiDrawer.draw(bg, bgColor);
        uiDrawer.drawString(text, centerX, DISPLAY_Y + BG_HEIGHT / 2, FontSize.MENU, true, textColor);
    }
}
