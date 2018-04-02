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
import org.destinationsol.game.item.Gun;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    private ItemContainer shipItemContainer;

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
        shipItemContainer = new ItemContainer();
        when(solShip.getItemContainer()).thenReturn(shipItemContainer);
    }

    @Test
    public void testSpawnOnGalaxySpawnPositionSetsShipPosition() {
        playerCreator.createPlayer(shipConfig, true, respawnState, solGame, false, false);
        verifyBuildNewFar(shipConfiguration().withPosition(galaxySpawnPosition));
    }

    @Test
    public void testSpawnOnGalaxySpawnPositionSetsCameraPosition() {
        playerCreator.createPlayer(shipConfig, true, respawnState, solGame, false, false);
        verify(solGame.getCam()).setPos(eq(galaxySpawnPosition));
    }

    @Test
    public void testSpawnOnShipConfigSpawnPositionSetsShipPosition() {
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verifyBuildNewFar(shipConfiguration().withPosition(shipConfigSpawnPosition));
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

    @Test
    public void testShipIsUsedToCreateHero() {
        Hero hero = playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        assertThat(hero.getShip()).isSameAs(solShip);
    }

    @Test
    public void testAddRespawnItems() {
        SolItem item0 = mock(SolItem.class);
        respawnState.getRespawnItems().add(item0);
        SolItem item1 = mock(SolItem.class);
        respawnState.getRespawnItems().add(item1);
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        //new item groups are created at the start of the container, therefore item1 ends in group 0
        assertThat(shipItemContainer.getGroup(0)).containsExactly(item1);
        assertThat(shipItemContainer.getGroup(1)).containsExactly(item0);
    }

    @Test
    public void testReEquipRespawnItemsSeen() {
        SolItem item = mock(SolItem.class);
        when(item.isEquipped()).thenReturn(1);
        respawnState.getRespawnItems().add(item);
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verify(solShip).maybeEquip(any(), eq(item), eq(true));
        assertThat(shipItemContainer.hasNew()).isFalse();
    }

    @Test
    public void testReEquipRespawnItemGunsPrimarySlot() {
        Gun gun = mock(Gun.class);
        when(gun.isEquipped()).thenReturn(1);
        respawnState.getRespawnItems().add(gun);
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verify(solShip).maybeEquip(any(), eq(gun), eq(false),eq(true));
    }

    @Test
    public void testReEquipRespawnItemGunsSecondarySlot() {
        Gun gun = mock(Gun.class);
        when(gun.isEquipped()).thenReturn(2);
        respawnState.getRespawnItems().add(gun);
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verify(solShip).maybeEquip(any(), eq(gun), eq(true),eq(true));
    }

    @Test
    public void testTutorialModeAddsSeenItemsIfRespawnItemsAreEmpty() {
        respawnState.getRespawnItems().clear();
        when(solGame.getTutMan()).thenReturn(mock(TutorialManager.class));
        int groupCountBefore = shipItemContainer.groupCount();
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        assertThat(shipItemContainer.groupCount()).isGreaterThan(groupCountBefore);
        assertThat(shipItemContainer.hasNew()).isFalse();
    }

    @Test
    public void testAddShipDelayed() {
        playerCreator.createPlayer(shipConfig, false, respawnState, solGame, false, false);
        verify(solGame.getObjectManager()).addObjDelayed(solShip);
    }

    private void verifyBuildNewFar(FarShipBuildConfiguration verification) {
        verify(shipBuilder).buildNewFar(any(),
                verification.position(),
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
        Vector2 position;
        Float money;
        Class<? extends Pilot> pilotClazz;
        HullConfig hullConfig;
        String items;
        Boolean giveAmmo;

        FarShipBuildConfiguration withPosition(Vector2 position) {
            this.position = position;
            return this;
        }

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

        Vector2 position() {
            return position == null ? any() : eq(position);
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