package com.miloshpetrov.sol2;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.miloshpetrov.sol2.common.SolMath;

import java.util.HashMap;
import java.util.Map;

public class TexMan {
  public static final String ICONS_DIR = "icons/";
  private final Map<String, TextureAtlas.AtlasRegion> myTexs;
  private final Map<TextureAtlas.AtlasRegion,TextureAtlas.AtlasRegion> myFlipped;
  private final Map<String, Array<TextureAtlas.AtlasRegion>> myPacks;
  public final TextureAtlas.AtlasRegion whiteTex;
  private final TexProvider myTexProvider;

  public TexMan() {
    FileHandle atlasFile = SolFiles.readOnly("res/imgs/sol.atlas");
    myTexProvider = atlasFile.exists() ? new AtlasBasedProvider(atlasFile) : new DevProvider();
    whiteTex = myTexProvider.getTex("misc/whiteTex");
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
    r = myTexProvider.getTex(name);
    if (r == null) throw new RuntimeException("texture not found: " + name);
    myTexs.put(name, r);
    return r;
  }

  public Array<TextureAtlas.AtlasRegion> getPack(String name) {
    Array<TextureAtlas.AtlasRegion> r = myPacks.get(name);
    if (r != null) return r;
    r = myTexProvider.getTexs(name);
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
    return myTexProvider.createSprite(name);
  }

  public void dispose() {
    myTexProvider.dispose();
  }

  private static interface TexProvider {
    TextureAtlas.AtlasRegion getTex(String name);
    void dispose();
    Sprite createSprite(String name);
    Array<TextureAtlas.AtlasRegion> getTexs(String name);
  }

  private static class AtlasBasedProvider implements TexProvider {
    private final TextureAtlas myAtlas;

    private AtlasBasedProvider(FileHandle atlasFile) {
      myAtlas = new TextureAtlas(atlasFile, true);
    }

    @Override
    public TextureAtlas.AtlasRegion getTex(String name) {
      return myAtlas.findRegion(name);
    }

    @Override
    public void dispose() {
      myAtlas.dispose();
    }

    @Override
    public Sprite createSprite(String name) {
      return myAtlas.createSprite(name);
    }

    @Override
    public Array<TextureAtlas.AtlasRegion> getTexs(String name) {
      return myAtlas.findRegions(name);
    }
  }

  private class DevProvider implements TexProvider {

    public static final String PREF = "imgSrcs/";
    public static final String SUFF = ".png";

    @Override
    public TextureAtlas.AtlasRegion getTex(String name) {
      FileHandle fh = SolFiles.readOnly(PREF + name + SUFF);
      return newTex(fh);
    }

    private TextureAtlas.AtlasRegion newTex(FileHandle fh) {
      Texture tex = new Texture(fh);
      TextureAtlas.AtlasRegion res = new TextureAtlas.AtlasRegion(tex, 0, 0, tex.getWidth(), tex.getHeight());
      res.flip(false, true);
      return res;
    }

    @Override
    public void dispose() {
      // forget it
    }

    @Override
    public Sprite createSprite(String name) {
      Texture tex = new Texture(SolFiles.readOnly(PREF + name + SUFF));
      return new Sprite(tex);
    }

    @Override
    public Array<TextureAtlas.AtlasRegion> getTexs(String name) {
      FileHandle file = SolFiles.readOnly(PREF + name + SUFF);
      FileHandle dir = file.parent();
      String baseName = file.nameWithoutExtension();
      Array<TextureAtlas.AtlasRegion> res = new Array<TextureAtlas.AtlasRegion>();
      for (FileHandle fh : dir.list()) {
        if (fh.isDirectory()) continue;
        String fhName = fh.nameWithoutExtension();
        String[] parts = fhName.split("_");
        if (parts.length != 2) continue;
        if (!parts[0].equals(baseName)) continue;
        // todo check that the rest is number;
        res.add(newTex(fh));
      }
      return res;
    }
  }
}
