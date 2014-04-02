package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.gun.GunConfig;
import com.miloshpetrov.sol2.game.particle.EffectTypes;
import com.miloshpetrov.sol2.game.projectile.ProjectileConfigs;
import com.miloshpetrov.sol2.game.ship.AbilityCharge;
import com.miloshpetrov.sol2.game.sound.SoundMan;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemMan {
  public static final String ITEM_CONFIGS_DIR = Const.CONFIGS_DIR + "items/";
  private final HashMap<String,SolItem> myM;
  private final ArrayList<SolItem> myL;
  public final ProjectileConfigs projConfigs;
  public final TextureAtlas.AtlasRegion moneyIcon;
  public final TextureAtlas.AtlasRegion bigMoneyIcon;
  public final TextureAtlas.AtlasRegion repairIcon;
  private final EngineItem.Configs myEngineConfigs;

  public ItemMan(TexMan texMan, SoundMan soundMan, EffectTypes effectTypes) {
    moneyIcon = texMan.getTex(TexMan.ICONS_DIR + "money", null);
    bigMoneyIcon = texMan.getTex(TexMan.ICONS_DIR + "bigMoney", null);
    repairIcon = texMan.getTex(TexMan.ICONS_DIR + "repairItem", null);
    myM = new HashMap<String, SolItem>();
    projConfigs = new ProjectileConfigs(texMan, soundMan, effectTypes);
    myEngineConfigs = EngineItem.Configs.load(soundMan, texMan, effectTypes);

    Shield.Config.loadConfigs(this, soundMan, texMan);
    Armor.Config.loadConfigs(this, soundMan, texMan);
    AbilityCharge.Config.load(this, texMan);

    ClipConfig.load(this, texMan);
    GunConfig.load(texMan, this, soundMan, texMan);

    myM.put("rep", RepairItem.EXAMPLE);

    myL = new ArrayList<SolItem>(myM.values());
  }

  public void fillContainer(ItemContainer c, String items) {
    for (String rec : items.split(" ")) {
      String[] parts = rec.split(":");
      if (parts.length == 0) continue;
      String[] names = parts[0].split("\\|");
      String name = names[SolMath.intRnd(names.length)].trim();

      float chance = 1;
      if (parts.length > 1) {
        chance = Float.parseFloat(parts[1]);
        if (chance <= 0 || 1 < chance) throw new AssertionError(chance);
      }

      int amt = 1;
      if (parts.length > 2) {
        amt = Integer.parseInt(parts[2]);
      }
      for (int i = 0; i < amt; i++) {
        if (SolMath.test(chance)) {
          SolItem example = getExample(name);
          if (example == null) {
            throw new AssertionError("unknown item " + name + "@" + parts[0] + "@" + rec + "@" + items);
          }
          SolItem item = example.copy();
          c.add(item);
        }
      }
    }
  }

  public SolItem getExample(String name) {
    return myM.get(name);
  }

  public SolItem random() {
    return myL.get(SolMath.intRnd(myM.size())).copy();
  }

  public void registerItem(String itemCode, SolItem example) {
    SolItem existing = getExample(itemCode);
    if (existing != null) {
      throw new AssertionError("2 item types registered for item code " + itemCode + ":\n" + existing + " and " + example);
    }
    myM.put(itemCode, example);
  }

  public EngineItem.Configs getEngineConfigs() {
    return myEngineConfigs;
  }
}
