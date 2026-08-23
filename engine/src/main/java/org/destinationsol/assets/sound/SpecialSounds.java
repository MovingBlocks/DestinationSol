/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.assets.sound;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.Nullable;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.material.MaterialType;
import org.terasology.gestalt.entitysystem.entity.EntityRef;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;

public class SpecialSounds {

    /**
     * Collisions gentler than this do not make a sound.
     */
    private static final float MIN_COLLISION_IMPULSE = .1f;

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

    @Inject
    public SpecialSounds(OggSoundManager soundManager) {
        // OggSound
        metalColl = soundManager.getSound("core:metalCollision");
        metalEnergyHit = soundManager.getSound("core:empty");
        rockColl = soundManager.getSound("core:rockCollision");
        rockEnergyHit = soundManager.getSound("core:empty");
        asteroidCrack = soundManager.getSound("core:asteroidCrack");
        shipExplosion = soundManager.getSound("core:shipExplosion");
        forceBeaconWork = soundManager.getSound("core:forceBeaconWork");
        doorMove = soundManager.getSound("core:controlEnabled");
        abilityRecharged = soundManager.getSound("core:abilityRecharged");
        abilityRefused = soundManager.getSound("core:abilityRefused");
        controlDisabled = soundManager.getSound("core:controlDisabled");
        controlEnabled = soundManager.getSound("core:controlEnabled");
        lootThrow = soundManager.getSound("core:rocketLauncherShoot");
        transcendentCreated = soundManager.getSound("core:teleport");
        transcendentFinished = soundManager.getSound("core:teleport");

        // OggSoundSet
        metalBulletHit = new OggSoundSet(soundManager, Arrays.asList("core:metalBulletHit0", "core:metalBulletHit1", "core:metalBulletHit2"), 1.1f);
        rockBulletHit = new OggSoundSet(soundManager, Arrays.asList("core:rockBulletHit0", "core:rockBulletHit1"));
        burning = new OggSoundSet(soundManager, Arrays.asList("core:burning2", "core:burning3", "core:burning4"));
        transcendentMove = new OggSoundSet(soundManager, Arrays.asList("core:transcendentMove", "core:transcendentMove2", "core:transcendentMove3", "core:transcendentMove4"));
    }

    /**
     * The sound made when something of the given material is hit by the given kind of damage.
     * <p>
     * This is the single place where hit sounds are selected: both the {@link SolObject} and the entity code paths
     * resolve their sound through it.
     *
     * @param materialType the material of the thing being hit, or null if it is not made of a known material
     * @param dmgType      the kind of damage being dealt, or null if it is not known
     * @return the sound to play, or empty if no sound is defined for that combination
     */
    public Optional<PlayableSound> hitSound(@Nullable MaterialType materialType, @Nullable DmgType dmgType) {
        if (materialType == null || dmgType == null) {
            return Optional.empty();
        }
        boolean metal = materialType == MaterialType.METAL;
        if (dmgType == DmgType.ENERGY) {
            return Optional.of(metal ? metalEnergyHit : rockEnergyHit);
        }
        if (dmgType == DmgType.BULLET) {
            return Optional.of(metal ? metalBulletHit : rockBulletHit);
        }
        return Optional.empty();
    }

    /**
     * The sound made when something of the given material collides with something else.
     * <p>
     * This is the single place where collision sounds are selected: both the {@link SolObject} and the entity code
     * paths resolve their sound through it.
     *
     * @param materialType the material of the colliding thing, or null if it is not made of a known material
     * @return the sound to play, or empty if no sound is defined for that material
     */
    public Optional<PlayableSound> collisionSound(@Nullable MaterialType materialType) {
        if (materialType == null) {
            return Optional.empty();
        }
        return Optional.of(materialType == MaterialType.METAL ? metalColl : rockColl);
    }

    public void playHit(SolGame game, SolObject o, Vector2 position, DmgType dmgType) {
        if (o == null) {
            return;
        }
        hitSound(materialTypeOf(o), dmgType)
                .ifPresent(sound -> game.getSoundManager().play(game, sound, position, o));
    }

    /**
     * The entity-based counterpart of {@link #playHit(SolGame, SolObject, Vector2, DmgType)}. An entity carries its
     * material as a component rather than through {@link SolObject#isMetal()}, and its position is not derivable from
     * the sound's bearer, so both are passed in; sound selection is shared.
     *
     * @param game         Game to play the sound in.
     * @param entity       The entity that was hit; the sound is attached to it for looping and debug purposes.
     * @param position     Where the hit happened.
     * @param dmgType      The kind of damage dealt.
     * @param materialType The material the entity is made of.
     */
    public void playHit(SolGame game, EntityRef entity, Vector2 position, @Nullable DmgType dmgType, @Nullable MaterialType materialType) {
        hitSound(materialType, dmgType)
                .ifPresent(sound -> game.getSoundManager().play(game, sound, position, entity));
    }

    public void playColl(SolGame game, float absImpulse, SolObject o, Vector2 position) {
        if (o == null || absImpulse < MIN_COLLISION_IMPULSE) {
            return;
        }
        collisionSound(materialTypeOf(o))
                .ifPresent(sound -> game.getSoundManager().play(game, sound, position, o, absImpulse * Const.IMPULSE_TO_COLL_VOL));
    }

    /**
     * The entity-based counterpart of {@link #playColl(SolGame, float, SolObject, Vector2)}. See
     * {@link #playHit(SolGame, EntityRef, Vector2, DmgType, MaterialType)} for why the material and position are
     * passed in rather than read off the sound's bearer.
     *
     * @param game         Game to play the sound in.
     * @param absImpulse   The magnitude of the impulse of the collision.
     * @param entity       The entity that collided; the sound is attached to it for looping and debug purposes.
     * @param position     Where the collision happened.
     * @param materialType The material the entity is made of.
     */
    public void playColl(SolGame game, float absImpulse, EntityRef entity, Vector2 position, @Nullable MaterialType materialType) {
        if (absImpulse < MIN_COLLISION_IMPULSE) {
            return;
        }
        collisionSound(materialType)
                .ifPresent(sound -> game.getSoundManager().play(game, sound, position, entity, absImpulse * Const.IMPULSE_TO_COLL_VOL));
    }

    /**
     * Bridges the {@link SolObject} representation of a material - a nullable {@link Boolean} "is it metal?" - to the
     * {@link MaterialType} used by entities.
     *
     * @return the object's material, or null if the object does not declare one
     */
    @Nullable
    private static MaterialType materialTypeOf(SolObject solObject) {
        Boolean metal = solObject.isMetal();
        if (metal == null) {
            return null;
        }
        return metal ? MaterialType.METAL : MaterialType.ROCK;
    }
}
