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
import org.destinationsol.game.ship.SolShip;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChangeShipCommandHandlerTest {
    private ChangeShipCommandHandler commandHandler;

    @Mock
    private SolGame game;

    @Mock
    private Hero hero;

    @Mock
    private Console console;

    @Before
    public void init() {
        commandHandler = new ChangeShipCommandHandler(hero, game);
    }

    @Test
    public void shouldPrintHelpOnInvalidInput() {
        commandHandler.handle("changeship xyz", console);
        verify(console, times(2)).warn(anyString());
    }

    @Test
    public void shouldPrintHelpOnEmptyInput() {
        commandHandler.handle("changeship", console);
        verify(console, times(2)).warn(anyString());
    }

    @Test
    public void shouldChangeShipOnValidInput() {
        commandHandler.handle("changeship core:imperialTiny", console);
        verify(hero, times(1)).setSolShip(any(), game);
    }
}
