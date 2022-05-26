/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.ui.nui.screens;

import org.destinationsol.game.console.Console;
import org.destinationsol.game.console.ConsoleImpl;
import org.destinationsol.game.console.CyclingTabCompletionEngine;
import org.destinationsol.game.console.Message;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.UICommandEntry;
import org.terasology.input.MouseInput;
import org.terasology.nui.BaseInteractionListener;
import org.terasology.nui.Canvas;
import org.terasology.nui.FontColor;
import org.terasology.nui.InteractionListener;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.events.NUIMouseClickEvent;
import org.terasology.nui.layouts.ScrollableArea;
import org.terasology.nui.widgets.UIText;

import javax.inject.Inject;
import java.util.List;

/**
 * The console screen. You can enter commands into this UI and they will be executed. The logic is in {@link Console}.
 * This was taken from Terasology originally.
 */
public class ConsoleScreen extends NUIScreenLayer {
    private Console console;
    private UICommandEntry commandLine;
    private boolean welcomePrinted;
    private boolean screenClosed;

    @Inject
    public ConsoleScreen(Console console) {
        this.console = console;
    }

    private InteractionListener screenListener = new BaseInteractionListener() {
        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            if (event.getMouseButton() == MouseInput.MOUSE_LEFT && commandLine != null) {
                focusManager.setFocus(commandLine);
            }
            return true;
        }
    };

    @Override
    public void initialise() {

        final ScrollableArea scrollArea = find("scrollArea", ScrollableArea.class);
        scrollArea.moveToBottom();

        commandLine = find("commandLine", UICommandEntry.class);
        commandLine.setTabCompletionEngine(new CyclingTabCompletionEngine(console));
        commandLine.bindCommandHistory(new ReadOnlyBinding<List<String>>() {
            @Override
            public List<String> get() {
                return console.getPreviousCommands();
            }
        });
        commandLine.subscribe(widget -> {
            String text = commandLine.getText();
            if (!text.isEmpty()) {
                console.execute(text);
            }
            scrollArea.moveToBottom();
        });

        final UIText history = find("messageHistory", UIText.class);
        history.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                StringBuilder messageList = new StringBuilder();
                for (Message message : console.getMessages()) {
                    messageList.append(FontColor.getColored(message.getMessage(), new org.terasology.nui.Color(message.getType().getColor().toIntBits())));
                    messageList.append(Console.NEW_LINE);
                }
                return messageList.toString();
            }
        });

        welcomePrinted = false;
    }

    @Override
    public void onAdded() {
        screenClosed = false;
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

        final ScrollableArea scrollArea = find("scrollArea", ScrollableArea.class);
        scrollArea.moveToBottom();

        focusManager.setFocus(commandLine);
    }

    @Override
    public void onRemoved() {
        screenClosed = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.addInteractionRegion(screenListener, canvas.getRegion());
    }

    @Override
    public boolean isBlockingInput() {
        return true;
    }

    public boolean isConsoleJustClosed() {
        if (screenClosed) {
            screenClosed = false;
            return true;
        }

        return false;
    }
}
