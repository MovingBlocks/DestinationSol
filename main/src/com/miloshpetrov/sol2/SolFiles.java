package com.miloshpetrov.sol2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class SolFiles {
  public static String REPO_PATH;

  public static FileHandle readOnly(String path) {
    if (REPO_PATH == null) return Gdx.files.internal(path);
    return Gdx.files.absolute(REPO_PATH + "/main/" + path);
  }
}
