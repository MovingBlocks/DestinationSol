package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.GameCols;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.*;

public class PlanetConfigs {
  private final Map<String, PlanetConfig> myConfigs;
  private final List<PlanetConfig> myEasy;
  private final List<PlanetConfig> myHard;

  public PlanetConfigs(TexMan texMan, HullConfigs hullConfigs, GameCols cols, ItemMan itemMan) {
    myConfigs = new HashMap<String, PlanetConfig>();
    myEasy = new ArrayList<PlanetConfig>();
    myHard = new ArrayList<PlanetConfig>();

    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "planets.json");
    JsonValue parsed = r.parse(configFile);
    for (JsonValue sh : parsed) {
      PlanetConfig c = PlanetConfig.load(texMan, hullConfigs, configFile, sh, cols, itemMan);
      myConfigs.put(sh.name, c);
      if (!c.hardOnly) myEasy.add(c);
      if (!c.easyOnly) myHard.add(c);
    }
  }

  public PlanetConfig getConfig(String name) {
    return myConfigs.get(name);
  }

  public PlanetConfig getRandom(boolean hard) {
    return SolMath.elemRnd(hard ? myHard : myEasy);
  }

  public Map<String, PlanetConfig> getConfigs() {
    return myConfigs;
  }
}
