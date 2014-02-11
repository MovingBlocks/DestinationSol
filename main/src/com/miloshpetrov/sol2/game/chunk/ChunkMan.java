package com.miloshpetrov.sol2.game.chunk;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.RemoveController;
import com.miloshpetrov.sol2.game.SolGame;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChunkMan {
  private final Set<Vector2> myActiveChunks;
  private final RemoveController myRemover;
  private final ChunkFiller myFiller;

  public ChunkMan() {
    myActiveChunks = new HashSet<Vector2>();
    myRemover = new MyRemover();
    myFiller = new ChunkFiller();
  }

  public void update(SolGame game) {
    Vector2 camPos = game.getCam().getPos();
    Vector2 currChunk = getCurrChunk(camPos);
    removeFarChunks(currChunk, game);
    addNewChunks(currChunk, game);
  }

  private void addNewChunks(Vector2 currChunk, SolGame game) {
    maybeAddChunk(currChunk, 0, 0, game);
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        if (i == 0 && j == 0) continue;
        maybeAddChunk(currChunk, i, j, game);
      }
    }
  }

  private void maybeAddChunk(Vector2 currChunk, int oX, int oY, SolGame game) {
    Vector2 v = SolMath.getVec(currChunk.x + oX * Const.CHUNK_SIZE, currChunk.y + oY * Const.CHUNK_SIZE);
    if (!myActiveChunks.contains(v)) {
      Vector2 chunk = new Vector2(v);
      myActiveChunks.add(chunk);
      myFiller.fill(game, chunk, myRemover);
    }
    SolMath.free(v);
  }

  private void removeFarChunks(Vector2 currChunk, SolGame game) {
    for (Iterator<Vector2> it = myActiveChunks.iterator(); it.hasNext(); ) {
      Vector2 chunk = it.next();
      if (shouldRemoveChunk(currChunk, chunk)) {
        it.remove();
      }
    }
  }

  private boolean shouldRemoveChunk(Vector2 currChunk, Vector2 chunk) {
    return chunk.x < currChunk.x - 2 * Const.CHUNK_SIZE || currChunk.x + 2 * Const.CHUNK_SIZE < chunk.x ||
      chunk.y < currChunk.y - 2 * Const.CHUNK_SIZE || currChunk.y + 2 * Const.CHUNK_SIZE < chunk.y;
  }

  private Vector2 getCurrChunk(Vector2 pos) {
    float x = pos.x / Const.CHUNK_SIZE;
    if (x < 0 ) x -= 1;
    x = (int) x * Const.CHUNK_SIZE;
    float y = pos.y / Const.CHUNK_SIZE;
    if (y < 0 ) y -= 1;
    y = (int) y * Const.CHUNK_SIZE;
    return new Vector2(x, y);
  }

  public boolean isInactive(Vector2 pos) {
    for (Vector2 chunk : myActiveChunks) {
      if (
        chunk.x < pos.x && pos.x < chunk.x + Const.CHUNK_SIZE &&
        chunk.y < pos.y && pos.y < chunk.y + Const.CHUNK_SIZE)
      {
        return false;
      }
    }
    return true;
  }

  public ChunkFiller getFiller() {
    return myFiller;
  }

  private class MyRemover implements RemoveController {
    @Override
    public boolean shouldRemove(Vector2 pos) {
      return isInactive(pos);
    }
  }

}
