package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.files.FileManager;
import com.miloshpetrov.sol2.game.GameColors;
import com.miloshpetrov.sol2.game.sound.SolSound;
import com.miloshpetrov.sol2.game.sound.SoundManager;

public class SolItemTypes {
  public final SolItemType clip;
  public final SolItemType shield;
  public final SolItemType armor;
  public final SolItemType abilityCharge;
  public final SolItemType gun;
  public final SolItemType money;
  public final SolItemType medMoney;
  public final SolItemType bigMoney;
  public final SolItemType repair;
  public final SolItemType fixedGun;

  public SolItemTypes(SoundManager soundManager, GameColors cols) {
    JsonReader r = new JsonReader();
    FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("types.json");
    JsonValue parsed = r.parse(configFile);
    clip = load("clip", soundManager, configFile, parsed, cols);
    shield = load("shield", soundManager, configFile, parsed, cols);
    armor = load("armor", soundManager, configFile, parsed, cols);
    abilityCharge = load("abilityCharge", soundManager, configFile, parsed, cols);
    gun = load("gun", soundManager, configFile, parsed, cols);
    fixedGun = load("fixedGun", soundManager, configFile, parsed, cols);
    money = load("money", soundManager, configFile, parsed, cols);
    medMoney = load("medMoney", soundManager, configFile, parsed, cols);
    bigMoney = load("bigMoney", soundManager, configFile, parsed, cols);
    repair = load("repair", soundManager, configFile, parsed, cols);
  }

  private SolItemType load(String name, SoundManager soundManager, FileHandle configFile, JsonValue parsed, GameColors cols) {
    JsonValue node = parsed.get(name);
    Color color = cols.load(node.getString("color"));
    SolSound pickUpSound = soundManager.getSound(node.getString("pickUpSound"), configFile);
    float sz = node.getFloat("sz");
    return new SolItemType(color, pickUpSound, sz);
  }
}
