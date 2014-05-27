package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.AbilityCommonConfigs;
import com.miloshpetrov.sol2.game.item.EngineItem;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.sound.SoundMan;

import java.util.ArrayList;
import java.util.HashMap;

public class HullConfigs {
  private final HashMap<String,HullConfig> myConfigs;

  public HullConfigs(ShipBuilder shipBuilder, TexMan texMan, ItemMan itemMan, AbilityCommonConfigs abilityCommonConfigs,
    SoundMan soundMan)
  {
    myConfigs = new HashMap<String, HullConfig>();

    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "hulls.json");
    JsonValue parsed = r.parse(configFile);
    for (JsonValue hullNode : parsed) {
      String texName = hullNode.getString("texName");
      float size = hullNode.getFloat("size");
      int maxLife = hullNode.getInt("maxLife");
      String e1PosS = hullNode.getString("e1Pos", null);
      Vector2 e1Pos = e1PosS == null ? new Vector2() : SolMath.readV2(e1PosS);
      String e2PosS = hullNode.getString("e2Pos", null);
      Vector2 e2Pos = e2PosS == null ? new Vector2() : SolMath.readV2(e2PosS);
      Vector2 g1Pos = SolMath.readV2(hullNode, "g1Pos");
      String g2PosStr = hullNode.getString("g2Pos", null);
      Vector2 g2Pos = g2PosStr == null ? null : SolMath.readV2(g2PosStr);
      ArrayList<Vector2> lightSrcPoss = SolMath.readV2List(hullNode, "lightSrcPoss");
      boolean hasBase = hullNode.getBoolean("hasBase", false);
      ArrayList<Vector2> forceBeaconPoss = SolMath.readV2List(hullNode, "forceBeaconPoss");
      ArrayList<Vector2> doorPoss = SolMath.readV2List(hullNode, "doorPoss");
      HullConfig.Type type = HullConfig.Type.forName(hullNode.getString("type"));
      float durability = type == HullConfig.Type.BIG ? 3 : .7f;
      TextureAtlas.AtlasRegion tex = texMan.getTex("hulls/" + texName, configFile);
      TextureAtlas.AtlasRegion icon = texMan.getTex(TexMan.ICONS_DIR + texName, configFile);
      String engineStr = hullNode.getString("engine", null);
      EngineItem.Config ec = itemMan.getEngineConfigs().get(engineStr);
      if (ec != null) {
        if (type == HullConfig.Type.STATION || ec.big != (type == HullConfig.Type.BIG)) {
          throw new AssertionError("incompatible engine in hull " + hullNode.name);
        }
      }
      AbilityConfig ability = loadAbility(hullNode, itemMan, abilityCommonConfigs, soundMan);
      boolean g1UnderShip = hullNode.getBoolean("g1UnderShip", false);
      boolean g2UnderShip = hullNode.getBoolean("g2UnderShip", false);
      boolean m1Fixed = hullNode.getBoolean("m1Fixed", false);
      boolean m2Fixed = hullNode.getBoolean("m2Fixed", false);
      String displayName = hullNode.getString("displayName", "---");
      float price = hullNode.getInt("price", 0);
      String desc = hullNode.getString("desc", "---");
      HullConfig c = new HullConfig(texName, size, maxLife, e1Pos, e2Pos, g1Pos, g2Pos, lightSrcPoss, durability,
        hasBase, forceBeaconPoss, doorPoss, type, icon, tex, ec, ability, g1UnderShip, g2UnderShip, m1Fixed, m2Fixed, displayName, price, desc);
      process(c, shipBuilder);
      myConfigs.put(hullNode.name, c);
    }
  }

  private AbilityConfig loadAbility(JsonValue hullNode, ItemMan itemMan, AbilityCommonConfigs abilityCommonConfigs,
    SoundMan soundMan)
  {
    JsonValue abNode = hullNode.get("ability");
    if (abNode == null) return null;
    String type = abNode.getString("type");
    if ("sloMo".equals(type)) return SloMo.Config.load(abNode, itemMan, abilityCommonConfigs.sloMo, soundMan);
    if ("teleport".equals(type)) return Teleport.Config.load(abNode, itemMan, abilityCommonConfigs.teleport);
    if ("knockBack".equals(type)) return KnockBack.Config.load(abNode, itemMan, abilityCommonConfigs.knockBack);
    if ("emWave".equals(type)) return EmWave.Config.load(abNode, itemMan, abilityCommonConfigs.emWave);
    if ("unShield".equals(type)) return UnShield.Config.load(abNode, itemMan, abilityCommonConfigs.unShield);
    return null;
  }

  public HullConfig getConfig(String name) {
    return myConfigs.get(name);
  }

  private void process(HullConfig config, ShipBuilder shipBuilder) {
    Vector2 o = shipBuilder.getOrigin(config.texName);
    config.origin.set(o);
    config.origin.scl(config.size);
    config.g1Pos.sub(o).scl(config.size);
    if (config.g2Pos != null) config.g2Pos.sub(o).scl(config.size);
    config.e1Pos.sub(o).scl(config.size);
    config.e2Pos.sub(o).scl(config.size);
    for (Vector2 pos : config.lightSrcPoss) pos.sub(o).scl(config.size);
    for (Vector2 pos : config.forceBeaconPoss) pos.sub(o).scl(config.size);
    for (Vector2 pos : config.doorPoss) pos.sub(o).scl(config.size);
  }
}
