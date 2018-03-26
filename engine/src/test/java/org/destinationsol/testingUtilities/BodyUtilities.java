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
package org.destinationsol.testingUtilities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public final class BodyUtilities {

    private static final World world = new World(new Vector2(0, 0), true);

    /**
     * Creates a new rectangular body with fixture of size {@code (1.0, 1.0)},at position {@code (1.0, 0.0)}, rotated by 30°
     * @return The created body
     */
    public final Body createDummyBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(1f, 0f);
        bodyDef.angle = 30 * MathUtils.degreesToRadians;
        Body body = world.createBody(bodyDef);
        PolygonShape rectangle = new PolygonShape();
        rectangle.setAsBox(0.5f, 0.5f, new Vector2(0, 0), 30 * MathUtils.degreesToRadians);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = rectangle;
        body.createFixture(fixtureDef);
        return body;
    }
}
