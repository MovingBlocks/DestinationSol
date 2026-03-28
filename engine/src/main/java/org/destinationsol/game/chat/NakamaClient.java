// Copyright 2026 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.destinationsol.game.chat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.Channel;
import com.heroiclabs.nakama.ChannelType;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.api.ChannelMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Lightweight Nakama integration for DestinationSol.
 * Connects to a shared chat channel for cross-game messaging.
 *
 * Enable via: -Dnakama.enabled=true -Dnakama.host=192.168.x.x -Dnakama.playerName=Bob
 */
public class NakamaClient {
    private static final Logger logger = LoggerFactory.getLogger(NakamaClient.class);
    private static final String GAME_ID = "destinationsol";
    private static final Map<String, String> GAME_PREFIXES;
    static {
        Map<String, String> m = new HashMap<>();
        m.put("terasology", "TS");
        m.put("destinationsol", "DS");
        m.put("minecraft", "MC");
        GAME_PREFIXES = Collections.unmodifiableMap(m);
    }

    private final NakamaConfig config;
    private Client client;
    private Session session;
    private SocketClient socket;
    private Channel channel;

    // Thread-safe queue for incoming messages to be consumed on the game thread
    private final ConcurrentLinkedQueue<String> incomingMessages = new ConcurrentLinkedQueue<>();

    public NakamaClient(NakamaConfig config) {
        this.config = config;
    }

    /**
     * Connect to Nakama and join the chat channel.
     * Call during game startup.
     */
    public void connect() {
        if (!config.isEnabled()) {
            logger.info("Nakama client disabled");
            return;
        }
        try {
            String deviceId = getOrCreateDeviceId();
            client = new DefaultClient("defaultkey", config.getHost(), config.getGrpcPort(), false);
            session = client.authenticateDevice(deviceId).get();

            if (!config.getPlayerName().isEmpty()) {
                client.updateAccount(session, null, config.getPlayerName()).get();
            }

            logger.info("Nakama: authenticated as {}", session.getUserId());

            socket = client.createSocket(config.getHost(), config.getWsPort(), false);
            socket.connect(session, new AbstractSocketListener() {
                @Override
                public void onChannelMessage(ChannelMessage message) {
                    handleIncomingMessage(message);
                }
            }).get();

            channel = socket.joinChat(config.getChannel(), ChannelType.ROOM).get();
            logger.info("Nakama: joined channel '{}'", config.getChannel());

        } catch (Exception e) {
            logger.warn("Nakama: connection failed, continuing without cross-game chat", e);
            cleanup();
        }
    }

    private void handleIncomingMessage(ChannelMessage message) {
        try {
            JsonObject content = JsonParser.parseString(message.getContent()).getAsJsonObject();
            String game = content.has("game") ? content.get("game").getAsString() : "";
            if (GAME_ID.equals(game)) {
                return; // Echo filter
            }
            String player = content.has("player") ? content.get("player").getAsString() : "???";
            String text = content.has("text") ? content.get("text").getAsString() : "";
            String prefix = "[" + GAME_PREFIXES.getOrDefault(game,
                    game.toUpperCase().substring(0, Math.min(game.length(), 2))) + "]";
            String formatted = prefix + " " + player + ": " + text;
            incomingMessages.add(formatted);
        } catch (Exception e) {
            logger.warn("Nakama: failed to parse incoming message", e);
        }
    }

    /**
     * Send a chat message. Called from the /say console command.
     */
    public boolean sendMessage(String text) {
        if (socket == null || channel == null) {
            return false;
        }
        try {
            String playerName = config.getPlayerName().isEmpty()
                    ? session.getUserId().substring(0, 8)
                    : config.getPlayerName();

            JsonObject content = new JsonObject();
            content.addProperty("game", GAME_ID);
            content.addProperty("player", playerName);
            content.addProperty("text", text);
            socket.writeChatMessage(channel.getId(), content.toString()).get();
            return true;
        } catch (Exception e) {
            logger.warn("Nakama: failed to send message", e);
            return false;
        }
    }

    /**
     * Poll for incoming messages. Call from the game loop.
     * Returns null if no messages are pending.
     */
    public String pollMessage() {
        return incomingMessages.poll();
    }

    public boolean isConnected() {
        return socket != null && channel != null;
    }

    public void disconnect() {
        cleanup();
    }

    private void cleanup() {
        if (socket != null) {
            try { socket.disconnect(); } catch (Exception ignored) { }
            socket = null;
        }
        channel = null;
        session = null;
        client = null;
    }

    private String getOrCreateDeviceId() {
        String id = System.getProperty("nakama.deviceId", "");
        if (!id.isEmpty()) {
            return id;
        }
        Path idFile = Paths.get(System.getProperty("user.home"), ".bifrost", "device-id");
        try {
            if (Files.exists(idFile)) {
                id = new String(Files.readAllBytes(idFile), StandardCharsets.UTF_8).trim();
                if (!id.isEmpty()) {
                    return id;
                }
            }
            id = UUID.randomUUID().toString();
            Files.createDirectories(idFile.getParent());
            Files.write(idFile, id.getBytes(StandardCharsets.UTF_8));
            logger.info("Nakama: created device ID {}", id);
        } catch (IOException e) {
            id = UUID.randomUUID().toString();
            logger.warn("Nakama: could not persist device ID, using ephemeral {}", id);
        }
        return id;
    }
}
