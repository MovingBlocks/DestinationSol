package org.destinationsol.game.screens;
import com.badlogic.gdx.math.Vector2;

public class ScreenToWorldMapper {
    private ScreenToWorldMapper() {
    }

    public static final int PIXEL_TO_WORLD_UNIT_RATIO = 5;

    /**
     * Maps a click position on the screen to the world 
     * @param screenDimensions The screen dimensions of the game window
     * @param clickPosition The click position on the screen. Is a vector2 with values between 0 and 1
     * @param camPos The camera position in the world
     * @param camAngle The camera angle in the world
     * @return
     */
    public static Vector2 screenClickPositionToWorldPosition(Vector2 screenDimensions, Vector2 clickPosition, Vector2 camPos, float camAngle, float zoom) {
        float ratio = getScreenRatio(screenDimensions);
        clickPosition = matchClickPositionWithCameraAngle(clickPosition, ratio, camAngle);
        clickPosition = matchClickPositionWithCameraZoom(clickPosition, zoom);
        return getCameraPositionOffsetToWorld(camPos, clickPosition, ratio, zoom);
    }

    /**
     * Getting the world position from a cam pos and a cam offset
     * 
     * @param camPos    The camera position in the world
     * @param camOffset The camera offset in the world
     * @param ratio     The screen ratio
     * @param zoom      The zoom of the world
     * @return The camera offset position in the world
     */
    private static Vector2 getCameraPositionOffsetToWorld(Vector2 camPos, Vector2 camOffset, float ratio, float zoom) {
        Vector2 finalPosition = matchCameraPositionToCenterOfWorld(camPos, zoom, ratio);
        finalPosition.add(camOffset);
        return finalPosition;
    }

    /**
     * Get the camera position to the center of the world 
     * @param camPos The camera pos
     * @param zoom The zoom of the world
     * @param ratio The screen ratio
     * @return The position as if the cameras position would be center of the world
     */
    private static Vector2 matchCameraPositionToCenterOfWorld(Vector2 camPos, float zoom, float ratio) {
        Vector2 camPosCopy = camPos.cpy();
        camPosCopy.x -= (ratio * zoom) / 2.0f * PIXEL_TO_WORLD_UNIT_RATIO;
        camPosCopy.y -= (zoom) / 2.0f * PIXEL_TO_WORLD_UNIT_RATIO;
        return camPosCopy;
    }

        /**
     * Takes a given clickPosition and match it with a given camera angle.
     * 
     * @param clickPosition The click position
     * @param screenRatio   The screen ratio
     * @param camAngle      The camera angle in degrees
     */
    private static Vector2 matchClickPositionWithCameraAngle(Vector2 clickPosition, float screenRatio, float camAngle) {
        Vector2 clickPositionCopy = clickPosition.cpy();
        Vector2 screenCenterOffset = getCenterOfScreenPercent(screenRatio);
        clickPositionCopy.add(screenCenterOffset.cpy().scl(-1.0f));
        clickPositionCopy.rotate(camAngle);
        clickPositionCopy.add(screenCenterOffset);
        return clickPositionCopy;
    }

    
    /**
     * Get the center of screen in percent format. That is, (0, 0) is the upper left
     * part of the screen. (1, 1) is the bottom right part of the screen. (0.5 *
     * ratio, 0.5) is the middle part of the screen.
     * 
     * @param screenRatio The screen ratio
     * @return The center of the screen in percent represented as a vector
     */
    private static Vector2 getCenterOfScreenPercent(float screenRatio) {
        return new Vector2(0.5f * screenRatio, 0.5f);
    }


    /**
     * Matches the click position with the world zoom
     * 
     * @param clickPosition The click position
     * @param zoom          The zoom level of the world
     */
    private static Vector2 matchClickPositionWithCameraZoom(Vector2 clickPosition, float zoom) {
        Vector2 clickPositionCopy = clickPosition.cpy();
        clickPositionCopy.scl(PIXEL_TO_WORLD_UNIT_RATIO);
        clickPositionCopy.scl(zoom);
        return clickPositionCopy;
    }

    /**
     * Get the screen ratio, (width / height)
     * 
     * @param screenDimensions The screen dimensions in any unit
     * @return The ratio between the width and height
     */
    private static float getScreenRatio(Vector2 screenDimensions) {
        float screenWidth = screenDimensions.x;
        float screenHeight = screenDimensions.y;
        return screenWidth / screenHeight;
    }
}
