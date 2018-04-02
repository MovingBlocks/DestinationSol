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
import org.destinationsol.Const;
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.BeaconDestProvider;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.input.UiControlledPilot;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.item.TradeConfig;
import org.destinationsol.game.ship.hulls.HullConfig;

class PlayerCreator {

    private static final int TUTORIAL_MONEY_AMOUNT = 200;

    Hero createPlayer(ShipConfig shipConfig, boolean shouldSpawnOnGalaxySpawnPosition, RespawnState respawnState, SolGame game, boolean isMouseControl, boolean isNewShip) {
        // If we continue a game, we should spawn from the same position
        Vector2 position = findPlayerSpawnPosition(shipConfig, shouldSpawnOnGalaxySpawnPosition, game);
        game.getCam().setPos(position);

        if (isMouseControl) {
            game.getBeaconHandler().init(game, position);
        }
        Pilot pilot = createPilot(game, isMouseControl);

        float money = grantPlayerMoney(shipConfig, respawnState, game);

        HullConfig hull = findHullConfig(shipConfig, respawnState);

        String itemsStr = !respawnState.getRespawnItems().isEmpty() ? "" : shipConfig.getItems();

        boolean giveAmmo = isNewShip && respawnState.getRespawnItems().isEmpty();
        Hero hero = new Hero(game.getShipBuilder().buildNewFar(game, new Vector2(position), null, 0, 0, pilot, itemsStr, hull, null, true, money, new TradeConfig(), giveAmmo).toObject(game));
        ItemContainer itemContainer = hero.getItemContainer();
        if (!respawnState.getRespawnItems().isEmpty()) {
            for (SolItem item : respawnState.getRespawnItems()) {
                itemContainer.add(item);
                // Ensure that previously equipped items stay equipped
                if (item.isEquipped() > 0) {
                    if (item instanceof Gun) {
                        hero.maybeEquip(game, item, item.isEquipped() == 2, true);
                    } else {
                        hero.maybeEquip(game, item, true);
                    }
                }
            }
        } else if (game.getTutMan() != null) {
            for (int i = 0; i < 50; i++) {
                if (itemContainer.groupCount() > 1.5f * Const.ITEM_GROUPS_PER_PAGE) {
                    break;
                }
                SolItem it = game.getItemMan().random();
                if (!(it instanceof Gun) && it.getIcon(game) != null && itemContainer.canAdd(it)) {
                    itemContainer.add(it.copy());
                }
            }
        }
        itemContainer.markAllAsSeen();

        // Don't change equipped items across load/respawn
        //AiPilot.reEquip(this, myHero);

        game.getObjectManager().addObjDelayed(hero.getShip());
        game.getObjectManager().resetDelays();
        return hero;
    }

    private HullConfig findHullConfig(ShipConfig shipConfig, RespawnState respawnState) {
        if (respawnState.getRespawnHull() != null) {
            return respawnState.getRespawnHull();
        }
        return shipConfig.getHull();
    }

    private float grantPlayerMoney(ShipConfig shipConfig, RespawnState respawnState, SolGame game) {
        if (respawnState.getRespawnMoney() != 0) {
            return respawnState.getRespawnMoney();
        }
        if (game.getTutMan() != null) {
            return TUTORIAL_MONEY_AMOUNT;
        }
        return shipConfig.getMoney();
    }

    private Pilot createPilot(SolGame game, boolean isMouseControl) {
        if (isMouseControl) {
            return new AiPilot(new BeaconDestProvider(), true, Faction.LAANI, false, "you", Const.AI_DET_DIST);
        } else {
            return new UiControlledPilot(game.getScreens().mainScreen);
        }
    }

    private Vector2 findPlayerSpawnPosition(ShipConfig shipConfig, boolean shouldSpawnOnGalaxySpawnPosition, SolGame game) {
        if (shouldSpawnOnGalaxySpawnPosition) {
            return game.getGalaxyFiller().getPlayerSpawnPos(game);
        } else {
            return shipConfig.getSpawnPos();
        }
    }
}
