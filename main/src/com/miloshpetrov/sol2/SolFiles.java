package com.miloshpetrov.sol2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class SolFiles {
  public static String REPO_PATH;

  public static FileHandle readOnly(String path) {
    return Gdx.files.internal(path);
  }
}
