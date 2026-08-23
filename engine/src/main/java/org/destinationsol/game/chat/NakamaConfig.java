// Copyright 2026 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.destinationsol.game.chat;

import org.destinationsol.IniReader;

/**
 * Configuration for the Nakama client integration.
 * Loaded from nakama.ini in the game's save directory.
 * If the file doesn't exist, defaults are used (disabled).
 */
public class NakamaConfig {
    private static final String CONFIG_FILE = "nakama.ini";

    private boolean enabled = false;
    private String host = "localhost";
    private int grpcPort = 7349;
    private int wsPort = 7350;
    private String channel = "bifrost.lobby";
    private String playerName = "";

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getGrpcPort() { return grpcPort; }
    public void setGrpcPort(int grpcPort) { this.grpcPort = grpcPort; }

    public int getWsPort() { return wsPort; }
    public void setWsPort(int wsPort) { this.wsPort = wsPort; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name; }

    /**
     * Load config from nakama.ini. Falls back to defaults if the file doesn't exist.
     */
    public static NakamaConfig load() {
        NakamaConfig config = new NakamaConfig();
        IniReader reader = new IniReader(CONFIG_FILE, null);
        config.enabled = reader.getBoolean("enabled", false);
        config.host = reader.getString("host", "localhost");
        config.grpcPort = reader.getInt("grpcPort", 7349);
        config.wsPort = reader.getInt("wsPort", 7350);
        config.channel = reader.getString("channel", "bifrost.lobby");
        config.playerName = reader.getString("playerName", "");
        return config;
    }

    /**
     * Save config to nakama.ini.
     */
    public void save() {
        IniReader.write(CONFIG_FILE,
                "enabled", enabled,
                "host", host,
                "grpcPort", grpcPort,
                "wsPort", wsPort,
                "channel", channel,
                "playerName", playerName
        );
    }
}
