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
package org.destinationsol.engine;

import com.badlogic.gdx.Gdx;
import org.destinationsol.Const;
import org.destinationsol.GameState;
import org.destinationsol.ModuleManager;
import org.destinationsol.SolApplication;
import org.destinationsol.game.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetTypeManager;
import org.terasology.entitysystem.core.EntityManager;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SolEngine implements GameEngine {
    private static final Logger logger = LoggerFactory.getLogger(SolEngine.class);

    private final EntityManager entityManager;
    private final Context context;
    private float timeAccumulator = 0;
    private float timeStep = 1;
    private float epoch = 0;
    private GameState currentState;

    public SolEngine(EngineFactory engineFactory){
        this.entityManager = engineFactory.entityManager();
        this.context = engineFactory.context();
        configureModules(engineFactory.assetTypeManager());
        setScaleStep(1.0f);
    }

    private void configureModules(AssetTypeManager assetTypeManager){
        ModuleManager moduleManager = new ModuleManager();

    }

    @Override
    public void initialize() {

    }

    @Override
    public void changeState(GameState newState) {
        if (currentState != null) {
            currentState.dispose();
        }
        currentState = newState;
        newState.init(this);

    }

    public void setScaleStep(float timeFactor){
        timeStep = Const.REAL_TIME_STEP * timeFactor;
    }

    @Override
    public boolean update() {
        if(currentState == null){
            return false;
        }
        timeAccumulator += Gdx.graphics.getDeltaTime();

        while (timeAccumulator > Const.REAL_TIME_STEP) {
            epoch += timeStep;
            currentState.update(timeStep);
            timeAccumulator -= Const.REAL_TIME_STEP;
        }
        return true;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public GameState getState() {
        return currentState;
    }

    @Override
    public Context context() {
        return null;
    }


}
