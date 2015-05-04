package com.miloshpetrov.sol2;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.files.FileManager;

import java.util.*;

public class TextureManager {
  public static final String ICONS_DIR = "ui/icons/";
  public static final String HULL_ICONS_DIR = "ui/hullIcons/";
  private final Map<String, TextureAtlas.AtlasRegion> myTexs;
  private final Map<TextureAtlas.AtlasRegion,TextureAtlas.AtlasRegion> myFlipped;
  private final Map<String, ArrayList<TextureAtlas.AtlasRegion>> myPacks;
  private final TextureProvider myTexProvider;

  public TextureManager() {
    FileHandle atlasFile = FileManager.getInstance().getImagesDirectory().child("sol.atlas");
    myTexProvider = atlasFile.exists() ? new AtlasTextureProvider(atlasFile) : new DevTextureProvider();
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

  public TextureAtlas.AtlasRegion getTex(String name, boolean flipped, FileHandle configFile) {
    TextureAtlas.AtlasRegion r = getTex(name, configFile);
    return flipped ? getFlipped(r) : r;
  }

  public TextureAtlas.AtlasRegion getTex(String fullName, FileHandle configFile) {
    TextureAtlas.AtlasRegion r = myTexs.get(fullName);
    if (r != null) return r;
    r = myTexProvider.getTex(fullName, configFile);
    if (r == null) throw new AssertionError("texture not found: " + fullName);
    myTexs.put(fullName, r);
    return r;
  }

  public ArrayList<TextureAtlas.AtlasRegion> getPack(String name, FileHandle configFile) {
    ArrayList<TextureAtlas.AtlasRegion> r = myPacks.get(name);
    if (r != null) return r;
    r = myTexProvider.getTexs(name, configFile);
    if (r.size() == 0) throw new AssertionError("textures not found: " + name);
    myPacks.put(name, r);
    return r;
  }

  public TextureAtlas.AtlasRegion getRndTex(String name, Boolean flipped, FileHandle configFile) {
    if (flipped == null) flipped = SolMath.test(.5f);
    ArrayList<TextureAtlas.AtlasRegion> pack = getPack(name, configFile);
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
