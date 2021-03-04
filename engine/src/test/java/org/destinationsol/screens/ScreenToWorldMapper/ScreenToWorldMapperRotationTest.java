
package org.destinationsol.screens.ScreenToWorldMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;

import org.destinationsol.game.screens.ScreenToWorldMapper;
import org.junit.jupiter.api.Test;

public class ScreenToWorldMapperRotationTest {
    
    /** 
     * Test that the a click on the center of the screen maps to the center of the world
     * when the cameras position is in the center but with a rotation with 45 degrees
     */
    @Test
    public void screenToWorldMapper_ClickCenterOfScreen_Rotation() {
        float screenHeight = 1000;
        float screenWidth = 1000;
        
        // The camera is located in a random place in the world
        Vector2 camPos = new Vector2(7.5f, 8.5f);
        
        // The users is clicking on the center of the screen
        Vector2 clickPos = new Vector2(0.5f, 0.5f);

        // Calculating the click position in the world with a 45 degree
        Vector2 worldPos = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            clickPos, 
            camPos, 
            45.0f,
            1
        );
        

        // Because the camera rotation is always in the center of the screen, the click position should be
        // equal to the camera position itself.
        assertEquals(camPos.x, worldPos.x, 0.00001);
        assertEquals(camPos.y, worldPos.y, 0.00001);
    }


    /** 
     * Test that the a click on the top left part of the screen with an 90 degree rotation
     */
    @Test
    public void screenToWorldMapper_ClickTopLeftWithRotation() {
        float screenWidth = 1000;
        float screenHeight = 1000;
        
        // The camera is located in a random location
        Vector2 camPos = new Vector2(4.0f, 8.0f);
        
        // The users is clicking on the top left part on the screen
        Vector2 clickPos = new Vector2(0.0f, 0.0f);

        // Calculating the click position in the world
        Vector2 worldPos = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            clickPos, 
            camPos,
            90.0f,
            1
        );

        // This location should be the same as the first calculated worldPos as the click position
        // is now the bottom left part of the screen. That is, the top left part rotated 90 degrees
        Vector2 worldPosCorrect = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            new Vector2(1.0f, 0.0f), 
            camPos,
            0.0f,
            1
        );

        // Because the camera is rotated 90 degrees anti-clockwise, the top left location should be
        assertEquals(
            worldPosCorrect,
            worldPos
        );
    }


    /** 
     * Test that rotation and zoom give the correct world location
     */
    @Test
    public void screenToWorldMapper_RotationAndZoom() {
        float screenWidth = 1000;
        float screenHeight = 1000;
        
        // The camera is located in a random place in the world
        Vector2 camPos = new Vector2(5.0f, 5.0f);
        
        // The users is clicking on the top left part of the screen
        Vector2 clickPos = new Vector2(1.0f, 1.0f);

        float zoom = 2.5f;

        // Calculating the click position in the world
        Vector2 worldPos = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            clickPos, 
            camPos,
            45.0f,
            zoom
        );

        Vector2 expectedValue = camPos.add(new Vector2(0.5f, 0.5f).rotate(45.0f).scl(ScreenToWorldMapper.PIXEL_TO_WORLD_UNIT_RATIO * zoom));
        assertEquals(expectedValue.x, worldPos.x, 0.00001, "X elements");
        assertEquals(expectedValue.y, worldPos.y, 0.00001, "Y elements");
        
    }

    /** 
     * Test that the method works with a rotation of 180 degrees.
     */
    @Test
    public void screenToWorldMapper_180DegreeCameraRotation() {
        float screenWidth = 1000;
        float screenHeight = 1000;
        
        // The camera is located in a random place in the world
        Vector2 camPos = new Vector2(5.0f, 5.0f);
        
        // The users is clicking on the top left part of the screen
        Vector2 clickPos = new Vector2(1.0f, 1.0f);

        // Calculating the click position in the world
        Vector2 worldPos = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            clickPos, 
            camPos,
            180.0f,
            1
        );


        Vector2 worldPosDuplicate = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            new Vector2(0.0f, 0.0f), 
            camPos,
            0.0f,
            1
        );

        assertEquals(worldPosDuplicate.x, worldPos.x, 0.00001);
        assertEquals(worldPosDuplicate.y, worldPos.y, 0.00001);
    }

    
    /** 
     * Test that the method works with a rotation of 360 degrees.
     */
    @Test
    public void screenToWorldMapper_FullRotation() {
        float screenWidth = 1000;
        float screenHeight = 1000;
        
        // The camera is located in a random place in the world
        Vector2 camPos = new Vector2(5.0f, 5.0f);
        
        // The users is clicking on the top left part of the screen
        Vector2 clickPos = new Vector2(1.0f, 1.0f);

        // Calculating the click position in the world
        Vector2 worldPos = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            clickPos, 
            camPos,
            360.0f,
            1
        );

        // Create a world pos duplicate that should have 
        Vector2 worldPosDuplicate = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            clickPos, 
            camPos,
            0.0f,
            1
        );

        assertEquals(worldPosDuplicate.x, worldPos.x, 0.00001);
        assertEquals(worldPosDuplicate.y, worldPos.y, 0.00001);
    }

}
