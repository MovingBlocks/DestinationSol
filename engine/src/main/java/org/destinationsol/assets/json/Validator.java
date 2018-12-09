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
package org.destinationsol.assets.json;

import org.destinationsol.assets.Assets;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validator {

    static Logger logger = LoggerFactory.getLogger(Validator.class);

    public static void validate(JSONObject json, String schemaPath) {
        JSONObject schema;

        try {
            schema = Assets.getJson(schemaPath).getJsonValue();
        } catch (RuntimeException e) {
            logger.warn("Json Schema " + schemaPath + " not found!", e);
            return;
        }

        Schema schemaValidator = SchemaLoader.load(schema);
        schemaValidator.validate(json);
    }

}
