package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.miloshpetrov.sol2.SolFiles;

import java.io.*;

public class EffectType {
  private final ParticleEmitter myEmitter;

  public EffectType(String fileName) {
    myEmitter = loadEmitter(fileName);
  }

  private static ParticleEmitter loadEmitter(final String fileName) {
    FileHandle effectFile = SolFiles.readOnly("res/emitters/" + fileName + ".p");
    InputStream input = effectFile.read();
    BufferedReader reader = new BufferedReader(new InputStreamReader(input), 512);
    ParticleEmitter emitter;
    try {
      emitter = new ParticleEmitter(reader);
    } catch (IOException ex) {
      throw new AssertionError("Error loading effect: " + effectFile, ex);
    } finally {
      try {
        reader.close();
      } catch (IOException ignore) {
      }
    }
    emitter.flipY();
    return emitter;
  }

  public ParticleEmitter newEmitter() {
    return new ParticleEmitter(myEmitter);
  }
}
