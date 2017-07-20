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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.DrawableObject;
import org.destinationsol.game.drawables.FarDrawable;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.planet.PlanetBind;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class BeaconHandler {
    private static final float TEX_SZ = .5f;
    private static final float ROT_SPD = 30f;

    private final RectSprite myAttackSprite;
    private final RectSprite myFollowSprite;
    private final RectSprite myMoveSprite;
    private final Vector2 myTargetRelPos;

    private DrawableObject myD;
    private FarDrawable myFarD;
    private Pilot myTargetPilot;
    private SolShip myTarget;
    private FarShip myFarTarget;
    private Action myCurrAction;
    private PlanetBind myPlanetBind;
    private float myClickTime;
    private Vector2 mySpd;
    private boolean myInitialized;

    public BeaconHandler() {
        TextureAtlas.AtlasRegion attackTex = Assets.getAtlasRegion("engine:uiBeaconAttack");
        myAttackSprite = new RectSprite(attackTex, TEX_SZ, 0, 0, new Vector2(), DrawableLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
        TextureAtlas.AtlasRegion followTex = Assets.getAtlasRegion("engine:uiBeaconFollow");
        myFollowSprite = new RectSprite(followTex, TEX_SZ, 0, 0, new Vector2(), DrawableLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
        TextureAtlas.AtlasRegion moveTex = Assets.getAtlasRegion("engine:uiBeaconMove");
        myMoveSprite = new RectSprite(moveTex, TEX_SZ, 0, 0, new Vector2(), DrawableLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
        myTargetRelPos = new Vector2();
        mySpd = new Vector2();
    }

    public void init(SolGame game, Vector2 pos) {
        ArrayList<Drawable> drawables = new ArrayList<>();
        drawables.add(myAttackSprite);
        drawables.add(myFollowSprite);
        drawables.add(myMoveSprite);
        myD = new DrawableObject(drawables, new Vector2(pos), new Vector2(), null, false, false);
        game.getObjMan().addObjDelayed(myD);
        myInitialized = true;
    }

    public void update(SolGame game) {
        if (!myInitialized) {
            return;
        }
        updateD(game);
        mySpd.set(0, 0);
        if (maybeUpdateTargetPos(game)) {
            return;
        }
        maybeUpdatePlanetPos(game);
    }

    private void maybeUpdatePlanetPos(SolGame game) {
        Vector2 beaconPos = getPos0();
        if (myPlanetBind == null) {
            myPlanetBind = PlanetBind.tryBind(game, beaconPos, 0);
            return;
        }
        Vector2 vec = SolMath.getVec();
        myPlanetBind.setDiff(vec, beaconPos, false);
        beaconPos.add(vec);
        SolMath.free(vec);
        myPlanetBind.getPlanet().calcSpdAtPos(mySpd, beaconPos);
    }

    private boolean maybeUpdateTargetPos(SolGame game) {
        updateTarget(game);
        if (myTargetPilot == null) {
            return false;
        }
        Vector2 beaconPos = getPos0();
        if (myTarget != null) {
            SolMath.toWorld(beaconPos, myTargetRelPos, myTarget.getAngle(), myTarget.getPosition(), false);
            mySpd.set(myTarget.getSpd());
        } else {
            beaconPos.set(myFarTarget.getPos());
        }
        return true;
    }

    private void updateTarget(SolGame game) {
        if (myTargetPilot == null) {
            return;
        }
        ObjectManager om = game.getObjMan();
        List<SolObject> objs = om.getObjs();
        List<FarShip> farShips = om.getFarShips();
        if (myTarget != null) {
            if (objs.contains(myTarget)) {
                return;
            }
            myTarget = null;
            for (FarShip ship : farShips) {
                if (ship.getPilot() != myTargetPilot) {
                    continue;
                }
                myFarTarget = ship;
                return;
            }
            applyAction(Action.MOVE);
            return;
        }
        if (myFarTarget == null) {
            throw new AssertionError();
        }
        if (om.getFarShips().contains(myFarTarget)) {
            return;
        }
        myFarTarget = null;
        for (SolObject o : objs) {
            if ((o instanceof SolShip)) {
                SolShip ship = (SolShip) o;
                if (ship.getPilot() != myTargetPilot) {
                    continue;
                }
                myTarget = ship;
                return;
            }
        }
        applyAction(Action.MOVE);
    }

    private void updateD(SolGame game) {
        ObjectManager om = game.getObjMan();
        List<SolObject> objs = om.getObjs();
        List<FarObjData> farObjs = om.getFarObjs();

        if (myD != null) {
            if (objs.contains(myD)) {
                return;
            }
            myD = null;
            for (FarObjData fod : farObjs) {
                FarObj fo = fod.fo;
                if (!(fo instanceof FarDrawable)) {
                    continue;
                }
                List<Drawable> drawables = ((FarDrawable) fo).getDrawables();
                if (drawables.size() != 3) {
                    continue;
                }
                Drawable drawable = drawables.get(0);
                if (drawable != myAttackSprite) {
                    continue;
                }
                myFarD = (FarDrawable) fo;
                return;
            }
            throw new AssertionError();
        }
        if (myFarD == null) {
            throw new AssertionError();
        }
        if (om.containsFarObj(myFarD)) {
            return;
        }
        myFarD = null;
        for (SolObject o : objs) {
            if ((o instanceof DrawableObject)) {
                List<Drawable> drawables = o.getDrawables();
                if (drawables.size() != 3) {
                    continue;
                }
                Drawable drawable = drawables.get(0);
                if (drawable != myAttackSprite) {
                    continue;
                }
                myD = (DrawableObject) o;
                return;
            }
        }
        throw new AssertionError();
    }

    public Action processMouse(SolGame g, Vector2 pos, boolean clicked, boolean onMap) {
        Action action;
        Pilot targetPilot = findPilotInPos(g, pos, onMap, clicked);
        if (targetPilot != null) {
            boolean enemies = g.getFactionMan().areEnemies(targetPilot.getFaction(), g.getHero().getPilot().getFaction());
            if (enemies) {
                action = Action.ATTACK;
                if (clicked) {
                    myTargetRelPos.set(0, 0);
                }
            } else {
                action = Action.FOLLOW;
                if (clicked) {
                    if (myTarget == null) {
                        myTargetRelPos.set(0, 0);
                    } else {
                        SolMath.toRel(pos, myTargetRelPos, myTarget.getAngle(), myTarget.getPosition());
                    }
                }
            }
        } else {
            action = Action.MOVE;
        }

        if (clicked) {
            applyAction(action);
            getPos0().set(pos);
            myClickTime = g.getTime();
        }
        return action;
    }

    private void applyAction(Action action) {
        myCurrAction = action;
        myAttackSprite.tint.a = myCurrAction == Action.ATTACK ? 1 : 0;
        myMoveSprite.tint.a = myCurrAction == Action.MOVE ? 1 : 0;
        myFollowSprite.tint.a = myCurrAction == Action.FOLLOW ? 1 : 0;
        myPlanetBind = null;
        if (myCurrAction == Action.MOVE) {
            myTargetPilot = null;
            myTarget = null;
            myFarTarget = null;
        }
    }

    private Pilot findPilotInPos(SolGame g, Vector2 pos, boolean onMap, boolean clicked) {
        ObjectManager om = g.getObjMan();
        SolShip h = g.getHero();
        float iconRad = onMap ? g.getMapDrawer().getIconRadius(g.getCam()) : 0;
        for (SolObject o : om.getObjs()) {
            if (o == h || !(o instanceof SolShip)) {
                continue;
            }
            SolShip s = (SolShip) o;
            Pilot pilot = s.getPilot();
            if (onMap && pilot.getMapHint() == null) {
                continue;
            }
            float dst = o.getPosition().dst(pos);
            float rad = iconRad == 0 ? s.getHull().config.getSize() : iconRad;
            if (dst < rad) {
                if (clicked) {
                    myTargetPilot = pilot;
                    myTarget = s;
                }
                return pilot;
            }
        }
        for (FarShip s : om.getFarShips()) {
            Pilot pilot = s.getPilot();
            if (onMap && pilot.getMapHint() == null) {
                continue;
            }
            float dst = s.getPos().dst(pos);
            float rad = iconRad == 0 ? s.getHullConfig().getApproxRadius() : iconRad;
            if (dst < rad) {
                if (clicked) {
                    myTargetPilot = pilot;
                    myFarTarget = s;
                }
                return pilot;
            }
        }
        return null;
    }

    public Vector2 getPos() {
        return getPos0();
    }

    // returns Vector itself
    private Vector2 getPos0() {
        return myD == null ? myFarD.getPos() : myD.getPosition();
    }

    public Action getCurrAction() {
        return myCurrAction;
    }

    public float getClickTime() {
        return myClickTime;
    }

    public Vector2 getSpd() {
        return mySpd;
    }

    public enum Action {
        MOVE, ATTACK, FOLLOW
    }
}
