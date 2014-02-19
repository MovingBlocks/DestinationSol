

package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.dra.*;

import java.util.*;

/**
 * Loads the collision fixtures defined with the Physics Body Editor
 * application. You only need to give it a body and the corresponding fixture
 * name, and it will attach these fixtures to your body.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class PathLoader {

  // Model
  private final Model model;

  // Reusable stuff
  private final List<Vector2> vectorPool = new ArrayList<Vector2>();
  private final PolygonShape tmpPolyShape = new PolygonShape();
  private final CircleShape tmpCircleShape = new CircleShape();
  private final Vector2 tmpV = new Vector2();

  // -------------------------------------------------------------------------
  // Ctors
  // -------------------------------------------------------------------------

  public PathLoader(String fileName) {
    FileHandle file = SolFiles.readOnly("res/paths/" + fileName + ".json");
    if (file == null) throw new NullPointerException("file is null");
    model = readJson(file.readString());
  }

  // -------------------------------------------------------------------------
  // Public API
  // -------------------------------------------------------------------------

  /**
   * Creates and applies the fixtures defined in the editor. The name
   * parameter is used to retrieve the right fixture from the loaded file.
   * <br/><br/>
   *
   * The body reference point (the red cross in the tool) is by default
   * located at the bottom left corner of the image. This reference point
   * will be put right over the BodyDef position point. Therefore, you should
   * place this reference point carefully to let you place your body in your
   * world easily with its BodyDef.position point. Note that to draw an image
   * at the position of your body, you will need to know this reference point
   * (see {@link #getOrigin(String, float)}.
   * <br/><br/>
   *
   * Also, saved shapes are normalized. As shown in the tool, the width of
   * the image is considered to be always 1 meter. Thus, you need to provide
   * a scale factor so the polygons get resized according to your needs (not
   * every body is 1 meter large in your game, I guess).
   *
   * @param body The Box2d body you want to attach the fixture to.
   * @param name The name of the fixture you want to load.
   * @param fd The fixture parameters to apply to the created body fixture.
   * @param scale The desired scale of the body. The default width is 1.
   */
  public void attachFixture(Body body, String name, FixtureDef fd, float scale) {
    RigidBodyModel rbModel = model.rigidBodies.get(name);
    if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

    Vector2 origin = tmpV.set(rbModel.origin).mul(scale);

    int polyCount = rbModel.polys.size();
    for (int i = 0; i < polyCount; i++) {
      PolygonModel poly = rbModel.polys.get(i);
      Vector2[] points = poly.tmpArray;

      int pointCount = points.length;
      for (int ii = 0; ii < pointCount; ii++) {
        Vector2 origPoint = poly.vertices.get(pointCount - ii - 1);
        points[ii] = newVec(origPoint).mul(scale);
        points[ii].sub(origin);
      }

      tmpPolyShape.set(points);
      fd.shape = tmpPolyShape;
      body.createFixture(fd);

      for (Vector2 point : points) free(point);
    }

    int circleCount = rbModel.circles.size();
    for (int i = 0; i < circleCount; i++) {
      CircleModel circle = rbModel.circles.get(i);
      Vector2 center = newVec(circle.center).mul(scale).sub(origin);
      float radius = circle.radius * scale;

      tmpCircleShape.setPosition(center);
      tmpCircleShape.setRadius(radius);
      fd.shape = tmpCircleShape;
      body.createFixture(fd);

      free(center);
    }
  }

  /**
   * Gets the image path attached to the given name.
   */
  public String getImagePath(String name) {
    RigidBodyModel rbModel = model.rigidBodies.get(name);
    if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

    return rbModel.imagePath;
  }

  /**
   * Gets the origin point attached to the given name. Since the point is
   * normalized in [0,1] coordinates, it needs to be scaled to your body
   * size. Warning: this method returns the same Vector2 object each time, so
   * copy it if you need it for later use.
   */
  public Vector2 getOrigin(String name, float scale) {
    RigidBodyModel rbModel = model.rigidBodies.get(name);
    if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");
    tmpV.set(rbModel.origin).mul(scale);
    return tmpV;
  }

  /**
   * <b>For advanced users only.</b> Lets you access the internal model of
   * this loader and modify it. Be aware that any modification is permanent
   * and that you should really know what you are doing.
   */
  public Model getInternalModel() {
    return model;
  }

  // -------------------------------------------------------------------------
  // Json Models
  // -------------------------------------------------------------------------

  public static class Model {
    public final Map<String, RigidBodyModel> rigidBodies = new HashMap<String, RigidBodyModel>();
  }

  public static class RigidBodyModel {
    public String name;
    public String imagePath;
    public final Vector2 origin = new Vector2();
    public final List<PolygonModel> polys = new ArrayList<PolygonModel>();
    public final List<PolygonModel> shapes = new ArrayList<PolygonModel>();
    public final List<CircleModel> circles = new ArrayList<CircleModel>();
  }

  public static class PolygonModel {
    public final List<Vector2> vertices = new ArrayList<Vector2>();
    private Vector2[] tmpArray; // used to avoid allocation in attachFixture()
  }

  public static class CircleModel {
    public final Vector2 center = new Vector2();
    public float radius;
  }

  // -------------------------------------------------------------------------
  // Json reading process
  // -------------------------------------------------------------------------

  private Model readJson(String str) {
    Model m = new Model();
    JsonValue rootElem = new JsonReader().parse(str);

    JsonValue bodiesElems = rootElem.get("rigidBodies");

    for (int i=0; i<bodiesElems.size; i++) {
      JsonValue bodyElem = bodiesElems.get(i);
      RigidBodyModel rbModel = readRigidBody(bodyElem);
      m.rigidBodies.put(rbModel.name, rbModel);
    }

    return m;
  }

  private RigidBodyModel readRigidBody(JsonValue bodyElem) {
    RigidBodyModel rbModel = new RigidBodyModel();
    rbModel.name = bodyElem.getString("name");
    rbModel.imagePath = bodyElem.getString("imagePath");

    JsonValue originElem = bodyElem.get("origin");
    rbModel.origin.x = originElem.getFloat("x");
    rbModel.origin.y = 1 - originElem.getFloat("y");

    // polygons

    JsonValue polygonsElem = bodyElem.get("polygons");

    for (int i=0; i<polygonsElem.size; i++) {
      PolygonModel polygon = new PolygonModel();
      rbModel.polys.add(polygon);

      JsonValue verticesElem = polygonsElem.get(i);
      for (int ii=0; ii<verticesElem.size; ii++) {
        JsonValue vertexElem = verticesElem.get(ii);
        float x = vertexElem.getFloat("x");
        float y = 1 - vertexElem.getFloat("y");
        polygon.vertices.add(new Vector2(x, y));
      }

      polygon.tmpArray = new Vector2[polygon.vertices.size()];
    }

    // shapes

    JsonValue shapeElems = bodyElem.get("shapes");

    for (int i=0; i<shapeElems.size; i++) {
      JsonValue shapeElem = shapeElems.get(i);
      String type = shapeElem.getString("type");
      if (!"POLYGON".equals(type)) continue;

      PolygonModel shape = new PolygonModel();
      rbModel.shapes.add(shape);

      JsonValue verticesElem = shapeElem.get("vertices");
      for (int ii=0; ii<verticesElem.size; ii++) {
        JsonValue vertexElem = verticesElem.get(ii);
        float x = vertexElem.getFloat("x");
        float y = 1 - vertexElem.getFloat("y");
        shape.vertices.add(new Vector2(x, y));
      }

      shape.tmpArray = new Vector2[shape.vertices.size()];
    }

    // circles

    JsonValue circlesElem = bodyElem.get("circles");

    for (int i=0; i<circlesElem.size; i++) {
      CircleModel circle = new CircleModel();
      rbModel.circles.add(circle);

      JsonValue circleElem = circlesElem.get(i);
      circle.center.x = circleElem.getFloat("cx");
      circle.center.y = 1 - circleElem.getFloat("cy");
      circle.radius = circleElem.getFloat("r");
    }

    return rbModel;
  }

  // -------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------

  private Vector2 newVec() {
    return newVec(null);
  }

  private Vector2 newVec(Vector2 v) {
    Vector2 res = vectorPool.isEmpty() ? new Vector2() : vectorPool.remove(0);
    if (v != null) res.set(v);
    return res;
  }

  private void free(Vector2 v) {
    vectorPool.add(v);
  }

  public Body getBodyAndSprite(SolGame game, String packName, String name, float scale, BodyDef.BodyType type,
    Vector2 pos, float angle, List<Dra> dras, float density, DraLevel level)
  {
    BodyDef bd = new BodyDef();
    bd.type = type;
    bd.angle = angle * SolMath.degRad;
    bd.angularDamping = 0;
    bd.position.set(pos);
    bd.linearDamping = 0;
    Body body = game.getObjMan().getWorld().createBody(bd);
    FixtureDef fd = new FixtureDef();
    fd.density = density;
    fd.friction = Const.FRICTION;
    String pathName = name + ".png";
    attachFixture(body, pathName, fd, scale);

    Vector2 orig = getOrigin(pathName, 1);
    String imgName = packName + "/" + name;
    TextureAtlas.AtlasRegion tex = game.getTexMan().getTex(imgName);
    RectSprite s = new RectSprite(tex, scale, orig.x - .5f, orig.y - .5f, new Vector2(), level, 0, 0, Col.W);
    dras.add(s);
    return body;
  }
}
