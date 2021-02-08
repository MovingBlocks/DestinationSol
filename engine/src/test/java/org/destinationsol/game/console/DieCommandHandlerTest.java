/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.game.console;

import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.commands.DieCommandHandler;
import org.destinationsol.game.console.exceptions.CommandExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DieCommandHandlerTest {

    private DieCommandHandler commandHandler;

    @Mock
    private SolGame game;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Hero hero;

    @BeforeEach
    public void init() {
        commandHandler = new DieCommandHandler();
    }

    @Test
    public void shouldKillWhenAliveAndNotTranscendent() {
        when(hero.isTranscendent()).thenReturn(false);
        when(hero.isAlive()).thenReturn(true);
        when(game.getHero()).thenReturn(hero);
        try {
            commandHandler.die(game);
        } catch (CommandExecutionException e) {
            fail();
        }
        verify(hero, times(1)).getShip();
    }

    @Test
    public void shouldNotKillWhenTranscendent() {
        when(hero.isTranscendent()).thenReturn(true);
        when(game.getHero()).thenReturn(hero);
        boolean threwException = false;
        try {
            commandHandler.die(game);
        } catch (CommandExecutionException e) {
            threwException = true;
        }

        if (!threwException) {
            fail();
        }

        verify(hero, never()).getShip();
    }

    @Test
    public void shouldNotKillWhenAlreadyDead() {
        when(hero.isTranscendent()).thenReturn(false);
        when(hero.isAlive()).thenReturn(false);
        when(game.getHero()).thenReturn(hero);
        boolean threwException = false;
        try {
            commandHandler.die(game);
        } catch (CommandExecutionException e) {
            threwException = true;
        }

        if (!threwException) {
            fail();
        }

        verify(hero, never()).getShip();
    }
}
