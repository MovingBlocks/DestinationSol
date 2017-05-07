/*
 * Copyright 2015 MovingBlocks
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

package org.destinationsol.game.particle;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import org.destinationsol.files.FileManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EffectType {
    public final boolean continuous;
    public final boolean additive;
    private final ParticleEmitter myEmitter;

    public EffectType(String fileName) {
        myEmitter = loadEmitter(fileName);
        continuous = myEmitter.isContinuous();
        myEmitter.setContinuous(false);
        additive = myEmitter.isAdditive();
        myEmitter.setAdditive(false);
    }

    private static ParticleEmitter loadEmitter(final String fileName) {
        FileHandle effectFile = FileManager.getInstance().getAssetsDirectory().child("emitters").child(fileName + ".p");
        InputStream input = effectFile.read();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input), 512);
        ParticleEmitter emitter;
        try {
            emitter = new ParticleEmitter(reader);
        } catch (IOException ex) {
            throw new AssertionError("Error loading effect: " + effectFile, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ignore) {
            }
        }
        emitter.flipY();
        return emitter;
    }

    public ParticleEmitter newEmitter() {
        return new ParticleEmitter(myEmitter);
    }
}
