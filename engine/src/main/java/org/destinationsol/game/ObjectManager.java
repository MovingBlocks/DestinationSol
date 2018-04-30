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
import org.destinationsol.game.drawables.FarDrawable;
import org.destinationsol.game.ship.FarShip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectManager {
    private final Map<Class, List<SolObject>> myObjs;
    private final Map<Class, List<Class>> inheritanceLists;
    private final List<SolObject> myToRemove;
    private final List<SolObject> myToAdd;
    private final List<FarObjData> myFarObjs;
    private final List<FarShip> myFarShips;
    private final List<StarPort.FarStarPort> myFarPorts;
    private final World myWorld;
    private final Box2DDebugRenderer myDr;

    private float myFarEndDist;
    private float myFarBeginDist;

    public ObjectManager(SolContactListener contactListener, FactionManager factionManager) {
        myObjs = new HashMap<>();
        myObjs.put(SolObject.class, new ArrayList<>());
        inheritanceLists = new HashMap<>();
        myToRemove = new ArrayList<>();
        myToAdd = new ArrayList<>();
        myFarObjs = new ArrayList<>();
        myFarShips = new ArrayList<>();
        myFarPorts = new ArrayList<>();
        myWorld = new World(new Vector2(0, 0), true);
        myWorld.setContactListener(contactListener);
        myWorld.setContactFilter(new SolContactFilter(factionManager));
        myDr = new Box2DDebugRenderer();
    }

    public boolean containsFarObj(FarObject fo) {
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
        Vector2 camPos = cam.getPosition();
        myFarEndDist = 1.5f * cam.getViewDistance();
        myFarBeginDist = 1.33f * myFarEndDist;


        for (SolObject o : myObjs.get(SolObject.class)) {
            o.update(game);
            SolMath.checkVectorsTaken(o);
            List<Drawable> drawables = o.getDrawables();
            for (Drawable drawable : drawables) {
                drawable.update(game, o);
            }

            final Hero hero = game.getHero();
            if (o.shouldBeRemoved(game)) {
                removeObjDelayed(o);
                if (hero.isAlive() && hero.isNonTranscendent() && o == hero.getShip()) {
                    hero.die();
                }
                continue;
            }
            if (isFar(o, camPos)) {
                if (hero.isAlive() && hero.isNonTranscendent() && o != hero.getShip()) {
                    FarObject fo = o.toFarObject();
                    if (fo != null) {
                        addFarObjNow(fo);
                    }
                    removeObjDelayed(o);
                }
            }
        }

        for (Iterator<FarObjData> it = myFarObjs.iterator(); it.hasNext(); ) {
            FarObjData fod = it.next();
            FarObject fo = fod.fo;
            fo.update(game);
            SolMath.checkVectorsTaken(fo);
            if (fo.shouldBeRemoved(game)) {
                removeFo(it, fo);
                continue;
            }
            if (isNear(fod, camPos, ts)) {
                SolObject o = fo.toObject(game);
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

    private void removeFo(Iterator<FarObjData> it, FarObject fo) {
        it.remove();
        if (fo instanceof FarShip) {
            myFarShips.remove(fo);
        }
        if (fo instanceof StarPort.FarStarPort) {
            myFarPorts.remove(fo);
        }
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
        for (Class clazz : inheritanceLists.get(o.getClass())) {
            myObjs.get(clazz).remove(o);
        }
        o.onRemove(game);
        game.getDrawableManager().removeObject(o);
    }

    public void addObjNow(SolGame game, SolObject o) {
        if (DebugOptions.ASSERTIONS && myObjs.get(SolObject.class).contains(o)) {
            throw new AssertionError();
        }
        if (!inheritanceLists.containsKey(o.getClass())) {
            getInheritanceListFor(o);
        }
        for (Class clazz : inheritanceLists.get(o.getClass())) {
            if (!myObjs.containsKey(clazz)) {
                myObjs.put(clazz, new ArrayList<>());
            }
            myObjs.get(clazz).add(o);
        }
        game.getDrawableManager().addObject(o);
    }

    private void getInheritanceListFor(SolObject o) {
        final ArrayList<Class> targetList = new ArrayList<>();
        inheritanceLists.put(o.getClass(), targetList);
        final LinkedList<Class> toScanList = new LinkedList<>();
        targetList.add(o.getClass());
        for (Class clazz : o.getClass().getInterfaces()) {
            if (SolObject.class.isAssignableFrom(clazz) && !toScanList.contains(clazz)) {
                toScanList.add(clazz);
                targetList.add(clazz);
            }
        }
        final Class<?> superclass = o.getClass().getSuperclass();
        if (SolObject.class.isAssignableFrom(superclass) && !toScanList.contains(superclass)) {
            toScanList.add(superclass);
            targetList.add(superclass);
        }
        while (!toScanList.isEmpty()) {
            Stream stream = toScanList.stream();
            toScanList.clear();
            stream.forEach(clazz -> {
                for (Class clzz : clazz.getClass().getInterfaces()) {
                    if (SolObject.class.isAssignableFrom(clzz) && !toScanList.contains(clzz) && !targetList.contains(clzz)) {
                        toScanList.add(clzz);
                        targetList.add(clzz);
                    }
                }
                final Class<?> superclss = clazz.getClass().getSuperclass();
                if (SolObject.class.isAssignableFrom(superclss) && !toScanList.contains(superclss)) {
                    toScanList.add(superclss);
                    targetList.add(superclss);
                }
            });
        }
    }

    private boolean isNear(FarObjData fod, Vector2 camPos, float ts) {
        if (fod.delay > 0) {
            fod.delay -= ts;
            return false;
        }
        FarObject fo = fod.fo;
        float r = fo.getRadius() * fod.depth;
        float dst = fo.getPosition().dst(camPos) - r;
        if (dst < myFarEndDist) {
            return true;
        }
        fod.delay = (dst - myFarEndDist) / (2 * Const.MAX_MOVE_SPD);
        return false;
    }

    private boolean isFar(SolObject o, Vector2 camPos) {
        float r = o.getRadius();
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
        for (SolObject o : myObjs.get(SolObject.class)) {
            Vector2 position = o.getPosition();
            String ds = o.toDebugString();
            if (ds != null) {
                drawer.drawString(ds, position.x, position.y, fontSize, true, SolColor.WHITE);
            }
        }
        for (FarObjData fod : myFarObjs) {
            FarObject fo = fod.fo;
            Vector2 position = fo.getPosition();
            String ds = fo.toDebugString();
            if (ds != null) {
                drawer.drawString(ds, position.x, position.y, fontSize, true, SolColor.G);
            }
        }
    }

    private void drawDebug0(GameDrawer drawer, SolGame game) {
        SolCam cam = game.getCam();
        float lineWidth = cam.getRealLineWidth();
        float vh = cam.getViewHeight();
        for (SolObject o : myObjs.get(SolObject.class)) {
            Vector2 position = o.getPosition();
            float r = o.getRadius();
            drawer.drawCircle(drawer.debugWhiteTexture, position, r, DebugCol.OBJ, lineWidth, vh);
            drawer.drawLine(drawer.debugWhiteTexture, position.x, position.y, o.getAngle(), r, DebugCol.OBJ, lineWidth);
        }
        for (FarObjData fod : myFarObjs) {
            FarObject fo = fod.fo;
            drawer.drawCircle(drawer.debugWhiteTexture, fo.getPosition(), fo.getRadius(), DebugCol.OBJ_FAR, lineWidth, vh);
        }
        drawer.drawCircle(drawer.debugWhiteTexture, cam.getPosition(), myFarBeginDist, SolColor.WHITE, lineWidth, vh);
        drawer.drawCircle(drawer.debugWhiteTexture, cam.getPosition(), myFarEndDist, SolColor.WHITE, lineWidth, vh);
    }

    public <T extends SolObject> List<T> getObjects(Class<T> clazz) {
        if (!myObjs.containsKey(clazz)) {
            myObjs.put(clazz, new ArrayList<>());
        }
        return myObjs.get(clazz).stream().map(clazz::cast).collect(Collectors.toList());
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

    public void addFarObjNow(FarObject fo) {
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
        if (fo instanceof StarPort.FarStarPort) {
            myFarPorts.add((StarPort.FarStarPort) fo);
        }
    }

    public List<FarShip> getFarShips() {
        return myFarShips;
    }

    public List<StarPort.FarStarPort> getFarPorts() {
        return myFarPorts;
    }

    public void dispose() {
        myWorld.dispose();
    }
}
