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
package org.destinationsol.assets.json;

import com.badlogic.gdx.utils.JsonValue;

public class JsonSanitizer {
    public static void sanitizeHull(JsonValue rootNode, String shipName) {
        final String errMsg1 = "Your " + shipName + " ship's JSON is missing a required field \"";
        final String errMsg2 = "\". This field should denote ";
        String fieldName = "size";
        if (!(rootNode.has(fieldName) && rootNode.get(fieldName).isNumber())) {
            throw new SolJsonException(errMsg1 + fieldName + errMsg2 + "how much the ship's image is scaled in-game. Its value has to be a number.");
        }
        fieldName = "maxLife";
        if (!(rootNode.has(fieldName) && rootNode.get(fieldName).isNumber())) {
            throw new SolJsonException(errMsg1 + fieldName + errMsg2 + "at what value your ships health will be capped. Its value has to be a number.");
        }
        // Not sanitizing "e1Pos" since it has a default value of "0 0"
        // Not sanitizing "e2Pos" since it has a default value of "0 0"
        // Not sanitizing "lightSrcPoss" since it has a default value of "[]"
        // Not sanitizing "hasBase" since it has a default value of false
        // Not sanitizing "forceBeaconPoss" since it has a default value of "[]"
        // Not sanitizing "doorPoss" since it has a default value of "[]"
        fieldName = "type";
        if (!(rootNode.has(fieldName) && rootNode.get(fieldName).isString())) {
            throw new SolJsonException(errMsg1 + fieldName + errMsg2 + "the type of the ship. Its value has to be one of \"std\", \"big\", \"station\".");
        }
        // Not sanitizing "engine" since it has a default value
        // Not sanitizing "ability" since it has a default value
        // Not sanitizing "displayName" since it has a default value of "---"
        // Not sanitizing "price" since it has a default value of 0
        // Not sanitizing "hirePrice" since it has a default value of 0
        fieldName = "rigidBody";
        if (!(rootNode.has(fieldName) && rootNode.get(fieldName).isObject())) {
            throw new SolJsonException(errMsg1 + fieldName + errMsg2 + "physical body of the ship. It has to be an object.");
        }
        sanitizeRigidBody(rootNode.get("rigidBody"), shipName);
        fieldName = "gunSlots";
        if (!(rootNode.has(fieldName) && rootNode.get(fieldName).isArray())) {
            throw new SolJsonException(errMsg1 + fieldName + errMsg2 + "position and types of gunSlots. It has to be an array of objects.");
        }
    }

    public static void sanitizeRigidBody(JsonValue rbObject, String shipName) {
        if (!(rbObject.has("origin") && rbObject.get("origin").isObject())) {
            throw new SolJsonException("Your " + shipName + " ship's JSON's rigidBody field is missing an \"origin\" field. It has to be an object.");
        }
        if (!(rbObject.get("origin").has("x") && rbObject.get("origin").get("x").isNumber())) {
            throw new SolJsonException("Your " + shipName + " ship's JSON's rigidBody's origin's field is missing an \"x\" field. It has to be a number.");
        }
        if (!(rbObject.get("origin").has("y") && rbObject.get("origin").get("y").isNumber())) {
            throw new SolJsonException("Your " + shipName + " ship's JSON's rigidBody's origin's field is missing an \"y\" field. It has to be a number.");
        }
        if (!(rbObject.has("polygons") && rbObject.has("circles") && rbObject.has("shapes"))) { // simplified sanitization
            throw new SolJsonException("Your " + shipName + " ship's JSON's rigidBody's field is missing either a \"polygons\", \"shapes\" or \"circles\" field. Try reexporting the rigidBody form box2d.");
        }
    }
}
