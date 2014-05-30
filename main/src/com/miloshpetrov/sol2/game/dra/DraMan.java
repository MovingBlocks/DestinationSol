package com.miloshpetrov.sol2.game.dra;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.DebugCol;
import com.miloshpetrov.sol2.game.*;

import java.util.*;

public class DraMan {
  private final Map<DraLevel, Map<Texture, List<Dra>>> myDras;
  private final Set<Dra> myInCam;
  private final GameDrawer myDrawer;

  public DraMan(GameDrawer drawer) {
    myDrawer = drawer;
    myDras = new EnumMap<DraLevel, Map<Texture, List<Dra>>>(DraLevel.class);
    for (DraLevel l : DraLevel.values()) {
      myDras.put(l, new HashMap<Texture, List<Dra>>());
    }
    myInCam = new HashSet<Dra>();
  }

  public void objRemoved(SolObj o) {
    List<Dra> dras = o.getDras();
    removeAll(dras);
  }

  public void removeAll(List<Dra> dras) {
    for (int i = 0, drasSize = dras.size(); i < drasSize; i++) {
      Dra dra = dras.get(i);
      DraLevel l = dra.getLevel();
      Map<Texture, List<Dra>> map = myDras.get(l);
      Texture tex = dra.getTex0();
      List<Dra> set = map.get(tex);
      if (set == null) continue;
      set.remove(dra);
      if (set.isEmpty()) map.remove(tex);
      myInCam.remove(dra);
    }
  }

  public void objAdded(SolObj o) {
    List<Dra> dras = o.getDras();
    addAll(dras);
  }

  public void addAll(List<Dra> dras) {
    for (int i = 0, drasSize = dras.size(); i < drasSize; i++) {
      Dra dra = dras.get(i);
      DraLevel l = dra.getLevel();
      Map<Texture, List<Dra>> map = myDras.get(l);
      Texture tex = dra.getTex0();
      List<Dra> set = map.get(tex);
      if (set == null) {
        set = new ArrayList<Dra>();
        map.put(tex, set);
      }
      if (set.contains(dra)) {
        continue;
      }
      set.add(dra);
      myInCam.remove(dra);
    }
  }

  public void draw(SolGame game) {
    MapDrawer mapDrawer = game.getMapDrawer();
    if (mapDrawer.isToggled()) {
      mapDrawer.draw(myDrawer, game);
      return;
    }

    SolCam cam = game.getCam();
    myDrawer.updateMtx(game);
    game.getFarBgManOld().draw(myDrawer, cam, game);
    Vector2 camPos = cam.getPos();
    float viewDist = cam.getViewDist();

    ObjMan objMan = game.getObjMan();
    for (SolObj o : objMan.getObjs()) {
      Vector2 objPos = o.getPos();
      float r = objMan.getRadius(o);
      List<Dra> dras = o.getDras();
      float draLevelViewDist = viewDist;
      if (dras.size() > 0) draLevelViewDist *= dras.get(0).getLevel().depth;
      boolean objInCam = isInCam(objPos, r, camPos, draLevelViewDist);
      for (int i = 0, drasSize = dras.size(); i < drasSize; i++) {
        Dra dra = dras.get(i);
        if (!objInCam || !dra.isEnabled()) {
          myInCam.remove(dra);
          continue;
        }
        dra.prepare(o);
        Vector2 draPos = dra.getPos();
        float rr = dra.getRadius();
        boolean draInCam = isInCam(draPos, rr, camPos, draLevelViewDist);
        if (draInCam) {
          myInCam.add(dra);
        } else {
          myInCam.remove(dra);
        }
      }
    }

    for (Map.Entry<DraLevel, Map<Texture, List<Dra>>> e : myDras.entrySet()) {
      DraLevel draLevel = e.getKey();
      Map<Texture, List<Dra>> map = e.getValue();
      for (List<Dra> dras : map.values()) {
        for (int i = 0, drasSize = dras.size(); i < drasSize; i++) {
          Dra dra = dras.get(i);
          if (myInCam.contains(dra)) {
            if (!DebugOptions.NO_DRAS) dra.draw(myDrawer, game);
          }
        }
      }
      if (draLevel.depth <= 1) {
        game.drawDebug(myDrawer);
      }
      if (draLevel == DraLevel.ATM) {
        if (!DebugOptions.NO_DRAS) {
          game.getPlanetMan().drawPlanetCoreHack(game, myDrawer);
          game.getPlanetMan().drawSunHack(game, myDrawer);
        }
      }
    }


    if (DebugOptions.DRAW_DRA_BORDERS) {
      for (Map<Texture, List<Dra>> map : myDras.values()) {
        for (List<Dra> dras : map.values()) {
          for (int i = 0, drasSize = dras.size(); i < drasSize; i++) {
            Dra dra = dras.get(i);
            drawDebug(myDrawer, game, dra);
          }
        }
      }
    }

    game.getSoundMan().drawDebug(myDrawer, game);
    myDrawer.maybeChangeAdditive(false);
  }

  private void drawDebug(GameDrawer drawer, SolGame game, Dra dra) {
    float lineWidth = game.getCam().getRealLineWidth();
    Color col = myInCam.contains(dra) ? DebugCol.DRA : DebugCol.DRA_OUT;
    Vector2 pos = dra.getPos();
    drawer.drawCircle(drawer.debugWhiteTex, pos, dra.getRadius(), col, lineWidth);
  }

  private boolean isInCam(Vector2 pos, float r, Vector2 camPos, float viewDist) {
    return camPos.dst(pos) - viewDist < r;
  }

  public void update(SolGame game) {
    if (DebugOptions.MISC_INFO) {
      int count = 0;
      for (Map<Texture, List<Dra>> map : myDras.values()) {
        for (List<Dra> dras : map.values()) {
          for (Dra dra : dras) {
            count++;
          }
        }
      }
    }
  }

  public static float radiusFromDras(List<Dra> dras) {
    float r = 0;
    for (int i = 0, drasSize = dras.size(); i < drasSize; i++) {
      Dra dra = dras.get(i);
      float rr = dra.getRelPos().len() + dra.getRadius();
      if (r < rr) r = rr;
    }
    return r;
  }

  public boolean isInCam(Dra dra) {
    return myInCam.contains(dra);
  }

  public void collectTexs(Collection<TextureAtlas.AtlasRegion> collector, Vector2 pos) {
    for (Dra dra : myInCam) {
      if (.5f * dra.getRadius() < dra.getPos().dst(pos)) continue;
      TextureAtlas.AtlasRegion tex = dra.getTex();
      if (tex == null) continue;
      collector.add(tex);
    }

  }
}
