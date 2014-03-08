package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.dra.*;

import java.util.ArrayList;

public class MazeBuilder {
  public static final float BORDER = 4f;

  public void build(SolGame game, Maze maze) {
    Vector2 pos = maze.getPos();
    float rad = maze.getRadius() - BORDER;
    ArrayList<Dra> dras = new ArrayList<Dra>();
    RectSprite s = new RectSprite(game.getTexMan().whiteTex, rad*2, 0, 0, new Vector2(), DraLevel.PART_BG_0, 0, 0, Col.G);
    dras.add(s);
    DrasObj ds = new DrasObj(dras, new Vector2(pos), new Vector2(), null, false, false);
    game.getObjMan().addObjDelayed(ds);
  }
}
