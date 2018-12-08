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
package org.destinationsol.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.SerializationException;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.projectile.ProjectileConfigs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemManager {
    public final ProjectileConfigs projConfigs;
    public final TextureAtlas.AtlasRegion moneyIcon;
    public final TextureAtlas.AtlasRegion medMoneyIcon;
    public final TextureAtlas.AtlasRegion bigMoneyIcon;
    public final TextureAtlas.AtlasRegion repairIcon;
    private final HashMap<String, SolItem> myM = new HashMap<>();
    private final ArrayList<SolItem> myL= new ArrayList<>();
    private final HashMap<String, Engine.Config> engineConfigs = new HashMap<>();
    private final SolItemTypes myTypes;
    private final RepairItem myRepairExample;
    private final OggSoundManager soundManager;
    private final EffectTypes effectTypes;
    private final GameColors gameColors;

    public ItemManager(OggSoundManager soundManager, EffectTypes effectTypes, GameColors gameColors) {
        this.soundManager = soundManager;
        this.effectTypes = effectTypes;
        this.gameColors = gameColors;

        moneyIcon = Assets.getAtlasRegion("engine:iconMoney");
        medMoneyIcon = Assets.getAtlasRegion("engine:iconMedMoney");
        bigMoneyIcon = Assets.getAtlasRegion("engine:iconBigMoney");
        repairIcon = Assets.getAtlasRegion("engine:iconRepairItem");

        myTypes = new SolItemTypes(soundManager, gameColors);
        projConfigs = new ProjectileConfigs(soundManager, effectTypes, gameColors);

        myRepairExample = new RepairItem(myTypes.repair);
        myM.put(myRepairExample.getCode(), myRepairExample);

        myL.addAll(myM.values());
    }

    public void fillContainer(ItemContainer itemContainer, String items) {
        List<ItemConfig> list = parseItems(items);
        for (ItemConfig itemConfig : list) {
            for (int i = 0; i < itemConfig.amount; i++) {
                if (SolRandom.test(itemConfig.chance)) {
                    SolItem item = SolRandom.randomElement(itemConfig.examples).copy();
                    itemContainer.add(item);
                }
            }
        }
    }

    public List<ItemConfig> parseItems(String items) {
        ArrayList<ItemConfig> result = new ArrayList<>();

        if (items.isEmpty()) {
            return result;
        }

        for (String rec : items.split(" ")) {
            String[] parts = rec.split("[|]");

            if (parts.length == 0) {
                continue;
            }

            int itemsIndex = 0;

            float chance = 1;
            if (parts.length == 2) {
                chance = Float.parseFloat(parts[0]);
                if (chance <= 0 || chance > 1) {
                    throw new AssertionError("Item chance has to lie between 0 and 1!");
                }
                itemsIndex = 1;
            } else if (parts.length != 1) {
                throw new AssertionError("Invalid item format!");
            }

            parts = parts[itemsIndex].split("[*]");

            itemsIndex = 0;

            int amt = 1;
            if (parts.length > 1) {
                amt = Integer.parseInt(parts[0]);
                if (amt <= 0) {
                    throw new AssertionError("Item amount has to be positive!");
                }
                itemsIndex = 1;
            } else if (parts.length != 1) {
                throw new AssertionError("Invalid item format!");
            }

            String[] itemNames = parts[itemsIndex].split("[+]");

            ArrayList<SolItem> examples = new ArrayList<>();
            for (String itemName : itemNames) {
                int wasEquipped = 0;

                if (itemName.endsWith("-1")) {
                    wasEquipped = 1;
                    itemName = itemName.substring(0, itemName.length() - 2); // Remove equipped number
                } else if (itemName.endsWith("-2")) {
                    wasEquipped = 2;
                    itemName = itemName.substring(0, itemName.length() - 2); // Remove equipped number
                }

                SolItem example = getExample(itemName);

                if (example == null) {
                    // TODO: Temporary hacky way!
                    try {
                        if (itemName.endsWith("Charge")) {
                            AbilityCharge.Config.load(itemName, this, myTypes);
                        } else if (itemName.endsWith("Armor")) {
                            Armor.Config.load(itemName, this, soundManager, myTypes);
                        } else if (itemName.endsWith("Clip")) {
                            Clip.Config.load(itemName, this, myTypes);
                        } else if (itemName.endsWith("Shield") || itemName.endsWith("shield")) {
                            Shield.Config.load(itemName, this, soundManager, myTypes);
                        } else {
                            Gun.Config.load(itemName, this, soundManager, myTypes);
                        }
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("The JSON of " + itemName + " is missing, or has malformed, a required parameter" + e.getMessage().split(":")[1]);
                    } catch (SerializationException e) {
                        throw new SerializationException("The JSON of " + itemName + " has invalid syntax at " + e.getMessage().split(" near")[0].split("on ")[1]);
                    }

                    example = getExample(itemName);
                }

                if (example == null) {
                    throw new AssertionError("Unknown item " + itemName + " @ " + parts[0] + " @ " + rec + " @ " + items);
                }

                SolItem itemCopy = example.copy();
                itemCopy.setEquipped(wasEquipped);

                examples.add(itemCopy);
            }

            if (examples.isEmpty()) {
                throw new AssertionError("No item specified @ " + parts[0] + " @ " + rec + " @ " + items);
            }

            ItemConfig itemConfig = new ItemConfig(examples, amt, chance);
            result.add(itemConfig);
        }

        return result;
    }

    public SolItem getExample(String name) {
        return myM.get(name);
    }

    public Engine.Config getEngineConfig(String engineName) {
        return engineConfigs.computeIfAbsent(engineName, engineConfig -> Engine.Config.load(engineConfig));
    }

    public SolItem random() {
        return myL.get(SolRandom.randomInt(myM.size())).copy();
    }

    public void registerItem(SolItem example) {
        String code = example.getCode();
        SolItem existing = getExample(code);
        if (existing != null) {
            throw new AssertionError("2 item types registered for item code " + code + ":\n" + existing + " and " + example);
        }
        myM.put(code, example);
        myL.add(example);
    }

    public MoneyItem moneyItem(float amt) {
        SolItemType t;
        if (amt == MoneyItem.BIG_AMT) {
            t = myTypes.bigMoney;
        } else if (amt == MoneyItem.MED_AMT) {
            t = myTypes.medMoney;
        } else {
            t = myTypes.money;
        }
        return new MoneyItem(amt, t);
    }

    public RepairItem getRepairExample() {
        return myRepairExample;
    }

    public void addAllGuns(ItemContainer ic) {
        for (SolItem i : myM.values()) {
            if (i instanceof Clip && !((Clip) i).getConfig().infinite) {
                for (int j = 0; j < 8; j++) {
                    ic.add(i.copy());
                }
            }
        }
        for (SolItem i : myM.values()) {
            if (i instanceof Gun) {
                if (ic.canAdd(i)) {
                    ic.add(i.copy());
                }
            }
        }
    }

    public List<MoneyItem> moneyToItems(float amt) {
        ArrayList<MoneyItem> res = new ArrayList<>();
        while (amt > MoneyItem.AMT) {
            MoneyItem example;
            if (amt > MoneyItem.BIG_AMT) {
                example = moneyItem(MoneyItem.BIG_AMT);
                amt -= MoneyItem.BIG_AMT;
            } else if (amt > MoneyItem.MED_AMT) {
                example = moneyItem(MoneyItem.MED_AMT);
                amt -= MoneyItem.MED_AMT;
            } else {
                example = moneyItem(MoneyItem.AMT);
                amt -= MoneyItem.AMT;
            }
            res.add(example.copy());
        }
        return res;
    }
}
