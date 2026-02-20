package com.frightnight.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

/**
 * Animated lightning background for MainActivity
 * Shows periodic lightning flashes across the screen
 */
public class LightningBackgroundView extends View {
    private Paint paint;
    private Random random;
    private int flashAlpha;
    private long lastFlashTime;
    private boolean isFlashing;
    
    public LightningBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        random = new Random();
        flashAlpha = 0;
        lastFlashTime = System.currentTimeMillis();
        isFlashing = false;
        
        // Start animation
        postInvalidateDelayed(16); // ~60 FPS
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Dark night background
        paint.setColor(Color.parseColor("#0A0A1E"));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        
        // Lightning flash effect
        if (flashAlpha > 0) {
            paint.setColor(Color.argb(flashAlpha, 255, 255, 255));
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }
        
        // Update flash
        updateFlash();
        
        // Continue animation
        postInvalidateDelayed(16);
    }
    
    private void updateFlash() {
        long currentTime = System.currentTimeMillis();
        
        // Trigger new flash randomly (every 3-7 seconds)
        if (!isFlashing && currentTime - lastFlashTime > 3000 + random.nextInt(4000)) {
            isFlashing = true;
            flashAlpha = 60; // Less intense than in-game
            lastFlashTime = currentTime;
        }
        
        // Fade out flash quickly
        if (flashAlpha > 0) {
            flashAlpha -= 5;
            if (flashAlpha < 0) {
                flashAlpha = 0;
                isFlashing = false;
            }
        }
    }
}
