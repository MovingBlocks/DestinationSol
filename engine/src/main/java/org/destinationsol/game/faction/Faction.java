/*
 * Copyright 2025 The Terasology Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.faction;

import org.joml.Math;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.nui.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A faction is an abstraction used to establish common diplomatic relations between aligned entities.
 * Factions hold an overall disposition towards other factions, which determines their approach towards entities
 * belonging to those factions. Factions holding a negative disposition towards others will be openly hostile.
 */
public class Faction {
    /**
     * The minimum possible reputation a faction can have with another.
     */
    public static final int MIN_REPUTATION = -100;
    /**
     * The maximum possible reputation a faction can have with another.
     */
    public static final int MAX_REPUTATION = 100;
    /**
     * The identifier used to uniquely identify this faction (e.g. "my-faction")
     */
    private final ResourceUrn id;
    /**
     * The name of this faction.
     */
    private final String name;
    /**
     * The faction's description.
     */
    private final String description;
    /**
     * The faction's primary colour.
     */
    private final Color colour;
    /**
     * Ship designs that can be produced by this faction.
     */
    private final List<ResourceUrn> shipDesigns;
    /**
     * The default standing that this faction has towards unknown factions.
     */
    private final int defaultDisposition;
    /**
     * The relations held between this faction and other factions.
     */
    private final Map<Faction, Integer> relations;
    /**
     * The impact that certain events will have on relations with another faction if they instigate a given event.
     * (For example, hitting the ship with a projectile loses reputation, having a negative impact.)
     * @see DefaultReputationEvent
     */
    private final Map<String, Integer> reputationImpacts;

    /**
     * Instantiates a new faction instance.
     * @param id the faction's id (this should be the urn corresponding to the faction's JSON definition file)
     * @param name the name of the faction
     * @param description a description for the faction
     * @param colour the faction's primary colour
     * @param defaultDisposition the faction's default disposition towards unknown factions
     * @param shipDesigns ship designs that can be built by this faction
     * @param reputationImpacts the impact certain events should have on relationships between this faction and others.
     */
    public Faction(ResourceUrn id, String name, String description, Color colour, int defaultDisposition,
                   List<ResourceUrn> shipDesigns, Map<String, Integer> reputationImpacts) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.colour = colour;
        this.defaultDisposition = Math.clamp(MIN_REPUTATION, MAX_REPUTATION, defaultDisposition);
        this.shipDesigns = shipDesigns;
        this.relations = new HashMap<>();
        this.reputationImpacts = reputationImpacts;
    }

    /**
     * Returns the faction's id.
     * @return the faction's id.
     */
    public ResourceUrn getId() {
        return id;
    }

    /**
     * Returns the faction's name.
     * @return the faction's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the faction's description.
     * @return the faction's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the faction's primary colour.
     * @return the faction's primary colour.
     */
    public Color getColour() {
        return colour;
    }

    /**
     * Returns a list of ship designs that can be manufactured by the faction.
     * @return a list of ship designs that this faction can make.
     */
    public List<ResourceUrn> getShipDesigns() {
        return shipDesigns;
    }

    /**
     * Gets the impact of an event on this faction's relationships with others.
     * @param event the event to query.
     * @return the change in reputation, if known, otherwise null.
     * @param <T> the type of event.
     */
    public <T extends Enum<T> & ReputationEvent> Integer getReputationImpact(T event) {
        return reputationImpacts.get(event.toString());
    }

    /**
     * Specifies whether a faction has formal relations with another (meaning they have an explicit reputation built-up).
     * @param faction the faction to check.
     * @return true, if the faction is known to this faction, otherwise false.
     */
    public boolean isAwareOf(Faction faction) {
        return relations.containsKey(faction);
    }

    /**
     * Returns the reputation held by another faction with this faction.
     * @param faction the faction to check.
     * @return the reputation value held with this faction.
     */
    public int getRelation(Faction faction) {
        if (faction == this) {
            return MAX_REPUTATION;
        }
        return relations.getOrDefault(faction, defaultDisposition);
    }

    /**
     * Sets the reputation held by this faction for another.
     * @param faction the faction to assign reputation with.
     * @param disposition the overall disposition of this faction towards the other.
     */
    public void setRelation(Faction faction, int disposition) {
        if (faction != this) {
            relations.put(faction, Math.clamp(MIN_REPUTATION, MAX_REPUTATION, disposition));
        }
    }
}
