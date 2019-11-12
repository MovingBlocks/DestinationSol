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
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DieCommandHandlerTest {

    private DieCommandHandler commandHandler;

    @Mock
    private SolGame game;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Hero hero;

    @Mock
    private Console console;

    @Before
    public void init() {
        commandHandler = new DieCommandHandler(hero, game);
    }

    @Test
    public void shouldKillWhenAliveAndNotTranscendent() {
        when(hero.isTranscendent()).thenReturn(false);
        when(hero.isAlive()).thenReturn(true);
        commandHandler.handle("die", console);
        verify(hero, Mockito.times(1)).getShip();
    }

    @Test
    public void shouldNotKillWhenTranscendent() {
        when(hero.isTranscendent()).thenReturn(true);
        commandHandler.handle("die", console);
        verify(console, times(1)).warn(anyString());
        verify(hero, never()).getShip();
    }

    @Test
    public void shouldNotKillWhenAlreadyDead() {
        when(hero.isAlive()).thenReturn(false);
        commandHandler.handle("die", console);
        verify(console, times(1)).warn(anyString());
        verify(hero, never()).getShip();
    }

}
