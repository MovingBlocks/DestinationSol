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
import org.destinationsol.common.SolException;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validator {

    static Logger logger = LoggerFactory.getLogger(Validator.class);

    public static JSONObject getValidatedJSON(String jsonPath, String schemaPath) {
        Json json = Assets.getJson(jsonPath);
        JSONObject jsonObject = json.getJsonValue();
        JSONObject schema;

        try {
            schema = Assets.getJson(schemaPath).getJsonValue();
        } catch (RuntimeException e) {
            //Checks if the RTE is for file not found
            if (e.getMessage().equals("Json " + schemaPath + " not found!")) {
                logger.warn("Json Schema " + schemaPath + " not found!");

                json.dispose();
                return jsonObject;
            }
            throw e;
        }

        Schema schemaValidator = SchemaLoader.load(schema);
        try {
            schemaValidator.validate(jsonObject);
        } catch (ValidationException e) {
            throw new SolException("JSON \"" + jsonPath + "\" could not be validated against schema \"" + schemaPath + "\"." + e.getErrorMessage());
        }

        json.dispose();

        return jsonObject;
    }

}
