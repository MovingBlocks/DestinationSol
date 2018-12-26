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
package org.destinationsol.game.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;

import java.util.ArrayList;
import java.util.List;

public class LightObject implements SolObject {

    private final LightSource lightSource;
    private final ArrayList<Drawable> drawables;
    private final Vector2 position;

    // consumes pos
    LightObject(float size, boolean hasHalo, float intensity, Vector2 position, float fadeTime, Color colours) {
        this.position = position;
        lightSource = new LightSource(size, hasHalo, intensity, new Vector2(), colours);
        lightSource.setFadeTime(fadeTime);
        lightSource.setWorking();
        drawables = new ArrayList<>();
        lightSource.collectDrawables(drawables);
    }

    @Override
    public void update(SolGame game) {
        lightSource.update(false, 0, game);
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return lightSource.isFinished();
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
        return null;
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
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return false;
    }
}
