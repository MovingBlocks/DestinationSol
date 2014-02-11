package com.miloshpetrov.sol2.save;

public class SaveMan {

  public boolean canResume() {
//    FileHandle save = Gdx.files.external("miloshpetrov/sol/save.json");
    return false; //save.exists();
  }

  public SaveData getData() {
    return null;
  }
}
