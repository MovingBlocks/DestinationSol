/*
 * Copyright 2019 MovingBlocks
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
package org.destinationsol.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.*;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class Waypoint implements SolObject {

    public final Vector2 position;
    public final Color color;
    public final String name;
    public ArrayList<Drawable> drawables = new ArrayList<>();

    public Waypoint(Vector2 position, Color color, String name, TextureAtlas.AtlasRegion waypointTexture) {
        this.position = position;
        this.color = color;
        this.name = name;
        drawables.add(new RectSprite(waypointTexture, .3f, 0f, .5f, new Vector2(), DrawableLevel.BODIES, 0f, 0f, this.color, true));
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

    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {

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
        FarObject farObject = new FarObject() {
            @Override
            public boolean shouldBeRemoved(SolGame game) {
                return false;
            }

            @Override
            public SolObject toObject(SolGame game) {
                for(Waypoint waypoint : game.getHero().getWaypoints())
                    if(waypoint.position.x == position.x && waypoint.position.y == position.y)
                        return waypoint;
                    System.out.println("RETURNDED NUL L");
                    return null;
            }

            @Override
            public void update(SolGame game) {

            }

            @Override
            public float getRadius() {
                return 0;
            }

            @Override
            public Vector2 getPosition() {
                return position;
            }

            @Override
            public String toDebugString() {
                return "Waypoint";
            }

            @Override
            public boolean hasBody() {
                return false;
            }
        };
        return farObject;
    }

    @Override
    public List<Drawable> getDrawables() {
        return drawables;
    }

    @Override
    public float getAngle() {
        return 0;
    }

    @Override
    public Vector2 getVelocity() {
        return null;
    }

    @Override
    public void handleContact(SolObject other, float absImpulse, SolGame game, Vector2 collPos) {

    }

    @Override
    public Boolean isMetal() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return true;
    }
}
