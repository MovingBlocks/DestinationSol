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
import java.util.ArrayList;
import java.util.List;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolException;
import org.destinationsol.game.console.ConsoleInputHandler;
import org.destinationsol.game.console.ShellInputHandler;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.UiDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.destinationsol.ui.responsiveUi.UiHeadlessButton;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;

/**
 * Command console opened by pressing {@code `} in-game. Singleton, until a better way to handle this is developed.
 * Can be hooked with custom command handlers, thus allowing for custom "programs" of sorts.
 */
public class Console extends SolUiBaseScreen {
    private static final Logger logger = LoggerFactory.getLogger(Console.class);

    /**
     * Magic happens here.
     *
     * Sets the maximum width a line of text can have to fit into the console, in some sort of magical units. If you
     * change {@link #TOP_LEFT}, {@link #BOTTOM_RIGHT} or {@link #FRAME_WIDTH}, be sure to change this number too. The
     * only way known to me how to figure out the expected value of this field, is to randomly change it until the text
     * fits into the console nicely. Be sure to uncomment the block of code in {@link #drawBackground(UiDrawer, SolApplication)}
     * to clearly see the expected width of the text.
     */
    private static final int MAX_WIDTH_OF_LINE = 1040;

    /**
     * Position of top left corner of the outermost frame of the console.
     *
     * See also {@link #MAX_WIDTH_OF_LINE}.
     */
    private static final Vector2 TOP_LEFT = new Vector2(0.03f, 0.03f);

    /**
     * Position of bottom right corner of the outermost frame of the console.
     *
     * See also {@link #MAX_WIDTH_OF_LINE}.
     */
    private static final Vector2 BOTTOM_RIGHT = new Vector2(0.8f, 0.5f);

    /**
     * Width of the gap between outer, inner and text area frames.
     *
     * See also {@link #MAX_WIDTH_OF_LINE}
     */
    private static final float FRAME_WIDTH = 0.02f;

    /**
     * "Line number" of the input line.
     */
    private static final float INPUT_LINE_Y = 20.666f;

    /**
     * "Line number" of the input line separator.
     */
    private static final float INPUT_LINE_SEPARATOR_Y = 20.333f;

    private static Console instance;

    /**
     * Stores all the lines of output printed to console, each line shorter in rendered text width than {@link #MAX_WIDTH_OF_LINE}.
     */
    private final List<String> linesOfOutput;
    /**
     * Basically the same font as {@link org.destinationsol.CommonDrawer#font}.
     *
     * Required for figuring out char widths.
     */
    private final BitmapFont font;
    private final ShellInputHandler defaultInputHandler;

    /**
     * Current line of user input.
     */
    private StringBuilder inputLine;

    /**
     * Handler to which each line entered in console is passed to handle.
     */
    private ConsoleInputHandler inputHandler;

    private boolean isActive;

    private Console() {
        font = Assets.getFont("engine:main").getBitmapFont();

        UiRelativeLayout relativeLayout = new UiRelativeLayout();

        UiHeadlessButton exitButton = new UiHeadlessButton().setTriggerKey(Input.Keys.ESCAPE)
                .setOnReleaseAction(uiElement -> SolApplication.changeScreen(SolApplication.getInstance().getGame().getScreens().mainGameScreen));

        relativeLayout.addHeadlessElement(exitButton);

        rootUiElement = relativeLayout;

        linesOfOutput = new ArrayList<>();
        inputLine = new StringBuilder();
        println("Welcome to the world of Destination Sol! Your journey begins!");
        defaultInputHandler = new ShellInputHandler();
        setInputHandler(defaultInputHandler);

        for (Class commandHandler : ModuleManager.getEnvironment().getSubtypesOf(ConsoleInputHandler.class)) {
            String commandName = commandHandler.getSimpleName().replace("Command", "");
            try {
                defaultInputHandler.registerCommand(commandName, (ConsoleInputHandler) commandHandler.newInstance());
            } catch (Exception e) {
                logger.error("Error creating instance of command " + commandHandler.getTypeName());
            }
        }
    }

    public ShellInputHandler getDefaultInputHandler() {
        return defaultInputHandler;
    }

    /**
     * Registers a line of text to be rendered in console.
     *
     * Lines too long will be automatically split into several for each to fit nicely into the console space.
     *
     * NOTE: Due to limitations of linGdx {@link BitmapFont}, only ASCII characters are allowed. Newlines are also
     * prohibited.
     * TODO allow unicode and newlines. Unicode can be handled by replacing required characters with others, newlines by splitting the input string and recursive calls
     *
     * @param s String to print.
     */
    public void println(String s) {
        try {
            int width = 0;
            StringBuilder currentLine = new StringBuilder();
            for (char c : s.toCharArray()) {
                width += (c == ' ' ? 3 : 1) * font.getData().getGlyph(c).width; // Why is this multiplier here? Well, don't ask, I don't know and I wrote it.
                if (width > MAX_WIDTH_OF_LINE) {
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

    /**
     * Sets the {@link ConsoleInputHandler} to use for interpreting user input.
     *
     * @param inputHandler Handler to use.
     */
    public void setInputHandler(ConsoleInputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    /**
     * Console is, for now, singleton.
     *
     * @return Instance of Console.
     */
    public static Console getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new Console();
            return instance;
        }
    }

    /**
     * Registers a char entered by user in-game.
     *
     * Char is handled only when Console is currently open.
     *
     * @param c Char user entered.
     */
    public void registerCharEntered(char c) {
        if (isActive) {
            if (c == '\r') {
                inputHandler.handle(inputLine.toString(), this);
                inputLine = new StringBuilder();
                return;
            }
            if (c == '\b') {
                if (inputLine.length() != 0) {
                    inputLine.deleteCharAt(inputLine.length() - 1);
                }
                return;
            }
            inputLine.append(c);
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
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        drawFrame(uiDrawer);
        // Text area - uncomment when you want drawn clear boundary of area meant for text to be in. Don't delete in case there is ever need to resize the console.
//        uiDrawer.draw(new Rectangle(TOP_LEFT.x + 2 * FRAME_WIDTH,
//                        TOP_LEFT.y + 2 * FRAME_WIDTH,
//                        (BOTTOM_RIGHT.x - TOP_LEFT.x) - 4 * FRAME_WIDTH,
//                        BOTTOM_RIGHT.y - TOP_LEFT.y - 4 * FRAME_WIDTH),
//                SolColor.UI_BG_LIGHT);
        drawTextEntrySeparator(uiDrawer);
    }

    /**
     * Draws the separator between the area for user input and the area to render console output into
     *
     * @param uiDrawer Drawer to draw to.
     */
    private void drawTextEntrySeparator(UiDrawer uiDrawer) {
        // 20.333f - magic constant, change is console is ever resized
        uiDrawer.drawLine(TOP_LEFT.x + 2 * FRAME_WIDTH, getLineY(INPUT_LINE_SEPARATOR_Y), 0, (BOTTOM_RIGHT.x - TOP_LEFT.x) - 4 * FRAME_WIDTH, Color.WHITE);
    }

    /**
     * Draws the two rectangles making the Console frame.
     *
     * @param uiDrawer Drawer to draw to.
     */
    private void drawFrame(UiDrawer uiDrawer) {
        uiDrawer.draw(new Rectangle(TOP_LEFT.x, TOP_LEFT.y,
                        (BOTTOM_RIGHT.x - TOP_LEFT.x), BOTTOM_RIGHT.y - TOP_LEFT.y),
                SolColor.UI_LIGHT);
        uiDrawer.draw(new Rectangle(TOP_LEFT.x + FRAME_WIDTH, TOP_LEFT.y + FRAME_WIDTH,
                        (BOTTOM_RIGHT.x - TOP_LEFT.x) - 2 * FRAME_WIDTH, BOTTOM_RIGHT.y - TOP_LEFT.y - 2 * FRAME_WIDTH),
                SolColor.UI_BG_LIGHT);
    }

    @Override
    public void draw(UiDrawer uiDrawer, SolApplication solApplication) {
        final float textX = TOP_LEFT.x + 2 * FRAME_WIDTH; // X position of all text
        for (int line = 0; line < 20; line++) { // Magic constant. Change if Console is resized.
            if (linesOfOutput.size() + line > 19) { // to prevent IndexOutOfBoundsException
                final String text = linesOfOutput.get(linesOfOutput.size() - 20 + line);
                uiDrawer.drawString(text, textX, getLineY(line), 0.5f, UiDrawer.TextAlignment.LEFT, false, Color.WHITE);
            }
        }
        drawInputLine(uiDrawer, textX);

    }

    /**
     * Renders the line of user input.
     *
     * When the line is longer than {@link #MAX_WIDTH_OF_LINE}, renders only the last part of it that fits.
     *
     * @param uiDrawer Drawer to draw to.
     * @param textX X position of the text.
     */
    private void drawInputLine(UiDrawer uiDrawer, float textX) {
        StringBuilder stringBuilder = new StringBuilder();
        if (System.currentTimeMillis() % 2000 > 1000) {
            stringBuilder.append('_');
        }
        int width = font.getData().getGlyph('_').width;
        for (char c : inputLine.reverse().toString().toCharArray()) {
            final BitmapFont.Glyph glyph = font.getData().getGlyph(c);
            if (glyph != null) {
                width += (c == ' ' ? 3 : 1) * glyph.width;
                if (width > MAX_WIDTH_OF_LINE) {
                    break;
                }
                stringBuilder.append(c);
            } else {
                inputLine.deleteCharAt(0);
            }
        }
        // 20.666f - magic constant, change if console is ever resized.
        uiDrawer.drawString(stringBuilder.reverse().toString(), textX, getLineY(INPUT_LINE_Y), 0.5f, UiDrawer.TextAlignment.LEFT, false, Color.WHITE);
        inputLine.reverse();
    }

    /**
     * Returns the Y position of given line.
     *
     * Magic constants, change if needed.
     *
     * @param line Line Y position of which to return
     * @return Computed Y position
     */
    private float getLineY(float line) {
        return TOP_LEFT.y + 2 * FRAME_WIDTH + line * UiDrawer.FONT_SIZE_PX / 1000f * 0.5f * 1.8f;
    }
}
