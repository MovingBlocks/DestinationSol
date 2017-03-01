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

package org.destinationsol.game.sound;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

import java.util.Arrays;

public class SpecialSounds {

    public final PlayableSound metalColl;
    public final PlayableSound metalEnergyHit;
    public final PlayableSound rockColl;
    public final PlayableSound rockEnergyHit;
    public final PlayableSound asteroidCrack;
    public final PlayableSound shipExplosion;
    public final PlayableSound forceBeaconWork;
    public final PlayableSound doorMove;
    public final PlayableSound abilityRecharged;
    public final PlayableSound abilityRefused;
    public final PlayableSound controlDisabled;
    public final PlayableSound controlEnabled;
    public final PlayableSound lootThrow;
    public final PlayableSound transcendentCreated;
    public final PlayableSound transcendentFinished;

    public final PlayableSound metalBulletHit;
    public final PlayableSound rockBulletHit;
    public final PlayableSound burning;
    public final PlayableSound transcendentMove;

    public SpecialSounds(OggSoundManager soundManager) {
        // OggSound
        metalColl = soundManager.getSound("Core:metalCollision");
        metalEnergyHit = soundManager.getSound("Core:empty");
        rockColl = soundManager.getSound("Core:rockCollision");
        rockEnergyHit = soundManager.getSound("Core:empty");
        asteroidCrack = soundManager.getSound("Core:asteroidCrack");
        shipExplosion = soundManager.getSound("Core:shipExplosion");
        forceBeaconWork = soundManager.getSound("Core:forceBeaconWork");
        doorMove = soundManager.getSound("Core:controlEnabled");
        abilityRecharged = soundManager.getSound("Core:abilityRecharged");
        abilityRefused = soundManager.getSound("Core:abilityRefused");
        controlDisabled = soundManager.getSound("Core:controlDisabled");
        controlEnabled = soundManager.getSound("Core:controlEnabled");
        lootThrow = soundManager.getSound("Core:rocketLauncherShoot");
        transcendentCreated = soundManager.getSound("Core:teleport");
        transcendentFinished = soundManager.getSound("Core:teleport");

        // OggSoundSet
        metalBulletHit = new OggSoundSet(soundManager, Arrays.asList("Core:metalBulletHit0", "Core:metalBulletHit1", "Core:metalBulletHit2"), 1.1f);
        rockBulletHit = new OggSoundSet(soundManager, Arrays.asList("Core:rockBulletHit0", "Core:rockBulletHit1"));
        burning = new OggSoundSet(soundManager, Arrays.asList("Core:burning2", "Core:burning3", "Core:burning4"));
        transcendentMove = new OggSoundSet(soundManager, Arrays.asList("Core:transcendentMove", "Core:transcendentMove2", "Core:transcendentMove3", "Core:transcendentMove4"));
    }

    public PlayableSound hitSound(boolean forMetal, DmgType dmgType) {
        if (dmgType == DmgType.ENERGY) {
            return forMetal ? metalEnergyHit : rockEnergyHit;
        }
        if (dmgType == DmgType.BULLET) {
            return forMetal ? metalBulletHit : rockBulletHit;
        }
        return null;
    }

    public void playHit(SolGame game, SolObject o, Vector2 pos, DmgType dmgType) {
        if (o == null) {
            return;
        }
        Boolean metal = o.isMetal();
        if (metal == null) {
            return;
        }
        PlayableSound sound = hitSound(metal, dmgType);
        if (sound == null) {
            return;
        }
        game.getSoundManager().play(game, sound, pos, o);
    }

    public void playColl(SolGame game, float absImpulse, SolObject o, Vector2 pos) {
        if (o == null || absImpulse < .1f) {
            return;
        }
        Boolean metal = o.isMetal();
        if (metal == null) {
            return;
        }
        game.getSoundManager().play(game, metal ? metalColl : rockColl, pos, o, absImpulse * Const.IMPULSE_TO_COLL_VOL);
    }
}
