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
package org.destinationsol.game.sound;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.Nullable;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.terasology.gestalt.entitysystem.entity.EntityRef;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DebugHintDrawer {
    private final Map<SolObject, DebugHint> tracedSolObjectNotes;
    private final Map<Vector2, DebugHint> freeNotes;
    private final Map<EntityRef, DebugHint> tracedEntityNotes;

    public DebugHintDrawer() {
        tracedSolObjectNotes = new HashMap<>();
        freeNotes = new HashMap<>();
        tracedEntityNotes = new HashMap<>();
    }

    public void add(@Nullable SolObject owner, Vector2 position, String value) {
        DebugHint dh;
        if (owner == null) {
            dh = freeNotes.computeIfAbsent(position, p -> new DebugHint(null, null, p));
        } else {
            dh = tracedSolObjectNotes.computeIfAbsent(owner, o -> new DebugHint(o, null, o.getPosition()));
        }
        dh.add(value);
    }

    public void add(EntityRef entity, Vector2 position, String value) {
        DebugHint debugHint;
        debugHint = tracedEntityNotes.computeIfAbsent(entity, entityRef -> new DebugHint(null, entityRef, position));
        debugHint.add(value);
    }

    public void update(SolGame game) {
        updateEach(game, tracedSolObjectNotes.values().iterator());
        updateEach(game, freeNotes.values().iterator());
        updateEach(game, tracedEntityNotes.values().iterator());
    }

    private void updateEach(SolGame game, Iterator<DebugHint> it) {
        while (it.hasNext()) {
            DebugHint n = it.next();
            n.update(game);
            if (n.shouldRemove()) {
                it.remove();
            }
        }
    }

    public void draw(GameDrawer drawer, SolGame solCam) {
        for (DebugHint n : tracedSolObjectNotes.values()) {
            n.draw(drawer, solCam);
        }
        for (DebugHint n : freeNotes.values()) {
            n.draw(drawer, solCam);
        }
        for (DebugHint n : tracedEntityNotes.values()) {
            n.draw(drawer, solCam);
        }
    }

}
