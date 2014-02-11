package com.miloshpetrov.sol2.game.dra;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.DebugCol;
import com.miloshpetrov.sol2.game.*;

import java.util.*;

public class DraMan {
  private final Map<DraLevel, Map<Texture, List<Dra>>> myDras;
  private final Map<Dra, Boolean> myInCam;
  private final Drawer myDrawer;

  public DraMan(Drawer drawer) {
    myDrawer = drawer;
    myDras = new EnumMap<DraLevel, Map<Texture, List<Dra>>>(DraLevel.class);
    for (DraLevel l : DraLevel.values()) {
      myDras.put(l, new HashMap<Texture, List<Dra>>());
    }
    myInCam = new HashMap<Dra, Boolean>();
  }

  public void objRemoved(SolObj o) {
    List<Dra> dras = o.getDras();
    removeAll(dras);
  }

  public void removeAll(List<Dra> dras) {
    for (Dra dra : dras) {
      DraLevel l = dra.getLevel();
      Map<Texture, List<Dra>> map = myDras.get(l);
      Texture tex = dra.getTex();
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
    for (Dra dra : dras) {
      DraLevel l = dra.getLevel();
      Map<Texture, List<Dra>> map = myDras.get(l);
      Texture tex = dra.getTex();
      List<Dra> set = map.get(tex);
      if (set == null) {
        set = new ArrayList<Dra>();
        map.put(tex, set);
      }
      if (set.contains(dra)) {
        continue;
      }
      set.add(dra);
      myInCam.put(dra, false);
    }
  }

  public void draw(SolGame game) {
    MapDrawer mapDrawer = game.getMapDrawer();
    if (mapDrawer.isToggled()) {
      mapDrawer.draw(myDrawer, game);
      return;
    }


    SolCam cam = game.getCam();
    myDrawer.begin(game);
    game.getFarBgManOld().draw(myDrawer, cam, game);
    Vector2 camPos = cam.getPos();
    float viewDist = cam.getViewDist();

    for (SolObj o : game.getObjMan().getObjs()) {
      Vector2 objPos = o.getPos();
      float r = o.getRadius();
      List<Dra> dras = o.getDras();
      float draLevelViewDist = viewDist;
      if (dras.size() > 0) draLevelViewDist *= dras.get(0).getLevel().depth;
      boolean objInCam = isInCam(objPos, r, camPos, draLevelViewDist);
      for (Dra dra : dras) {
        if (!objInCam || !dra.isEnabled()) {
          myInCam.put(dra, false);
          continue;
        }
        dra.prepare(o);
        Vector2 draPos = dra.getPos();
        float rr = dra.getRadius();
        boolean draInCam = isInCam(draPos, rr, camPos, draLevelViewDist);
        myInCam.put(dra, draInCam);
      }
    }

    for (Map.Entry<DraLevel, Map<Texture, List<Dra>>> e : myDras.entrySet()) {
      DraLevel draLevel = e.getKey();
      Map<Texture, List<Dra>> map = e.getValue();
      for (List<Dra> dras : map.values()) {
        for (Dra dra : dras) {
          if (myInCam.get(dra)) dra.draw(myDrawer, game);
        }
      }
      if (draLevel.depth <= 1) {
        game.drawDebug(myDrawer);
      }
    }

    if (DebugAspects.DRAS) {
      for (Map<Texture, List<Dra>> map : myDras.values()) {
        for (List<Dra> dras : map.values()) {
          for (Dra dra : dras) {
            drawDebug(myDrawer, game, dra);
          }
        }
      }
    }
    myDrawer.end();
  }

  private void drawDebug(Drawer drawer, SolGame game, Dra dra) {
    float lineWidth = game.getCam().getRealLineWidth();
    Color col = myInCam.get(dra) ? DebugCol.DRA : DebugCol.DRA_OUT;
    Vector2 pos = dra.getPos();
    drawer.drawCircle(pos, dra.getRadius(), col, lineWidth);
  }

  private boolean isInCam(Vector2 pos, float r, Vector2 camPos, float viewDist) {
    return camPos.dst(pos) - viewDist < r;
  }

  public void update(SolGame game) {
    if (DebugAspects.VALS) {
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
    for (Dra dra : dras) {
      float rr = 0;
      rr = dra.getRelPos().len() + dra.getRadius();
      if (r < rr) r = rr;
    }
    return r;
  }

  public boolean isInCam(Dra dra) {
    Boolean res = myInCam.get(dra);
    return res != null && res;
  }

  public void dispose() {
    myDrawer.dispose();
  }
}
