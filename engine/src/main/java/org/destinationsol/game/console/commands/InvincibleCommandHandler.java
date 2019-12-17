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
package org.destinationsol.game.console.commands;

import org.destinationsol.game.Console;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.ConsoleInputHandler;
import org.destinationsol.game.ship.SolShip;

/**
 * A command used to make player invincible
 */
public class InvincibleCommandHandler implements ConsoleInputHandler {

    private Hero hero;
    private SolGame game;
    private SolShip solShip;

    public InvincibleCommandHandler(Hero hero, SolGame game, SolShip solShip) {
        this.hero = hero;
        this.game = game;
        this.solShip = solShip;
    }

    @Override
    public void handle(String input, Console console) {
        if (hero.isDead()) {
            console.warn("Cannot make invincible when dead");
            return;
        }
        if (hero.isTranscendent()) {
            console.warn("Cannot make invincible when transdencent");
            return;
        }
        if (!solShip.isInvincible()) {
            console.info("Set player as invincible");
            solShip.setInvincible(true);
        } else if (solShip.isInvincible()) {
            console.info("Set player as non invincible");
            solShip.setInvincible(false);
        }
    }
}
