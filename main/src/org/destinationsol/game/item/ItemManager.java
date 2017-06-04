/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.TextureManager;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.projectile.ProjectileConfigs;
import org.destinationsol.game.sound.OggSoundManager;
import org.terasology.assets.ResourceUrn;

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
    private final HashMap<ResourceUrn, Engine.Config> engineConfigs = new HashMap<>();
    private final SolItemTypes myTypes;
    private final RepairItem myRepairExample;
    private final OggSoundManager soundManager;
    private final TextureManager textureManager;
    private final EffectTypes effectTypes;
    private final GameColors gameColors;

    public ItemManager(TextureManager textureManager, OggSoundManager soundManager, EffectTypes effectTypes, GameColors gameColors) {
        this.soundManager = soundManager;
        this.textureManager = textureManager;
        this.effectTypes = effectTypes;
        this.gameColors = gameColors;

        moneyIcon = textureManager.getTexture(TextureManager.ICONS_DIR + "money");
        medMoneyIcon = textureManager.getTexture(TextureManager.ICONS_DIR + "medMoney");
        bigMoneyIcon = textureManager.getTexture(TextureManager.ICONS_DIR + "bigMoney");
        repairIcon = textureManager.getTexture(TextureManager.ICONS_DIR + "repairItem");

        myTypes = new SolItemTypes(soundManager, gameColors);
        projConfigs = new ProjectileConfigs(textureManager, soundManager, effectTypes, gameColors);

        myRepairExample = new RepairItem(myTypes.repair);
        myM.put(myRepairExample.getCode(), myRepairExample);

        myL.addAll(myM.values());
    }

    public void fillContainer(ItemContainer c, String items) {
        List<ItemConfig> list = parseItems(items);
        for (ItemConfig ic : list) {
            for (int i = 0; i < ic.amt; i++) {
                if (SolMath.test(ic.chance)) {
                    SolItem item = SolMath.elemRnd(ic.examples).copy();
                    c.add(item);
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
                    if (itemName.endsWith("Charge")) {
                        AbilityCharge.Config.load(new ResourceUrn(itemName), this, myTypes);
                    } else if (itemName.endsWith("Armor")) {
                        Armor.Config.load(new ResourceUrn(itemName), this, soundManager, myTypes);
                    } else if (itemName.endsWith("Clip")) {
                        Clip.Config.load(new ResourceUrn(itemName), this, myTypes);
                    } else if (itemName.endsWith("Shield") || itemName.endsWith("shield")) {
                        Shield.Config.load(new ResourceUrn(itemName), this, soundManager, myTypes);
                    } else {
                        Gun.Config.load(new ResourceUrn(itemName), this, soundManager, myTypes);
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

            ItemConfig ic = new ItemConfig(examples, amt, chance);
            result.add(ic);
        }

        return result;
    }

    public SolItem getExample(String name) {
        return myM.get(name);
    }

    public Engine.Config getEngineConfig(ResourceUrn engineName) {
        return engineConfigs.computeIfAbsent(engineName, engineConfig -> Engine.Config.load(engineConfig, soundManager, effectTypes, textureManager, gameColors));
    }

    public SolItem random() {
        return myL.get(SolMath.intRnd(myM.size())).copy();
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
        ArrayList<MoneyItem> res = new ArrayList<MoneyItem>();
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
