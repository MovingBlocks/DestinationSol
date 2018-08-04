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
import com.badlogic.gdx.utils.JsonValue;
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
        Json json = Assets.getJson("core:types");
        JsonValue rootNode = json.getJsonValue();

        clip = load(rootNode.get("clip"), soundManager, cols);
        shield = load(rootNode.get("shield"), soundManager, cols);
        armor = load(rootNode.get("armor"), soundManager, cols);
        abilityCharge = load(rootNode.get("abilityCharge"), soundManager, cols);
        gun = load(rootNode.get("gun"), soundManager, cols);
        fixedGun = load(rootNode.get("fixedGun"), soundManager, cols);
        money = load(rootNode.get("money"), soundManager, cols);
        medMoney = load(rootNode.get("medMoney"), soundManager, cols);
        bigMoney = load(rootNode.get("bigMoney"), soundManager, cols);
        repair = load(rootNode.get("repair"), soundManager, cols);

        json.dispose();
    }

    private SolItemType load(JsonValue itemNode, OggSoundManager soundManager, GameColors cols) {
        Color color = cols.load(itemNode.getString("color"));
        OggSound pickUpSound = soundManager.getSound(itemNode.getString("pickUpSound"));
        float sz = itemNode.getFloat("sz");
        return new SolItemType(color, pickUpSound, sz);
    }
}
