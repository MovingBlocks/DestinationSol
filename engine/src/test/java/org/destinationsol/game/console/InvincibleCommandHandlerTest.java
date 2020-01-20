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
import org.destinationsol.game.console.commands.InvincibleCommandHandler;
import org.destinationsol.game.console.exceptions.CommandExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvincibleCommandHandlerTest {

    private InvincibleCommandHandler commandHandler;

    @Mock
    private SolGame game;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Hero hero;

    @Before
    public void init() {
        commandHandler = new InvincibleCommandHandler();
    }

    @Test
    public void shouldChangeInvincibilityWhenAliveAndNonTranscendent() {
        when(hero.isDead()).thenReturn(false);
        when(hero.isTranscendent()).thenReturn(false);
        when(game.getHero()).thenReturn(hero);
        try {
            commandHandler.godMode(game);
        } catch (CommandExecutionException e) {
            fail();
        }

        verify(hero, times(1)).setInvincible(anyBoolean());
    }

    @Test
    public void shouldNotChangeInvincibilityWhenAliveAndTranscendent() {
        when(hero.isDead()).thenReturn(false);
        when(hero.isTranscendent()).thenReturn(true);
        when(game.getHero()).thenReturn(hero);
        boolean threwException = false;
        try {
            commandHandler.godMode(game);
        } catch (CommandExecutionException e) {
            threwException = true;
        }

        if (!threwException) {
            fail();
        }

        verify(hero, never()).setInvincible(anyBoolean());
    }

    @Test
    public void shouldNotChangeInvincibilityWhenDead() {
        when(hero.isDead()).thenReturn(true);
        when(game.getHero()).thenReturn(hero);
        boolean threwException = false;
        try {
            commandHandler.godMode(game);
        } catch (CommandExecutionException e) {
            threwException = true;
        }

        if (!threwException) {
            fail();
        }

        verify(hero, never()).setInvincible(anyBoolean());
    }

}
