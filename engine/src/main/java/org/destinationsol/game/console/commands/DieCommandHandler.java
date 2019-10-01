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
import org.destinationsol.game.DmgType;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.ConsoleInputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A command used to instantly destroy the hero's ship, mostly for debugging purposes.
 */
public class DieCommandHandler implements ConsoleInputHandler {
    public Hero hero;
    public SolGame game;

    public DieCommandHandler(Hero hero, SolGame game) {
        this.hero = hero;
        this.game = game;
    }

    private static Logger logger = LoggerFactory.getLogger(DieCommandHandler.class);

    @Override
    public void handle(String input, Console console) {
        if(hero.isTranscendent()) {
            logger.warn("Cannot kill hero when transcendent!");
            console.println("Cannot kill hero when transcendent!");
        }
        if(!hero.isAlive()) {
            logger.warn("Hero is already dead!");
            console.println("Hero is already dead!");
        }
        hero.getShip().receivePiercingDmg(hero.getHull().getHullConfig().getMaxLife() + 1, game, hero.getPosition(), DmgType.CRASH);
    }
}
