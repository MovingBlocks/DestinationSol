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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.assets.Assets;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.testingUtilities.BodyUtilities;
import org.destinationsol.testingUtilities.InitializationUtilities;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class RubbleTest {

    private static ArrayList<Drawable> drawables;

    private static final SolObject RUBBLE_CONSTANT;

    static {
        InitializationUtilities.init();
        RUBBLE_CONSTANT = createRubble();
    }

    private static SolObject createRubble() {
        drawables = new ArrayList<>(1);
        Gdx.app.postRunnable(() -> drawables.add(new RectSprite(Assets.listTexturesMatching("engine:rubble_.*").get(0), 1, 1, 1, new Vector2(), DrawableLevel.PART_FG_0, 0, 0, Color.WHITE, false)));
        return new Rubble(BodyUtilities.createDummyBody(), drawables);
    }

    @Test
    public void getPosition() {
        // Default position of dummy bodies is (1f, 0f)
        // Epsilon testing because of float accuracy
        assertTrue(RUBBLE_CONSTANT.getPosition().epsilonEquals(1f, 0f, 0.01f));
    }

    @Test
    public void toFarObject() {
        // For performance reasons, rubbles should not persist while offscreen.
        // They are pretty tiny in terms of gameplay, after all, so they don't even need to persist
        assertNull(RUBBLE_CONSTANT.toFarObject());
    }

    @Test
    public void getDrawables() {
        assertEquals(RUBBLE_CONSTANT.getDrawables(), drawables);
    }

    @Test
    public void getAngle() {
        // 30° is the default angle of dummyBody
        assertEquals(RUBBLE_CONSTANT.getAngle(), 30f, 0.25f);
    }

    @Test
    public void getSpeed() {
        Body body = BodyUtilities.createDummyBody();
        body.setLinearVelocity(1f, 2f);
        final Rubble rubble = new Rubble(body, drawables);
        assertTrue(rubble.getVelocity().epsilonEquals(1f, 2f, 0.01f));
        assertTrue(RUBBLE_CONSTANT.getVelocity().epsilonEquals(0f, 0f, 0.01f));
    }

    @Test
    public void handleContact() {
        // Rubbles are not big enough to cause damage or get damage or anything, so this just shouldn't crash
        RUBBLE_CONSTANT.handleContact(new Rubble(BodyUtilities.createDummyBody(), drawables), 10f, InitializationUtilities.game, new Vector2(0f, 0f));
    }

    @Test
    public void isMetal() {
        // Rubbles are so tiny that no sound should be played when one hits anything
        assertNull(RUBBLE_CONSTANT.isMetal());
    }

    @Test
    public void hasBody() {
        assertTrue(RUBBLE_CONSTANT.hasBody());
    }

    @Test
    public void update() {
        // Just should not throw exception - all the moving and stuff is handled by body, this has just to exist
        RUBBLE_CONSTANT.update(InitializationUtilities.game);
    }

    @Test
    public void shouldBeRemoved() {
        // This should persist for as long as seen/loaded
        assertFalse(RUBBLE_CONSTANT.shouldBeRemoved(InitializationUtilities.game));
    }

    @Test
    public void onRemove() {
        // TODO onRemove() should free its resources. How to test that?
        // I guess this just should not crash
        new Rubble(BodyUtilities.createDummyBody(), drawables).onRemove(InitializationUtilities.game);
    }

    @Test
    public void receiveDmg() {
        // Rubbles have no health, and thus receiveDamage() just should not crash
        RUBBLE_CONSTANT.receiveDmg(100, InitializationUtilities.game, null, DmgType.BULLET);
    }

    @Test
    public void receivesGravity() {
        // When ship is shattered in gravity, its Rubbles should fall to the ground
        assertTrue(RUBBLE_CONSTANT.receivesGravity());
    }

    @Disabled
    @Test
    public void receiveForce() {
        // TODO I don't quite know what does this even do, so better leave this for sb else
        fail("Implement this test!");
    }
}
