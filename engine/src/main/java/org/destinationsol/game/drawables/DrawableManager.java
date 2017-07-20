/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.drawables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import org.destinationsol.common.DebugCol;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DrawableManager {
    private final DrawableLevel[] myDlVals;
    private final ArrayList<OrderedMap<Texture, List<Drawable>>> drawables;
    private final Set<Drawable> myInCam = new HashSet<>();
    private final GameDrawer myDrawer;

    public DrawableManager(GameDrawer drawer) {
        myDlVals = DrawableLevel.values();
        myDrawer = drawer;
        drawables = new ArrayList<>();
        for (DrawableLevel myDlVal : myDlVals) {
            drawables.add(new OrderedMap<>());
        }
    }

    public static float radiusFromDras(List<Drawable> drawables) {
        float r = 0;
        for (Drawable drawable : drawables) {
            float rr = drawable.getRelPos().len() + drawable.getRadius();
            if (r < rr) {
                r = rr;
            }
        }
        return r;
    }

    public void objRemoved(SolObject o) {
        List<Drawable> drawables = o.getDrawables();
        removeAll(drawables);
    }

    public void removeAll(List<Drawable> drawables) {
        for (Drawable drawable : drawables) {
            DrawableLevel l = drawable.getLevel();
            OrderedMap<Texture, List<Drawable>> map = this.drawables.get(l.ordinal());
            Texture tex = drawable.getTex0();
            List<Drawable> set = map.get(tex);
            if (set == null) {
                continue;
            }
            set.remove(drawable);
            myInCam.remove(drawable);
        }
    }

    public void objAdded(SolObject o) {
        List<Drawable> drawables = o.getDrawables();
        addAll(drawables);
    }

    public void addAll(List<Drawable> drawables) {
        for (Drawable drawable : drawables) {
            DrawableLevel l = drawable.getLevel();
            OrderedMap<Texture, List<Drawable>> map = this.drawables.get(l.ordinal());
            Texture tex = drawable.getTex0();
            List<Drawable> set = map.get(tex);
            if (set == null) {
                set = new ArrayList<>();
                map.put(tex, set);
            }
            if (set.contains(drawable)) {
                continue;
            }
            set.add(drawable);
            myInCam.remove(drawable);
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
        for (SolObject o : objs) {
            Vector2 objPos = o.getPosition();
            float r = objectManager.getPresenceRadius(o);
            List<Drawable> drawables = o.getDrawables();
            float draLevelViewDist = viewDist;
            if (drawables.size() > 0) {
                draLevelViewDist *= drawables.get(0).getLevel().depth;
            }
            boolean objInCam = isInCam(objPos, r, camPos, draLevelViewDist);
            for (Drawable drawable : drawables) {
                if (!objInCam || !drawable.isEnabled()) {
                    myInCam.remove(drawable);
                    continue;
                }
                drawable.prepare(o);
                Vector2 draPos = drawable.getPos();
                float rr = drawable.getRadius();
                boolean draInCam = isInCam(draPos, rr, camPos, draLevelViewDist);
                if (draInCam) {
                    myInCam.add(drawable);
                } else {
                    myInCam.remove(drawable);
                }
            }
        }

        for (int dlIdx = 0, dlCount = myDlVals.length; dlIdx < dlCount; dlIdx++) {
            DrawableLevel drawableLevel = myDlVals[dlIdx];
            if (drawableLevel == DrawableLevel.PART_FG_0) {
                game.getMountDetectDrawer().draw(myDrawer);
            }
            OrderedMap<Texture, List<Drawable>> map = drawables.get(dlIdx);
            Array<Texture> texs = map.orderedKeys();
            for (int texIdx = 0, sz = texs.size; texIdx < sz; texIdx++) {
                Texture tex = texs.get(texIdx);
                List<Drawable> drawables = map.get(tex);
                for (Drawable drawable : drawables) {
                    if (myInCam.contains(drawable)) {
                        if (!DebugOptions.NO_DRAS) {
                            drawable.draw(myDrawer, game);
                        }
                    }
                }
            }
            if (drawableLevel.depth <= 1) {
                game.drawDebug(myDrawer);
            }
            if (drawableLevel == DrawableLevel.ATM) {
                if (!DebugOptions.NO_DRAS) {
                    game.getPlanetMan().drawPlanetCoreHack(game, myDrawer);
                    game.getPlanetMan().drawSunHack(game, myDrawer);
                }
            }
        }

        if (DebugOptions.DRAW_DRA_BORDERS) {
            for (OrderedMap<Texture, List<Drawable>> map : drawables) {
                for (List<Drawable> drawables : map.values()) {
                    for (Drawable drawable : drawables) {
                        drawDebug(myDrawer, game, drawable);
                    }
                }
            }
        }

        game.getSoundManager().drawDebug(myDrawer, game);
        myDrawer.maybeChangeAdditive(false);
    }

    private void drawDebug(GameDrawer drawer, SolGame game, Drawable drawable) {
        SolCam cam = game.getCam();
        float lineWidth = cam.getRealLineWidth();
        Color col = myInCam.contains(drawable) ? DebugCol.DRA : DebugCol.DRA_OUT;
        Vector2 pos = drawable.getPos();
        drawer.drawCircle(drawer.debugWhiteTex, pos, drawable.getRadius(), col, lineWidth, cam.getViewHeight());
    }

    private boolean isInCam(Vector2 pos, float r, Vector2 camPos, float viewDist) {
        return camPos.dst(pos) - viewDist < r;
    }

    public void update(SolGame game) {
    }

    public boolean isInCam(Drawable drawable) {
        return myInCam.contains(drawable);
    }

    public void collectTexs(Collection<TextureAtlas.AtlasRegion> collector, Vector2 pos) {
        for (Drawable drawable : myInCam) {
            if (.5f * drawable.getRadius() < drawable.getPos().dst(pos)) {
                continue;
            }
            TextureAtlas.AtlasRegion tex = drawable.getTex();
            if (tex == null) {
                continue;
            }
            collector.add(tex);
        }

    }
}
