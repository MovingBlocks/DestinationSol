/*
 * Copyright 2020 MovingBlocks
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
package org.destinationsol.game.console.commands;

import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.Console;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.ConsoleInputHandler;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.omg.SendingContext.RunTime;

import java.util.List;
import java.util.Optional;

/**
 * Allows you to equip weapons from different modules onto your current ship
 */
public class EquipWeaponCommandHandler implements ConsoleInputHandler {

    private Hero hero;
    private SolGame game;

    public EquipWeaponCommandHandler(Hero hero, SolGame game) {
        this.hero = hero;
        this.game = game;
    }

    @Override
    public void handle(String input, Console console) {
        String[] args = input.split(" ");
        SolShip ship = hero.getShip();

        if (args.length != 3) {
            console.warn("Usage: equipWeapon moduleName:gunName slotNumber");
            console.warn("Available slots: " + (ship.getHull().getGunMount(false) != null ? "1" : "<none>") + (ship.getHull().getGunMount(true) != null ? ", 2" : ""));
            return;
        }
        if (!SolMath.isInt(args[2])) {
            console.warn("slotNumber must be a valid number!");
            return;
        }

        int slot = Integer.parseInt(args[2]);
        Optional<SolItem> gunItem = Optional.ofNullable(getGun(args[1]));
        if (!gunItem.isPresent()) {
            console.warn("Could not find " + args[1]);
            return;
        }

        boolean canEquip = ship.maybeEquip(game, gunItem.get(), slot == 2, false);
        if (!canEquip) {
            console.warn("Cannot equip this weapon in that slot!");
            return;
        }
        ItemContainer itemContainer = ship.getItemContainer();
        boolean alreadyExists = false;
        for (List<SolItem> group : itemContainer) {
            if (group.get(0).getCode().equals(gunItem.get().getCode())) {
                gunItem = Optional.of(group.get(0));
                alreadyExists = true;
                break;
            }
        }
        if (!alreadyExists) {
            ship.getItemContainer().add(gunItem.get());
        }

        Gun gun = (Gun) gunItem.get();
        gun.ammo = gun.config.clipConf.size;
        ship.maybeEquip(game, gun, slot == 2, true);
    }

    public SolItem getGun(String gunUrn) {
        SolItem solItem = game.getItemMan().getExample(gunUrn);
        if (solItem != null) {
            return solItem;
        }
        try {
            Assets.getJson(gunUrn);
        } catch (RuntimeException e) {
            return null;
        }
        Gun.Config.load(gunUrn, game.getItemMan(), game.getSoundManager(), game.getItemMan().getSolItemTypes());
        return getGun(gunUrn);
    }
}
