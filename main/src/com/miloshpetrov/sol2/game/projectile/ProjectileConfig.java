package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
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
  public final EffectConfig collisionEffect;
  public final EffectConfig collisionEffectBg;
  public final boolean zeroAbsSpd;
  public final Vector2 origin;
  public final float acc;
  public final SolSound workSound;
  public final boolean massless;
  public final float density;
  public final float guideRotSpd;
  public final float dmg;
  public final float emTime;

  public ProjectileConfig(TextureAtlas.AtlasRegion tex, float texSz, float spdLen, boolean stretch,
    float physSize, DmgType dmgType, SolSound collisionSound, float lightSz, EffectConfig trailEffect,
    EffectConfig bodyEffect, EffectConfig collisionEffect, EffectConfig collisionEffectBg,
    boolean zeroAbsSpd, Vector2 origin, float acc, SolSound workSound, boolean massless, float density,
    float guideRotSpd, float dmg, float emTime)
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
    this.collisionEffect = collisionEffect;
    this.collisionEffectBg = collisionEffectBg;
    this.zeroAbsSpd = zeroAbsSpd;
    this.origin = origin;
    this.acc = acc;
    this.workSound = workSound;
    this.massless = massless;
    this.density = density;
    this.guideRotSpd = guideRotSpd;
    this.dmg = dmg;
    this.emTime = emTime;
    if (physSize == 0 && massless) throw new AssertionError("only projectiles with physSize > 0 can be massless");
    if (density > 0 && (physSize == 0 || massless)) throw new AssertionError();
  }

}
