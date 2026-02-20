package com.frightnight.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Virtual touch joystick for movement control
 * Displayed in bottom-left corner
 */
public class TouchJoystick {
    
    private ShapeRenderer shapeRenderer;
    private Vector2 joystickCenter;
    private Vector2 joystickTouch;
    private float outerRadius = 80f;
    private float innerRadius = 35f;
    private boolean isTouched = false;
    private int touchPointer = -1;
    
    public TouchJoystick() {
        shapeRenderer = new ShapeRenderer();
        joystickCenter = new Vector2(120, 120); // Bottom-left position
        joystickTouch = new Vector2(joystickCenter);
    }
    
    public void render() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Outer circle (boundary)
        shapeRenderer.setColor(1, 1, 1, 0.3f); // Semi-transparent white
        shapeRenderer.circle(joystickCenter.x, joystickCenter.y, outerRadius);
        
        // Inner circle (thumb position)
        if (isTouched) {
            shapeRenderer.setColor(1, 0, 0, 0.7f); // Red when touched
        } else {
            shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.5f); // Gray when idle
        }
        shapeRenderer.circle(joystickTouch.x, joystickTouch.y, innerRadius);
        
        shapeRenderer.end();
    }
    
    public void onTouchDown(float screenX, float screenY, int pointer) {
        // Convert screen Y to LibGDX coordinates (bottom-left origin)
        float y = Gdx.graphics.getHeight() - screenY;
        
        // Check if touch is within joystick area
        float dx = screenX - joystickCenter.x;
        float dy = y - joystickCenter.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance < outerRadius * 1.5f) { // Allow some tolerance
            isTouched = true;
            touchPointer = pointer;
            updateJoystick(screenX, y);
        }
    }
    
    public void onTouchDragged(float screenX, float screenY, int pointer) {
        if (isTouched && pointer == touchPointer) {
            float y = Gdx.graphics.getHeight() - screenY;
            updateJoystick(screenX, y);
        }
    }
    
    public void onTouchUp(int pointer) {
        if (pointer == touchPointer) {
            isTouched = false;
            touchPointer = -1;
            joystickTouch.set(joystickCenter); // Reset to center
        }
    }
    
    private void updateJoystick(float touchX, float touchY) {
        float dx = touchX - joystickCenter.x;
        float dy = touchY - joystickCenter.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > outerRadius) {
            // Clamp to outer radius
            float angle = (float) Math.atan2(dy, dx);
            joystickTouch.x = joystickCenter.x + (float) Math.cos(angle) * outerRadius;
            joystickTouch.y = joystickCenter.y + (float) Math.sin(angle) * outerRadius;
        } else {
            joystickTouch.set(touchX, touchY);
        }
    }
    
    /**
     * Get normalized movement vector
     * X: -1 (left) to 1 (right)
     * Y: -1 (backward) to 1 (forward)
     */
    public Vector3 getMovement() {
        if (!isTouched) {
            return new Vector3(0, 0, 0);
        }
        
        float dx = joystickTouch.x - joystickCenter.x;
        float dy = joystickTouch.y - joystickCenter.y;
        
        // Normalize
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length < 0.01f) {
            return new Vector3(0, 0, 0);
        }
        
        return new Vector3(dx / outerRadius, dy / outerRadius, 0);
    }
    
    public void dispose() {
        shapeRenderer.dispose();
    }
}
