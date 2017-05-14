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

package org.destinationsol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;

public class TextureChecker {
    private final ArrayList<String> myCollected = new ArrayList<String>();
    private Texture myCurr = null;
    private int myAwait;

    public void onString(Texture texture) {
        evt(texture, "text");
    }

    private void evt(Texture texture, String name) {
        if (texture == null || name == null || name.isEmpty()) {
            throw new AssertionError("null atlas or no atlas name");
        }
        if (myAwait > 0) {
            return;
        }
        if (texture.equals(myCurr)) {
            return;
        }
        myCollected.add(name);
        myCurr = texture;
    }

    public void onReg(TextureAtlas.AtlasRegion tr) {
        evt(tr.getTexture(), tr.name);
    }

    public void onSprite(Texture texture, TextureAtlas.AtlasRegion tex) {
        evt(texture, tex.name);
    }

    public void onEnd() {
        if (myAwait == 0) {
            myCollected.clear();
            myCurr = null;
            myAwait = 120;
            return;
        }
        if (myAwait > 0) {
            myAwait--;
        }
    }
}
