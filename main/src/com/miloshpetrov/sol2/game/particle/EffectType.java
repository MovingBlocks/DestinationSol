package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.miloshpetrov.sol2.files.FileManager;

import java.io.*;

public class EffectType {
  private final ParticleEmitter myEmitter;
  public final boolean continuous;
  public final boolean additive;

  public EffectType(String fileName) {
    myEmitter = loadEmitter(fileName);
    continuous = myEmitter.isContinuous();
    myEmitter.setContinuous(false);
    additive = myEmitter.isAdditive();
    myEmitter.setAdditive(false);
  }

  private static ParticleEmitter loadEmitter(final String fileName) {
    FileHandle effectFile = FileManager.getInstance().getAssetsDirectory().child("emitters").child(fileName + ".p");
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
