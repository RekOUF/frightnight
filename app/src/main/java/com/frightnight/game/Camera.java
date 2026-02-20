package com.frightnight.game;

public class Camera {
    private float x, y;
    private int viewportWidth, viewportHeight;
    private int worldWidth, worldHeight;
    
    public Camera(int viewportWidth, int viewportHeight, int worldWidth, int worldHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.x = 0;
        this.y = 0;
    }
    
    public void centerOn(float targetX, float targetY) {
        // Center camera on target
        x = targetX - viewportWidth / 2;
        y = targetY - viewportHeight / 2;
        
        // Clamp to world boundaries
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > worldWidth - viewportWidth) x = worldWidth - viewportWidth;
        if (y > worldHeight - viewportHeight) y = worldHeight - viewportHeight;
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    
    public int worldToScreenX(float worldX) {
        return (int) (worldX - x);
    }
    
    public int worldToScreenY(float worldY) {
        return (int) (worldY - y);
    }
    
    public float screenToWorldX(float screenX) {
        return screenX + x;
    }
    
    public float screenToWorldY(float screenY) {
        return screenY + y;
    }
}
