// Copyright 2026 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.destinationsol.game.chat;

import com.google.gson.JsonObject;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.annotations.Command;
import org.destinationsol.game.console.annotations.CommandParam;
import org.destinationsol.game.console.annotations.RegisterCommands;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Console commands and static helpers for Nakama cross-game chat and item linking.
 */
@RegisterCommands
public class SayCommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(SayCommandHandler.class);

    private static NakamaClient nakamaClient;

    public static void setNakamaClient(NakamaClient client) {
        nakamaClient = client;
    }

    public static NakamaClient getNakamaClient() {
        return nakamaClient;
    }

    @Command(shortDescription = "Send a message to the Bifrost chat channel")
    public String say(@CommandParam(value = "message") String message) {
        if (nakamaClient == null || !nakamaClient.isConnected()) {
            return "Nakama not connected. Enable with nakama.ini";
        }
        boolean sent = nakamaClient.sendMessage(message);
        return sent ? "Sent: " + message : "Failed to send message";
    }

    /**
     * Called by the Beam Out inventory button — shares selected item as an item link.
     */
    public static void beamOutItem(SolItem item) {
        if (nakamaClient == null || !nakamaClient.isConnected()) {
            logger.warn("Beam Out: Nakama not connected");
            return;
        }
        boolean sent = nakamaClient.sendItemLink(
                item.getDisplayName(),
                item.getDescription(),
                item.getPrice()
        );
        if (sent) {
            logger.info("Beam Out: sent [{}]", item.getDisplayName());
        }
    }

    /**
     * Called by the Beam In inventory button — materializes last received item link
     * as a money token representing the item's value.
     */
    public static void beamInItem(SolGame game, SolShip target) {
        if (nakamaClient == null) {
            return;
        }
        JsonObject link = nakamaClient.consumeItemLink();
        if (link == null) {
            logger.info("Beam In: no item link pending");
            return;
        }
        String itemName = link.has("name") ? link.get("name").getAsString() : "Unknown";
        float price = link.has("price") ? link.get("price").getAsFloat() : 10f;
        String sourceGame = link.has("game") ? link.get("game").getAsString() : "unknown";

        // Create a money token representing the beamed item's trade value
        SolItem token = game.getItemMan().moneyItem(price);
        if (target.getItemContainer().canAdd(token)) {
            target.getItemContainer().add(token);
            logger.info("Beam In: materialized [{}] from {} as {} credits", itemName, sourceGame, price);
        } else {
            logger.warn("Beam In: inventory full, could not materialize [{}]", itemName);
        }
    }
}
