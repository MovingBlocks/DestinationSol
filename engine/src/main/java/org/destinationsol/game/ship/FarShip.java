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

package org.destinationsol.game.ship;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.Armor;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.item.TradeContainer;
import org.destinationsol.game.ship.hulls.HullConfig;

public class FarShip implements FarObject {
    private final Vector2 position;
    private final Vector2 velocity;
    private final Shield shield;
    private final Armor armor;
    private final float rotationSpeed;
    private final Pilot pilot;
    private final ItemContainer container;
    private final HullConfig hullConfig;
    private final Gun gun1;
    private final Gun gun2;
    private final RemoveController removeController;
    private final Engine engine;
    private final TradeContainer tradeContainer;
    private float angle;
    private float life;
    private ShipRepairer repairer;
    private float money;

    public FarShip(Vector2 position, Vector2 velocity, float angle, float rotationSpeed, Pilot pilot, ItemContainer container,
                   HullConfig hullConfig, float life,
                   Gun gun1, Gun gun2, RemoveController removeController, Engine engine,
                   ShipRepairer repairer, float money, TradeContainer tradeContainer, Shield shield, Armor armor) {
        this.position = position;
        this.velocity = velocity;
        this.angle = angle;
        this.rotationSpeed = rotationSpeed;
        this.pilot = pilot;
        this.container = container;
        this.hullConfig = hullConfig;
        this.life = life;
        this.gun1 = gun1;
        this.gun2 = gun2;
        this.removeController = removeController;
        this.engine = engine;
        this.repairer = repairer;
        this.money = money;
        this.tradeContainer = tradeContainer;
        this.shield = shield;
        this.armor = armor;

        if (this.pilot.isPlayer()) {
            if (this.shield != null) {
                this.shield.setEquipped(1);
            }
            if (this.armor != null) {
                this.armor.setEquipped(1);
            }
            if (this.gun1 != null) {
                this.gun1.setEquipped(1);
            }
            if (this.gun2 != null) {
                this.gun2.setEquipped(2);
            }
        }
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return removeController != null && removeController.shouldRemove(position);
    }

    @Override
    public SolShip toObject(SolGame game) {
        return game.getShipBuilder().build(game, position, velocity, angle, rotationSpeed, pilot, container, hullConfig, life, gun1,
                gun2, removeController, engine, repairer, money, tradeContainer, shield, armor);
    }

    @Override
    public void update(SolGame game) {
        pilot.updateFar(game, this);
        if (tradeContainer != null) {
            tradeContainer.update(game);
        }
        if (repairer != null) {
            life += repairer.tryRepair(game, container, life, hullConfig);
        }
    }

    @Override
    public float getRadius() {
        return hullConfig.getApproxRadius();
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    public void setPos(Vector2 position) {
        this.position.set(position);
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    public Pilot getPilot() {
        return pilot;
    }

    public HullConfig getHullConfig() {
        return hullConfig;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }

    public Engine getEngine() {
        return engine;
    }

    public Gun getGun(boolean secondary) {
        return secondary ? gun2 : gun1;
    }

    public Shield getShield() {
        return shield;
    }

    public Armor getArmor() {
        return armor;
    }

    public float getLife() {
        return life;
    }

    public boolean mountCanFix(boolean sec) {
        final int slotNr = (sec) ? 1 : 0;

        return !hullConfig.getGunSlot(slotNr).allowsRotation();
    }

    public float getMoney() {
        return money;
    }

    public ItemContainer getIc() {
        return container;
    }
}
