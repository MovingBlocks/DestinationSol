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
import org.destinationsol.common.SolException;
import org.destinationsol.game.console.commands.PositionCommandHandler;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.Armor;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.item.TradeContainer;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.ShipAbility;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.Hull;

/**
 * A wrapper class for the Hero, that handles the normal and transcendent ships transparently.
 */
public class Hero {
    private SolShip shipHero;
    private StarPort.Transcendent transcendentHero;
    private FarShip transcendentHeroShip;
    private boolean isTranscendent;
    private boolean isDead;

    public Hero(SolShip shipHero) {
        this.shipHero = shipHero;
        if (shipHero == null) {
            throw new SolException("Something is trying to create the hero when there is no ship linked to him.");
        }
        isTranscendent = false;
    }

    public void initialise() {
        if (!Console.getInstance().getDefaultInputHandler().commandExists("position")) {
            Console.getInstance().getDefaultInputHandler().registerCommand("position", new PositionCommandHandler(this));
        } else {
            ((PositionCommandHandler) Console.getInstance().getDefaultInputHandler().getRegisteredCommand("position")).hero = this;
        }
    }

    public void setTranscendent(StarPort.Transcendent transcendentHero) {
        this.transcendentHero = transcendentHero;
        transcendentHeroShip = transcendentHero.getShip();
        isTranscendent = true;
    }

    public void setSolShip(SolShip hero) {
        isDead = false;
        this.shipHero = hero;
        isTranscendent = false;
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

    public Vector2 getSpeed() {
        return isTranscendent ? transcendentHero.getSpeed() : shipHero.getSpeed();
    }

    public float getAcceleration() {
        assertNonTranscendent();
        return shipHero.getAcceleration();
    }

    public Vector2 getPosition() {
        return isTranscendent ? transcendentHero.getPosition() : shipHero.getPosition();
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

    public TradeContainer getTradeContainer() {
        assertNonTranscendent();
        return shipHero.getTradeContainer();
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
}
