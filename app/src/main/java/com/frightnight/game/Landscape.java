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
    private int sunX, sunY;
    
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
        
        // Sun position
        sunX = worldWidth - 200;
        sunY = 150;
        
        // Generate scary trees
        generateTrees();
        
        // Generate clouds
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
        for (int i = 0; i < 8; i++) {
            int x = random.nextInt(worldWidth);
            int y = random.nextInt(300) + 50;
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
    }
    
    public void draw(Canvas canvas, Paint paint, Camera camera) {
        // Sky gradient (blue)
        paint.setColor(Color.parseColor("#87CEEB"));
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        
        // Sun
        drawSun(canvas, paint, camera);
        
        // Clouds
        drawClouds(canvas, paint, camera);
        
        // Grass
        paint.setColor(Color.parseColor("#228B22"));
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        
        // Road
        drawRoad(canvas, paint, camera);
        
        // Trees
        drawTrees(canvas, paint, camera);
        
        // Fence
        drawFence(canvas, paint, camera);
    }
    
    private void drawSun(Canvas canvas, Paint paint, Camera camera) {
        int screenX = camera.worldToScreenX(sunX);
        int screenY = camera.worldToScreenY(sunY);
        
        if (screenX > -100 && screenX < canvas.getWidth() + 100 && 
            screenY > -100 && screenY < canvas.getHeight() + 100) {
            // Sun glow
            paint.setColor(Color.parseColor("#FFFF99"));
            canvas.drawCircle(screenX, screenY, 70, paint);
            
            // Sun
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(screenX, screenY, 50, paint);
        }
    }
    
    private void drawClouds(Canvas canvas, Paint paint, Camera camera) {
        paint.setColor(Color.WHITE);
        for (Cloud cloud : clouds) {
            int screenX = camera.worldToScreenX(cloud.x);
            int screenY = camera.worldToScreenY(cloud.y);
            
            if (screenX > -150 && screenX < canvas.getWidth() + 150) {
                canvas.drawCircle(screenX, screenY, 40, paint);
                canvas.drawCircle(screenX + 30, screenY, 35, paint);
                canvas.drawCircle(screenX - 30, screenY, 35, paint);
                canvas.drawCircle(screenX + 15, screenY - 20, 30, paint);
            }
        }
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
}
