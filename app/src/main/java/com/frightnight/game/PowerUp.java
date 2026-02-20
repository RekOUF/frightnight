package com.frightnight.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class PowerUp {
    private int x, y;
    private int size = 25;
    private long spawnTime;
    
    public PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
        this.spawnTime = System.currentTimeMillis();
    }
    
    public void draw(Canvas canvas, Paint paint) {
        // Pulsating effect
        long time = System.currentTimeMillis() - spawnTime;
        int pulseSize = size + (int) (Math.sin(time / 200.0) * 5);
        
        // Draw star shape (power-up)
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(x, y, pulseSize, paint);
        
        // Draw cross inside
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        canvas.drawLine(x - 10, y, x + 10, y, paint);
        canvas.drawLine(x, y - 10, x, y + 10, paint);
        paint.setStrokeWidth(1);
    }
    
    public boolean collidesWith(Player player) {
        Rect playerBounds = player.getBounds();
        Rect powerUpBounds = new Rect(x - size, y - size, x + size, y + size);
        return Rect.intersects(playerBounds, powerUpBounds);
    }
    
    public boolean isOffScreen(int screenWidth, int screenHeight) {
        // Power-ups don't move, so they're never off-screen unless screen changes
        return false;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
}
