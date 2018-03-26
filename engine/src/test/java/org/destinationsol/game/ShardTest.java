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

import org.destinationsol.assets.Assets;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.testingUtilities.BodyUtilities;
import org.destinationsol.testingUtilities.InitializationUtilities;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ShardTest {

    private static void init() {
        InitializationUtilities.init();
    }

    private static final SolObject constantShard;

    static {
        init();
        constantShard = createShard();
    }

    private static SolObject createShard() {
        BodyUtilities utilities = new BodyUtilities();
        ArrayList<Drawable> drawables = new ArrayList<>(1);
        drawables.add(new RectSprite(Assets.listTexturesMatching("engine:shard_.*").get(0), 1, 1, 1, null, null, 0, 0, null, false));
        return new Shard(utilities.createDummyBody(), drawables);
    }

    @Test
    public void getPosition() {
        // Default position of dummy bodies is (1f, 0f)
        // Epsilon testing because of float accuracy
        assertTrue(constantShard.getPosition().epsilonEquals(1f, 0f, 0.01f));
    }

    @Test
    public void toFarObject() {
        // For performance reasons, shards should not persist while offscreen.
        // They are pretty tiny in terms of gameplay, after all, so they don't even need to persist
        assertNull(constantShard.toFarObject());
    }

    @Test
    public void getDrawables() {
    }

    @Test
    public void getAngle() {
    }

    @Test
    public void getSpeed() {
    }

    @Test
    public void handleContact() {
    }

    @Test
    public void isMetal() {
        // Shards are so tiny that no sound should be played when one hits anything
        assertNull(constantShard.isMetal());
    }

    @Test
    public void hasBody() {
    }

    @Test
    public void update() {
    }

    @Test
    public void shouldBeRemoved() {
    }

    @Test
    public void onRemove() {
    }

    @Test
    public void receiveDmg() {
    }

    @Test
    public void receivesGravity() {
    }

    @Test
    public void receiveForce() {
    }
}
