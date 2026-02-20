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
    
    public void update() {
        // Move towards target
        int dx = targetX - x;
        int dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > speed) {
            x += (int) (dx / distance * speed);
            y += (int) (dy / distance * speed);
        } else {
            x = targetX;
            y = targetY;
        }
        
        // Update invincibility
        if (invincible && System.currentTimeMillis() - invincibilityStart > INVINCIBILITY_DURATION) {
            invincible = false;
        }
    }
    
    public void draw(Canvas canvas, Paint paint) {
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
        canvas.drawCircle(x, y, size, paint);
        
        // Draw eyes
        paint.setColor(Color.BLACK);
        canvas.drawCircle(x - 10, y - 5, 5, paint);
        canvas.drawCircle(x + 10, y - 5, 5, paint);
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
