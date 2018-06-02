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
package org.destinationsol.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolException;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Console implements SolUiScreen {
    private static final Vector2 TOP_LEFT = new Vector2(0.03f, 0.03f);
    private static final Vector2 BOTTOM_RIGHT = new Vector2(0.8f, 0.5f);
    private static final float FRAME_WIDTH = 0.02f;
    private static Console instance;
    private final BitmapFont font;
    private final SolUiControl exitControl;
    private final List<String> linesOfOutput;

    private final List<SolUiControl> controls;

    private boolean isActive;

    private Console() {
        font = Assets.getFont("engine:main").getBitmapFont();
        exitControl = new SolUiControl(null, true, Input.Keys.ESCAPE);
        controls = Collections.singletonList(exitControl);
        linesOfOutput = new ArrayList<>();
        println("Welceome to the world of Destination Sol! Your journey begins!");
    }

    public void println(String s) {
        try {
            int width = 0;
            StringBuilder currentLine = new StringBuilder();
            for (char c : s.toCharArray()) {
                width += (c == ' ' ? 3 : 1) * font.getData().getGlyph(c).width; // Why is this multiplier here? Well, don't ask, I don't know and I wrote it.
                if (width > 1040) {
                    linesOfOutput.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    width = (c == ' ' ? 3 : 1) * font.getData().getGlyph(c).width;
                }
                currentLine.append(c);
            }
            linesOfOutput.add(currentLine.toString());
        } catch (NullPointerException e) {
            throw new SolException("Exception in console: Unicode characters are not permitted. Newlines are not permitted.");
        }
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (exitControl.isJustOff()) {
            solApplication.getInputManager().setScreen(solApplication, solApplication.getGame().getScreens().mainScreen);
        }
    }

    public static Console getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new Console();
            return instance;
        }
    }

    public void registerCharEntered(char c) {
        if (isActive) {
            System.out.println(Character.getNumericValue(c));
            if (c == '\r') {
                System.out.println("newline");
            }
            if (c == '\b') {
                System.out.println("backSpace");
            }
        }
    }

    @Override
    public boolean isCursorOnBackground(SolInputManager.InputPointer inputPointer) {
        return true;
    }

    @Override
    public void blurCustom(SolApplication solApplication) {
        isActive = false;
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        isActive = true;
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        drawFrame(uiDrawer);
        // Text area
        uiDrawer.draw(new Rectangle(TOP_LEFT.x + 2 * FRAME_WIDTH,
                        TOP_LEFT.y + 2 * FRAME_WIDTH,
                        (BOTTOM_RIGHT.x - TOP_LEFT.x) - 4 * FRAME_WIDTH,
                        BOTTOM_RIGHT.y - TOP_LEFT.y - 4 * FRAME_WIDTH),
                SolColor.UI_BG_LIGHT);
        drawTextEntrySeparator(uiDrawer);
    }

    private void drawTextEntrySeparator(UiDrawer uiDrawer) {
        uiDrawer.drawLine(TOP_LEFT.x + 2 * FRAME_WIDTH, getLineY(20.333f), 0, (BOTTOM_RIGHT.x - TOP_LEFT.x) - 4 * FRAME_WIDTH, Color.WHITE);
    }

    private void drawFrame(UiDrawer uiDrawer) {
        uiDrawer.draw(new Rectangle(TOP_LEFT.x, TOP_LEFT.y,
                        (BOTTOM_RIGHT.x - TOP_LEFT.x), BOTTOM_RIGHT.y - TOP_LEFT.y),
                SolColor.UI_LIGHT);
        uiDrawer.draw(new Rectangle(TOP_LEFT.x + FRAME_WIDTH, TOP_LEFT.y + FRAME_WIDTH,
                        (BOTTOM_RIGHT.x - TOP_LEFT.x) - 2 * FRAME_WIDTH, BOTTOM_RIGHT.y - TOP_LEFT.y - 2 * FRAME_WIDTH),
                SolColor.UI_BG_LIGHT);
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        String s = "randomtext";
        final float textX = TOP_LEFT.x + 2 * FRAME_WIDTH;
        for (int line = 0; line < 20; line++) {
            if (linesOfOutput.size() + line > 19) {
                final String text = linesOfOutput.get(linesOfOutput.size() - 20 + line);
                uiDrawer.drawString(text, textX, getLineY(line), 0.5f, UiDrawer.TextAlignment.LEFT, false, Color.WHITE);
            }
        }
        uiDrawer.drawString(s, textX, getLineY(20.666f), 0.5f, UiDrawer.TextAlignment.LEFT, false, Color.WHITE);

    }

    private float getLineY(float line) {
        return TOP_LEFT.y + 2 * FRAME_WIDTH + line * UiDrawer.FONT_SIZE * 0.5f * 1.8f;
    }
}
