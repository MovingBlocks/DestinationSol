package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.item.BulletClip;
import com.miloshpetrov.sol2.game.item.SolItem;

public class ProjectileConfig {

  public final TextureAtlas.AtlasRegion tex;
  public final float sz;
  public final float spdLen;
  public final boolean explode;
  public final float physSize;
  public final boolean hasFlame;
  public final boolean smokeOnExplosion;
  public final boolean stretch;

  public ProjectileConfig(TextureAtlas.AtlasRegion tex, float sz, float spdLen, boolean explode, boolean stretch,
    float physSize, boolean hasFlame, boolean smokeOnExplosion)
  {
    this.tex = tex;
    this.sz = sz;
    this.spdLen = spdLen;
    this.explode = explode;
    this.stretch = stretch;
    this.physSize = physSize;
    this.hasFlame = hasFlame;
    this.smokeOnExplosion = smokeOnExplosion;
  }

  public float getProjSpd() {
    return spdLen;
  }

  public int getAmmoPerClip() {
    return BulletClip.AMMO_PER_CLIP;
  }

  public String getClipTexName() {
    return BulletClip.TEX_NAME;
  }

  public SolItem getClipExample() {
    return BulletClip.EXAMPLE;
  }
}
