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

    private final Set<Vector2> filledChunks;
    private final Set<Vector2> backgroundFilledChunks;
    private final RemoveController removeController;
    private final RemoveController backgroundRemoveController;
    private final ChunkFiller filler;

    private int x;
    private int y;

    public ChunkManager() {
        filledChunks = new HashSet<>();
        backgroundFilledChunks = new HashSet<>();
        removeController = new MyRemover(MIN_REMOVE_DIST);
        backgroundRemoveController = new MyRemover(MIN_BG_REMOVE_DIST);
        filler = new ChunkFiller();
    }

    public void update(SolGame game) {
        Vector2 camPos = game.getCam().getPosition();
        boolean refill = updateCurrChunk(camPos);
        if (refill) {
            clearFarChunks(filledChunks, MIN_REMOVE_DIST);
            addNewChunks(filledChunks, MAX_FILL_DIST, game);
            clearFarChunks(backgroundFilledChunks, MIN_BG_REMOVE_DIST);
            addNewChunks(backgroundFilledChunks, MAX_BG_FILL_DIST, game);
        }
    }

    private boolean updateCurrChunk(Vector2 position) {
        int oldX = x;
        int oldY = y;
        x = posToChunkIdx(position.x);
        y = posToChunkIdx(position.y);
        return oldX != x || oldY != y;
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

    private boolean isChunkFar(int otherX, int otherY, int dist) {
        return otherX <= x - dist || x + dist <= otherX || otherY <= y - dist || y + dist <= otherY;
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
        Vector2 v = SolMath.getVec(x + oX, y + oY);
        if (!chunks.contains(v)) {
            Vector2 chunk = new Vector2(v);
            chunks.add(chunk);
            boolean background = chunks == backgroundFilledChunks;
            filler.fill(game, chunk, background ? backgroundRemoveController : removeController, background);
        }
        SolMath.free(v);
    }

    private boolean isInactive(Vector2 position, int dist) {
        int x = posToChunkIdx(position.x);
        int y = posToChunkIdx(position.y);
        return isChunkFar(x, y, dist);
    }

    private class MyRemover implements RemoveController {
        private final int myMinRemoveDist;

        MyRemover(int minRemoveDist) {
            myMinRemoveDist = minRemoveDist;
        }

        @Override
        public boolean shouldRemove(Vector2 position) {
            return isInactive(position, myMinRemoveDist);
        }
    }

}
