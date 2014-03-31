package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.DmgType;
import com.miloshpetrov.sol2.game.particle.EffectConfig;
import com.miloshpetrov.sol2.game.sound.SolSound;

public class ProjectileConfig {

  public final TextureAtlas.AtlasRegion tex;
  public final float texSz;
  public final float spdLen;
  public final float physSize;
  public final boolean stretch;
  public final DmgType dmgType;
  public final SolSound collisionSound;
  public final float lightSz;
  public final EffectConfig trailEffect;
  public final EffectConfig bodyEffect;
  public final EffectConfig collisionEffect1;
  public final EffectConfig collisionEffect2;

  public ProjectileConfig(TextureAtlas.AtlasRegion tex, float texSz, float spdLen, boolean stretch,
    float physSize, DmgType dmgType, SolSound collisionSound, float lightSz, EffectConfig trailEffect,
    EffectConfig bodyEffect, EffectConfig collisionEffect1, EffectConfig collisionEffect2)
  {
    this.tex = tex;
    this.texSz = texSz;
    this.spdLen = spdLen;
    this.stretch = stretch;
    this.physSize = physSize;
    this.dmgType = dmgType;
    this.collisionSound = collisionSound;
    this.lightSz = lightSz;
    this.trailEffect = trailEffect;
    this.bodyEffect = bodyEffect;
    this.collisionEffect1 = collisionEffect1;
    this.collisionEffect2 = collisionEffect2;
  }

}
