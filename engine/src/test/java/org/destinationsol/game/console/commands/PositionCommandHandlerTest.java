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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.Console;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.SolShip;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PositionCommandHandlerTest {

    private PositionCommandHandler commandHandler;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SolGame game;

    @Mock
    private SolShip ship;

    @Mock
    private Console console;

    @Before
    public void init() {
        when(ship.getPosition()).thenReturn(new Vector2(1f, 2f));
        Hero hero = new Hero(ship, game);
        commandHandler = new PositionCommandHandler(hero);
    }

    @Test
    public void shouldPrintWarningOnUnknownFormat() {
        commandHandler.handle("position xyz", console);
        verify(ship, never()).getPosition();
        verify(console, never()).info(anyString());
        verify(console, times(1)).warn("Invalid position format: \"xyz\"!");
    }

    @Test
    public void shouldPrintAvailableFormatsOnUnknownFormat() {
        int formatsCount = PositionCommandHandler.PositionFormat.values().length;
        int additionalLines = 2; //one for warning about unsupported format, one for list header
        int totalLines = formatsCount + additionalLines;
        commandHandler.handle("position xyz", console);
        verify(console, times(totalLines)).warn(anyString());
    }

    @Test
    public void checkDefaultOutput() {
        commandHandler.handle("position", console);
        verify(console, only()).info("X: " + ship.getPosition().x + "   Y: " + ship.getPosition().y);
    }

    @Test
    public void checkTerseOutput() {
        commandHandler.handle("position terse", console);
        verify(console, only()).info("X: " + ship.getPosition().x + "   Y: " + ship.getPosition().y);
    }

    @Test
    public void checkVerboseOutput() {
        commandHandler.handle("position verbose", console);
        verify(console, times(1)).info("The hero's X co-ordinate is: " + ship.getPosition().x);
        verify(console, times(1)).info("The hero's Y co-ordinate is: " + ship.getPosition().y);
    }

    @Test
    public void checkBoldOutput() {
        commandHandler.handle("position bold", console);
        //Size of wanted message is known for test coordinates as well for the separator
        verify(console, times(2)).info("************");
        verify(console, times(1)).info("X: " + ship.getPosition().x);
        verify(console, times(1)).info("Y: " + ship.getPosition().y);
    }

    @Test
    public void checkInternalOutput() {
        commandHandler.handle("position internal", console);
        verify(console, times(1)).info(ship.getPosition().toString());
    }


}
