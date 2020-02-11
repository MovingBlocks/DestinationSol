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
import org.destinationsol.game.console.commands.ChangeShipCommandHandler;
import org.destinationsol.game.console.exceptions.CommandExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChangeShipCommandHandlerTest {

    private ChangeShipCommandHandler commandHandler;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SolGame game;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Hero hero;

    @Before
    public void init() {
        commandHandler = new ChangeShipCommandHandler();
    }

    @Test
    public void shouldNotChangeShipWhenNonExistingShipPassed() {
        boolean threwException = false;
        try {
            commandHandler.changeShip(game, null);
        } catch (CommandExecutionException e) {
            threwException = true;
        }

        if (!threwException) {
            fail();
        }

        verify(hero, never()).setSolShip(any(), eq(game));
    }
}
