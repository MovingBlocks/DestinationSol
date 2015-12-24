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

package org.destinationsol.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.Faction;
import org.destinationsol.game.FactionMan;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.dra.Dra;
import org.destinationsol.game.dra.RectSprite;
import org.destinationsol.game.input.Pilot;

import java.util.ArrayList;
import java.util.List;

public class Door {
  public static final float SPD_LEN = .4f;
  public static final float SENSOR_DIST = 3f;
  public static final float DOOR_LEN = 1.1f;
  public static final float MAX_OPEN_AWAIT = DOOR_LEN / SPD_LEN;
  private final PrismaticJoint myJoint;
  private final RectSprite myS;
  private float myOpenAwait;

  public Door(PrismaticJoint joint, RectSprite s) {
    myJoint = joint;
    myS = s;
  }

  public void update(SolGame game, SolShip ship) {
    Vector2 doorPos = getBody().getPosition();
    boolean open = myOpenAwait <= 0 && shouldOpen(game, ship, doorPos);
    if (open) {
      myOpenAwait = MAX_OPEN_AWAIT;
      myJoint.setMotorSpeed(SPD_LEN);
      game.getSoundMan().play(game, game.getSpecialSounds().doorMove, doorPos, ship);
    } else if (myOpenAwait > 0) {
      myOpenAwait -= game.getTimeStep();
      if (myOpenAwait < 0) {
        myJoint.setMotorSpeed(-SPD_LEN);
        game.getSoundMan().play(game, game.getSpecialSounds().doorMove, doorPos, ship);
      }
    }

    Vector2 shipPos = ship.getPos();
    float shipAngle = ship.getAngle();
    SolMath.toRel(doorPos, myS.getRelPos(), shipAngle, shipPos);
  }

  private boolean shouldOpen(SolGame game, SolShip ship, Vector2 doorPos) {
    Faction faction = ship.getPilot().getFaction();
    FactionMan factionMan = game.getFactionMan();
    List<SolObject> objs = game.getObjMan().getObjs();
    for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
      SolObject o = objs.get(i);
      if (o == ship) continue;
      if (!(o instanceof SolShip)) continue;
      SolShip ship2 = (SolShip) o;
      Pilot pilot2 = ship2.getPilot();
      if (!pilot2.isUp()) continue;
      if (factionMan.areEnemies(pilot2.getFaction(), faction)) continue;
      if (ship2.getPos().dst(doorPos) < SENSOR_DIST) return true;
    }
    return false;
  }

  public void collectDras(ArrayList<Dra> dras) {
    dras.add(myS);
  }

  public Body getBody() {
    return myJoint.getBodyB();
  }

  public void onRemove(SolGame game) {
    World w = game.getObjMan().getWorld();
    Body doorBody = getBody();
    w.destroyJoint(myJoint);
    w.destroyBody(doorBody);
  }
}
