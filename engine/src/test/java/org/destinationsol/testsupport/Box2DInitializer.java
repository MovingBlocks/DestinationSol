package org.destinationsol.testsupport;

import com.badlogic.gdx.physics.box2d.Box2D;
import org.junit.jupiter.api.BeforeAll;

/**
 * Use this for all tests which used Box2D in any case.
 */
public interface Box2DInitializer {

    @BeforeAll
    static void initBox2D(){
        Box2D.init();
    }
}
