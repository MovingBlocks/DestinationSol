package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraLevel;
import com.miloshpetrov.sol2.game.particle.LightSrc;
import com.miloshpetrov.sol2.game.particle.ParticleSrc;

import java.util.ArrayList;

public class ProjectileBuilder {
  private final PathLoader myPathLoader;

  public ProjectileBuilder() {
    myPathLoader = new PathLoader(Gdx.files.internal("res/paths/projectiles.json"));
  }

  public Rocket buildRocket(SolGame game, Vector2 pos, float angle, Vector2 gunSpd, Fraction fraction, float dmg) {
    ArrayList<Dra> dras = new ArrayList<Dra>();
    Body body = myPathLoader.getBodyAndSprite(game, "projectiles", "rocket", .15f, BodyDef.BodyType.DynamicBody, pos,
      angle, dras, 1, DraLevel.PROJECTILES);
    Vector2 spd = SolMath.fromAl(angle, Rocket.INITIAL_SPD_LEN);
    spd.add(gunSpd);
    body.setLinearVelocity(spd);
    SolMath.free(spd);

    ParticleSrc flameSrc = game.getPartMan().buildFlameSrc(game, new Vector2());
    flameSrc.setWorking(true);
    dras.add(flameSrc);
    LightSrc lightSrc = new LightSrc(game, .25f, true, 1f, new Vector2());
    lightSrc.collectDras(dras);

    Rocket m = new Rocket(body, dras, flameSrc, lightSrc, fraction, dmg);
    body.setUserData(m);
    return m;
  }
}
