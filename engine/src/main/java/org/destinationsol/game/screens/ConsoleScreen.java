/*
 * Copyright 2019 MovingBlocks
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
package org.destinationsol.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.Console;
import org.destinationsol.game.console.ConsoleImpl;
import org.destinationsol.game.console.ConsoleSubscriber;
import org.destinationsol.game.console.CoreMessageType;
import org.destinationsol.game.console.CyclingTabCompletionEngine;
import org.destinationsol.game.console.Message;
import org.destinationsol.game.console.TabCompletionEngine;
import org.destinationsol.game.context.Context;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ConsoleScreen implements SolUiScreen, ConsoleSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleScreen.class);

    /**
     * Magic happens here.
     * <p>
     * Sets the maximum width a line of text can have to fit into the console, in some sort of magical units. If you
     * change {@link #TOP_LEFT}, {@link #BOTTOM_RIGHT} or {@link #FRAME_WIDTH}, be sure to change this number too. The
     * only way known to me how to figure out the expected value of this field, is to randomly change it until the text
     * fits into the console nicely. Be sure to uncomment the block of code in {@link #drawBackground(UiDrawer, SolApplication)}
     * to clearly see the expected width of the text.
     */

    /**
     * Position of top left corner of the outermost frame of the console.
     */
    private static final Vector2 TOP_LEFT = new Vector2(0.03f, 0.03f);

    /**
     * Position of bottom right corner of the outermost frame of the console.
     */
    private static final Vector2 BOTTOM_RIGHT = new Vector2(0.8f, 0.5f);

    /**
     * Width of the gap between outer, inner and text area frames.
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

    private Console console;

    private static ConsoleScreen instance;

    private boolean welcomePrinted;

    private boolean isActive;

    public final BitmapFont font;
    private final SolUiControl exitControl;
    private final SolUiControl commandHistoryUpControl;
    private final SolUiControl commandHistoryDownControl;
    private final List<SolUiControl> controls;

    private int commandHistoryIndex;

    private TabCompletionEngine completionEngine;
    private StringBuilder inputLine;

    public ConsoleScreen(Context context) {
        font = Assets.getFont("engine:main").getBitmapFont();

        this.console = new ConsoleImpl(font, context);

        exitControl = new SolUiControl(null, true, Input.Keys.ESCAPE);
        commandHistoryUpControl = new SolUiControl(null, true, Input.Keys.UP);
        commandHistoryDownControl = new SolUiControl(null, true, Input.Keys.DOWN);
        controls = new ArrayList<>();
        controls.add(exitControl);
        controls.add(commandHistoryUpControl);
        controls.add(commandHistoryDownControl);
        inputLine = new StringBuilder();
        instance = this;
        completionEngine = new CyclingTabCompletionEngine(console);

        commandHistoryIndex = console.getPreviousCommands().size();

        welcomePrinted = false;
    }

    public void init(SolGame game) {
        console.init(game);
    }

    public static Optional<ConsoleScreen> getInstance() {
        if (instance == null) {
            return Optional.empty();
        } else {
            return Optional.of(instance);
        }
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        isActive = true;
        if (!welcomePrinted) {
            console.addMessage("Welcome to the world of Destination Sol! Your journey begins!" + Console.NEW_LINE +
                    "Type 'help' to see a list with available commands or 'help <commandName>' for command details." + Console.NEW_LINE +
                    "Text parameters do not need quotes, unless containing spaces. No commas between parameters." + Console.NEW_LINE +
                    "You can use auto-completion by typing a partial command then hitting [tab] - examples:" + Console.NEW_LINE + Console.NEW_LINE +
                    "go + [tab] => 'godMode'" + Console.NEW_LINE +
                    "help gh + [tab] => 'help godMode' (can auto complete commands fed to help)" + Console.NEW_LINE +
                    "(use [tab] again to cycle between choices)" + Console.NEW_LINE +
                    "gM + [tab] => 'godMode' (camel casing abbreviated commands)" + Console.NEW_LINE);
            welcomePrinted = true;
        }
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (exitControl.isJustOff()) {
            solApplication.getInputManager().setScreen(solApplication, solApplication.getGame().getScreens().mainGameScreen);
        }
        if (commandHistoryUpControl.isJustOff()) {
            if (commandHistoryIndex > 0) {
                commandHistoryIndex--;
                this.inputLine = new StringBuilder();
                this.inputLine.append(console.getPreviousCommands().get(commandHistoryIndex));
            }
        } else if (commandHistoryDownControl.isJustOff()) {
            if (commandHistoryIndex < console.getPreviousCommands().size()) {
                commandHistoryIndex++;
                if (commandHistoryIndex == console.getPreviousCommands().size()) {
                    this.inputLine = new StringBuilder();
                } else {
                    this.inputLine = new StringBuilder();
                    this.inputLine.append(console.getPreviousCommands().get(commandHistoryIndex));
                }
            }
        }
    }

    @Override
    public boolean isCursorOnBackground(SolInputManager.InputPointer inputPointer) {
        return false;
    }

    @Override
    public void blurCustom(SolApplication solApplication) {
        isActive = false;
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        drawFrame(uiDrawer);
        drawTextEntrySeparator(uiDrawer);
    }

    private void drawFrame(UiDrawer uiDrawer) {
        uiDrawer.draw(new Rectangle(TOP_LEFT.x, TOP_LEFT.y,
                        (BOTTOM_RIGHT.x - TOP_LEFT.x), BOTTOM_RIGHT.y - TOP_LEFT.y),
                SolColor.UI_LIGHT);
        uiDrawer.draw(new Rectangle(TOP_LEFT.x + FRAME_WIDTH, TOP_LEFT.y + FRAME_WIDTH,
                        (BOTTOM_RIGHT.x - TOP_LEFT.x) - 2 * FRAME_WIDTH, BOTTOM_RIGHT.y - TOP_LEFT.y - 2 * FRAME_WIDTH),
                SolColor.UI_BG_LIGHT);
    }

    private void drawTextEntrySeparator(UiDrawer uiDrawer) {
        // 20.333f - magic constant, change is console is ever resized
        uiDrawer.drawLine(TOP_LEFT.x + 2 * FRAME_WIDTH, getLineY(INPUT_LINE_SEPARATOR_Y), 0, (BOTTOM_RIGHT.x - TOP_LEFT.x) - 4 * FRAME_WIDTH, Color.WHITE);
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        final float textX = TOP_LEFT.x + 2 * FRAME_WIDTH; // X position of all text

        Iterator<Message> iterator = console.getMessages().iterator();

        int lineNumber = 0;
        while (iterator.hasNext() && lineNumber < ConsoleImpl.MAX_MESSAGE_HISTORY) {
            Message message = iterator.next();
            uiDrawer.drawString(message.getMessage(), textX, getLineY(lineNumber), 0.5f,
                    UiDrawer.TextAlignment.LEFT, false, message.getType().getColor());
            lineNumber++;
        }
        drawInputLine(uiDrawer, textX);
    }

    private void drawInputLine(UiDrawer uiDrawer, float textX) {
        StringBuilder stringBuilder = new StringBuilder();
        if (System.currentTimeMillis() % 2000 > 1000) {
            stringBuilder.append('_');
        }
        int width = font.getData().getGlyph('_').width;
        for (char c : inputLine.reverse().toString().toCharArray()) {
            final BitmapFont.Glyph glyph = font.getData().getGlyph(c);
            if (glyph != null) {
                width += glyph.width < 10 ? 10 : glyph.width;
                if (width > ConsoleImpl.MAX_WIDTH_OF_LINE) {
                    break;
                }
                stringBuilder.append(c);
            } else {
                inputLine.deleteCharAt(0);
            }
        }
        // 20.666f - magic constant, change if console is ever resized.
        uiDrawer.drawString(stringBuilder.reverse().toString(), textX, getLineY(INPUT_LINE_Y), 0.5f,
                UiDrawer.TextAlignment.LEFT, false, Color.WHITE);
        inputLine.reverse();
    }

    @Override
    public void onNewConsoleMessage(Message message) {

    }

    public void onCharEntered (char character) {
        if (isActive) {
            if (character == '\t' && this.inputLine.length() > 0) {
                this.inputLine = new StringBuilder(this.completionEngine.complete(inputLine.toString()));
            } else if (character != '\t') {
                this.completionEngine.reset();
            }
            if (character == '\r' || character == '\n') {
                console.addMessage("> " + inputLine.toString(), CoreMessageType.WARN);
                console.execute(inputLine.toString());
                inputLine = new StringBuilder();
                commandHistoryIndex = console.getPreviousCommands().size();
                return;
            }
            if (character == '\b') {
                if (inputLine.length() != 0) {
                    inputLine.deleteCharAt(inputLine.length() - 1);
                }
                return;
            }
            inputLine.append(character);
        }
    }

    private float getLineY(float line) {
        return TOP_LEFT.y + 2 * FRAME_WIDTH + line * UiDrawer.FONT_SIZE * 0.5f * 1.8f;
    }

    public Console getConsole() {
        return console;
    }
}
