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
import org.destinationsol.game.ship.ShipRepairer;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.Optional;

/**
 * A command used to change current ship on the fly.
 * Takes one argument - id of the ship you want to load
 */
public class ChangeShipCommandHandler implements ConsoleInputHandler {

    private Hero hero;
    private SolGame game;

    public ChangeShipCommandHandler(Hero hero, SolGame game) {
        this.hero = hero;
        this.game = game;
    }

    @Override
    public void handle(String input, Console console) {
        String[] args = input.split(" ", 2);

        if(args.length != 2) {
            printHelp(console);
            return;
        }

        Optional<SolShip> newShip = cloneAndModifyShip(hero.getShip(), args[1]);
        if(!newShip.isPresent()) {
            printHelp(console);
            return;
        }

        game.getObjectManager().removeObjDelayed(hero.getShip());
        game.getObjectManager().addObjDelayed(newShip.get());
        hero.setSolShip(newShip.get(), game);
    }

    private void printHelp(Console console) {
        console.warn("Invalid or Unknown ship ID.");
        console.warn("Usage: \"changeship module:shipName\"");
    }

    private Optional<SolShip> cloneAndModifyShip(SolShip originalShip, String newShipID) {
        HullConfig newHullConfig;
        try {
            newHullConfig = game.getHullConfigManager().getConfig(newShipID);
        }
        catch (RuntimeException e) {
            return Optional.empty();
        }

        SolShip newShip = game.getShipBuilder().build(game, originalShip.getPosition(), originalShip.getVelocity(), originalShip.getAngle(),
                originalShip.getRotationSpeed(), originalShip.getPilot(), originalShip.getItemContainer(), newHullConfig,
                newHullConfig.getMaxLife(), originalShip.getHull().getGun(false), originalShip.getHull().getGun(true), null,
                newHullConfig.getEngineConfig().exampleEngine.copy(), new ShipRepairer(), originalShip.getMoney(), null, originalShip.getShield(), originalShip.getArmor());
        return Optional.of(newShip);
    }
}
