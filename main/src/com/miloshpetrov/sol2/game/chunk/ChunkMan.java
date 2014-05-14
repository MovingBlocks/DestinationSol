package com.miloshpetrov.sol2.game.chunk;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.RemoveController;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.HullConfigs;
import com.miloshpetrov.sol2.ui.DebugCollector;

import java.util.*;

public class ChunkMan {
  private static final int MAX_FILL_DIST = 1;
  public static final int MIN_REMOVE_DIST = MAX_FILL_DIST + 2;
  private static final int MAX_BG_FILL_DIST = 2;
  public static final int MIN_BG_REMOVE_DIST = MAX_BG_FILL_DIST + 1;

  private final Set<Vector2> myFilledChunks;
  private final Set<Vector2> myBgFilledChunks;
  private final RemoveController myRemover;
  private final RemoveController myBgRemover;
  private final ChunkFiller myFiller;

  private int myX;
  private int myY;

  public ChunkMan(HullConfigs hullConfigs, TexMan texMan) {
    myFilledChunks = new HashSet<Vector2>();
    myBgFilledChunks = new HashSet<Vector2>();
    myRemover = new MyRemover(MIN_REMOVE_DIST);
    myBgRemover = new MyRemover(MIN_BG_REMOVE_DIST);
    myFiller = new ChunkFiller();
  }

  public void update(SolGame game) {
    Vector2 camPos = game.getCam().getPos();
    boolean refill = updateCurrChunk(camPos);
    if (refill) {
      clearFarChunks(myFilledChunks, MIN_REMOVE_DIST);
      addNewChunks(myFilledChunks, MAX_FILL_DIST, game);
      clearFarChunks(myBgFilledChunks, MIN_BG_REMOVE_DIST);
      addNewChunks(myBgFilledChunks, MAX_BG_FILL_DIST, game);
    }
    DebugCollector.debug("chunks", myFilledChunks.size());
    DebugCollector.debug("bgChunks", myBgFilledChunks.size());
  }

  private boolean updateCurrChunk(Vector2 pos) {
    int oldX = myX;
    int oldY = myY;
    myX = (int)(pos.x / Const.CHUNK_SIZE);
    if (myX < 0) myX -= 1;
    myY = (int)(pos.y / Const.CHUNK_SIZE);
    if (myY < 0) myY -= 1;
    return oldX != myX || oldY != myY;
  }

  private void clearFarChunks(Set<Vector2> chunks, int dist) {
    for (Iterator<Vector2> it = chunks.iterator(); it.hasNext(); ) {
      Vector2 chunk = it.next();
      if (isChunkFar((int)chunk.x, (int)chunk.y, dist)) {
        it.remove();
      }
    }
  }

  private boolean isChunkFar(int x, int y, float dist) {
    return x <= myX - dist || myX + dist <= x || y <= myY - dist || myY + dist <= y;
  }

  private void addNewChunks(Set<Vector2> chunks, int dist, SolGame game) {
    maybeAddChunk(chunks, 0, 0, game);
    for (int i = -dist; i < dist + 1; i++) {
      for (int j = -dist; j < dist + 1; j++) {
        if (i == 0 && j == 0) continue;
        maybeAddChunk(chunks, i, j, game);
      }
    }
  }

  private void maybeAddChunk(Set<Vector2> chunks, int oX, int oY, SolGame game) {
    Vector2 v = SolMath.getVec(myX + oX, myY + oY);
    if (!chunks.contains(v)) {
      Vector2 chunk = new Vector2(v);
      chunks.add(chunk);
      boolean bg = chunks == myBgFilledChunks;
      myFiller.fill(game, chunk, bg ? myBgRemover : myRemover, bg);
    }
    SolMath.free(v);
  }

  public boolean isInactive(Vector2 pos, int dist) {
    int x = (int)(pos.x / Const.CHUNK_SIZE);
    int y = (int)(pos.y / Const.CHUNK_SIZE);
    return isChunkFar(x, y, dist);
  }

  private class MyRemover implements RemoveController {
    private final int myMinRemoveDist;

    public MyRemover(int minRemoveDist) {
      myMinRemoveDist = minRemoveDist;
    }

    @Override
    public boolean shouldRemove(Vector2 pos) {
      return isInactive(pos, myMinRemoveDist);
    }
  }

}
