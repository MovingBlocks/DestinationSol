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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import org.destinationsol.common.Immutable;
import org.destinationsol.common.In;
import org.destinationsol.common.SolMath;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.force.events.ContactEvent;
import org.destinationsol.force.events.ImpulseEvent;
import org.destinationsol.game.projectile.Projectile;
import org.terasology.context.annotation.Introspected;
import org.terasology.gestalt.entitysystem.entity.EntityRef;

import javax.inject.Inject;
import javax.inject.Provider;

@Introspected
public class SolContactListener implements ContactListener {

    @Inject
    protected EntitySystemManager entitySystemManager;

    private final Provider<SolGame> myGame;

    @Inject
    public SolContactListener(Provider<SolGame> game) {
        myGame = game;
    }

    @Override
    public void beginContact(Contact contact) {
        Object dataA = contact.getFixtureA().getBody().getUserData();
        Object dataB = contact.getFixtureB().getBody().getUserData();

        if (dataA instanceof EntityRef && dataB instanceof EntityRef) {
            EntityRef entityA = (EntityRef) dataA;
            EntityRef entityB = (EntityRef) dataB;
            entitySystemManager.sendEvent(new ContactEvent(entityB, contact), entityA);
            entitySystemManager.sendEvent(new ContactEvent(entityA, contact), entityB);
        }

        //TODO This is a patch to smooth over contact between an Entity and a Projectile. Once Projectile has been converted
        // to be an Entity, this can be removed.
        if (dataA instanceof EntityRef) {
            dataA = new SolObjectEntityWrapper((EntityRef) dataA);
        }
        if (dataB instanceof EntityRef) {
            dataB = new SolObjectEntityWrapper((EntityRef) dataB);
        }

        //TODO This is legacy code for handling contact with a Projectile, which currently is designed to work with SolObjects.
        // Once Projectile has been converted to be an entity, this should be refactored.
        SolObject firstSolObject = (SolObject) dataA;
        SolObject secondSolObject = (SolObject) dataB;
        boolean firstSolObjectIsProjectile = firstSolObject instanceof Projectile;
        if (!firstSolObjectIsProjectile && !(secondSolObject instanceof Projectile)) {
            return;
        }
        Projectile projectile = (Projectile) (firstSolObjectIsProjectile ? firstSolObject : secondSolObject);
        SolObject solObject = firstSolObjectIsProjectile ? secondSolObject : firstSolObject;
        projectile.setObstacle(solObject, myGame.get());
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

        Object dataA = contact.getFixtureA().getBody().getUserData();
        Object dataB = contact.getFixtureB().getBody().getUserData();

        Vector2 collPos = contact.getWorldManifold().getPoints()[0];
        float absImpulse = calcAbsImpulse(impulse);

        if (dataA instanceof EntityRef) {
            entitySystemManager.sendEvent(new ImpulseEvent(collPos, absImpulse), (EntityRef) dataA);

            //TODO This is a patch to smooth over contact between an entity and a SolObject.
            // Once every SolObject has been converted to an entity, this can be removed.
            dataA = new SolObjectEntityWrapper((EntityRef) dataA);
        }

        if (dataB instanceof EntityRef) {
            entitySystemManager.sendEvent(new ImpulseEvent(collPos, absImpulse), (EntityRef) dataB);

            //TODO This is a patch to smooth over contact between an entity and a SolObject.
            // Once every SolObject has been converted to an entity, this can be removed.
            dataB = new SolObjectEntityWrapper((EntityRef) dataB);
        }

        //TODO This is legacy code for handling contact between SolObjects.
        // Once every SolObject has been converted to an entity, this can be removed.
        SolObject firstSolObject = (SolObject) dataA;
        SolObject secondSolObject = (SolObject) dataB;
        if (firstSolObject instanceof Projectile && ((Projectile) firstSolObject).getConfig().density <= 0) {
            return;
        }
        if (secondSolObject instanceof Projectile && ((Projectile) secondSolObject).getConfig().density <= 0) {
            return;
        }
        firstSolObject.handleContact(secondSolObject, absImpulse, myGame.get(), collPos);
        secondSolObject.handleContact(firstSolObject, absImpulse, myGame.get(), collPos);
        myGame.get().getSpecialSounds().playColl(myGame.get(), absImpulse, firstSolObject, collPos);
        myGame.get().getSpecialSounds().playColl(myGame.get(), absImpulse, secondSolObject, collPos);

    }

    private float calcAbsImpulse(ContactImpulse impulse) {
        float absImpulse = 0;
        int pointCount = impulse.getCount();
        float[] normImpulses = impulse.getNormalImpulses();
        for (int i = 0; i < pointCount; i++) {
            float normImpulse = normImpulses[i];
            normImpulse = SolMath.abs(normImpulse);
            if (absImpulse < normImpulse) {
                absImpulse = normImpulse;
            }
        }
        return absImpulse;
    }
}
