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

package org.destinationsol.game.chunk;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;

import java.util.HashSet;
import java.util.Set;

public class ChunkManager {
    private static final int MAX_FILL_DIST = 1;
    private static final int MIN_REMOVE_DIST = MAX_FILL_DIST + 2;
    private static final int MAX_BG_FILL_DIST = 2;
    private static final int MIN_BG_REMOVE_DIST = MAX_BG_FILL_DIST + 1;

    private final Set<Vector2> myFilledChunks;
    private final Set<Vector2> myBackgroundFilledChunks;
    private final RemoveController myRemover;
    private final RemoveController myBackgroundRemover;
    private final ChunkFiller myFiller;

    private int myX;
    private int myY;

    public ChunkManager() {
        myFilledChunks = new HashSet<>();
        myBackgroundFilledChunks = new HashSet<>();
        myRemover = new MyRemover(MIN_REMOVE_DIST);
        myBackgroundRemover = new MyRemover(MIN_BG_REMOVE_DIST);
        myFiller = new ChunkFiller();
    }

    public void update(SolGame game) {
        Vector2 camPos = game.getCam().getPosition();
        boolean refill = updateCurrChunk(camPos);
        if (refill) {
            clearFarChunks(myFilledChunks, MIN_REMOVE_DIST);
            addNewChunks(myFilledChunks, MAX_FILL_DIST, game);
            clearFarChunks(myBackgroundFilledChunks, MIN_BG_REMOVE_DIST);
            addNewChunks(myBackgroundFilledChunks, MAX_BG_FILL_DIST, game);
        }
    }

    private boolean updateCurrChunk(Vector2 position) {
        int oldX = myX;
        int oldY = myY;
        myX = posToChunkIdx(position.x);
        myY = posToChunkIdx(position.y);
        return oldX != myX || oldY != myY;
    }

    private int posToChunkIdx(float v) {
        int i = (int) (v / Const.CHUNK_SIZE);
        if (v < 0) {
            i -= 1;
        }
        return i;
    }

    private void clearFarChunks(Set<Vector2> chunks, int dist) {
        chunks.removeIf(chunk -> isChunkFar((int) chunk.x, (int) chunk.y, dist));
    }

    private boolean isChunkFar(int x, int y, int dist) {
        return x <= myX - dist || myX + dist <= x || y <= myY - dist || myY + dist <= y;
    }

    private void addNewChunks(Set<Vector2> chunks, int dist, SolGame game) {
        maybeAddChunk(chunks, 0, 0, game);
        for (int i = -dist; i < dist + 1; i++) {
            for (int j = -dist; j < dist + 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                maybeAddChunk(chunks, i, j, game);
            }
        }
    }

    private void maybeAddChunk(Set<Vector2> chunks, int oX, int oY, SolGame game) {
        Vector2 v = SolMath.getVec(myX + oX, myY + oY);
        if (!chunks.contains(v)) {
            Vector2 chunk = new Vector2(v);
            chunks.add(chunk);
            boolean background = chunks == myBackgroundFilledChunks;
            myFiller.fill(game, chunk, background ? myBackgroundRemover : myRemover, background);
        }
        SolMath.free(v);
    }

    public boolean isInactive(Vector2 position, int dist) {
        int x = posToChunkIdx(position.x);
        int y = posToChunkIdx(position.y);
        return isChunkFar(x, y, dist);
    }

    private class MyRemover implements RemoveController {
        private final int myMinRemoveDist;

        public MyRemover(int minRemoveDist) {
            myMinRemoveDist = minRemoveDist;
        }

        @Override
        public boolean shouldRemove(Vector2 position) {
            return isInactive(position, myMinRemoveDist);
        }
    }

}
