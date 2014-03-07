package com.miloshpetrov.sol2;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.common.SolMath;

import java.util.*;

public class TexMan {
  public static final String ICONS_DIR = "icons/";
  private final Map<String, TextureAtlas.AtlasRegion> myTexs;
  private final Map<TextureAtlas.AtlasRegion,TextureAtlas.AtlasRegion> myFlipped;
  private final Map<String, ArrayList<TextureAtlas.AtlasRegion>> myPacks;
  public final TextureAtlas.AtlasRegion whiteTex;
  private final TexProvider myTexProvider;

  public TexMan() {
    FileHandle atlasFile = SolFiles.readOnly("res/imgs/sol.atlas");
    myTexProvider = atlasFile.exists() ? new AtlasTexProvider(atlasFile) : new DevTexProvider();
    whiteTex = myTexProvider.getTex("misc/whiteTex", null);
    myPacks = new HashMap<String, ArrayList<TextureAtlas.AtlasRegion>>();
    myTexs = new HashMap<String, TextureAtlas.AtlasRegion>();
    myFlipped = new HashMap<TextureAtlas.AtlasRegion, TextureAtlas.AtlasRegion>();
  }

  public TextureAtlas.AtlasRegion getFlipped(TextureAtlas.AtlasRegion tex) {
    TextureAtlas.AtlasRegion r = myFlipped.get(tex);
    if (r != null) return r;
    r = myTexProvider.getCopy(tex);
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
    r = myTexProvider.getTex(name, null);
    if (r == null) throw new RuntimeException("texture not found: " + name);
    myTexs.put(name, r);
    return r;
  }

  public ArrayList<TextureAtlas.AtlasRegion> getPack(String name) {
    ArrayList<TextureAtlas.AtlasRegion> r = myPacks.get(name);
    if (r != null) return r;
    r = myTexProvider.getTexs(name, null);
    if (r.size() == 0) throw new RuntimeException("textures not found: " + name);
    myPacks.put(name, r);
    return r;
  }

  public TextureAtlas.AtlasRegion getRndTex(String name, Boolean flipped) {
    if (flipped == null) flipped = SolMath.test(.5f);
    ArrayList<TextureAtlas.AtlasRegion> pack = getPack(name);
    TextureAtlas.AtlasRegion r = SolMath.elemRnd(pack);
    if (flipped) {
      r = getFlipped(r);
    }
    return r;
  }

  public Sprite createSprite(String name) {
    return myTexProvider.createSprite(name);
  }

  public void dispose() {
    myTexProvider.dispose();
  }

}
