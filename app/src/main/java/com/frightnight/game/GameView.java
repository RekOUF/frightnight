package com.frightnight.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
    
    private int screenWidth;
    private int screenHeight;
    private int score;
    private int highScore;
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
        
        // Load high score
        SharedPreferences prefs = context.getSharedPreferences("FrightNightPrefs", Context.MODE_PRIVATE);
        highScore = prefs.getInt("highScore", 0);
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
           Update landscape
        landscape.update();
        
        //  return;
        }
        
        long currentTime = System.currentTimeMillis();
        
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
        
        // Update enemies
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(player.getX(), player.getY());
            
            // Check collision with player
            if (enemy.collidesWith(player) && !player.isInvincible()) {
                gameOver();
                returnenemies that are too far from player
            int dx = enemy.getX() - player.getX();
            int dy = enemy.getY() - player.getY();
            if (Math.sqrt(dx * dx + dy * dy) > 1500) {
                enemies.remove(i);
                score += 10; // Bonus for surviving
            }
        }
        
        // Update power-ups
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            
            // Check collision with player
            if (powerUp.collidesWith(player)) {
                player.activateInvincibility();
                powerUps.remove(i);
                score += 50;
            }
        }
        
        // Update player and enforce boundaries
        player.update(landscape);
        
        // Update camera to follow player
        camera.centerOn(player.getX(), player.getY());
        
        }
        
        player.update();
        score++;
    }
landscape (sky, grass, road, trees, fence)
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
            
            // Draw UI overlay (score) - always on screen
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(10, 10, 280, 80, paint);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(10, 10, 280, 80, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(40);
            canvas.drawText("Score: " + score, 30, 55, paint);
            
            // Draw power-ups
            for (PowerUp powerUp : powerUps) {
                powerUp.draw(canvas, paint);
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
    }// Spawn enemies around player position (off-screen)
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
        y = Math.max(100, Math.min(y, Landscape.WORLD_HEIGHT - 100));       x = screenWidth + 50;
                y = random.nextInt(screenHeight);
        // Spawn power-up somewhere in visible area around player
        int playerX = player.getX();
        int playerY = player.getY();
        
        int x = playerX + random.nextInt(400) - 200;
        int y = playerY + random.nextInt(400) - 200;
        
        // Ensure within world bounds
        x = Math.max(100, Math.min(x, Landscape.WORLD_WIDTH - 100));
        y = Math.max(100, Math.min(y, Landscape.WORLD_HEIGHT - 100));
        
                x = random.nextInt(screenWidth);
                y = screenHeight + 50;
                break;
            default: // Left
                x = -50;
                y = random.nextInt(screenHeight);
                break;
        }
        
        enemies.add(new Enemy(x, y));
    }

    private void spawnPowerUp() {
        int x = random.nextInt(screenWidth - 100) + 50;
        int y = random.nextInt(screenHeight - 100) + 50;
        powerUps.add(new PowerUp(x, y));
    }

    private void gameOver() {
        isGameOver = true;
        
        // U// Convert screen coordinates to world coordinates
            float worldX = camera.screenToWorldX(event.getX());
            float worldY = camera.screenToWorldY(event.getY());
            player.moveTo((int) worldX, (int) worldY
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
            player.moveTo((int) event.getX(), (int) event.getY());
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
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
