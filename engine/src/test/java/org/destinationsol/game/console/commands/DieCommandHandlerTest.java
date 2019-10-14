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
import org.mockito.Mockito;

public class DieCommandHandlerTest {

    private DieCommandHandler commandHandler;
    private Hero hero;
    private Console console;

    @Before
    public void init() {
        SolGame game = Mockito.mock(SolGame.class);
        hero = Mockito.mock(Hero.class, Mockito.RETURNS_DEEP_STUBS);
        commandHandler = new DieCommandHandler(hero, game);
        console = Mockito.mock(Console.class);
    }

    @Test
    public void shouldKillWhenAliveAndNotTranscendent() {
        Mockito.when(hero.isTranscendent()).thenReturn(false);
        Mockito.when(hero.isAlive()).thenReturn(true);
        Mockito.verify(hero, Mockito.never()).getShip();
    }

    @Test
    public void shouldNotKillWhenTranscendent() {
        Mockito.when(hero.isTranscendent()).thenReturn(true);
        commandHandler.handle("respawn", console);
        Mockito.verify(hero, Mockito.never()).getShip();
    }

    @Test
    public void shouldNotKillWhenAlreadyDead() {
        Mockito.when(hero.isAlive()).thenReturn(false);
        commandHandler.handle("respawn", console);
        Mockito.verify(hero, Mockito.never()).getShip();
    }

}
