package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.Fraction;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.particle.LightSrc;
import com.miloshpetrov.sol2.game.particle.ParticleSrc;

import java.util.ArrayList;

public class ProjectileBuilder {

  public ProjectileBuilder() {
  }

  public Rocket buildRocket(SolGame game, Vector2 pos, float angle, Vector2 gunSpd, Fraction fraction, float dmg) {
    ArrayList<Dra> dras = new ArrayList<Dra>();
    RectSprite s = new RectSprite(game.getTexMan().getTex("projectiles/rocket"), .15f, 0, 0, new Vector2(), DraLevel.PROJECTILES, 0, 0, Col.W);
    dras.add(s);

    BodyDef bd = new BodyDef();
    bd.type = BodyDef.BodyType.DynamicBody;
    bd.angle = angle * SolMath.degRad;
    bd.angularDamping = 0;
    bd.position.set(pos);
    bd.linearDamping = 0;
    Body body = game.getObjMan().getWorld().createBody(bd);
    FixtureDef fd = new FixtureDef();
    fd.density = 1;
    fd.friction = Const.FRICTION;
    fd.shape = new CircleShape();
    fd.shape.setRadius(.05f);
    body.createFixture(fd);
    fd.shape.dispose();

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
