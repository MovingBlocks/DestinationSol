/*
 * Copyright 2019 MovingBlocks
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

import org.destinationsol.game.Console;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.ConsoleInputHandler;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;

import java.util.List;

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
        if (args.length != 3) {
            console.warn("Usage: equipWeapon moduleName:gunName slotNumber");
            return;
        }
        if (!isInt(args[2])) {
            console.warn("slotNumber must be a valid number!");
            return;
        }

        int slot = Integer.parseInt(args[2]);
        SolItem gunItem = getGun(args[1]);
        if (slot < 1 || slot > 2) {
            console.warn("Available slots: 1, 2");
            return;
        }
        if (gunItem == null) {
            console.warn("Could not find " + args[1]);
            return;
        }

        SolShip ship = hero.getShip();
        boolean canEquip = ship.maybeEquip(game, gunItem, slot == 2 ? true : false, false);
        if (!canEquip) {
            console.warn("Cannot equip this weapon in that slot!");
            return;
        }
        ItemContainer itemContainer = ship.getItemContainer();
        boolean alreadyExists = false;
        for (List<SolItem> group : itemContainer) {
            if (group.get(0).getCode().equals(gunItem.getCode())) {
                gunItem = group.get(0);
                alreadyExists = true;
                break;
            }
        }
        if (!alreadyExists) {
            ship.getItemContainer().add(gunItem);
        }

        Gun gun = (Gun) gunItem;
        gun.ammo = gun.config.clipConf.size;
        ship.maybeEquip(game, gunItem, slot == 2 ? true : false, true);
    }

    public SolItem getGun(String gunID) {
        return game.getItemMan().getExample(gunID);
    }

    public boolean isInt(String input) {
        try {
            Integer.parseInt(input);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
