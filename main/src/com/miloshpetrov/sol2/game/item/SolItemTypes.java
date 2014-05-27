package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.game.GameCols;
import com.miloshpetrov.sol2.game.sound.SolSound;
import com.miloshpetrov.sol2.game.sound.SoundMan;

public class SolItemTypes {
  public final SolItemType clip;
  public final SolItemType shield;
  public final SolItemType armor;
  public final SolItemType abilityCharge;
  public final SolItemType gun;
  public final SolItemType money;
  public final SolItemType bigMoney;
  public final SolItemType repair;

  public SolItemTypes(SoundMan soundMan, GameCols cols) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(ItemMan.ITEM_CONFIGS_DIR + "types.json");
    JsonValue parsed = r.parse(configFile);
    clip = load("clip", soundMan, configFile, parsed, cols);
    shield = load("shield", soundMan, configFile, parsed, cols);
    armor = load("armor", soundMan, configFile, parsed, cols);
    abilityCharge = load("abilityCharge", soundMan, configFile, parsed, cols);
    gun = load("gun", soundMan, configFile, parsed, cols);
    money = load("money", soundMan, configFile, parsed, cols);
    bigMoney = load("bigMoney", soundMan, configFile, parsed, cols);
    repair = load("repair", soundMan, configFile, parsed, cols);
  }

  private SolItemType load(String name, SoundMan soundMan, FileHandle configFile, JsonValue parsed, GameCols cols) {
    JsonValue node = parsed.get(name);
    Color color = cols.load(node.getString("color"));
    SolSound pickUpSound = soundMan.getSound(node.getString("pickUpSound"), configFile);
    return new SolItemType(color, pickUpSound);
  }
}
