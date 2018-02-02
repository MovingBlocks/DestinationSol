/*
 * Copyright 2017 MovingBlocks
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

import org.destinationsol.common.SolDescriptiveException;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.ship.SolShip;

public class Hero {
    private SolShip hero;
    private StarPort.Transcendent transcendentHero;
    private boolean isTranscendent;
    private final String transcendentNotExistingPropertyExceptionMessage = "Something is trying to get a property of hero that doesn't exist in transcendent state, while the hero is in transcendent state.";

    public Hero(SolShip hero, StarPort.Transcendent transcendentHero) {
        this.hero = hero;
        this.transcendentHero = transcendentHero;
        if (hero != null && transcendentHero != null) {
            throw new SolDescriptiveException("Something tries to set both hero and transcended hero. This should not be possible. Please report this.");
        }
        isTranscendent = transcendentHero != null;
    }

    public boolean isTranscendent() {
        return isTranscendent;
    }

    public Pilot getPilot(){
        if (isTranscendent) {
            throw new SolDescriptiveException(transcendentNotExistingPropertyExceptionMessage);
        }
        return hero.getPilot();
    }

    public SolShip getHero() {
        return hero;
    }

    public StarPort.Transcendent getTranscendentHero() {
        return transcendentHero;
    }


}
