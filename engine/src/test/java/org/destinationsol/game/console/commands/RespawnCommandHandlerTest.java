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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class RespawnCommandHandlerTest {

    private RespawnCommandHandler commandHandler;
    private SolGame game;
    private Hero hero;
    private Console console;

    @Before
    public void init() {
        game = Mockito.mock(SolGame.class);
        hero = Mockito.mock(Hero.class);
        commandHandler = new RespawnCommandHandler(hero, game);
        console = Mockito.mock(Console.class);
    }

    @Test
    public void shouldRespawnWhenDead() {
        Mockito.when(hero.isAlive()).thenReturn(false);
        commandHandler.handle("respawn", console);
        Mockito.verify(game, Mockito.times(1)).respawn();
    }

    @Test
    public void shouldNotRespawnWhenAlive() {
        Mockito.when(hero.isAlive()).thenReturn(true);
        commandHandler.handle("respawn", console);
        Mockito.verify(game, Mockito.never()).respawn();
    }
}
