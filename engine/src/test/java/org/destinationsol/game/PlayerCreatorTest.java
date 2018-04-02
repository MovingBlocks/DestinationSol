/*
 * Copyright 2017 MovingBlocks
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
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.ShipBuilder;
import org.destinationsol.game.ship.SolShip;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlayerCreatorTest {

    private PlayerCreator playerCreator;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SolGame solGame;
    @Mock
    private ShipConfig shipConfig;
    @Mock
    private ShipBuilder shipBuilder;
    private RespawnState respawnState;
    private Vector2 galaxySpawnPosition;
    private Vector2 shipConfigSpawnPosition;
    @Mock
    private FarShip farShip;
    @Mock
    private SolShip solShip;

    @Before
    public void setUp() {
        playerCreator = new PlayerCreator();
        respawnState = new RespawnState();
        galaxySpawnPosition = new Vector2(42, 43);
        shipConfigSpawnPosition = new Vector2(11,12);
        when(solGame.getGalaxyFiller().getPlayerSpawnPos(any())).thenReturn(galaxySpawnPosition);
        when(shipConfig.getSpawnPos()).thenReturn(shipConfigSpawnPosition);
        mockShipBuilding();
    }

    private void mockShipBuilding() {
        when(solGame.getShipBuilder()).thenReturn(shipBuilder);
        when(shipBuilder.buildNewFar(any(), any(), any(), anyFloat(), anyFloat(), any(), any(), any(), any(), anyBoolean(), anyFloat(), any(), anyBoolean())).thenReturn(farShip);
        when(farShip.toObject(any())).thenReturn(solShip);
        when(solShip.getItemContainer()).thenReturn(new ItemContainer());
    }

    @Test
    public void testSpawnOnGalaxySpawnPosition() {
        when(solGame.getTutMan()).thenReturn(null);
        playerCreator.createPlayer(shipConfig, true, respawnState, solGame, false, false);
        verify(shipBuilder).buildNewFar(any(), eq(galaxySpawnPosition), any(), anyFloat(), anyFloat(), any(), any(), any(), any(), anyBoolean(), anyFloat(), any(), anyBoolean());
    }

    @Test
    public void testSpawnOnShipConfigSpawnPosition() {
        when(solGame.getTutMan()).thenReturn(null);
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verify(shipBuilder).buildNewFar(any(), eq(shipConfigSpawnPosition), any(), anyFloat(), anyFloat(), any(), any(), any(), any(), anyBoolean(), anyFloat(), any(), anyBoolean());
    }
}