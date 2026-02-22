package com.frightnight.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * First-person camera controller with touch drag support
 * Handles pitch (up/down) and yaw (left/right) camera rotation
 */
public class FirstPersonController {
    
    private PerspectiveCamera camera;
    private Vector3 position;
    private Vector3 direction;
    
    // Rotation angles
    private float yaw = 0f;   // Horizontal rotation (degrees)
    private float pitch = -30f; // Vertical rotation (degrees) - look down more to see terrain
    
    // Rotation constraints
    private static final float MAX_PITCH = 89f;
    private static final float MIN_PITCH = -89f;
    
    // Touch drag state
    private boolean isDragging = false;
    private float lastTouchX = 0f;
    private float lastTouchY = 0f;
    private int dragPointer = -1;
    
    // Sensitivity settings
    private float lookSensitivity = 0.2f;
    
    public FirstPersonController(PerspectiveCamera camera, Vector3 startPosition) {
        this.camera = camera;
        this.position = new Vector3(startPosition);
        this.direction = new Vector3(0, 0, -1);
        
        // Initialize camera - higher up to see terrain better
        camera.position.set(position.x, position.y + 3f, position.z); // 3 units higher
        camera.near = 0.1f;
        camera.far = 300f;
        updateCameraDirection();
    }
    
    /**
     * Called when touch/mouse is pressed down
     * Returns true if this controller should handle the input
     */
    public boolean touchDown(float screenX, float screenY, int pointer) {
        // Only start dragging from right half of screen (camera look area)
        if (screenX > Gdx.graphics.getWidth() * 0.4f) {
            isDragging = true;
            dragPointer = pointer;
            lastTouchX = screenX;
            lastTouchY = screenY;
            return true;
        }
        return false;
    }
    
    /**
     * Called when touch/mouse is dragged
     */
    public void touchDragged(float screenX, float screenY, int pointer) {
        if (isDragging && pointer == dragPointer) {
            // Calculate delta
            float deltaX = screenX - lastTouchX;
            float deltaY = screenY - lastTouchY;
            
            // Update rotation based on delta
            yaw -= deltaX * lookSensitivity;
            pitch += deltaY * lookSensitivity;
            
            // Clamp pitch to prevent camera flipping
            if (pitch > MAX_PITCH) pitch = MAX_PITCH;
            if (pitch < MIN_PITCH) pitch = MIN_PITCH;
            
            // Wrap yaw to 0-360 range
            while (yaw < 0) yaw += 360f;
            while (yaw >= 360) yaw -= 360f;
            
            // Update for next frame
            lastTouchX = screenX;
            lastTouchY = screenY;
            
            updateCameraDirection();
        }
    }
    
    /**
     * Called when touch/mouse is released
     */
    public void touchUp(int pointer) {
        if (pointer == dragPointer) {
            isDragging = false;
            dragPointer = -1;
        }
    }
    
    /**
     * Update camera direction based on yaw and pitch
     */
    private void updateCameraDirection() {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        
        direction.x = (float) (Math.cos(pitchRad) * Math.sin(yawRad));
        direction.y = (float) (-Math.sin(pitchRad));
        direction.z = (float) (Math.cos(pitchRad) * -Math.cos(yawRad));
        direction.nor();
    }
    
    /**
     * Move the camera/player in a direction
     * @param moveX Horizontal movement (-1 left, 1 right)
     * @param moveZ Forward movement (-1 back, 1 forward)
     * @param speed Movement speed
     * @param delta Delta time
     */
    public void move(float moveX, float moveZ, float speed, float delta) {
        // Calculate forward and right vectors (horizontal plane only)
        Vector3 forward = new Vector3(direction.x, 0, direction.z).nor();
        Vector3 right = new Vector3(forward).crs(Vector3.Y).nor();
        
        // Build movement vector
        Vector3 moveDir = new Vector3();
        moveDir.add(new Vector3(forward).scl(moveZ));
        moveDir.add(new Vector3(right).scl(moveX));
        
        if (moveDir.len() > 0) {
            moveDir.nor().scl(speed * delta);
            position.add(moveDir);
        }
    }
    
    /**
     * Update camera position and direction
     */
    public void update() {
        camera.position.set(position);
        
        // Look at point in front of camera
        Vector3 lookAt = new Vector3(position).add(direction);
        camera.lookAt(lookAt);
        camera.up.set(Vector3.Y);
        camera.update();
    }
    
    /**
     * Clamp position to boundaries
     */
    public void clampPosition(float minX, float maxX, float minZ, float maxZ) {
        position.x = Math.max(minX, Math.min(maxX, position.x));
        position.z = Math.max(minZ, Math.min(maxZ, position.z));
    }
    
    // Getters
    public Vector3 getPosition() {
        return position;
    }
    
    public Vector3 getDirection() {
        return direction;
    }
    
    public float getYaw() {
        return yaw;
    }
    
    public float getPitch() {
        return pitch;
    }
    
    public boolean isDragging() {
        return isDragging;
    }
    
    // Setters
    public void setLookSensitivity(float sensitivity) {
        this.lookSensitivity = sensitivity;
    }
    
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }
}
