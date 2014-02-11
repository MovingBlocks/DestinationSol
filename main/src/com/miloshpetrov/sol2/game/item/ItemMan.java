package com.miloshpetrov.sol2.game.item;

import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.gun.GunConfigs;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemMan {
  public final GunConfigs gunConfigs;
  private final HashMap<String,SolItem> myM;
  public final Armor.Configs armorConfigs;
  public final Shield.Configs shieldConfigs;
  public final EngineItem.Configs engineConfigs;
  private final ArrayList<SolItem> myL;

  public ItemMan(TexMan texMan) {
    gunConfigs = new GunConfigs(texMan);
    armorConfigs = new Armor.Configs();
    shieldConfigs = new Shield.Configs();
    engineConfigs = new EngineItem.Configs();

    myM = new HashMap<String, SolItem>();

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

    myM.put("s", shieldConfigs.std.example);
    myM.put("sBig", shieldConfigs.big.example);
    myM.put("sMed", shieldConfigs.med.example);

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
          SolItem item = myM.get(name).copy();
          if (item == null) throw new AssertionError("unknown item " + name + "@" + parts[0] + "@" + rec + "@" + items);
          c.add(item);
        }
      }
    }
  }

  public SolItem random() {
    return myL.get(SolMath.intRnd(myM.size())).copy();
  }
}
