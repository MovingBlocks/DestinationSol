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
package org.destinationsol.game.item;

import com.badlogic.gdx.graphics.Color;
import org.destinationsol.assets.json.Validator;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.json.Json;
import org.destinationsol.game.GameColors;

public class SolItemTypes {
    public final SolItemType clip;
    public final SolItemType shield;
    public final SolItemType armor;
    public final SolItemType abilityCharge;
    public final SolItemType gun;
    public final SolItemType money;
    public final SolItemType medMoney;
    public final SolItemType bigMoney;
    public final SolItemType repair;
    public final SolItemType fixedGun;

    public SolItemTypes(OggSoundManager soundManager, GameColors cols) {
        JSONObject rootNode = Validator.getValidatedJSON("core:types", "engine:schemaTypes");

        clip = load(rootNode.getJSONObject("clip"), soundManager, cols);
        shield = load(rootNode.getJSONObject("shield"), soundManager, cols);
        armor = load(rootNode.getJSONObject("armor"), soundManager, cols);
        abilityCharge = load(rootNode.getJSONObject("abilityCharge"), soundManager, cols);
        gun = load(rootNode.getJSONObject("gun"), soundManager, cols);
        fixedGun = load(rootNode.getJSONObject("fixedGun"), soundManager, cols);
        money = load(rootNode.getJSONObject("money"), soundManager, cols);
        medMoney = load(rootNode.getJSONObject("medMoney"), soundManager, cols);
        bigMoney = load(rootNode.getJSONObject("bigMoney"), soundManager, cols);
        repair = load(rootNode.getJSONObject("repair"), soundManager, cols);
    }

    private SolItemType load(JSONObject itemNode, OggSoundManager soundManager, GameColors cols) {
        Color color = cols.load(itemNode.getString("color"));
        OggSound pickUpSound = soundManager.getSound(itemNode.getString("pickUpSound"));
        float sz = (float) itemNode.getDouble("sz");
        return new SolItemType(color, pickUpSound, sz);
    }
}
