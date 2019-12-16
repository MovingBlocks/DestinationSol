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

import com.badlogic.gdx.math.MathUtils;
import org.destinationsol.game.item.Armor;
import org.destinationsol.game.item.Clip;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemConfig;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.maze.MazeConfig;
import org.destinationsol.game.planet.PlanetConfig;
import org.destinationsol.game.planet.SysConfig;
import org.destinationsol.game.projectile.ProjectileConfig;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.GunSlot;
import org.destinationsol.game.ship.hulls.Hull;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.Iterator;
import java.util.List;

public class HardnessCalc {

    public static final float SHIELD_MUL = 1.2f;

    public static float getGunMeanDps(Gun.Config gc) {
        Clip.Config cc = gc.clipConf;
        ProjectileConfig pc = cc.projConfig;

        float projDmg = pc.dmg;
        if (pc.emTime > 0) {
            projDmg = 150;
        } else if (pc.density > 0) {
            projDmg += 10;
        }

        float projHitChance;
        if (pc.guideRotationSpeed > 0) {
            projHitChance = .9f;
        } else if (pc.zeroAbsSpeed) {
            projHitChance = 0.1f;
        } else {
            projHitChance = (pc.speed + pc.acc) / 6;
            if (pc.physSize > 0) {
                projHitChance += pc.physSize;
            }
            projHitChance = MathUtils.clamp(projHitChance, .1f, (float) 1);
            if (gc.fixed) {
                projHitChance *= .3f;
            }
        }

        float shotDmg = projDmg * projHitChance;

        return getShotDps(gc, shotDmg);
    }

    public static float getShotDps(Gun.Config gc, float shotDmg) {
        Clip.Config cc = gc.clipConf;
        int projectilesPerShot = cc.projectilesPerShot;
        if (gc.timeBetweenShots == 0) {
            projectilesPerShot = cc.size;
        }
        if (projectilesPerShot > 1) {
            shotDmg *= .6f * projectilesPerShot;
        }

        float timeBetweenShots = gc.timeBetweenShots == 0 ? gc.reloadTime : gc.timeBetweenShots;
        return shotDmg / timeBetweenShots;
    }

    private static float getItemCfgDps(ItemConfig ic, boolean fixed) {
        float dps = 0;
        for (SolItem e : ic.examples) {
            if (!(e instanceof Gun)) {
                throw new AssertionError("all item options must be of the same type");
            }
            Gun g = (Gun) e;
            if (g.config.fixed != fixed) {
                String items = "";
                for (SolItem ex : ic.examples) {
                    items += ex.getDisplayName() + " ";
                }
                throw new AssertionError("all gun options must have equal fixed param: " + items);
            }
            dps += g.config.meanDps;
        }

        return dps / ic.examples.size() * ic.chance;
    }

    public static float getShipConfDps(ShipConfig sc, ItemManager itemManager) {
        final List<ItemConfig> parsedItems = itemManager.parseItems(sc.items);
        final List<GunSlot> unusedGunSlots = sc.hull.getGunSlotList();

        float dps = 0;
        Iterator<ItemConfig> itemConfigIterator = parsedItems.iterator();

        while (itemConfigIterator.hasNext() && !unusedGunSlots.isEmpty()) {
            ItemConfig itemConfig = itemConfigIterator.next();
            final SolItem item = itemConfig.examples.get(0);

            if (item instanceof Gun) {
                final Gun gun = (Gun) item;
                final Iterator<GunSlot> gunSlotIterator = unusedGunSlots.listIterator();

                boolean matchingSlotFound = false;
                while (gunSlotIterator.hasNext() && !matchingSlotFound) {
                    final GunSlot gunSlot = gunSlotIterator.next();

                    if (gun.config.fixed != gunSlot.allowsRotation()) {
                        dps += getItemCfgDps(itemConfig, gun.config.fixed);
                        gunSlotIterator.remove();
                        matchingSlotFound = true;
                    }
                }
            }
        }

        return dps;
    }

    public static float getShipCfgDmgCap(ShipConfig shipConfig, ItemManager itemManager) {
        List<ItemConfig> parsed = itemManager.parseItems(shipConfig.items);
        float meanShieldLife = 0;
        float meanArmorPercentage = 0;
        for (ItemConfig itemConfig : parsed) {
            SolItem item = itemConfig.examples.get(0);
            if (meanShieldLife == 0 && item instanceof Shield) {
                for (SolItem example : itemConfig.examples) {
                    meanShieldLife += ((Shield) example).getLife();
                }
                meanShieldLife /= itemConfig.examples.size();
                meanShieldLife *= itemConfig.chance;
            }
            if (meanArmorPercentage == 0 && item instanceof Armor) {
                for (SolItem example : itemConfig.examples) {
                    meanArmorPercentage += ((Armor) example).getPerc();
                }
                meanArmorPercentage /= itemConfig.examples.size();
                meanArmorPercentage *= itemConfig.chance;
            }
        }
        return shipConfig.hull.getMaxLife() / (1 - meanArmorPercentage) + meanShieldLife * SHIELD_MUL;
    }

    private static float getShipConfListDps(List<ShipConfig> ships) {
        float maxDps = 0;
        for (ShipConfig e : ships) {
            if (maxDps < e.dps) {
                maxDps = e.dps;
            }
        }
        return maxDps;
    }

    public static float getGroundDps(PlanetConfig pc, float grav) {
        float groundDps = getShipConfListDps(pc.groundEnemies);
        float bomberDps = getShipConfListDps(pc.lowOrbitEnemies);
        float res = bomberDps < groundDps ? groundDps : bomberDps;
        float gravFactor = 1 + grav * .5f;
        return res * gravFactor;
    }

    public static float getAtmDps(PlanetConfig pc) {
        return getShipConfListDps(pc.highOrbitEnemies);
    }

    public static float getMazeDps(MazeConfig c) {
        float outer = getShipConfListDps(c.outerEnemies);
        float inner = getShipConfListDps(c.innerEnemies);
        float res = inner < outer ? outer : inner;
        return res * 1.25f;
    }

    public static float getBeltDps(SysConfig c) {
        return 1.2f * getShipConfListDps(c.tempEnemies);
    }

    public static float getSysDps(SysConfig c, boolean inner) {
        return getShipConfListDps(inner ? c.innerTempEnemies : c.tempEnemies);
    }

    private static float getGunDps(Gun g) {
        if (g == null) {
            return 0;
        }
        return g.config.meanDps;
    }

    public static float getShipDps(SolShip s) {
        Hull h = s.getHull();
        return getGunDps(h.getGun(false)) + getGunDps(h.getGun(true));
    }

    public static float getFarShipDps(FarShip s) {
        return getGunDps(s.getGun(false)) + getGunDps(s.getGun(true));
    }

    public static float getShipDmgCap(SolShip s) {
        return getDmgCap(s.getHull().config, s.getArmor(), s.getShield());
    }

    public static float getFarShipDmgCap(FarShip s) {
        return getDmgCap(s.getHullConfig(), s.getArmor(), s.getShield());
    }

    private static float getDmgCap(HullConfig hull, Armor armor, Shield shield) {
        float r = hull.getMaxLife();
        if (armor != null) {
            r *= 1 / (1 - armor.getPerc());
        }
        if (shield != null) {
            r += shield.getMaxLife() * SHIELD_MUL;
        }
        return r;
    }

    public static boolean isDangerous(float destDmgCap, float dps) {
        float killTime = destDmgCap / dps;
        return killTime < 5;
    }

    public static boolean isDangerous(float destDmgCap, Object srcObj) {
        float dps = getShipObjDps(srcObj);
        return isDangerous(destDmgCap, dps);
    }

    public static float getShipObjDps(Object srcObj) {
        return srcObj instanceof SolShip ? getShipDps((SolShip) srcObj) : getFarShipDps((FarShip) srcObj);
    }
}
