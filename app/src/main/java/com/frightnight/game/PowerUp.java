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
    
    public void draw(Canvas canvas, Paint paint, Camera camera) {
        int screenX = camera.worldToScreenX(x);
        int screenY = camera.worldToScreenY(y);
        
        // Pulsating effect
        long time = System.currentTimeMillis() - spawnTime;
        int pulseSize = size + (int) (Math.sin(time / 200.0) * 5);
        
        // Draw star shape (power-up)
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(screenX, screenY, pulseSize, paint);
        
        // Draw cross inside
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        canvas.drawLine(screenX - 10, screenY, screenX + 10, screenY, paint);
        canvas.drawLine(screenX, screenY - 10, screenX, screenY + 10, paint);
        paint.setStrokeWidth(1);
    }
    
    public boolean collidesWith(Player player) {
        Rect playerBounds = player.getBounds();
        Rect powerUpBounds = new Rect(x - size, y - size, x + size, y + size);
        return Rect.intersects(playerBounds, powerUpBounds);
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
}
