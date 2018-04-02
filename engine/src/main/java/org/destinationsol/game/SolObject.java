/*
 * Copyright 2017 MovingBlocks
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
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.Nullable;
import org.destinationsol.game.drawables.Drawable;

import java.util.List;


/**
 * Generally speaking, SolObjects are all the game's foreground objects that are in the proximity of player/visible screen.
 * <p>
 * {@code SolObject} can be generally crashed into. Background objects aren't usually {@code SolObject}s, but they are
 * rather just sprites rendered using some background manager. {@code SolObject} that gets too far from player/camera is
 * usually transformed to {@link FarObject}, which has less runtime executable code and thus is faster and can be
 * recreated back into {@code SolObject}. Some gameplay-wise unimportant {@code SolObjects}s are also just removed when
 * they get too far from player/camera, and new ones are generated as needed.
 */
public interface SolObject {

    /**
     * Called on every game's frame, allows for handling of object-specific runtime needs.
     * <p>
     * This method can be used for instance for handling pull of loot towards ships, flickering of lights, aiming
     * crosses for ships and stuff alike.
     *
     * @param game Game this object belongs to.
     */
    void update(SolGame game);

    /**
     * Denotes whether the object should be removed as of the time of calling.
     * <p>
     * Object should usually be removed when its health reaches zero, or, for instance, if the object is projectile, it
     * reaches its target. This method should not handle removal of object for optimization purposes when it gets too
     * far away, unless the object is specifically designed to exist only in close proximity of player, like some sort
     * of kinetic defense. Removal for optimization purposes is handled in {@link #toFarObject()}.
     *
     * @param game Game this object belongs to.
     * @return Boolean denoting whether the object should be removed.
     */
     boolean shouldBeRemoved(SolGame game);

    /**
     * Called whenever an object is due to be removed from game.
     * <p>
     * This method should handle things like throwing out {@link Loot} when ships are destroyed, or creating smaller
     * asteroids when a big {@link Asteroid} is destroyed. This method is also responsible for freeing its resources,
     * when such is applicable. Note that this method is also called when an object is due to be removed for
     * optimization purposes, and thus you need to explicitly check for {@link #shouldBeRemoved(SolGame)} if you have
     * some code specific to destruction of object.
     *
     * @param game Game this object belongs to.
     */
    void onRemove(SolGame game);

    /**
     * Called whenever this object should receive damage.
     * <p>
     * Whenever the object is hit by some projectile or other sort of damage dealing thing, this method is called on the
     * object to allow it to perform its damage handling. This usually means just subtracting the damage from object's
     * health and playing a hit sound, but if the object has no health pool or should be otherwise indestructible, or
     * invincible against some types of damage, this method can be freely left blank.
     *
     * @param dmg      Damage the object receives.
     * @param game     Game this object belongs to.
     * @param position Position the object was hit at, if hit by point-based damage. Null if not applicable, such as fire.
     * @param dmgType  Type of the damage object receives.
     */
    void receiveDmg(float dmg, SolGame game, @Nullable Vector2 position, DmgType dmgType);

    /**
     * Denotes whether this object is affected by gravity, this is usually constant.
     *
     * @return True if object is affected by gravity, false otherwise.
     */
    boolean receivesGravity();

    /**
     * Applies external force to object.
     * <p>
     * This method usually just scales the {@code force} by objects mass if {@code acc} is set, and then passes the
     * force to object's internal {@link Body}.
     *
     * @param force Force to apply to the object.
     * @param game  Game this object belongs to.
     * @param acc   Whether this objects is accelerating by the force, such as in gravity.
     */
    void receiveForce(Vector2 force, SolGame game, boolean acc);

    /**
     * Returns object's position.
     * <p>
     * This method usually just returns the vector retrieved from object's internal {@link Body}.
     *
     * @return Position of the object.
     */
    Vector2 getPosition();

    /**
     * Called when an object is too far from player/camera, and is thus to be converted to {@link FarObject}.
     * <p>
     * This method is used for optimization of game's objects, by converting to their more resource friendly versions
     * when they get too far from player/camera. The {@code FarObject} returned by this method should be usable to get
     * copy of this object back in reverse. If null is returned by this method, this object is designated to be removed
     * when it gets too far from player/camera, and instead of recreating from {@code FarObject}, to be generated again.
     *
     * @return FarObject representation of this object, or null if object is to be removed.
     */
    FarObject toFarObject();

    /**
     * Returns list of all {@link Drawable Drawables} this object has assigned.
     *
     * @return List of drawables this object has.
     */
    List<Drawable> getDrawables();

    /**
     * Returns object's angle.
     * <p>
     * This method usually just returns the angle retrieved from object's internal {@link Body}.
     *
     * @return Angle of this object, in degrees.
     */
    float getAngle();

    /**
     * Returns object's speed.
     * <p>
     * This method usually just returns the vector retrieved from object's internal {@link Body}.
     *
     * @return Speed of this object.
     */
    Vector2 getSpeed();

    /**
     * Called whenever this object touches another object, and can be used for custom collisions.
     * <p>
     * This method is for instance used for collecting {@link Loot} by ships when these two touch. This method is not to
     * be used for handling of applying force between the two objects nor playing any collision sounds, unless the
     * object is specifically created to do so. This method should however apply collision damage. This method can be
     * freely left blank if there is no custom contact handling.
     *
     * @param other      Object this object touches.
     * @param absImpulse Impulse the objects apply to each other.
     * @param game       Game this object belongs to.
     * @param collPos    Position where the two objects touch.
     */
    void handleContact(SolObject other, float absImpulse, SolGame game, Vector2 collPos);

    /**
     * Used for retrieval of object's debug string.
     * <p>
     * Any kind of information can be used in the debug string, if you don't need/want to display any debug information,
     * you can freely have this method return null. To display debug strings in-game, set the flag {@link DebugOptions#OBJ_INFO}.
     * These strings should then be rendered in the proximity of their objects.
     *
     * @return Debug string with information about the object.
     */
    String toDebugString();

    /**
     * Denotes whether this object is meant to behave as if made from metallic material.
     * <p>
     * This method is used mainly for choosing the sound to play after collision. Value true will play metallic sound,
     * false will play rocky sound. Value of null means no sound will be played.
     *
     * @return Whether this object is made from metal.
     */
    //TODO allow for more complex collision sounds. Or even better, completely rework the material system.
    Boolean isMetal();

    /**
     * Denotes whether this object has a {@link Body} associated with it.
     * <p>
     * Generally, everything that can be touched has its {@code Body}.
     *
     * @return True if this object has {@code Body} associated, false otherwise.
     */
    boolean hasBody();
}
