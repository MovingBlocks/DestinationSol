/*
 * Copyright 2023 The Terasology Foundation
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

package org.destinationsol.game.tutorial.steps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.assets.Assets;
import org.destinationsol.game.Hero;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.Waypoint;

import javax.inject.Inject;

public class DestroyObjectsStep extends TutorialStep {
    @Inject
    protected SolGame game;
    protected final SolObject[] objects;
    protected final String message;
    protected final Waypoint[] objectWaypoints;

    @Inject
    protected DestroyObjectsStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public DestroyObjectsStep(SolObject[] objects, String message) {
        this.objects = objects;
        this.message = message;
        this.objectWaypoints = new Waypoint[objects.length];
    }

    @Override
    public void start() {
        setTutorialText(message);

        Hero hero = game.getHero();
        ObjectManager objectManager = game.getObjectManager();

        TextureAtlas.AtlasRegion targetWaypointTexture = Assets.getAtlasRegion("engine:mapObjects/beaconAttack");
        for (int objectNo = 0; objectNo < objects.length; objectNo++) {
            objectWaypoints[objectNo] = new Waypoint(objects[objectNo].getPosition(), Color.RED, targetWaypointTexture);
            hero.addWaypoint(objectWaypoints[objectNo]);
            objectManager.addObjDelayed(objectWaypoints[objectNo]);
        }
    }

    @Override
    public boolean checkComplete(float timeStep) {
        boolean allObjectsDestroyed = true;
        for (int objectNo = 0; objectNo < objects.length; objectNo++) {
            SolObject object = objects[objectNo];
            Waypoint waypoint = objectWaypoints[objectNo];

            waypoint.position.set(object.getPosition());
            if (object.shouldBeRemoved(game)) {
                game.getHero().removeWaypoint(waypoint);
                game.getObjectManager().removeObjDelayed(waypoint);
            } else {
                allObjectsDestroyed = false;
            }
        }
        return allObjectsDestroyed;
    }
}
