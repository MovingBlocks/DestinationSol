package org.destinationsol.game.planet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.common.SolMath;
import org.destinationsol.files.FileManager;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.chunk.SpaceEnvConfig;
import org.destinationsol.game.item.ItemMan;
import org.destinationsol.game.item.TradeConfig;
import org.destinationsol.game.ship.HullConfigs;

import java.util.*;

public class SysConfigs {
  private final Map<String, SysConfig> myConfigs;
  private final Map<String, SysConfig> myHardConfigs;
  private final Map<String, SysConfig> myBeltConfigs;
  private final Map<String, SysConfig> myHardBeltConfigs;

  public SysConfigs(TextureManager textureManager, HullConfigs hullConfigs, ItemMan itemMan) {
    myConfigs = new HashMap<String, SysConfig>();
    myHardConfigs = new HashMap<String, SysConfig>();
    myBeltConfigs = new HashMap<String, SysConfig>();
    myHardBeltConfigs = new HashMap<String, SysConfig>();

    load(textureManager, hullConfigs, false, "systems.json", itemMan);
    load(textureManager, hullConfigs, true, "asteroidBelts.json", itemMan);
  }

  private void load(TextureManager textureManager, HullConfigs hullConfigs, boolean belts, String configName,
    ItemMan itemMan)
  {
    JsonReader r = new JsonReader();
    FileHandle configFile = FileManager.getInstance().getConfigDirectory().child(configName);
    JsonValue parsed = r.parse(configFile);
    for (JsonValue sh : parsed) {
      ArrayList<ShipConfig> tempEnemies = ShipConfig.loadList(sh.get("temporaryEnemies"), hullConfigs, itemMan);
      ArrayList<ShipConfig> innerTempEnemies = ShipConfig.loadList(sh.get("innerTemporaryEnemies"), hullConfigs, itemMan);
      SpaceEnvConfig envConfig = new SpaceEnvConfig(sh.get("environment"), textureManager, configFile);

      ArrayList<ShipConfig> constEnemies = null;
      ArrayList<ShipConfig> constAllies = null;
      if (!belts) {
        constEnemies = ShipConfig.loadList(sh.get("constantEnemies"), hullConfigs, itemMan);
        constAllies = ShipConfig.loadList(sh.get("constantAllies"), hullConfigs, itemMan);
      }
      TradeConfig tradeConfig = TradeConfig.load(itemMan, sh.get("trading"), hullConfigs);
      boolean hard = sh.getBoolean("hard", false);
      SysConfig c = new SysConfig(sh.name, tempEnemies, envConfig, constEnemies, constAllies, tradeConfig, innerTempEnemies, hard);
      Map<String, SysConfig> configs = belts ? hard ? myHardBeltConfigs : myBeltConfigs : hard ? myHardConfigs : myConfigs;
      configs.put(sh.name, c);
    }
  }

  public SysConfig getRandomBelt(boolean hard) {
    Map<String, SysConfig> config = hard ? myHardBeltConfigs : myBeltConfigs;
    return SolMath.elemRnd(new ArrayList<SysConfig>(config.values()));
  }

  public SysConfig getConfig(String name) {
    SysConfig res = myConfigs.get(name);
    if (res != null) return res;
    return myHardConfigs.get(name);
  }

  public SysConfig getRandomCfg(boolean hard) {
    Map<String, SysConfig> config = hard ? myHardConfigs : myConfigs;
    return SolMath.elemRnd(new ArrayList<SysConfig>(config.values()));
  }

  public void addAllConfigs(ArrayList<ShipConfig> l) {
    for (SysConfig sc : myConfigs.values()) {
      l.addAll(sc.constAllies);
      l.addAll(sc.constEnemies);
      l.addAll(sc.tempEnemies);
      l.addAll(sc.innerTempEnemies);
    }
    for (SysConfig sc : myHardConfigs.values()) {
      l.addAll(sc.constAllies);
      l.addAll(sc.constEnemies);
      l.addAll(sc.tempEnemies);
      l.addAll(sc.innerTempEnemies);
    }
    for (SysConfig sc : myBeltConfigs.values()) {
      l.addAll(sc.tempEnemies);
    }
    for (SysConfig sc : myHardBeltConfigs.values()) {
      l.addAll(sc.tempEnemies);
    }
  }
}
