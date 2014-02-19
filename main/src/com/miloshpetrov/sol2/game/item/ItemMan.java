package com.miloshpetrov.sol2.game.item;

import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.gun.GunConfigs;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemMan {
  public static final String ITEM_CONFIGS_DIR = "res/configs/items/";
  public final GunConfigs gunConfigs;
  private final HashMap<String,SolItem> myM;
  public final Armor.Configs armorConfigs;
  public final EngineItem.Configs engineConfigs;
  private final ArrayList<SolItem> myL;

  public ItemMan(TexMan texMan) {
    myM = new HashMap<String, SolItem>();

    gunConfigs = new GunConfigs(texMan);
    armorConfigs = new Armor.Configs();
    Shield.Config.loadConfigs(this);
    engineConfigs = new EngineItem.Configs();

    myM.put("e", engineConfigs.std.example);
    myM.put("eBig", engineConfigs.big.example);

    myM.put("wbo", gunConfigs.weakBolter.example);
    myM.put("bo", gunConfigs.bolter.example);
    myM.put("sg", gunConfigs.slowGun.example);
    myM.put("mg", gunConfigs.miniGun.example);
    myM.put("rl", gunConfigs.rocketLauncher.example);

    myM.put("a", armorConfigs.std.example);
    myM.put("aBig", armorConfigs.big.example);
    myM.put("aMed", armorConfigs.med.example);

    myM.put("b", BulletClip.EXAMPLE);
    myM.put("r", RocketClip.EXAMPLE);

    myM.put("rep", RepairItem.EXAMPLE);
    myM.put("sloMo", SloMoCharge.EXAMPLE);

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
          SolItem example = myM.get(name);
          if (example == null) {
            SolCmp.fatalError("unknown item " + name + "@" + parts[0] + "@" + rec + "@" + items);
            return;
          }
          SolItem item = example.copy();
          c.add(item);
        }
      }
    }
  }

  public SolItem random() {
    return myL.get(SolMath.intRnd(myM.size())).copy();
  }

  public void registerItem(String itemCode, SolItem example) {
    SolItem existing = myM.get(itemCode);
    if (existing != null) {
      SolCmp.fatalError("2 item types registered for item code " + itemCode + ":\n" + existing + " and " + example);
    }
    myM.put(itemCode, example);
  }
}
