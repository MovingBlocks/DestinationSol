// Copyright 2026 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.destinationsol.game.chat;

import org.destinationsol.game.console.annotations.Command;
import org.destinationsol.game.console.annotations.CommandParam;
import org.destinationsol.game.console.annotations.RegisterCommands;

/**
 * Console command for sending chat messages via Nakama.
 * Usage: say "Hello from space!"
 *
 * Multi-word messages must be quoted: say "Read you loud and clear."
 */
@RegisterCommands
public class SayCommandHandler {

    private static NakamaClient nakamaClient;

    public static void setNakamaClient(NakamaClient client) {
        nakamaClient = client;
    }

    @Command(shortDescription = "Send a message to the Bifrost chat channel")
    public String say(@CommandParam(value = "message") String message) {
        if (nakamaClient == null || !nakamaClient.isConnected()) {
            return "Nakama not connected. Enable with -Dnakama.enabled=true";
        }
        boolean sent = nakamaClient.sendMessage(message);
        return sent ? "Sent: " + message : "Failed to send message";
    }
}
