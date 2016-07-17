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

package org.destinationsol.game.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.files.FileManager;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.sound.OggSoundManager;

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
        JsonReader r = new JsonReader();
        FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("types.json");
        JsonValue parsed = r.parse(configFile);
        clip = load("clip", soundManager, parsed, cols);
        shield = load("shield", soundManager, parsed, cols);
        armor = load("armor", soundManager, parsed, cols);
        abilityCharge = load("abilityCharge", soundManager, parsed, cols);
        gun = load("gun", soundManager, parsed, cols);
        fixedGun = load("fixedGun", soundManager, parsed, cols);
        money = load("money", soundManager, parsed, cols);
        medMoney = load("medMoney", soundManager, parsed, cols);
        bigMoney = load("bigMoney", soundManager, parsed, cols);
        repair = load("repair", soundManager, parsed, cols);
    }

    private SolItemType load(String name, OggSoundManager soundManager, JsonValue parsed, GameColors cols) {
        JsonValue node = parsed.get(name);
        Color color = cols.load(node.getString("color"));
        OggSound pickupSound = soundManager.getSound(node.getString("pickUpSound"));
        float sz = node.getFloat("sz");
        return new SolItemType(color, pickupSound, sz);
    }
}
