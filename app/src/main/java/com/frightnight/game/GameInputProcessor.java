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
        if (game == null || game.fpsController == null || game.joystick == null) {
            return false;
        }
        
        // First try FPS controller for camera look (right side of screen)
        if (game.fpsController.touchDown(screenX, screenY, pointer)) {
            return true;
        }
        
        // Otherwise, pass to joystick (left side)
        game.joystick.onTouchDown(screenX, screenY, pointer);
        return true;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (game == null || game.fpsController == null || game.joystick == null) {
            return false;
        }
        
        // Send to FPS controller for camera rotation
        game.fpsController.touchDragged(screenX, screenY, pointer);
        
        // Also send to joystick for movement
        game.joystick.onTouchDragged(screenX, screenY, pointer);
        return true;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (game == null || game.fpsController == null || game.joystick == null) {
            return false;
        }
        
        // Release FPS controller
        game.fpsController.touchUp(pointer);
        
        // Release joystick
        game.joystick.onTouchUp(pointer);
        return true;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        // Handle back button to exit game
        if (keycode == com.badlogic.gdx.Input.Keys.BACK || keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
            // Exit the game and return to MainActivity
            com.badlogic.gdx.Gdx.app.exit();
            return true;
        }
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
