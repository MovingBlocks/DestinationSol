package org.destinationsol.screens.ScreenToWorldMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.gdx.math.Vector2;

import org.destinationsol.game.screens.ScreenToWorldMapper;
import org.junit.jupiter.api.Test;

public class ScreenToWorldMapperNoRotationTest {
    
    /** 
     * Test that the a click on the center of the screen maps to the center of the world
     * when the cameras position is in the center
     */
    @Test
    public void screenToWorldMapper_ClickCenterOfScreen() {
        float screenHeight = 1000;
        float screenWidth = 1000;
        
        // The camera is located in the center of the world
        Vector2 camPos = new Vector2(0.0f, 0.0f);
        
        // The users is clicking on the center of the screen
        Vector2 clickPos = new Vector2(0.5f, 0.5f);

        // Calculating the click position in the world
        Vector2 worldPos = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            clickPos, 
            camPos, 
            0.0f,
            1.0f
        );

        // As the users clicks on the center of the screen and when the camera is in the center of the world,
        // The clicked screen position should also map to the center of the world.
        assertEquals(new Vector2(0.0f, 0.0f), worldPos);
    }


    /** 
     * Test that the a click on the center of a screen that is not equal of height and width  
     * when the cameras position is in the center
     */
    @Test
    public void screenToWorldMapper_ClickCenterOfUnSquaredScreen() {
        float screenWidth = 2000;
        float screenHeight = 1000;
        
        // The camera is located in the center of the world
        Vector2 camPos = new Vector2(0.0f, 0.0f);
        
        // The users is clicking on the center of the screen
        Vector2 clickPos = new Vector2(1f, 0.5f);

        // Calcu    lating the click position in the world
        Vector2 worldPos = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            clickPos, 
            camPos,
            0.0f,
            1.0f
        );

        // As the users clicks on the center of the screen and when the camera is in the center of the world,
        // The clicked screen position should also map to the center of the world.
        assertEquals(new Vector2(0.0f, 0.0f), worldPos);
    }


    /** 
     * Test that the a click on the center of the screen maps correctly to the cameras current position
     */
    @Test
    public void screenToWorldMapper_ClickCenterOfScreen_WithCameraOffset() {
        float screenWidth = 1000;
        float screenHeight = 1000;
        
        // The camera is located in a random place in the world
        Vector2 camPos = new Vector2(27.0f, 27.0f);
        
        // The users is clicking on the center of the screen
        Vector2 clickPos = new Vector2(0.5f, 0.5f);

        // Calculating the click position in the world
        Vector2 worldPos = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            clickPos, 
            camPos, 
            0.0f,
            1
        );

        assertEquals(new Vector2(27.0f, 27.0f), worldPos);
    }

    /** 
     * Test that the a click on the center of the screen maps to the center of the world
     * when the cameras position is in the center
     */
    @Test
    public void screenToWorldMapper_ClickTopLeftOnScreen_WithCameraOffset() {
        float screenWidth = 1000;
        float screenHeight = 1000;
        
        // The camera is located in a random place in the world
        Vector2 camPos = new Vector2(5.0f, 5.0f);
        
        // The users is clicking on the top left part of the screen
        Vector2 clickPos = new Vector2(0.0f, 0.0f);

        // Calculating the click position in the world
        Vector2 worldPos = ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            clickPos, 
            camPos,
            0.0f,
            1
        );

        assertEquals(
            new Vector2(5.0f, 5.0f)
                    .sub(new Vector2(0.5f, 0.5f)
                    .scl(ScreenToWorldMapper.PIXEL_TO_WORLD_UNIT_RATIO)
            ), 
            worldPos
        );
    }

}
