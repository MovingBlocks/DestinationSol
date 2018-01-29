package org.destinationsol.mercenary;

import org.destinationsol.Const;
import org.destinationsol.common.SolDescriptiveException;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.Faction;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.Guardian;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

import com.badlogic.gdx.math.Vector2;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MercenaryUtils {

    /**
     * Creates a new mercenary, SolShip and all. It also adds the MercItem and SolShip to the
     * proper registries and sets the proper variables.
     *
     * @param game         The instance of the game we're dealing with
     * @param heroOptional The player
     * @param mercItem     The mercenary item to build everything else off of
     * @return True if the ship could be created, false if not
     */
    public static boolean createMerc(@NotNull SolGame game, Optional<SolShip> heroOptional, @NotNull MercItem mercItem) {
        SolShip hero = heroOptional.orElseThrow(() -> new SolDescriptiveException("Something is trying to create a mercenary with null hero. This isn't supposed to happen in any way."));
        ShipConfig config = mercItem.getConfig();
        Guardian guardian = new Guardian(game, config.hull, hero.getPilot(), hero.getPosition(), hero.getHull().config, SolMath.rnd(180));
        AiPilot pilot = new AiPilot(guardian, true, Faction.LAANI, false, "Merc", Const.AI_DET_DIST);
        Vector2 pos = getPos(game, hero, config.hull);
        if (pos == null) {
            return false;
        }
        SolShip merc = game.getShipBuilder().buildNewFar(game, pos, new Vector2(), 0, 0, pilot, config.items, config.hull, null, true, config.money, null, true)
                .toObj(game);

        merc.setMerc(mercItem);
        mercItem.setSolShip(merc);

        game.getHero().getTradeContainer().getMercs().add(mercItem);
        game.getObjectManager().addObjNow(game, merc);
        return true;
    }

    /**
     * Finds the position at which to spawn the mercenary
     *
     * @param game The instance of the game we're dealing with
     * @param hero The player
     * @param hull The hull of the mercenary in question
     * @return The position to spawn the mercenary at, or null for no available position
     */
    private static Vector2 getPos(SolGame game, SolShip hero, HullConfig hull) {
        Vector2 pos = new Vector2();
        float dist = hero.getHull().config.getApproxRadius() + Guardian.DIST + hull.getApproxRadius();
        Vector2 heroPos = hero.getPosition();
        Planet np = game.getPlanetManager().getNearestPlanet(heroPos);
        boolean nearGround = np.isNearGround(heroPos);
        float fromPlanet = SolMath.angle(np.getPos(), heroPos);
        for (int i = 0; i < 50; i++) {
            float relAngle;
            if (nearGround) {
                relAngle = fromPlanet;
            } else {
                relAngle = SolMath.rnd(180);
            }
            SolMath.fromAl(pos, relAngle, dist);
            pos.add(heroPos);
            if (game.isPlaceEmpty(pos, false)) {
                return pos;
            }
            dist += Guardian.DIST;
        }
        return null;
    }

}
