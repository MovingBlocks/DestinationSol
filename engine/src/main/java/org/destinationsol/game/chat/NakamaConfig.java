// Copyright 2026 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.destinationsol.game.chat;

/**
 * Configuration for the Nakama client integration.
 * Read from system properties for the POC.
 */
public class NakamaConfig {
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

    public static NakamaConfig fromSystemProperties() {
        NakamaConfig config = new NakamaConfig();
        config.setEnabled(Boolean.parseBoolean(System.getProperty("nakama.enabled", "false")));
        config.setHost(System.getProperty("nakama.host", "localhost"));
        config.setGrpcPort(Integer.parseInt(System.getProperty("nakama.grpcPort", "7349")));
        config.setWsPort(Integer.parseInt(System.getProperty("nakama.wsPort", "7350")));
        config.setChannel(System.getProperty("nakama.channel", "bifrost.lobby"));
        config.setPlayerName(System.getProperty("nakama.playerName", ""));
        return config;
    }
}
