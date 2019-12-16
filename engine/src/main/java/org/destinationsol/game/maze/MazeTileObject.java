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

package org.destinationsol.game.maze;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.destinationsol.Const;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;

import java.util.ArrayList;
import java.util.List;

public class MazeTileObject implements SolObject {
    private final List<Drawable> drawables;
    private final Body body;
    private final Vector2 position;
    private final float angle;
    private final MazeTile tile;
    private final boolean isFlipped;

    public MazeTileObject(MazeTile tile, List<Drawable> drawables, Body body, Vector2 position, float angle, boolean flipped) {
        this.tile = tile;
        this.drawables = drawables;
        this.body = body;
        this.position = position;
        this.angle = angle;
        isFlipped = flipped;
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
        if (body != null) {
            body.getWorld().destroyBody(body);
        }
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {
        game.getSpecialSounds().playHit(game, this, position, dmgType);
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
        return position;
    }

    @Override
    public FarObject toFarObject() {
        return new MyFar(tile, angle, position, isFlipped);
    }

    @Override
    public List<Drawable> getDrawables() {
        return drawables;
    }

    @Override
    public float getAngle() {
        return angle;
    }

    @Override
    public Vector2 getVelocity() {
        return null;
    }

    @Override
    public void handleContact(SolObject other, float absImpulse,
                              SolGame game, Vector2 collPos) {
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return tile.metal;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    public static class MyFar implements FarObject {

        private final MazeTile tile;
        private final float angle;
        private final Vector2 position;
        private final boolean isFlipped;

        public MyFar(MazeTile tile, float angle, Vector2 position, boolean flipped) {
            this.tile = tile;
            this.angle = angle;
            this.position = position;
            isFlipped = flipped;
        }

        @Override
        public boolean shouldBeRemoved(SolGame game) {
            return false;
        }

        @Override
        public SolObject toObject(SolGame game) {
            return new Builder().build(game, tile, position, angle, isFlipped);
        }

        @Override
        public void update(SolGame game) {
        }

        @Override
        public float getRadius() {
            return MazeBuilder.TILE_SZ / 2;
        }

        @Override
        public Vector2 getPosition() {
            return position;
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
        public MazeTileObject build(SolGame game, MazeTile tile, Vector2 position, float angle, boolean flipped) {
            List<Drawable> drawables = new ArrayList<>();
            TextureAtlas.AtlasRegion tex = new TextureAtlas.AtlasRegion(tile.tex);
            TextureAtlas.AtlasRegion backgroundTexture = new TextureAtlas.AtlasRegion(tile.backgroundTexture);
            if (flipped) {
                tex.flip(!tex.isFlipX(), !tex.isFlipY());
                backgroundTexture.flip(!backgroundTexture.isFlipX(), !backgroundTexture.isFlipY());
            }
            RectSprite s = new RectSprite(tex, MazeBuilder.TILE_SZ, 0, 0, new Vector2(), DrawableLevel.GROUND, 0, 0, SolColor.WHITE, false);
            drawables.add(s);
            RectSprite s2 = new RectSprite(backgroundTexture, MazeBuilder.TILE_SZ, 0, 0, new Vector2(), DrawableLevel.DECO, 0, 0, SolColor.WHITE, false);
            drawables.add(s2);
            Body body = buildBody(game, angle, position, tile, flipped);
            MazeTileObject res = new MazeTileObject(tile, drawables, body, position, angle, flipped);
            body.setUserData(res);
            return res;
        }

        private Body buildBody(SolGame game, float angle, Vector2 position, MazeTile tile, boolean flipped) {
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.KinematicBody;
            def.position.set(position);
            def.angle = angle * MathUtils.degRad;
            def.angularDamping = 0;
            Body body = game.getObjectManager().getWorld().createBody(def);

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
