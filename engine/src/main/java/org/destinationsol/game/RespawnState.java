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
package org.destinationsol.game;

import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.ArrayList;
import java.util.List;

public class RespawnState {

    private boolean isPlayerRespawned;
    private final List<SolItem> respawnItems = new ArrayList<>();
    private HullConfig respawnHull;
    private float respawnMoney;

    public boolean isPlayerRespawned() {
        return isPlayerRespawned;
    }

    public void setPlayerRespawned(boolean playerRespawned) {
        isPlayerRespawned = playerRespawned;
    }

    public List<SolItem> getRespawnItems() {
        return respawnItems;
    }

    public HullConfig getRespawnHull() {
        return respawnHull;
    }

    public void setRespawnHull(HullConfig respawnHull) {
        this.respawnHull = respawnHull;
    }

    public float getRespawnMoney() {
        return respawnMoney;
    }

    public void setRespawnMoney(float respawnMoney) {
        this.respawnMoney = respawnMoney;
    }
}
