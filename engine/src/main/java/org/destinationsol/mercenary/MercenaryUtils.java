/*
 * Copyright 2018 MovingBlocks
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
package org.destinationsol.mercenary;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.Faction;
import org.destinationsol.game.Hero;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.Guardian;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

public class MercenaryUtils {
    /**
     * Creates a new mercenary, SolShip and all.
     * This method also adds the MercItem and SolShip to the proper registries and sets the proper variables.
     * @param game The instance of the game we're dealing with
     * @param hero The player
     * @param mercItem The mercenary item to build everything else off of
     * @return true if the ship could be created, false if not
     */
    public static boolean createMerc(SolGame game, Hero hero, MercItem mercItem) {
        ShipConfig config = mercItem.getConfig();
        Guardian guardian = new Guardian(game, config.hull, hero.getPilot(), hero.getPosition(), hero.getHull().config, SolRandom.randomFloat(180));
        AiPilot pilot = new AiPilot(guardian, true, Faction.LAANI, false, "Merc", Const.AI_DET_DIST);
        Vector2 position = getPos(game, hero, config.hull);
        if (position == null) {
            return false;
        }
        SolShip merc = game.getShipBuilder().buildNewFar(game, position, new Vector2(), 0, 0, pilot, config.items, config.hull, null, true, config.money, null, true)
                .toObject(game);
        
        merc.setMerc(mercItem);
        mercItem.setSolShip(merc);
        
        game.getHero().getMercs().add(mercItem);
        game.getObjectManager().addObjNow(game, merc);
        return true;
    }
    
    /**
     * Finds the position at which to spawn the mercenary
     * @param game The instance of the game we're dealing with
     * @param hero The player
     * @param hull The hull of the mercenary in question
     * @return The position to spawn the mercenary at, or null for no available position
     */
    private static Vector2 getPos(SolGame game, Hero hero, HullConfig hull) {
        Vector2 position = new Vector2();
        float dist = hero.getHull().config.getApproxRadius() + Guardian.DIST + hull.getApproxRadius();
        Vector2 heroPos = hero.getPosition();
        Planet nearestPlanet = game.getPlanetManager().getNearestPlanet(heroPos);
        boolean nearGround = nearestPlanet.isNearGround(heroPos);
        float fromPlanet = SolMath.angle(nearestPlanet.getPosition(), heroPos);
        for (int i = 0; i < 50; i++) {
            float relAngle;
            if (nearGround) {
                relAngle = fromPlanet;
            } else {
                relAngle = SolRandom.randomInt(180);
            }
            SolMath.fromAl(position, relAngle, dist);
            position.add(heroPos);
            if (game.isPlaceEmpty(position, false)) {
                return position;
            }
            dist += Guardian.DIST;
        }
        return null;
    }
    
}
