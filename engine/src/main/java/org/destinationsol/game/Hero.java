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
import org.destinationsol.common.SolDescriptiveException;
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

public class Hero {
    private SolShip hero;
    private StarPort.Transcendent transcendentHero;
    private FarShip transcendentHeroShip;
    private boolean isTranscendent;
    private boolean isDead;

    public Hero(SolShip hero) {
        this.hero = hero;
        if (hero == null) {
            throw new SolDescriptiveException("Something tries to create hero, when there is no ship linked to hiim. Please report this..");
        }
        isTranscendent = false;
    }

    public void toTranscendent(StarPort.Transcendent transcendentHero) {
        this.transcendentHero = transcendentHero;
        transcendentHeroShip = transcendentHero.getShip();
        isTranscendent = true;
    }

    public void toSolShip(SolShip hero) {
        isDead = false;
        this.hero = hero;
        isTranscendent = false;
    }

    public boolean isTranscendent() {
        return isTranscendent;
    }

    public Pilot getPilot() {
        return isTranscendent ? transcendentHeroShip.getPilot() : hero.getPilot();
    }

    public SolShip getHero() {
        if (isTranscendent) {
            throw new SolDescriptiveException("Something is trying to get a SolShip hero while the hero is Transcendent state.");
        }
        return hero;
    }

    public SolShip getHeroUnchecked() {
        return hero;
    }

    public StarPort.Transcendent getTranscendentHero() {
        if (!isTranscendent) {
            throw new SolDescriptiveException("Something is trying to get a Transcendent hero while the hero is in SolShip state.");
        }
        return transcendentHero;
    }

    public float getAngle() {
        return isTranscendent ? transcendentHeroShip.getAngle() : hero.getAngle();
    }

    public Shield getShield() {
        return isTranscendent ? transcendentHeroShip.getShield() : hero.getShield();
    }

    public Armor getArmor() {
        return isTranscendent ? transcendentHeroShip.getArmor() : hero.getArmor();
    }

    public ShipAbility getAbility() {
        onlySolShipHero();
        return hero.getAbility();
    }

    public float getAbilityAwait() {
        onlySolShipHero();
        return hero.getAbilityAwait();
    }

    public boolean canUseAbility() {
        onlySolShipHero();
        return hero.canUseAbility();
    }

    public Vector2 getSpd() {
        return isTranscendent ? transcendentHero.getSpd() : hero.getSpd();
    }

    public float getAcc() {
        onlySolShipHero();
        return hero.getAcc(); // Transcendent hero doesn't accelerate
    }

    public Vector2 getPosition() {
        return isTranscendent ? transcendentHero.getPosition() : hero.getPosition();
    }

    public Hull getHull() {
        onlySolShipHero(); // Transcendent hero has no hull
        return hero.getHull();
    }

    public float getLife() {
        return isTranscendent ? transcendentHeroShip.getLife() : hero.getLife();
    }

    public float getRotAcc() {
        onlySolShipHero();
        return hero.getRotAcc();
    }

    public float getRotSpd() {
        onlySolShipHero();
        return hero.getRotSpd();
    }

    public float getMoney() {
        return isTranscendent ? transcendentHeroShip.getMoney() : hero.getMoney();
    }

    public void setMoney(float money) {
        onlySolShipHero(); // Transcendent hero should by design be not modified
        hero.setMoney(money);
    }

    public TradeContainer getTradeContainer() {
        onlySolShipHero(); // Trading should by no means be possible when in transcendent state
        return hero.getTradeContainer();
    }

    public ItemContainer getItemContainer() {
        return isTranscendent ? transcendentHeroShip.getIc() : hero.getItemContainer();
    }

    public void die() {
        isDead = true;
    }

    public boolean maybeEquip(SolGame game, SolItem item, boolean equip) {
        onlySolShipHero();
        return hero.maybeEquip(game, item, equip);
    }

    public boolean maybeEquip(SolGame game, SolItem item, boolean secondarySlot, boolean equip) {
        onlySolShipHero();
        return hero.maybeEquip(game, item, secondarySlot, equip);
    }

    public boolean maybeUnequip(SolGame game, SolItem item, boolean equip) {
        onlySolShipHero();
        return hero.maybeUnequip(game, item, equip);
    }

    public boolean maybeUnequip(SolGame game, SolItem item, boolean secondarySlot, boolean equip) {
        onlySolShipHero();
        return hero.maybeUnequip(game, item, secondarySlot, equip);
    }

    private void onlySolShipHero() {
        if (isTranscendent) {
            throw new SolDescriptiveException("Something is trying to get a property of hero that doesn't exist in transcendent state, while the hero is in transcendent state.");
        }
    }

    public boolean isDead() {
        return isDead;
    }
}
