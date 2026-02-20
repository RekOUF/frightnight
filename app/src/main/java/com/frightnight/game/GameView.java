package com.frightnight.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {
    private Thread gameThread;
    private volatile boolean isPlaying;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private Canvas canvas;
    
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<PowerUp> powerUps;
    private Random random;
    private Camera camera;
    private Landscape landscape;
    private MediaPlayer lightningSound;
    private boolean wasLightningActive;
    
    private int screenWidth;
    private int screenHeight;
    private int score;
    private int highScore;
    private int scaryLevel;
    private long lastEnemySpawn;
    private long lastPowerUpSpawn;
    private boolean isGameOver;
    
    private static final int ENEMY_SPAWN_INTERVAL = 2000; // 2 seconds
    private static final int POWERUP_SPAWN_INTERVAL = 5000; // 5 seconds
    
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        surfaceHolder = getHolder();
        paint = new Paint();
        random = new Random();
        
        enemies = new ArrayList<>();
        powerUps = new ArrayList<>();
        
        // Load high score and scary level
        SharedPreferences prefs = context.getSharedPreferences("FrightNightPrefs", Context.MODE_PRIVATE);
        highScore = prefs.getInt("highScore", 0);
        scaryLevel = prefs.getInt("scaryLevel", 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        
        // Initialize landscape and camera
        landscape = new Landscape();
        camera = new Camera(screenWidth, screenHeight, 
                           Landscape.WORLD_WIDTH, Landscape.WORLD_HEIGHT);
        
        // Initialize player in the center of world
        player = new Player(Landscape.WORLD_WIDTH / 2, Landscape.WORLD_HEIGHT / 2);
        
        score = 0;
        isGameOver = false;
        lastEnemySpawn = System.currentTimeMillis();
        lastPowerUpSpawn = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        if (isGameOver) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Update landscape
        landscape.update();
        
        // Play lightning sound when lightning strikes
        if (landscape.isLightningActive() && !wasLightningActive) {
            playLightningSound();
        }
        wasLightningActive = landscape.isLightningActive();
        
        // Only spawn enemies and power-ups if scary level > 0
        if (scaryLevel > 0) {
            // Spawn enemies
            if (currentTime - lastEnemySpawn > ENEMY_SPAWN_INTERVAL) {
                spawnEnemy();
                lastEnemySpawn = currentTime;
            }
            
            // Spawn power-ups
            if (currentTime - lastPowerUpSpawn > POWERUP_SPAWN_INTERVAL) {
                spawnPowerUp();
                lastPowerUpSpawn = currentTime;
            }
        }
        
        // Update enemies
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(player.getX(), player.getY());
            
            // Check collision with player
            if (enemy.collidesWith(player) && !player.isInvincible()) {
                gameOver();
                return;
            }
            
            // Remove enemies that are too far from player
            int dx = enemy.getX() - player.getX();
            int dy = enemy.getY() - player.getY();
            if (Math.sqrt(dx * dx + dy * dy) > 1500) {
                enemies.remove(i);
                if (scaryLevel > 0) {
                    score += 10; // Bonus for surviving
                }
            }
        }
        
        // Update power-ups
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            
            // Check collision with player
            if (powerUp.collidesWith(player)) {
                player.activateInvincibility();
                powerUps.remove(i);
                if (scaryLevel > 0) {
                    score += 50;
                }
            }
        }
        
        // Update player and enforce boundaries
        player.update(landscape);
        
        // Update camera to follow player
        camera.centerOn(player.getX(), player.getY());
        
        // Only increment score if in survival mode (scary level > 0)
        if (scaryLevel > 0) {
            score++;
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            
            // Draw landscape (sky, grass, road, trees, fence)
            landscape.draw(canvas, paint, camera);
            
            // Draw power-ups (in world coordinates)
            for (PowerUp powerUp : powerUps) {
                powerUp.draw(canvas, paint, camera);
            }
            
            // Draw player (in world coordinates)
            player.draw(canvas, paint, camera);
            
            // Draw enemies (in world coordinates)
            for (Enemy enemy : enemies) {
                enemy.draw(canvas, paint, camera);
            }
            
            // Draw UI overlay - always on screen
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(10, 10, 320, 80, paint);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(10, 10, 320, 80, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(40);
            
            // Show different info based on scary level
            if (scaryLevel == 0) {
                canvas.drawText("🌳 Exploration", 30, 55, paint);
            } else {
                canvas.drawText("Score: " + score, 30, 55, paint);
            }
            
            // Draw game over
            if (isGameOver) {
                paint.setColor(Color.RED);
                paint.setTextSize(100);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("GAME OVER", screenWidth / 2, screenHeight / 2, paint);
                
                paint.setTextSize(50);
                canvas.drawText("Score: " + score, screenWidth / 2, screenHeight / 2 + 80, paint);
                canvas.drawText("Tap to return", screenWidth / 2, screenHeight / 2 + 140, paint);
                paint.setTextAlign(Paint.Align.LEFT);
            }
            
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17); // ~60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void spawnEnemy() {
        // Spawn enemies around player position (off-screen)
        int side = random.nextInt(4); // 0=top, 1=right, 2=bottom, 3=left
        int x, y;
        int spawnDistance = 400; // Distance from player
        
        int playerX = player.getX();
        int playerY = player.getY();
        
        switch (side) {
            case 0: // Top
                x = playerX + random.nextInt(600) - 300;
                y = playerY - spawnDistance;
                break;
            case 1: // Right
                x = playerX + spawnDistance;
                y = playerY + random.nextInt(600) - 300;
                break;
            case 2: // Bottom
                x = playerX + random.nextInt(600) - 300;
                y = playerY + spawnDistance;
                break;
            default: // Left
                x = playerX - spawnDistance;
                y = playerY + random.nextInt(600) - 300;
                break;
        }
        
        // Ensure within world bounds
        x = Math.max(100, Math.min(x, Landscape.WORLD_WIDTH - 100));
        y = Math.max(100, Math.min(y, Landscape.WORLD_HEIGHT - 100));
        
        enemies.add(new Enemy(x, y));
    }

    private void spawnPowerUp() {
        // Spawn power-up somewhere in visible area around player
        int playerX = player.getX();
        int playerY = player.getY();
        
        int x = playerX + random.nextInt(400) - 200;
        int y = playerY + random.nextInt(400) - 200;
        
        // Ensure within world bounds
        x = Math.max(100, Math.min(x, Landscape.WORLD_WIDTH - 100));
        y = Math.max(100, Math.min(y, Landscape.WORLD_HEIGHT - 100));
        
        powerUps.add(new PowerUp(x, y));
    }

    private void gameOver() {
        isGameOver = true;
        
        // Update high score
        if (score > highScore) {
            highScore = score;
            SharedPreferences prefs = getContext().getSharedPreferences("FrightNightPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore", highScore);
            editor.apply();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            ((GameActivity) getContext()).finish();
            return true;
        }
        
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            // Convert screen coordinates to world coordinates
            float worldX = camera.screenToWorldX(event.getX());
            float worldY = camera.screenToWorldY(event.getY());
            player.moveTo((int) worldX, (int) worldY);
        }
        
        return true;
    }

    public void pause() {
        isPlaying = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Cleanup lightning sound
        if (lightningSound != null) {
            lightningSound.release();
            lightningSound = null;
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    private void playLightningSound() {
        try {
            // Create new MediaPlayer for each strike
            if (lightningSound != null) {
                lightningSound.release();
            }
            lightningSound = MediaPlayer.create(getContext(), R.raw.lightning);
            if (lightningSound != null) {
                lightningSound.setVolume(0.8f, 0.8f); // Loud but not deafening
                lightningSound.start();
            }
        } catch (Exception e) {
            // Silently fail - don't crash game for sound issues
        }
    }
}
