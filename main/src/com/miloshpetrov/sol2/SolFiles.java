package com.miloshpetrov.sol2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.miloshpetrov.sol2.game.DebugOptions;

public class SolFiles {

  public static FileHandle readOnly(String path) {
    if (DebugOptions.DEV_ROOT_PATH == null) return Gdx.files.internal(path);
    return Gdx.files.absolute(DebugOptions.DEV_ROOT_PATH + path);
  }
}
