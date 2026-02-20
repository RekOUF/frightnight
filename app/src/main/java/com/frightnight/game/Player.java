package com.frightnight.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Player {
    private int x, y;
    private int size = 40;
    private int targetX, targetY;
    private int speed = 8;
    private boolean invincible;
    private long invincibilityStart;
    private static final long INVINCIBILITY_DURATION = 3000; // 3 seconds
    
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.invincible = false;
    }
    
    public void moveTo(int targetX, int targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }
    
    public void update(Landscape landscape) {
        // Move towards target
        int dx = targetX - x;
        int dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > speed) {
            int newX = x + (int) (dx / distance * speed);
            int newY = y + (int) (dy / distance * speed);
            
            // Check boundaries
            if (landscape.isInBounds(newX, newY, size)) {
                x = newX;
                y = newY;
            } else {
                // Try moving only horizontally
                if (landscape.isInBounds(newX, y, size)) {
                    x = newX;
                }
                // Try moving only vertically
                if (landscape.isInBounds(x, newY, size)) {
                    y = newY;
                }
            }
        } else {
            if (landscape.isInBounds(targetX, targetY, size)) {
                x = targetX;
                y = targetY;
            }
        }
        
        // Update invincibility
        if (invincible && System.currentTimeMillis() - invincibilityStart > INVINCIBILITY_DURATION) {
            invincible = false;
        }
    }
    
    public void draw(Canvas canvas, Paint paint, Camera camera) {
        int screenX = camera.worldToScreenX(x);
        int screenY = camera.worldToScreenY(y);
        
        if (invincible) {
            // Flash between blue and white when invincible
            if ((System.currentTimeMillis() / 200) % 2 == 0) {
                paint.setColor(Color.CYAN);
            } else {
                paint.setColor(Color.WHITE);
            }
        } else {
            paint.setColor(Color.GREEN);
        }
        canvas.drawCircle(screenX, screenY, size, paint);
        
        // Draw eyes
        paint.setColor(Color.BLACK);
        canvas.drawCircle(screenX - 10, screenY - 5, 5, paint);
        canvas.drawCircle(screenX + 10, screenY - 5, 5, paint);
    }
    
    public void activateInvincibility() {
        invincible = true;
        invincibilityStart = System.currentTimeMillis();
    }
    
    public boolean isInvincible() {
        return invincible;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    
    public Rect getBounds() {
        return new Rect(x - size, y - size, x + size, y + size);
    }
}
