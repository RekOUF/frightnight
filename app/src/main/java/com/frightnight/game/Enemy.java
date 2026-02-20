package com.frightnight.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Enemy {
    private int x, y;
    private int size = 35;
    private int speed = 3;
    
    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void update(int playerX, int playerY) {
        // Move towards player
        int dx = playerX - x;
        int dy = playerY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            x += (int) (dx / distance * speed);
            y += (int) (dy / distance * speed);
        }
    }
    
    public void draw(Canvas canvas, Paint paint) {
        // Draw monster body (red circle)
        paint.setColor(Color.RED);
        canvas.drawCircle(x, y, size, paint);
        
        // Draw evil eyes (yellow)
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(x - 12, y - 8, 6, paint);
        canvas.drawCircle(x + 12, y - 8, 6, paint);
        
        // Draw pupils (black)
        paint.setColor(Color.BLACK);
        canvas.drawCircle(x - 12, y - 8, 3, paint);
        canvas.drawCircle(x + 12, y - 8, 3, paint);
        
        // Draw scary mouth
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        canvas.drawLine(x - 15, y + 10, x + 15, y + 10, paint);
        paint.setStrokeWidth(1);
    }
    
    public boolean collidesWith(Player player) {
        Rect playerBounds = player.getBounds();
        Rect enemyBounds = new Rect(x - size, y - size, x + size, y + size);
        return Rect.intersects(playerBounds, enemyBounds);
    }
    
    public boolean isOffScreen(int screenWidth, int screenHeight) {
        return x < -100 || x > screenWidth + 100 || y < -100 || y > screenHeight + 100;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
}
