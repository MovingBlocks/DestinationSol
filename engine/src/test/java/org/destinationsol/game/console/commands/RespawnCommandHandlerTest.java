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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RespawnCommandHandlerTest {

    private RespawnCommandHandler commandHandler;

    @Mock
    private SolGame game;

    @Mock
    private Hero hero;

    @Mock
    private Console console;

    @Before
    public void init() {
        commandHandler = new RespawnCommandHandler(hero, game);
    }

    @Test
    public void shouldRespawnWhenDead() {
        when(hero.isAlive()).thenReturn(false);
        commandHandler.handle("respawn", console);
        verify(game, times(1)).respawn();
    }

    @Test
    public void shouldNotRespawnWhenAlive() {
        when(hero.isAlive()).thenReturn(true);
        commandHandler.handle("respawn", console);
        verify(console, times(1)).warn(anyString());
        verify(game, never()).respawn();
    }
}
