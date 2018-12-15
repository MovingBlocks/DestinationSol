/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

public class BeaconHandler implements UpdateAwareSystem{
    private static final float TEX_SZ = .5f;
    private static final float ROT_SPD = 30f;

    private final RectSprite attackSprite;
    private final RectSprite followSprite;
    private final RectSprite moveSprite;
    private final Vector2 targetRelativePosition;

    private DrawableObject drawable;
    private FarDrawable farDrawable;
    private Pilot targetPilot;
    private SolShip target;
    private FarShip farTarget;
    private Action currentAction;
    private PlanetBind planetBind;
    private float clickTime;
    private Vector2 velocity;
    private boolean isInitialized;

    public BeaconHandler() {
        TextureAtlas.AtlasRegion attackTexture = Assets.getAtlasRegion("engine:uiBeaconAttack");
        attackSprite = new RectSprite(attackTexture, TEX_SZ, 0, 0, new Vector2(), DrawableLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
        TextureAtlas.AtlasRegion followTexture = Assets.getAtlasRegion("engine:uiBeaconFollow");
        followSprite = new RectSprite(followTexture, TEX_SZ, 0, 0, new Vector2(), DrawableLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
        TextureAtlas.AtlasRegion moveTexture = Assets.getAtlasRegion("engine:uiBeaconMove");
        moveSprite = new RectSprite(moveTexture, TEX_SZ, 0, 0, new Vector2(), DrawableLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
        targetRelativePosition = new Vector2();
        velocity = new Vector2();
    }

    public void init(SolGame game, Vector2 position) {
        ArrayList<Drawable> drawables = new ArrayList<>();
        drawables.add(attackSprite);
        drawables.add(followSprite);
        drawables.add(moveSprite);
        drawable = new DrawableObject(drawables, new Vector2(position), new Vector2(), null, false, false);
        game.getObjectManager().addObjDelayed(drawable);
        isInitialized = true;
    }

    @Override
    public void update(SolGame game, float timeStep) {
        if (!isInitialized) {
            return;
        }
        updateD(game);
        velocity.set(0, 0);
        if (maybeUpdateTargetPos(game)) {
            return;
        }
        maybeUpdatePlanetPos(game);
    }

    private void maybeUpdatePlanetPos(SolGame game) {
        Vector2 beaconPos = getPos0();
        if (planetBind == null) {
            planetBind = PlanetBind.tryBind(game, beaconPos, 0);
            return;
        }
        Vector2 vec = SolMath.getVec();
        planetBind.setDiff(vec, beaconPos, false);
        beaconPos.add(vec);
        SolMath.free(vec);
        planetBind.getPlanet().calculateVelocityAtPosition(velocity, beaconPos);
    }

    private boolean maybeUpdateTargetPos(SolGame game) {
        updateTarget(game);
        if (targetPilot == null) {
            return false;
        }
        Vector2 beaconPos = getPos0();
        if (target != null) {
            SolMath.toWorld(beaconPos, targetRelativePosition, target.getAngle(), target.getPosition());
            velocity.set(target.getVelocity());
        } else {
            beaconPos.set(farTarget.getPosition());
        }
        return true;
    }

    private void updateTarget(SolGame game) {
        if (targetPilot == null) {
            return;
        }
        ObjectManager om = game.getObjectManager();
        List<SolObject> objs = om.getObjects();
        List<FarShip> farShips = om.getFarShips();
        if (target != null) {
            if (objs.contains(target)) {
                return;
            }
            target = null;
            for (FarShip ship : farShips) {
                if (ship.getPilot() != targetPilot) {
                    continue;
                }
                farTarget = ship;
                return;
            }
            applyAction(Action.MOVE);
            return;
        }
        if (farTarget == null) {
            throw new AssertionError("Far target does not exist!");
        }
        if (om.getFarShips().contains(farTarget)) {
            return;
        }
        farTarget = null;
        for (SolObject o : objs) {
            if ((o instanceof SolShip)) {
                SolShip ship = (SolShip) o;
                if (ship.getPilot() != targetPilot) {
                    continue;
                }
                target = ship;
                return;
            }
        }
        applyAction(Action.MOVE);
    }

    private void updateD(SolGame game) {
        ObjectManager om = game.getObjectManager();
        List<SolObject> objs = om.getObjects();
        List<FarObjData> farObjs = om.getFarObjs();

        if (drawable != null) {
            if (objs.contains(drawable)) {
                return;
            }
            drawable = null;
            for (FarObjData fod : farObjs) {
                FarObject fo = fod.fo;
                if (!(fo instanceof FarDrawable)) {
                    continue;
                }
                List<Drawable> drawables = ((FarDrawable) fo).getDrawables();
                if (drawables.size() != 3) {
                    continue;
                }
                Drawable drawable = drawables.get(0);
                if (drawable != attackSprite) {
                    continue;
                }
                farDrawable = (FarDrawable) fo;
                return;
            }
            throw new AssertionError();
        }
        if (farDrawable == null) {
            throw new AssertionError("Far drawable does not exist!");
        }
        if (om.containsFarObj(farDrawable)) {
            return;
        }
        farDrawable = null;
        for (SolObject o : objs) {
            if ((o instanceof DrawableObject)) {
                List<Drawable> drawables = o.getDrawables();
                if (drawables.size() != 3) {
                    continue;
                }
                Drawable drawable = drawables.get(0);
                if (drawable != attackSprite) {
                    continue;
                }
                this.drawable = (DrawableObject) o;
                return;
            }
        }
        throw new AssertionError();
    }

    public Action processMouse(SolGame game, Vector2 position, boolean clicked, boolean onMap) {
        Action action;
        Pilot targetPilot = findPilotInPos(game, position, onMap, clicked);
        if (targetPilot != null) {
            boolean enemies = game.getFactionMan().areEnemies(targetPilot.getFaction(), game.getHero().getPilot().getFaction());
            if (enemies) {
                action = Action.ATTACK;
                if (clicked) {
                    targetRelativePosition.set(0, 0);
                }
            } else {
                action = Action.FOLLOW;
                if (clicked) {
                    if (target == null) {
                        targetRelativePosition.set(0, 0);
                    } else {
                        SolMath.toRel(position, targetRelativePosition, target.getAngle(), target.getPosition());
                    }
                }
            }
        } else {
            action = Action.MOVE;
        }

        if (clicked) {
            applyAction(action);
            getPos0().set(position);
            clickTime = game.getTime();
        }
        return action;
    }

    private void applyAction(Action action) {
        currentAction = action;
        attackSprite.tint.a = currentAction == Action.ATTACK ? 1 : 0;
        moveSprite.tint.a = currentAction == Action.MOVE ? 1 : 0;
        followSprite.tint.a = currentAction == Action.FOLLOW ? 1 : 0;
        planetBind = null;
        if (currentAction == Action.MOVE) {
            targetPilot = null;
            target = null;
            farTarget = null;
        }
    }

    private Pilot findPilotInPos(SolGame game, Vector2 position, boolean onMap, boolean clicked) {
        ObjectManager objectManager = game.getObjectManager();
        Hero hero = game.getHero();
        float iconRad = onMap ? game.getMapDrawer().getIconRadius(game.getCam()) : 0;
        for (SolObject o : objectManager.getObjects()) {
            if (o == hero.getShipUnchecked() || !(o instanceof SolShip)) {
                continue;
            }
            SolShip s = (SolShip) o;
            Pilot pilot = s.getPilot();
            if (onMap && pilot.getMapHint() == null) {
                continue;
            }
            float dst = o.getPosition().dst(position);
            float rad = iconRad == 0 ? s.getHull().config.getSize() : iconRad;
            if (dst < rad) {
                if (clicked) {
                    targetPilot = pilot;
                    target = s;
                }
                return pilot;
            }
        }
        for (FarShip s : objectManager.getFarShips()) {
            Pilot pilot = s.getPilot();
            if (onMap && pilot.getMapHint() == null) {
                continue;
            }
            float dst = s.getPosition().dst(position);
            float rad = iconRad == 0 ? s.getHullConfig().getApproxRadius() : iconRad;
            if (dst < rad) {
                if (clicked) {
                    targetPilot = pilot;
                    farTarget = s;
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
        return drawable == null ? farDrawable.getPosition() : drawable.getPosition();
    }

    public Action getCurrAction() {
        return currentAction;
    }

    public float getClickTime() {
        return clickTime;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public enum Action {
        MOVE, ATTACK, FOLLOW
    }
}
