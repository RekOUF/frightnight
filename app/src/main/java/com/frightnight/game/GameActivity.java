package com.frightnight.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

/**
 * Game Activity using LibGDX 3D
 * Integrates with existing Android app infrastructure
 */
public class GameActivity extends AndroidApplication {
    private FrightNightGame3D game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load scary level from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("FrightNightPrefs", Context.MODE_PRIVATE);
        int scaryLevel = prefs.getInt("scaryLevel", 0);
        
        // Create LibGDX game
        game = new FrightNightGame3D(scaryLevel);
        
        // Configure LibGDX
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useGyroscope = false;
        config.useImmersiveMode = true; // Fullscreen
        
        // Initialize LibGDX
        initialize(game, config);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Save high score when pausing
        if (game != null && !game.isGameOver()) {
            saveScore(game.getScore());
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Save final score
        if (game != null) {
            saveScore(game.getScore());
        }
    }
    
    private void saveScore(int score) {
        SharedPreferences prefs = getSharedPreferences("FrightNightPrefs", Context.MODE_PRIVATE);
        int currentHighScore = prefs.getInt("highScore", 0);
        
        if (score > currentHighScore) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore", score);
            editor.apply();
        }
    }
}
