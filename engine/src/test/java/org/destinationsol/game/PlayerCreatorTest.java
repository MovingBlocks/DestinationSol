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
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.input.UiControlledPilot;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.ShipBuilder;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.ui.TutorialManager;
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
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlayerCreatorTest {

    public static final float TUTORIAL_MONEY = 200f;
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
        shipConfigSpawnPosition = new Vector2(11, 12);
        when(solGame.getGalaxyFiller().getPlayerSpawnPos(any())).thenReturn(galaxySpawnPosition);
        when(shipConfig.getSpawnPos()).thenReturn(shipConfigSpawnPosition);
        mockShipBuilding();
        //no tutorial manager == not in tutorial mode
        when(solGame.getTutMan()).thenReturn(null);
    }

    private void mockShipBuilding() {
        when(solGame.getShipBuilder()).thenReturn(shipBuilder);
        when(shipBuilder.buildNewFar(any(), any(), any(), anyFloat(), anyFloat(), any(), any(), any(), any(), anyBoolean(), anyFloat(), any(), anyBoolean())).thenReturn(farShip);
        when(farShip.toObject(any())).thenReturn(solShip);
        when(solShip.getItemContainer()).thenReturn(new ItemContainer());
    }

    @Test
    public void testSpawnOnGalaxySpawnPositionSetsShipPosition() {
        playerCreator.createPlayer(shipConfig, true, respawnState, solGame, false, false);
        verify(shipBuilder).buildNewFar(any(), eq(galaxySpawnPosition), any(), anyFloat(), anyFloat(), any(), any(), any(), any(), anyBoolean(), anyFloat(), any(), anyBoolean());
    }

    @Test
    public void testSpawnOnGalaxySpawnPositionSetsCameraPosition() {
        playerCreator.createPlayer(shipConfig, true, respawnState, solGame, false, false);
        verify(solGame.getCam()).setPos(eq(galaxySpawnPosition));
    }

    @Test
    public void testSpawnOnShipConfigSpawnPositionSetsShipPosition() {
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verify(shipBuilder).buildNewFar(any(), eq(shipConfigSpawnPosition), any(), anyFloat(), anyFloat(), any(), any(), any(), any(), anyBoolean(), anyFloat(), any(), anyBoolean());
    }

    @Test
    public void testSpawnOnShipConfigSpawnPositionSetsCameraPosition() {
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verify(solGame.getCam()).setPos(eq(shipConfigSpawnPosition));
    }

    @Test
    public void testBeaconHandlerNotInitializedIfNotMouseControl() {
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verify(solGame.getBeaconHandler(), never()).init(any(), any());
    }

    @Test
    public void testSpawnOnGalaxySpawnPositionInitsBeaconHandlerOnMouseControl() {
        playerCreator.createPlayer(shipConfig, true, respawnState, solGame, true, false);
        verify(solGame.getBeaconHandler()).init(any(), eq(galaxySpawnPosition));
    }

    @Test
    public void testSpawnOnShipConfigSpawnPositionInitsBeaconHandlerOnMouseControl() {
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, true, false);
        verify(solGame.getBeaconHandler()).init(any(), eq(shipConfigSpawnPosition));
    }

    @Test
    public void testMouseControlCreatesAiPilot() {
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, true, false);
        verifyBuildNewFar(shipConfiguration().withPilot(AiPilot.class));
    }

    @Test
    public void testNoMouseControlCreatesUiControlledPilot() {
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verifyBuildNewFar(shipConfiguration().withPilot(UiControlledPilot.class));
    }

    @Test
    public void testUseRespawnMoneyIfNotZero() {
        float respawnMoney = 42f;
        respawnState.setRespawnMoney(respawnMoney);
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verifyBuildNewFar(shipConfiguration().withMoney(respawnMoney));
    }

    @Test
    public void testUseShipConfigMoneyIfNoRespawnMoneyAndNoTutorial() {
        int shipConfigMoney = 42;
        when(shipConfig.getMoney()).thenReturn(shipConfigMoney);
        respawnState.setRespawnMoney(0);
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verifyBuildNewFar(shipConfiguration().withMoney(shipConfigMoney));
    }

    @Test
    public void testUseTutorialMoneyIfNoRespawnMoneyAndTutorialActive() {
        when(solGame.getTutMan()).thenReturn(mock(TutorialManager.class));
        respawnState.setRespawnMoney(0);
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verifyBuildNewFar(shipConfiguration().withMoney(TUTORIAL_MONEY));
    }

    @Test
    public void testUseRespawnHullIfNotNull() {
        HullConfig hullConfig = mock(HullConfig.class);
        respawnState.setRespawnHull(hullConfig);
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verifyBuildNewFar(shipConfiguration().withHullConfig(hullConfig));
    }

    @Test
    public void testUseShipConfigHullIfRespawnHullIsNull() {
        HullConfig hullConfig = mock(HullConfig.class);
        respawnState.setRespawnHull(null);
        when(shipConfig.getHull()).thenReturn(hullConfig);
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verifyBuildNewFar(shipConfiguration().withHullConfig(hullConfig));
    }

    @Test
    public void testUseEmptyItemsIfRespawnItemsNotEmpty() {
        respawnState.getRespawnItems().add(mock(SolItem.class));
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verifyBuildNewFar(shipConfiguration().withItems(""));
    }

    @Test
    public void testUseShipConfigItemsIfRespawnItemsIsEmpty() {
        respawnState.getRespawnItems().clear();
        String items = "core:plasmaGun+core:blaster 0.36|core:smallShield";
        when(shipConfig.getItems()).thenReturn(items);
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verifyBuildNewFar(shipConfiguration().withItems(items));
    }

    @Test
    public void testGiveAmmoIfNewShipAndRespawnItemsIsEmpty() {
        respawnState.getRespawnItems().clear();
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, true);
        verifyBuildNewFar(shipConfiguration().withGiveAmmo(true));
    }

    @Test
    public void testGiveNoAmmoIfNewShipAndRespawnItemsNotEmpty() {
        respawnState.getRespawnItems().add(mock(SolItem.class));
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, true);
        verifyBuildNewFar(shipConfiguration().withGiveAmmo(false));
    }

    @Test
    public void testGiveNoAmmoIfNoNewShipAndRespawnItemsEmpty() {
        respawnState.getRespawnItems().clear();
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verifyBuildNewFar(shipConfiguration().withGiveAmmo(false));
    }

    private void verifyBuildNewFar(FarShipBuildConfiguration verification) {
        verify(shipBuilder).buildNewFar(any(),
                any(),
                any(),
                anyFloat(),
                anyFloat(),
                verification.pilot(),
                verification.items(),
                verification.hullConfig(),
                any(),
                anyBoolean(),
                verification.money(),
                any(),
                verification.giveAmmo());
    }

    private static class FarShipBuildConfiguration {
        Float money;
        Class<? extends Pilot> pilotClazz;
        HullConfig hullConfig;
        String items;
        Boolean giveAmmo;

        FarShipBuildConfiguration withMoney(float money) {
            this.money = money;
            return this;
        }

        FarShipBuildConfiguration withPilot(Class<? extends Pilot> pilotClazz) {
            this.pilotClazz = pilotClazz;
            return this;
        }

        FarShipBuildConfiguration withHullConfig(HullConfig hullConfig) {
            this.hullConfig = hullConfig;
            return this;
        }

        FarShipBuildConfiguration withItems(String items) {
            this.items = items;
            return this;
        }

        FarShipBuildConfiguration withGiveAmmo(boolean giveAmmo) {
            this.giveAmmo = giveAmmo;
            return this;
        }

        float money() {
            return money == null ? anyFloat() : eq(money.floatValue());
        }

        Pilot pilot() {
            return pilotClazz == null ? any() : any(pilotClazz);
        }

        HullConfig hullConfig() {
            return hullConfig == null ? any() : same(hullConfig);
        }

        String items() {
            return items == null ? any() : eq(items);
        }

        boolean giveAmmo() {
            return giveAmmo == null ? anyBoolean() : eq(giveAmmo.booleanValue());
        }
    }

    private FarShipBuildConfiguration shipConfiguration() {
        return new FarShipBuildConfiguration();
    }

}