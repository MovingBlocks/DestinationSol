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

package org.destinationsol.game.maze;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.destinationsol.Const;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObj;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;

import java.util.ArrayList;
import java.util.List;

public class MazeTileObject implements SolObject {
    private final List<Drawable> myDrawables;
    private final Body myBody;
    private final Vector2 myPos;
    private final float myAngle;
    private final MazeTile myTile;
    private final boolean myFlipped;

    public MazeTileObject(MazeTile tile, List<Drawable> drawables, Body body, Vector2 pos, float angle, boolean flipped) {
        myTile = tile;
        myDrawables = drawables;
        myBody = body;
        myPos = pos;
        myAngle = angle;
        myFlipped = flipped;
    }

    @Override
    public void update(SolGame game) {
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
    }

    @Override
    public void onRemove(SolGame game) {
        if (myBody != null) {
            myBody.getWorld().destroyBody(myBody);
        }
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
        game.getSpecialSounds().playHit(game, this, pos, dmgType);
    }

    @Override
    public boolean receivesGravity() {
        return false;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
    }

    @Override
    public Vector2 getPosition() {
        return myPos;
    }

    @Override
    public FarObj toFarObj() {
        return new MyFar(myTile, myAngle, myPos, myFlipped);
    }

    @Override
    public List<Drawable> getDrawables() {
        return myDrawables;
    }

    @Override
    public float getAngle() {
        return myAngle;
    }

    @Override
    public Vector2 getSpd() {
        return null;
    }

    @Override
    public void handleContact(SolObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                              SolGame game, Vector2 collPos) {
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return myTile.metal;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    public static class MyFar implements FarObj {

        private final MazeTile myTile;
        private final float myAngle;
        private final Vector2 myPos;
        private final boolean myFlipped;

        public MyFar(MazeTile tile, float angle, Vector2 pos, boolean flipped) {
            myTile = tile;
            myAngle = angle;
            myPos = pos;
            myFlipped = flipped;
        }

        @Override
        public boolean shouldBeRemoved(SolGame game) {
            return false;
        }

        @Override
        public SolObject toObj(SolGame game) {
            return new Builder().build(game, myTile, myPos, myAngle, myFlipped);
        }

        @Override
        public void update(SolGame game) {
        }

        @Override
        public float getRadius() {
            return MazeBuilder.TILE_SZ / 2;
        }

        @Override
        public Vector2 getPos() {
            return myPos;
        }

        @Override
        public String toDebugString() {
            return null;
        }

        @Override
        public boolean hasBody() {
            return true;
        }
    }

    public static class Builder {
        public MazeTileObject build(SolGame game, MazeTile tile, Vector2 pos, float angle, boolean flipped) {
            List<Drawable> drawables = new ArrayList<>();
            TextureAtlas.AtlasRegion tex = new TextureAtlas.AtlasRegion(tile.tex);
            TextureAtlas.AtlasRegion bgTex = new TextureAtlas.AtlasRegion(tile.bgTex);
            if (flipped) {
                tex.flip(!tex.isFlipX(), !tex.isFlipY());
                bgTex.flip(!bgTex.isFlipX(), !bgTex.isFlipY());
            }
            RectSprite s = new RectSprite(tex, MazeBuilder.TILE_SZ, 0, 0, new Vector2(), DrawableLevel.GROUND, 0, 0, SolColor.WHITE, false);
            drawables.add(s);
            RectSprite s2 = new RectSprite(bgTex, MazeBuilder.TILE_SZ, 0, 0, new Vector2(), DrawableLevel.DECO, 0, 0, SolColor.WHITE, false);
            drawables.add(s2);
            Body body = buildBody(game, angle, pos, tile, flipped);
            MazeTileObject res = new MazeTileObject(tile, drawables, body, pos, angle, flipped);
            body.setUserData(res);
            return res;
        }

        private Body buildBody(SolGame game, float angle, Vector2 pos, MazeTile tile, boolean flipped) {
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.KinematicBody;
            def.position.set(pos);
            def.angle = angle * SolMath.degRad;
            def.angularDamping = 0;
            Body body = game.getObjMan().getWorld().createBody(def);

            for (List<Vector2> pts : tile.points) {
                ChainShape shape = new ChainShape();
                List<Vector2> points = new ArrayList<>();
                int sz = pts.size();
                for (int i = 0; i < sz; i++) {
                    Vector2 curr = pts.get(flipped ? sz - i - 1 : i);
                    Vector2 v = new Vector2(curr);
                    v.add(-.5f, -.5f);
                    if (flipped) {
                        v.x *= -1;
                    }
                    v.scl(MazeBuilder.TILE_SZ);
                    points.add(v);
                }
                Vector2[] v = points.toArray(new Vector2[] {});
                shape.createLoop(v);
                Fixture f = body.createFixture(shape, 0);
                f.setFriction(Const.FRICTION);
                shape.dispose();
            }

            return body;
        }
    }
}