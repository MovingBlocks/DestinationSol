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
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.Const;
import org.destinationsol.common.DebugCol;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableManager;
import org.destinationsol.game.drawables.FarDrawable;
import org.destinationsol.game.ship.FarShip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ObjectManager {
    private static final float MAX_RADIUS_RECALC_AWAIT = 1f;
    private final List<SolObject> myObjs;
    private final List<SolObject> myToRemove;
    private final List<SolObject> myToAdd;
    private final List<FarObjData> myFarObjs;
    private final List<FarShip> myFarShips;
    private final List<StarPort.MyFar> myFarPorts;
    private final World myWorld;
    private final Box2DDebugRenderer myDr;
    private final HashMap<SolObject, Float> myRadii;

    private float myFarEndDist;
    private float myFarBeginDist;
    private float myRadiusRecalcAwait;

    public ObjectManager(SolContactListener contactListener, FactionManager factionManager) {
        myObjs = new ArrayList<>();
        myToRemove = new ArrayList<>();
        myToAdd = new ArrayList<>();
        myFarObjs = new ArrayList<>();
        myFarShips = new ArrayList<>();
        myFarPorts = new ArrayList<>();
        myWorld = new World(new Vector2(0, 0), true);
        myWorld.setContactListener(contactListener);
        myWorld.setContactFilter(new SolContactFilter(factionManager));
        myDr = new Box2DDebugRenderer();
        myRadii = new HashMap<>();
    }

    public boolean containsFarObj(FarObj fo) {
        for (FarObjData fod : myFarObjs) {
            if (fod.fo == fo) {
                return true;
            }
        }
        return false;
    }

    public void update(SolGame game) {
        addRemove(game);

        float ts = game.getTimeStep();
        myWorld.step(ts, 6, 2);

        SolCam cam = game.getCam();
        Vector2 camPos = cam.getPos();
        myFarEndDist = 1.5f * cam.getViewDist();
        myFarBeginDist = 1.33f * myFarEndDist;

        boolean recalcRad = false;
        if (myRadiusRecalcAwait > 0) {
            myRadiusRecalcAwait -= ts;
        } else {
            myRadiusRecalcAwait = MAX_RADIUS_RECALC_AWAIT;
            recalcRad = true;
        }

        for (SolObject o : myObjs) {
            o.update(game);
            SolMath.checkVectorsTaken(o);
            List<Drawable> drawables = o.getDrawables();
            for (Drawable drawable : drawables) {
                drawable.update(game, o);
            }

            if (o.shouldBeRemoved(game)) {
                removeObjDelayed(o);
                if (game.getHero().isAlive() && game.getHero().isNonTranscendent() && o == game.getHero().getHero()) {
                    game.getHero().die();
                }
                continue;
            }
            if (isFar(o, camPos)) {
                if (game.getHero().isAlive() && game.getHero().isNonTranscendent() && o != game.getHero().getHero()) {
                    FarObj fo = o.toFarObj();
                    if (fo != null) {
                        addFarObjNow(fo);
                    }
                    removeObjDelayed(o);
                    continue;
                }
            }
            if (recalcRad) {
                recalcRadius(o);
            }
        }

        for (Iterator<FarObjData> it = myFarObjs.iterator(); it.hasNext(); ) {
            FarObjData fod = it.next();
            FarObj fo = fod.fo;
            fo.update(game);
            SolMath.checkVectorsTaken(fo);
            if (fo.shouldBeRemoved(game)) {
                removeFo(it, fo);
                continue;
            }
            if (isNear(fod, camPos, ts)) {
                SolObject o = fo.toObj(game);
                // Ensure that StarPorts are added straight away so that we can see if they overlap
                if (o instanceof StarPort) {
                    addObjNow(game, o);
                } else {
                    addObjDelayed(o);
                }
                removeFo(it, fo);
            }
        }
        addRemove(game);
    }

    private void removeFo(Iterator<FarObjData> it, FarObj fo) {
        it.remove();
        if (fo instanceof FarShip) {
            myFarShips.remove(fo);
        }
        if (fo instanceof StarPort.MyFar) {
            myFarPorts.remove(fo);
        }
    }

    private void recalcRadius(SolObject o) {
        float rad = DrawableManager.radiusFromDras(o.getDrawables());
        myRadii.put(o, rad);
    }

    public float getPresenceRadius(SolObject o) {
        Float res = getRadius(o);
        return res + Const.MAX_MOVE_SPD * (MAX_RADIUS_RECALC_AWAIT - myRadiusRecalcAwait);
    }

    public Float getRadius(SolObject o) {
        Float res = myRadii.get(o);
        if (res == null) {
            throw new AssertionError("no radius for " + o);
        }
        return res;
    }

    private void addRemove(SolGame game) {
        for (SolObject o : myToRemove) {
            removeObjNow(game, o);
        }
        myToRemove.clear();

        for (SolObject o : myToAdd) {
            addObjNow(game, o);
        }
        myToAdd.clear();
    }

    private void removeObjNow(SolGame game, SolObject o) {
        myObjs.remove(o);
        myRadii.remove(o);
        o.onRemove(game);
        game.getDrawableManager().objRemoved(o);
    }

    public void addObjNow(SolGame game, SolObject o) {
        if (DebugOptions.ASSERTIONS && myObjs.contains(o)) {
            throw new AssertionError();
        }
        myObjs.add(o);
        recalcRadius(o);
        game.getDrawableManager().objAdded(o);
    }

    private boolean isNear(FarObjData fod, Vector2 camPos, float ts) {
        if (fod.delay > 0) {
            fod.delay -= ts;
            return false;
        }
        FarObj fo = fod.fo;
        float r = fo.getRadius() * fod.depth;
        float dst = fo.getPos().dst(camPos) - r;
        if (dst < myFarEndDist) {
            return true;
        }
        fod.delay = (dst - myFarEndDist) / (2 * Const.MAX_MOVE_SPD);
        return false;
    }

    private boolean isFar(SolObject o, Vector2 camPos) {
        float r = getPresenceRadius(o);
        List<Drawable> drawables = o.getDrawables();
        if (drawables != null && drawables.size() > 0) {
            r *= drawables.get(0).getLevel().depth;
        }
        float dst = o.getPosition().dst(camPos) - r;
        return myFarBeginDist < dst;
    }

    public void drawDebug(GameDrawer drawer, SolGame game) {
        if (DebugOptions.DRAW_OBJ_BORDERS) {
            drawDebug0(drawer, game);
        }
        if (DebugOptions.OBJ_INFO) {
            drawDebugStrings(drawer, game);
        }

        if (DebugOptions.DRAW_PHYSIC_BORDERS) {
            drawer.end();
            myDr.render(myWorld, game.getCam().getMtx());
            drawer.begin();
        }
    }

    private void drawDebugStrings(GameDrawer drawer, SolGame game) {
        float fontSize = game.getCam().getDebugFontSize();
        for (SolObject o : myObjs) {
            Vector2 pos = o.getPosition();
            String ds = o.toDebugString();
            if (ds != null) {
                drawer.drawString(ds, pos.x, pos.y, fontSize, true, SolColor.WHITE);
            }
        }
        for (FarObjData fod : myFarObjs) {
            FarObj fo = fod.fo;
            Vector2 pos = fo.getPos();
            String ds = fo.toDebugString();
            if (ds != null) {
                drawer.drawString(ds, pos.x, pos.y, fontSize, true, SolColor.G);
            }
        }
    }

    private void drawDebug0(GameDrawer drawer, SolGame game) {
        SolCam cam = game.getCam();
        float lineWidth = cam.getRealLineWidth();
        float vh = cam.getViewHeight();
        for (SolObject o : myObjs) {
            Vector2 pos = o.getPosition();
            float r = getRadius(o);
            drawer.drawCircle(drawer.debugWhiteTex, pos, r, DebugCol.OBJ, lineWidth, vh);
            drawer.drawLine(drawer.debugWhiteTex, pos.x, pos.y, o.getAngle(), r, DebugCol.OBJ, lineWidth);
        }
        for (FarObjData fod : myFarObjs) {
            FarObj fo = fod.fo;
            drawer.drawCircle(drawer.debugWhiteTex, fo.getPos(), fo.getRadius(), DebugCol.OBJ_FAR, lineWidth, vh);
        }
        drawer.drawCircle(drawer.debugWhiteTex, cam.getPos(), myFarBeginDist, SolColor.WHITE, lineWidth, vh);
        drawer.drawCircle(drawer.debugWhiteTex, cam.getPos(), myFarEndDist, SolColor.WHITE, lineWidth, vh);
    }

    public List<SolObject> getObjs() {
        return myObjs;
    }

    public void addObjDelayed(SolObject p) {
        if (DebugOptions.ASSERTIONS && myToAdd.contains(p)) {
            throw new AssertionError();
        }
        myToAdd.add(p);
    }

    public void removeObjDelayed(SolObject obj) {
        if (DebugOptions.ASSERTIONS && myToRemove.contains(obj)) {
            throw new AssertionError();
        }
        myToRemove.add(obj);
    }

    public World getWorld() {
        return myWorld;
    }

    public void resetDelays() {
        for (FarObjData data : myFarObjs) {
            data.delay = 0;
        }

    }

    public List<FarObjData> getFarObjs() {
        return myFarObjs;
    }

    public void addFarObjNow(FarObj fo) {
        float depth = 1f;
        if (fo instanceof FarDrawable) {
            List<Drawable> drawables = ((FarDrawable) fo).getDrawables();
            if (drawables != null && drawables.size() > 0) {
                depth = drawables.get(0).getLevel().depth;
            }
        }
        FarObjData fod = new FarObjData(fo, depth);
        myFarObjs.add(fod);
        if (fo instanceof FarShip) {
            myFarShips.add((FarShip) fo);
        }
        if (fo instanceof StarPort.MyFar) {
            myFarPorts.add((StarPort.MyFar) fo);
        }
    }

    public List<FarShip> getFarShips() {
        return myFarShips;
    }

    public List<StarPort.MyFar> getFarPorts() {
        return myFarPorts;
    }

    public void dispose() {
        myWorld.dispose();
    }
}
