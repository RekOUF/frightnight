package com.frightnight.game;

import com.badlogic.gdx.InputProcessor;

/**
 * Handles all touch input for the game
 * Delegates to joystick, camera controls, etc.
 */
public class GameInputProcessor implements InputProcessor {
    
    private FrightNightGame3D game;
    
    public GameInputProcessor(FrightNightGame3D game) {
        this.game = game;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Safety checks
        if (game == null || game.cameraController == null || game.joystick == null) {
            return false;
        }
        
        // Check if touch is on camera rotation buttons
        if (game.cameraController.isLeftButtonPressed(screenX, screenY)) {
            game.rotateCameraLeft(3f);
            return true;
        }
        if (game.cameraController.isRightButtonPressed(screenX, screenY)) {
            game.rotateCameraRight(3f);
            return true;
        }
        if (game.cameraController.isSprintButtonPressed(screenX, screenY)) {
            game.setRunning(true);
            return true;
        }
        
        // Otherwise, pass to joystick
        game.joystick.onTouchDown(screenX, screenY, pointer);
        return true;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (game != null && game.joystick != null) {
            game.joystick.onTouchDragged(screenX, screenY, pointer);
        }
        return true;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (game == null || game.cameraController == null || game.joystick == null) {
            return false;
        }
        
        // Release sprint button
        if (game.cameraController.isSprintButtonPressed(screenX, screenY)) {
            game.setRunning(false);
        }
        
        game.joystick.onTouchUp(pointer);
        return true;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        // For testing on desktop (optional)
        return false;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        if (game != null && game.joystick != null) {
            game.joystick.onTouchUp(pointer);
        }
        return false;
    }
}
