package com.miloshpetrov.sol2.game.dra;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.miloshpetrov.sol2.common.DebugCol;
import com.miloshpetrov.sol2.game.*;

import java.util.*;

public class DraMan {
  private final DraLevel[] myDlVals;
  private final ArrayList<OrderedMap<Texture, List<Dra>>> myDras;
  private final Set<Dra> myInCam;
  private final GameDrawer myDrawer;

  public DraMan(GameDrawer drawer) {
    myDlVals = DraLevel.values();
    myDrawer = drawer;
    myDras = new ArrayList<OrderedMap<Texture, List<Dra>>>();
    for (int i = 0, sz = myDlVals.length; i < sz; i++) {
      myDras.add(new OrderedMap<Texture, List<Dra>>());
    }
    myInCam = new HashSet<Dra>();
  }

  public void objRemoved(SolObject o) {
    List<Dra> dras = o.getDras();
    removeAll(dras);
  }

  public void removeAll(List<Dra> dras) {
    for (int i = 0, drasSize = dras.size(); i < drasSize; i++) {
      Dra dra = dras.get(i);
      DraLevel l = dra.getLevel();
      OrderedMap<Texture, List<Dra>> map = myDras.get(l.ordinal());
      Texture tex = dra.getTex0();
      List<Dra> set = map.get(tex);
      if (set == null) continue;
      set.remove(dra);
      myInCam.remove(dra);
    }
  }

  public void objAdded(SolObject o) {
    List<Dra> dras = o.getDras();
    addAll(dras);
  }

  public void addAll(List<Dra> dras) {
    for (int i = 0, drasSize = dras.size(); i < drasSize; i++) {
      Dra dra = dras.get(i);
      DraLevel l = dra.getLevel();
      OrderedMap<Texture, List<Dra>> map = myDras.get(l.ordinal());
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

    ObjectManager objectManager = game.getObjMan();
    List<SolObject> objs = objectManager.getObjs();
    for (int i1 = 0, objsSize = objs.size(); i1 < objsSize; i1++) {
      SolObject o = objs.get(i1);
      Vector2 objPos = o.getPos();
      float r = objectManager.getPresenceRadius(o);
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

    for (int dlIdx = 0, dlCount = myDlVals.length; dlIdx < dlCount; dlIdx++) {
      DraLevel draLevel = myDlVals[dlIdx];
      if (draLevel == DraLevel.PART_FG_0) {
        game.getMountDetectDrawer().draw(myDrawer);
      }
      OrderedMap<Texture, List<Dra>> map = myDras.get(dlIdx);
      Array<Texture> texs = map.orderedKeys();
      for (int texIdx = 0, sz = texs.size; texIdx < sz; texIdx++) {
        Texture tex = texs.get(texIdx);
        List<Dra> dras = map.get(tex);
        for (int draIdx = 0, drasSize = dras.size(); draIdx < drasSize; draIdx++) {
          Dra dra = dras.get(draIdx);
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
      for (OrderedMap<Texture, List<Dra>> map : myDras) {
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
    SolCam cam = game.getCam();
    float lineWidth = cam.getRealLineWidth();
    Color col = myInCam.contains(dra) ? DebugCol.DRA : DebugCol.DRA_OUT;
    Vector2 pos = dra.getPos();
    drawer.drawCircle(drawer.debugWhiteTex, pos, dra.getRadius(), col, lineWidth, cam.getViewHeight());
  }

  private boolean isInCam(Vector2 pos, float r, Vector2 camPos, float viewDist) {
    return camPos.dst(pos) - viewDist < r;
  }

  public void update(SolGame game) {
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
