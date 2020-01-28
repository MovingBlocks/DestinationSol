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
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.GameOptions;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.common.SolException;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.Armor;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.ShipAbility;
import org.destinationsol.game.ship.ShipRepairer;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.Hull;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.ui.Waypoint;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A wrapper class for the Hero, that handles the normal and transcendent ships transparently.
 */
public class Hero {
    private SolShip shipHero;
    private StarPort.Transcendent transcendentHero;
    private ItemContainer mercs;
    private FarShip transcendentHeroShip;
    private boolean isTranscendent;
    private boolean isDead;
    private boolean isInvincible;
    private ArrayList<Waypoint> waypoints;

    public Hero(SolShip shipHero, SolGame solGame) {
        if (shipHero == null) {
            throw new SolException("Something is trying to create the hero when there is no ship linked to him.");
        }
        setSolShip(shipHero, solGame);
    }

    public void initialise(SolGame game) {
    }

    public void setTranscendent(StarPort.Transcendent transcendentHero) {
        this.transcendentHero = transcendentHero;
        transcendentHeroShip = transcendentHero.getShip();
        isTranscendent = true;
    }

    public void setSolShip(SolShip hero, SolGame solGame) {
        waypoints = new ArrayList<>();
        isDead = false;
        if (hero != shipHero && !isTranscendent) {
            mercs = new ItemContainer();
        }
        this.shipHero = hero;
        isTranscendent = false;
        if (shipHero.getTradeContainer() != null) {
            throw new SolException("Hero is not supposed to have TradeContainer.");
        }
        GameOptions options = solGame.getSolApplication().getOptions();
        //Satisfying unit tests
        if (hero.getHull() != null)
            solGame.getSolApplication().getMusicManager().registerModuleMusic(hero.getHull().getHullConfig().getInternalName().split(":")[0], options);
        solGame.getSolApplication().getMusicManager().playMusic(OggMusicManager.GAME_MUSIC_SET, options);
    }

    public String changeShip(Hero hero, HullConfig newHullConfig, SolGame game) {
        SolShip originalShip = hero.getShip();
        Optional<SolShip> newShip;

        AtomicInteger objectCount = new AtomicInteger(0);
        game.getObjectManager().doToAllCloserThan(newHullConfig.getSize(), originalShip.getPosition(), (SolObject obj) ->
                objectCount.addAndGet(1)
        );
        //If an object other than the ship itself is present inside the ship's drawable radius
        if (objectCount.get() > 1) {
            return "Not enough space available to spawn this ship!";
        }

        //Don't spawn the ship if it doesn't have an engine attached (like a turret)
        if (newHullConfig.getEngineConfig() == null) {
            return "Cannot spawn a ship which has no engine configuration!";
        }

        newShip = Optional.of(game.getShipBuilder().build(game, originalShip.getPosition(), originalShip.getVelocity(), originalShip.getAngle(),
                originalShip.getRotationSpeed(), originalShip.getPilot(), originalShip.getItemContainer(), newHullConfig,
                newHullConfig.getMaxLife(), originalShip.getHull().getGun(false), originalShip.getHull().getGun(true), null,
                newHullConfig.getEngineConfig().exampleEngine.copy(), new ShipRepairer(),
                originalShip.getMoney(), null, originalShip.getShield(), originalShip.getArmor()));

        game.getObjectManager().removeObjDelayed(hero.getShip());
        game.getObjectManager().addObjDelayed(newShip.get());
        hero.setSolShip(newShip.get(), game);
        return "Ship changed succesfully!";
    }

    public boolean isTranscendent() {
        return isTranscendent;
    }

    public boolean isNonTranscendent() {
        return !isTranscendent;
    }

    public Pilot getPilot() {
        return isTranscendent ? transcendentHeroShip.getPilot() : shipHero.getPilot();
    }

    public SolShip getShip() {
        if (isTranscendent) {
            throw new SolException("Something is trying to get a SolShip hero while the hero is in Transcendent state.");
        }
        return shipHero;
    }

    public SolShip getShipUnchecked() {
        return shipHero;
    }

    public StarPort.Transcendent getTranscendentHero() {
        if (!isTranscendent) {
            throw new SolException("Something is trying to get a Transcendent hero while the hero is in SolShip state.");
        }
        return transcendentHero;
    }

    public float getAngle() {
        return isTranscendent ? transcendentHeroShip.getAngle() : shipHero.getAngle();
    }

    public Shield getShield() {
        return isTranscendent ? transcendentHeroShip.getShield() : shipHero.getShield();
    }

    public Armor getArmor() {
        return isTranscendent ? transcendentHeroShip.getArmor() : shipHero.getArmor();
    }

    public ShipAbility getAbility() {
        assertNonTranscendent();
        return shipHero.getAbility();
    }

    public float getAbilityAwait() {
        assertNonTranscendent();
        return shipHero.getAbilityAwait();
    }

    public boolean canUseAbility() {
        assertNonTranscendent();
        return shipHero.canUseAbility();
    }

    public Vector2 getVelocity() {
        return isTranscendent ? transcendentHero.getVelocity() : shipHero.getVelocity();
    }

    public float getAcceleration() {
        assertNonTranscendent();
        return shipHero.getAcceleration();
    }

    public Vector2 getPosition() {
        return isTranscendent ? transcendentHero.getPosition() : shipHero.getPosition();
    }

    public void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void removeWaypoint(Waypoint waypoint) {
        if (waypoints.contains(waypoint)) {
            waypoints.remove(waypoint);
        }
    }

    public Hull getHull() {
        assertNonTranscendent();
        return shipHero.getHull();
    }

    public float getLife() {
        return isTranscendent ? transcendentHeroShip.getLife() : shipHero.getLife();
    }

    public float getRotationAcceleration() {
        assertNonTranscendent();
        return shipHero.getRotationAcceleration();
    }

    public float getRotationSpeed() {
        assertNonTranscendent();
        return shipHero.getRotationSpeed();
    }

    public float getMoney() {
        return isTranscendent ? transcendentHeroShip.getMoney() : shipHero.getMoney();
    }

    public void setMoney(float money) {
        assertNonTranscendent();
        shipHero.setMoney(money);
    }

    public ItemContainer getMercs() {
        assertNonTranscendent();
        return mercs;
    }

    public ItemContainer getItemContainer() {
        return isTranscendent ? transcendentHeroShip.getIc() : shipHero.getItemContainer();
    }

    public void die() {
        isDead = true;
    }

    public boolean maybeEquip(SolGame game, SolItem item, boolean equip) {
        assertNonTranscendent();
        return shipHero.maybeEquip(game, item, equip);
    }

    public boolean maybeEquip(SolGame game, SolItem item, boolean secondarySlot, boolean equip) {
        assertNonTranscendent();
        return shipHero.maybeEquip(game, item, secondarySlot, equip);
    }

    public boolean maybeUnequip(SolGame game, SolItem item, boolean equip) {
        assertNonTranscendent();
        return shipHero.maybeUnequip(game, item, equip);
    }

    public boolean maybeUnequip(SolGame game, SolItem item, boolean secondarySlot, boolean equip) {
        assertNonTranscendent();
        return shipHero.maybeUnequip(game, item, secondarySlot, equip);
    }

    private void assertNonTranscendent() {
        if (isTranscendent) {
            throw new SolException("Something is trying to get a property of hero that doesn't exist in transcendent state, while the hero is in transcendent state.");
        }
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isAlive() {
        return !isDead;
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public void setInvincible(boolean b) {
        isInvincible = b;
    }
}
