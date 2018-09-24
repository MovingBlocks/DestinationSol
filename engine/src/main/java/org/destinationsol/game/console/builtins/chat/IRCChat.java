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
package org.destinationsol.game.console.builtins.chat;

import org.destinationsol.game.Console;
import org.destinationsol.game.console.ConsoleInputHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IRCChat {
    private static final String SERVER = "chat.freenode.net";
    private static final int PORT = 6667;
    private static final String CHANNEL = "##destination-sol-players";
    private String channel;
    private Socket socket;
    private String nick;
    private BufferedWriter writer;
    private BufferedReader reader;

    public boolean isConnected() {
        return socket != null;
    }

    public void connect() throws IOException {
        connect("SolSeeker" + (System.currentTimeMillis() % 1000));
    }

    public void connect(String nick) throws IOException {
        connect(nick, SERVER, PORT, CHANNEL);
    }

    public void connect(String nick, String server, int port, String channel) throws IOException {
        socket = new Socket(server, port);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.nick = nick;
        writer.write("NICK " + nick + "\n");
        writer.flush();
        writer.write("USER " + nick + " DestSol DestSol :" + nick + "\n"); // RFC 1459 compliant
        writer.flush();
        writer.write("JOIN " + channel + "\n");
        writer.flush();
        this.channel = channel;
    }

    public Optional<String> getLine() throws IOException {
        if (reader.ready()) {
            return Optional.ofNullable(reader.readLine());
        }
        else {
            return Optional.empty();
        }
    }

    public void sendMessage(String message) throws IOException {
        writer.write("PRIVMSG " + channel + " :" + message + '\n');
        writer.flush();
    }

    public void quit() {
        try {
            writer.write("QUIT\n");
            writer.flush();
            socket.close();
        } catch (IOException e) {
            System.out.println("CHAT: Connection crashed, but user is already exiting");
        }
    }

    public static class ConsoleHandler implements ConsoleInputHandler {
        private IRCChat chatInstance;
        private Predicate<String> helpMatcher = Pattern.compile("^[./\\\\:][hH][eE][lL][pP]").asPredicate();
        private Predicate<String> quitMatcher = Pattern.compile("^[./\\\\:][qQ][uU][iI][tT]").asPredicate();
        private boolean isConnected = false;

        public ConsoleHandler() {
            chatInstance = new IRCChat();
        }

        @Override
        public void handle(String input, Console console) {
            if (helpMatcher.test(input)) {
                console.println("$ Available commands:");
                console.println("$ /HELP : Prints out this help");
                console.println("$ /QUIT : Exits chat");
                return;
            }
            if (quitMatcher.test(input)) {
                chatInstance.quit();
                console.println("Chat closed");
                console.setInputHandler(console.getDefaultInputHandler());
                return;
            }
            try {
                chatInstance.sendMessage(input);
                console.println("You (" + chatInstance.nick + "): " + input);
            } catch (IOException e) {
                console.println("There was an error with connection to the chat room, shutting chat down...");
                console.setInputHandler(console.getDefaultInputHandler());
            }
        }

        @Override
        public void update(Console console) {
            if (!chatInstance.isConnected()) {
                try {
                    chatInstance.connect();
                    console.println("Connecting to chat server...");
                } catch (IOException e) {
                    console.println("Could not connect to the chat server, please check your internet connection.");
                    console.setInputHandler(console.getDefaultInputHandler());
                }
            }
            try {
                chatInstance.getLine().ifPresent(s -> {
                    if (s.contains("PRIVMSG") && isConnected) {
                        console.println(s.substring(1).split("!")[0] + ": " + s.substring(1).split(":", 2)[1]);
                        return;
                    }
                    if (s.contains("nd of ") && !isConnected) {
                        isConnected = true;
                        console.println("Connected to chat server.");
                    }
                    if (s.contains("PING")) {
                        try {
                            chatInstance.writer.write("PONG pingis\n");
                            chatInstance.writer.flush();
                        } catch (IOException e) {
                            console.println("There was an error with connection to the chat room, shutting chat down...");
                            console.setInputHandler(console.getDefaultInputHandler());
                        }
                    }
                });
            } catch (IOException e) {
                console.println("There was an error with connection to the chat room, shutting chat down...");
                console.setInputHandler(console.getDefaultInputHandler());
            }
        }
    }
}
