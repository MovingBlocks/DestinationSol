package com.miloshpetrov.sol2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.miloshpetrov.sol2.common.SolMath;

import java.util.HashMap;
import java.util.Map;

public class TexMan {
  public static final String ICONS_DIR = "icons/";
  private final TextureAtlas myAtlas;
  private final Map<String, TextureAtlas.AtlasRegion> myTexs;
  private final Map<TextureAtlas.AtlasRegion,TextureAtlas.AtlasRegion> myFlipped;
  private final Map<String, Array<TextureAtlas.AtlasRegion>> myPacks;
  public final TextureAtlas.AtlasRegion whiteTex;

  public TexMan() {
    myAtlas = new TextureAtlas(Gdx.files.internal("res/imgs/sol.atlas"));
    for (TextureAtlas.AtlasRegion r : myAtlas.getRegions()) {
      r.flip(false, true);
    }
    whiteTex = myAtlas.findRegion("misc/whiteTex");
    myPacks = new HashMap<String, Array<TextureAtlas.AtlasRegion>>();
    myTexs = new HashMap<String, TextureAtlas.AtlasRegion>();
    myFlipped = new HashMap<TextureAtlas.AtlasRegion, TextureAtlas.AtlasRegion>();
  }

  public TextureAtlas.AtlasRegion getFlipped(TextureAtlas.AtlasRegion tex) {
    TextureAtlas.AtlasRegion r = myFlipped.get(tex);
    if (r != null) return r;
    r = new TextureAtlas.AtlasRegion(tex);
    r.flip(true, false);
    myFlipped.put(tex, r);
    return r;
  }

  public TextureAtlas.AtlasRegion getTex(String name, boolean flipped) {
    TextureAtlas.AtlasRegion r = getTex(name);
    return flipped ? getFlipped(r) : r;
  }

  public TextureAtlas.AtlasRegion getTex(String name) {
    TextureAtlas.AtlasRegion r = myTexs.get(name);
    if (r != null) return r;
    r = myAtlas.findRegion(name);
    if (r == null) throw new RuntimeException("texture not found: " + name);
    myTexs.put(name, r);
    return r;
  }

  public Array<TextureAtlas.AtlasRegion> getPack(String name) {
    Array<TextureAtlas.AtlasRegion> r = myPacks.get(name);
    if (r != null) return r;
    r = myAtlas.findRegions(name);
    if (r.size == 0) throw new RuntimeException("textures not found: " + name);
    myPacks.put(name, r);
    return r;
  }

  public TextureAtlas.AtlasRegion getRndTex(String name, Boolean flipped) {
    if (flipped == null) flipped = SolMath.test(.5f);
    Array<TextureAtlas.AtlasRegion> pack = getPack(name);
    TextureAtlas.AtlasRegion r = pack.get(SolMath.intRnd(pack.size));
    if (flipped) {
      r = getFlipped(r);
    }
    return r;
  }

  public Sprite createSprite(String name) {
    return myAtlas.createSprite(name);
  }

  public void dispose() {
    myAtlas.dispose();
  }
}
