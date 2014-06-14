package com.miloshpetrov.sol2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.miloshpetrov.sol2.game.DebugOptions;

public class SolFiles {

  public static FileHandle readOnly(String path) {
    return getFile(path, true);
  }

  public static FileHandle writable(String path) {
    return getFile(path, true);
  }

  public static FileHandle getFile(String path, boolean readOnly) {
    if (DebugOptions.DEV_ROOT_PATH != null) return Gdx.files.absolute(DebugOptions.DEV_ROOT_PATH + path);
    return readOnly ? Gdx.files.internal(path) : Gdx.files.local(path);
  }
}
