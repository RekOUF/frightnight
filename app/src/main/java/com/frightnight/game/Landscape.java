package com.frightnight.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.Random;

public class Landscape {
    private int worldWidth, worldHeight;
    private Random random;
    private ArrayList<Tree> trees;
    private ArrayList<Cloud> clouds;
    private int roadY;
    private int roadWidth;
    private int moonX, moonY;
    private ArrayList<LightningBolt> lightningBolts;
    private long lastLightningTime;
    private boolean showLightning;
    private int lightningFlashAlpha;
    
    // 4 voetbalvelden = ongeveer 420m x 270m
    // Schaal: 3 pixels per meter = 1260 x 810 pixels
    public static final int WORLD_WIDTH = 2400;
    public static final int WORLD_HEIGHT = 1600;
    private static final int FENCE_MARGIN = 50;
    
    public Landscape() {
        this.worldWidth = WORLD_WIDTH;
        this.worldHeight = WORLD_HEIGHT;
        this.random = new Random();
        this.trees = new ArrayList<>();
        this.clouds = new ArrayList<>();
        
        // Road in center (horizontal)
        roadY = worldHeight / 2 - 75;
        roadWidth = 150;
        
        // Moon position
        moonX = worldWidth - 300;
        moonY = 200;
        
        // Lightning system
        lightningBolts = new ArrayList<>();
        lastLightningTime = System.currentTimeMillis();
        showLightning = false;
        lightningFlashAlpha = 0;
        
        // Generate scary trees
        generateTrees();
        
        // Generate dark clouds
        generateClouds();
    }
    
    private void generateTrees() {
        // Trees along the road
        for (int i = 0; i < 30; i++) {
            int x = random.nextInt(worldWidth - 200) + 100;
            int y;
            
            // Trees above and below road
            if (random.nextBoolean()) {
                y = random.nextInt(roadY - 100) + 50;
            } else {
                y = random.nextInt(worldHeight - (roadY + roadWidth + 100)) + roadY + roadWidth + 50;
            }
            
            trees.add(new Tree(x, y));
        }
        
        // Trees along fence borders
        for (int i = 0; i < worldWidth; i += 100) {
            trees.add(new Tree(i, FENCE_MARGIN + 30));
            trees.add(new Tree(i, worldHeight - FENCE_MARGIN - 30));
        }
        for (int i = 0; i < worldHeight; i += 100) {
            trees.add(new Tree(FENCE_MARGIN + 30, i));
            trees.add(new Tree(worldWidth - FENCE_MARGIN - 30, i));
        }
    }
    
    private void generateClouds() {
        // More clouds for ominous atmosphere
        for (int i = 0; i < 12; i++) {
            int x = random.nextInt(worldWidth);
            int y = random.nextInt(400) + 50;
            clouds.add(new Cloud(x, y));
        }
    }
    
    public void update() {
        // Move clouds slowly
        for (Cloud cloud : clouds) {
            cloud.x += 0.2f;
            if (cloud.x > worldWidth + 100) {
                cloud.x = -100;
            }
        }
        
        // Update lightning
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLightningTime > 5000 + random.nextInt(5000)) {
            // Trigger lightning every 5-10 seconds
            showLightning = true;
            lightningFlashAlpha = 80;
            lastLightningTime = currentTime;
            generateLightning();
        }
        
        // Fade out lightning flash
        if (lightningFlashAlpha > 0) {
            lightningFlashAlpha -= 8;
            if (lightningFlashAlpha < 0) lightningFlashAlpha = 0;
        }
        
        // Clear lightning after flash
        if (lightningFlashAlpha == 0 && showLightning) {
            showLightning = false;
            lightningBolts.clear();
        }
    }
    
    private void generateLightning() {
        lightningBolts.clear();
        // Create FRIGHTNIGHT text with lightning effect
        String text = "FRIGHTNIGHT";
        int startX = worldWidth / 2 - 400;
        int startY = 250;
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int letterX = startX + i * 80;
            lightningBolts.add(new LightningBolt(letterX, startY, c));
        }
    }
    
    public boolean isLightningActive() {
        return showLightning;
    }
    
    public void draw(Canvas canvas, Paint paint, Camera camera) {
        // Dark night sky
        paint.setColor(Color.parseColor("#0A0A1E"));
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        
        // Lightning flash effect
        if (lightningFlashAlpha > 0) {
            paint.setColor(Color.argb(lightningFlashAlpha, 255, 255, 255));
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        }
        
        // Moon
        drawMoon(canvas, paint, camera);
        
        // Dark clouds
        drawClouds(canvas, paint, camera);
        
        // Lightning bolts forming FRIGHTNIGHT
        if (showLightning) {
            drawLightning(canvas, paint, camera);
        }
        
        // Dark grass
        paint.setColor(Color.parseColor("#0F2F0F"));
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        
        // Road
        drawRoad(canvas, paint, camera);
        
        // Trees
        drawTrees(canvas, paint, camera);
        
        // Fence
        drawFence(canvas, paint, camera);
    }
    
    private void drawMoon(Canvas canvas, Paint paint, Camera camera) {
        int screenX = camera.worldToScreenX(moonX);
        int screenY = camera.worldToScreenY(moonY);
        
        if (screenX > -150 && screenX < canvas.getWidth() + 150 && 
            screenY > -150 && screenY < canvas.getHeight() + 150) {
            // Moon glow
            paint.setColor(Color.parseColor("#505070"));
            canvas.drawCircle(screenX, screenY, 85, paint);
            
            // Full moon
            paint.setColor(Color.parseColor("#E0E0E0"));
            canvas.drawCircle(screenX, screenY, 70, paint);
            
            // Moon craters (details)
            paint.setColor(Color.parseColor("#C0C0C0"));
            canvas.drawCircle(screenX - 20, screenY - 15, 12, paint);
            canvas.drawCircle(screenX + 15, screenY + 10, 10, paint);
            canvas.drawCircle(screenX + 5, screenY - 25, 8, paint);
        }
    }
    
    private void drawClouds(Canvas canvas, Paint paint, Camera camera) {
        // Dark ominous clouds
        paint.setColor(Color.parseColor("#1A1A2E"));
        for (Cloud cloud : clouds) {
            int screenX = camera.worldToScreenX(cloud.x);
            int screenY = camera.worldToScreenY(cloud.y);
            
            if (screenX > -150 && screenX < canvas.getWidth() + 150) {
                // Larger, darker clouds
                canvas.drawCircle(screenX, screenY, 50, paint);
                canvas.drawCircle(screenX + 40, screenY, 45, paint);
                canvas.drawCircle(screenX - 40, screenY, 45, paint);
                canvas.drawCircle(screenX + 20, screenY - 25, 40, paint);
                
                // Darker bottom edge
                paint.setColor(Color.parseColor("#0F0F1A"));
                canvas.drawCircle(screenX, screenY + 20, 40, paint);
                canvas.drawCircle(screenX + 30, screenY + 15, 35, paint);
                paint.setColor(Color.parseColor("#1A1A2E"));
            }
        }
    }
    
    private void drawLightning(Canvas canvas, Paint paint, Camera camera) {
        paint.setTextSize(60);
        paint.setStyle(Paint.Style.FILL);
        
        for (LightningBolt bolt : lightningBolts) {
            int screenX = camera.worldToScreenX(bolt.x);
            int screenY = camera.worldToScreenY(bolt.y);
            
            if (screenX > -100 && screenX < canvas.getWidth() + 100) {
                // Lightning glow
                paint.setColor(Color.parseColor("#8080FF"));
                paint.setTextSize(70);
                canvas.drawText(String.valueOf(bolt.letter), screenX - 20, screenY, paint);
                
                // Lightning text
                paint.setColor(Color.parseColor("#FFFFFF"));
                paint.setTextSize(60);
                canvas.drawText(String.valueOf(bolt.letter), screenX - 20, screenY, paint);
                
                // Electric core
                paint.setColor(Color.parseColor("#FFFF00"));
                paint.setTextSize(55);
                canvas.drawText(String.valueOf(bolt.letter), screenX - 18, screenY - 2, paint);
            }
        }
        
        paint.setStyle(Paint.Style.FILL);
    }
    
    private void drawRoad(Canvas canvas, Paint paint, Camera camera) {
        int screenY = camera.worldToScreenY(roadY);
        
        // Road
        paint.setColor(Color.parseColor("#505050"));
        canvas.drawRect(0, screenY, canvas.getWidth(), screenY + roadWidth, paint);
        
        // Road lines
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(5);
        for (int x = 0; x < worldWidth; x += 60) {
            int screenX = camera.worldToScreenX(x);
            if (screenX > -30 && screenX < canvas.getWidth() + 30) {
                canvas.drawLine(screenX, screenY + roadWidth/2, 
                               screenX + 30, screenY + roadWidth/2, paint);
            }
        }
        paint.setStrokeWidth(1);
    }
    
    private void drawTrees(Canvas canvas, Paint paint, Camera camera) {
        for (Tree tree : trees) {
            int screenX = camera.worldToScreenX(tree.x);
            int screenY = camera.worldToScreenY(tree.y);
            
            if (screenX > -50 && screenX < canvas.getWidth() + 50 && 
                screenY > -70 && screenY < canvas.getHeight() + 70) {
                // Trunk (dark brown)
                paint.setColor(Color.parseColor("#2F1B0C"));
                canvas.drawRect(screenX - 8, screenY, screenX + 8, screenY + 40, paint);
                
                // Leaves (dark green/black for scary effect)
                paint.setColor(Color.parseColor("#0A3D0A"));
                canvas.drawCircle(screenX, screenY - 10, 35, paint);
                canvas.drawCircle(screenX - 20, screenY, 25, paint);
                canvas.drawCircle(screenX + 20, screenY, 25, paint);
                
                // Evil eyes
                paint.setColor(Color.parseColor("#FFFF00"));
                canvas.drawCircle(screenX - 10, screenY - 10, 5, paint);
                canvas.drawCircle(screenX + 10, screenY - 10, 5, paint);
                
                paint.setColor(Color.RED);
                canvas.drawCircle(screenX - 10, screenY - 10, 2, paint);
                canvas.drawCircle(screenX + 10, screenY - 10, 2, paint);
            }
        }
    }
    
    private void drawFence(Canvas canvas, Paint paint, Camera camera) {
        paint.setColor(Color.parseColor("#3E2723"));
        paint.setStrokeWidth(4);
        
        // Top fence
        int y = camera.worldToScreenY(FENCE_MARGIN);
        if (y > -10 && y < canvas.getHeight() + 10) {
            for (int x = 0; x < worldWidth; x += 20) {
                int screenX = camera.worldToScreenX(x);
                if (screenX > -20 && screenX < canvas.getWidth() + 20) {
                    canvas.drawLine(screenX, y, screenX, y + 30, paint);
                }
            }
            canvas.drawLine(0, y + 15, canvas.getWidth(), y + 15, paint);
        }
        
        // Bottom fence
        y = camera.worldToScreenY(worldHeight - FENCE_MARGIN);
        if (y > -10 && y < canvas.getHeight() + 10) {
            for (int x = 0; x < worldWidth; x += 20) {
                int screenX = camera.worldToScreenX(x);
                if (screenX > -20 && screenX < canvas.getWidth() + 20) {
                    canvas.drawLine(screenX, y, screenX, y + 30, paint);
                }
            }
            canvas.drawLine(0, y + 15, canvas.getWidth(), y + 15, paint);
        }
        
        // Left fence
        int x = camera.worldToScreenX(FENCE_MARGIN);
        if (x > -10 && x < canvas.getWidth() + 10) {
            for (int yPos = 0; yPos < worldHeight; yPos += 20) {
                int screenY = camera.worldToScreenY(yPos);
                if (screenY > -20 && screenY < canvas.getHeight() + 20) {
                    canvas.drawLine(x, screenY, x + 30, screenY, paint);
                }
            }
            canvas.drawLine(x + 15, 0, x + 15, canvas.getHeight(), paint);
        }
        
        // Right fence
        x = camera.worldToScreenX(worldWidth - FENCE_MARGIN);
        if (x > -10 && x < canvas.getWidth() + 10) {
            for (int yPos = 0; yPos < worldHeight; yPos += 20) {
                int screenY = camera.worldToScreenY(yPos);
                if (screenY > -20 && screenY < canvas.getHeight() + 20) {
                    canvas.drawLine(x, screenY, x + 30, screenY, paint);
                }
            }
            canvas.drawLine(x + 15, 0, x + 15, canvas.getHeight(), paint);
        }
        
        paint.setStrokeWidth(1);
    }
    
    public boolean isInBounds(int x, int y, int margin) {
        return x >= FENCE_MARGIN + margin && 
               x <= worldWidth - FENCE_MARGIN - margin &&
               y >= FENCE_MARGIN + margin && 
               y <= worldHeight - FENCE_MARGIN - margin;
    }
    
    public int getWorldWidth() { return worldWidth; }
    public int getWorldHeight() { return worldHeight; }
    
    private static class Tree {
        float x, y;
        Tree(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
    
    private static class Cloud {
        float x, y;
        Cloud(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
    
    private static class LightningBolt {
        int x, y;
        char letter;
        LightningBolt(int x, int y, char letter) {
            this.x = x;
            this.y = y;
            this.letter = letter;
        }
    }
}
