/*
 * Copyright 2015 MovingBlocks
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
import org.destinationsol.assets.AssetHelper;
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
    private final EngineItem.Configs myEngineConfigs;
    private final SolItemTypes myTypes;
    private final RepairItem myRepairExample;
    private final AssetHelper assetHelper;
    private final OggSoundManager soundManager;

    public ItemManager(TextureManager textureManager, OggSoundManager soundManager, EffectTypes effectTypes, GameColors gameColors, AssetHelper assetHelper) {
        this.assetHelper = assetHelper;
        this.soundManager = soundManager;

        moneyIcon = textureManager.getTexture(TextureManager.ICONS_DIR + "money");
        medMoneyIcon = textureManager.getTexture(TextureManager.ICONS_DIR + "medMoney");
        bigMoneyIcon = textureManager.getTexture(TextureManager.ICONS_DIR + "bigMoney");
        repairIcon = textureManager.getTexture(TextureManager.ICONS_DIR + "repairItem");

        myTypes = new SolItemTypes(soundManager, gameColors, assetHelper);
        projConfigs = new ProjectileConfigs(textureManager, soundManager, effectTypes, gameColors, assetHelper);
        myEngineConfigs = EngineItem.Configs.load(soundManager, textureManager, effectTypes, gameColors, assetHelper);

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
            String[] parts = rec.split(":");

            if (parts.length == 0) {
                continue;
            }

            String[] names = parts[0].split("\\|");

            ArrayList<SolItem> examples = new ArrayList<>();
            for (String name : names) {
                int wasEquipped = 0;

                if (name.endsWith("-1")) {
                    wasEquipped = 1;
                    name = name.substring(0, name.length() - 2); // Remove equipped number
                } else if (name.endsWith("-2")) {
                    wasEquipped = 2;
                    name = name.substring(0, name.length() - 2); // Remove equipped number
                }
                name = name.trim();

                SolItem example = getExample(name);

                // TODO: Remove the explicit Core from here
                name = "Core:" + name;

                // TODO: Remove this
                if (example == null) {
                    example = getExample(name);
                }

                if (example == null) {
                    // TODO: Temporary hacky way!
                    if (name.endsWith("Charge")) {
                        AbilityCharge.Config.load(new ResourceUrn(name), this, myTypes, assetHelper);
                    } else if (name.endsWith("Armor")) {
                        Armor.Config.load(new ResourceUrn(name), this, soundManager, myTypes, assetHelper);
                    } else if (name.endsWith("Clip")) {
                        Clip.Config.load(new ResourceUrn(name), this, myTypes, assetHelper);
                    } else if (name.endsWith("Shield") || name.endsWith("shield")) {
                        Shield.Config.load(new ResourceUrn(name), this, soundManager, myTypes, assetHelper);
                    } else {
                        Gun.Config.load(new ResourceUrn(name), this, soundManager, myTypes, assetHelper);
                    }

                    example = getExample(name);
                }

                if (example == null) {
                    throw new AssertionError("unknown item " + name + "@" + parts[0] + "@" + rec + "@" + items);
                }

                SolItem itemCopy = example.copy();
                itemCopy.setEquipped(wasEquipped);

                examples.add(itemCopy);
            }

            if (examples.isEmpty()) {
                throw new AssertionError("no item specified @ " + parts[0] + "@" + rec + "@" + items);
            }

            float chance = 1;

            if (parts.length > 1) {
                chance = Float.parseFloat(parts[1]);
                if (chance <= 0 || 1 < chance) {
                    throw new AssertionError(chance);
                }
            }

            int amt = 1;
            if (parts.length > 2) {
                amt = Integer.parseInt(parts[2]);
            }

            ItemConfig ic = new ItemConfig(examples, amt, chance);
            result.add(ic);
        }

        return result;
    }

    public SolItem getExample(String name) {
        return myM.get(name);
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

    public EngineItem.Configs getEngineConfigs() {
        return myEngineConfigs;
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
