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
        
        // Initialize player in the center
        player = new Player(screenWidth / 2, screenHeight / 2);
        
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
                return;
            }
            
            // Remove off-screen enemies
            if (enemy.isOffScreen(screenWidth, screenHeight)) {
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
            } else if (powerUp.isOffScreen(screenWidth, screenHeight)) {
                powerUps.remove(i);
            }
        }
        
        player.update();
        score++;
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            
            // Draw background
            canvas.drawColor(Color.parseColor("#1A1A1A"));
            
            // Draw score
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            canvas.drawText("Score: " + score, 50, 60, paint);
            
            // Draw player
            player.draw(canvas, paint);
            
            // Draw enemies
            for (Enemy enemy : enemies) {
                enemy.draw(canvas, paint);
            }
            
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
    }

    private void sleep() {
        try {
            Thread.sleep(17); // ~60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void spawnEnemy() {
        int side = random.nextInt(4); // 0=top, 1=right, 2=bottom, 3=left
        int x, y;
        
        switch (side) {
            case 0: // Top
                x = random.nextInt(screenWidth);
                y = -50;
                break;
            case 1: // Right
                x = screenWidth + 50;
                y = random.nextInt(screenHeight);
                break;
            case 2: // Bottom
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
