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
package org.destinationsol.game.console;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.destinationsol.assets.Assets;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.annotations.RegisterCommands;
import org.destinationsol.game.console.exceptions.CommandExecutionException;
import org.destinationsol.game.context.Context;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.util.CircularBuffer;
import org.destinationsol.util.InjectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The console handles commands and messages.
 */
public class ConsoleImpl implements Console {
    public static final int MAX_MESSAGE_HISTORY = 20;
    public static final int MAX_WIDTH_OF_LINE = 1040;
    private static final String PARAM_SPLIT_REGEX = " (?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final int MAX_COMMAND_HISTORY = 30;
    private static final Logger logger = LoggerFactory.getLogger(ConsoleImpl.class);

    private final CircularBuffer<Message> messageHistory = CircularBuffer.create(MAX_MESSAGE_HISTORY);
    private final CircularBuffer<String> localCommandHistory = CircularBuffer.create(MAX_COMMAND_HISTORY);
    private final Map<String, ConsoleCommand> commandRegistry = Maps.newHashMap();
    private final Set<ConsoleSubscriber> messageSubscribers = Sets.newHashSet();
    private final Context context;

    private BitmapFont font;

    @Inject
    public ConsoleImpl(Context context) {
        this.font = Assets.getFont("engine:main").getBitmapFont();
        this.context = context;
    }

    private static String cleanCommand(String rawCommand) {
        // trim and remove double spaces
        return rawCommand.trim().replaceAll("\\s\\s+", " ");
    }

    private static List<String> splitParameters(String paramStr) {
        String[] rawParams = paramStr.split(PARAM_SPLIT_REGEX);
        List<String> params = Lists.newArrayList();
        for (String s : rawParams) {
            String param = s;

            if (param.trim().isEmpty()) {
                continue;
            }
            if (param.length() > 1 && param.startsWith("\"") && param.endsWith("\"")) {
                param = param.substring(1, param.length() - 1);
            }
            params.add(param);
        }
        return params;
    }

    public void init(SolGame game) {
        commandRegistry.clear();
        for (Class commands : context.get(ModuleManager.class).getEnvironment().getTypesAnnotatedWith(RegisterCommands.class)) {
            try {
                Object commandsObject = commands.newInstance();
                InjectionHelper.inject(commandsObject, context);
                MethodCommand.registerAvailable(commandsObject, this, game, context);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Registers a {@link org.destinationsol.game.console.MethodCommand  }.
     *
     * @param command The command to be registered
     */
    @Override
    public void registerCommand(ConsoleCommand command) {
        String commandName = command.getName().toLowerCase();

        if (commandRegistry.containsKey(commandName)) {
            logger.warn("Command with name '{}' already registered by class '{}', skipping '{}'",
                    commandName, commandRegistry.get(commandName).getSource().getClass().getCanonicalName(),
                    command.getSource().getClass().getCanonicalName());
        } else {
            commandRegistry.put(commandName, command);
            logger.debug("Command '{}' successfully registered for class '{}'.", commandName,
                    command.getSource().getClass().getCanonicalName());
        }
    }

    @Override
    public void dispose() {
        commandRegistry.clear();
        messageHistory.clear();
    }

    /**
     * Adds a message to the console (as a CoreMessageType.CONSOLE message)
     *
     * @param message The message content
     */
    @Override
    public void addMessage(String message) {
        addMessage(new Message(message));
    }

    /**
     * Adds a message to the console
     *
     * @param message The content of the message
     * @param type    The type of the message
     */
    @Override
    public void addMessage(String message, MessageType type) {
        addMessage(new Message(message, type));
    }

    private void addErrorMessage(String message) {
        addMessage(new Message(message, CoreMessageType.ERROR));
    }

    /**
     * Adds a message to the console
     *
     * @param message The message to be added, as a string.
     */

    /**
     * Adds a message to the console
     *
     * @param message The message to be added
     */
    @Override
    public void addMessage(Message message) {
        String uncoloredText = message.getMessage();
        logger.info("[{}] {}", message.getType(), uncoloredText);

        List<Message> newlinedMessages = new ArrayList<>();

        //check for newlines and split into submessages
        if (message.getMessage().indexOf(NEW_LINE) > 0) {
            for (String line : message.getMessage().split(NEW_LINE)) {
                newlinedMessages.add(new Message(line, message.getType()));
            }
        } else {
            newlinedMessages.add(message);
        }

        //check if line fits into the console window and split into submessages
        BitmapFont.BitmapFontData fontData = font.getData();
        for (Message messageToCheck : newlinedMessages) {
            int messageWidth = 0;
            StringBuilder fittingMessageText = new StringBuilder();
            for (char c : messageToCheck.getMessage().toCharArray()) {
                int charWidth = fontData.getGlyph(c).width;
                charWidth = charWidth < 10 ? 10 : charWidth;
                messageWidth += charWidth;
                fittingMessageText.append(c);
                if (messageWidth > MAX_WIDTH_OF_LINE) {
                    Message newMessage = new Message(fittingMessageText.toString(), message.getType());
                    messageHistory.add(newMessage);
                    fittingMessageText = new StringBuilder();
                    messageWidth = 0;
                }
            }
            Message newMessage = new Message(fittingMessageText.toString(), message.getType());
            messageHistory.add(newMessage);
        }

        for (ConsoleSubscriber subscriber : messageSubscribers) {
            subscriber.onNewConsoleMessage(message);
        }
    }

    @Override
    public void removeMessage(Message message) {
        messageHistory.remove(message);
    }

    /**
     * Clears the console of all previous messages.
     */
    @Override
    public void clear() {
        messageHistory.clear();
    }

    @Override
    public void replaceMessage(Message oldMsg, Message newMsg) {
        int idx = messageHistory.indexOf(oldMsg);
        if (idx >= 0) {
            messageHistory.set(idx, newMsg);
        }
    }

    /**
     * @return An iterator over all messages in the console
     */
    @Override
    public Iterable<Message> getMessages() {
        return messageHistory;
    }

    @Override
    public Iterable<Message> getMessages(MessageType... types) {
        final List<MessageType> allowedTypes = Arrays.asList(types);

        return Collections2.filter(messageHistory, input -> allowedTypes.contains(input.getType()));
    }

    @Override
    public List<String> getPreviousCommands() {
        return ImmutableList.copyOf(localCommandHistory);
    }

    /**
     * Subscribe for notification of all messages added to the console
     */
    @Override
    public void subscribe(ConsoleSubscriber subscriber) {
        this.messageSubscribers.add(subscriber);
    }

    /**
     * Unsubscribe from receiving notification of messages being added to the console
     */
    @Override
    public void unsubscribe(ConsoleSubscriber subscriber) {
        this.messageSubscribers.remove(subscriber);
    }

    @Override
    public boolean execute(String rawCommand) {
        String commandName = processCommandName(rawCommand);
        commandName = commandName.toLowerCase();
        List<String> processedParameters = processParameters(rawCommand);

        if (!rawCommand.isEmpty() && (localCommandHistory.isEmpty() || !localCommandHistory.getLast().equals(rawCommand))) {
            localCommandHistory.add(rawCommand);
        }

        return execute(commandName, processedParameters);
    }

    @Override
    public boolean execute(String commandName, List<String> params) {
        if (commandName.isEmpty()) {
            return false;
        }

        //get the command
        ConsoleCommand cmd = getCommand(commandName);

        //check if the command is loaded
        if (cmd == null) {
            addErrorMessage("Unknown command '" + commandName + "'");
            return false;
        }

        if (params.size() < cmd.getRequiredParameterCount()) {
            addMessage(new Message("Please, provide required arguments marked by <>.", CoreMessageType.WARN));
            addMessage(cmd.getUsage(), CoreMessageType.WARN);
            return false;
        }

        try {
            String result = cmd.execute(params);
            if (result != null) {
                this.addMessage(result);
            }
            return true;
        } catch (CommandExecutionException e) {
            Throwable cause = e.getCause();
            String causeMessage;
            if (cause != null) {
                causeMessage = cause.getLocalizedMessage();
                if (Strings.isNullOrEmpty(causeMessage)) {
                    causeMessage = cause.toString();
                }
            } else {
                causeMessage = e.getLocalizedMessage();
            }

            logger.error("An error occurred while executing a command '" + cmd.getName() + "' : " + e.getMessage(), CoreMessageType.ERROR);

            if (!Strings.isNullOrEmpty(causeMessage)) {
                this.addMessage(new Message("Error: " + causeMessage));
            }
            return false;
        }
    }

    @Override
    public String processCommandName(String rawCommand) {
        String cleanedCommand = cleanCommand(rawCommand);
        int commandEndIndex = cleanedCommand.indexOf(" ");

        if (commandEndIndex >= 0) {
            return cleanedCommand.substring(0, commandEndIndex);
        } else {
            return cleanedCommand;
        }
    }

    @Override
    public List<String> processParameters(String rawCommand) {
        String cleanedCommand = cleanCommand(rawCommand);
        //get the command name
        int commandEndIndex = cleanedCommand.indexOf(" ");

        if (commandEndIndex < 0) {
            commandEndIndex = cleanedCommand.length();
        }

        //remove command name from string
        String parameterPart = cleanedCommand.substring(commandEndIndex).trim();

        //get the parameters
        List<String> params = splitParameters(parameterPart);

        return params;
    }

    /**
     * Get a group of commands by their name. These will vary by the number of parameters they accept
     *
     * @param name The name of the command.
     * @return An array of commands with given name
     */
    @Override
    public ConsoleCommand getCommand(String name) {
        return commandRegistry.get(name.toLowerCase());
    }

    /**
     * Get the list of all loaded commands.
     *
     * @return Returns the command list.
     */
    @Override
    public Collection<ConsoleCommand> getCommands() {
        return commandRegistry.values();
    }
}
