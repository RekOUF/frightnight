package com.frightnight.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button playButton;
    private TextView highScoreText;
    private TextView scaryLevelValue;
    private SeekBar scaryLevelSeekBar;
    private SharedPreferences prefs;
    private int currentScaryLevel = 0;
    private MediaPlayer thunderPlayer;
    
    // Demo mode (attract mode)
    private Handler demoHandler;
    private Runnable demoRunnable;
    private static final long DEMO_DELAY_MS = 30000; // 30 seconds
    private boolean isDemoMode = false;
    
    private static final String[] SCARY_LEVEL_LABELS = {
        "0 - Vrij Wandelen 🌳",
        "1 - Beetje Spooky 👻",
        "2 - Licht Onrustig 😰",
        "3 - Verdacht 🤨",
        "4 - Eng Geluid 🔊",
        "5 - Schaduwen 🌑",
        "6 - Spanning 😨",
        "7 - Gevaarlijk 💀",
        "8 - Terrificerend 😱",
        "9 - Nachtmerrie 🔥",
        "10 - Pure Horror 💀🔥"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("FrightNightPrefs", Context.MODE_PRIVATE);
        
        playButton = findViewById(R.id.playButton);
        highScoreText = findViewById(R.id.highScoreText);
        scaryLevelValue = findViewById(R.id.scaryLevelValue);
        scaryLevelSeekBar = findViewById(R.id.scaryLevelSeekBar);

        // Load saved scary level
        currentScaryLevel = prefs.getInt("scaryLevel", 0);
        scaryLevelSeekBar.setProgress(currentScaryLevel);
        scaryLevelValue.setText(SCARY_LEVEL_LABELS[currentScaryLevel]);
        
        // SeekBar listener
        scaryLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentScaryLevel = progress;
                scaryLevelValue.setText(SCARY_LEVEL_LABELS[progress]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Save scary level when user stops dragging
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("scaryLevel", currentScaryLevel);
                editor.apply();
            }
        });
        
        // Load and display high score
        int highScore = prefs.getInt("highScore", 0);
        highScoreText.setText(String.format(getString(R.string.high_score), highScore));

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDemoMode();
                startGame(false);
            }
        });
        
        // Start looping thunder sound
        playThunderLoop();
        
        // Setup demo mode timer
        setupDemoMode();
    }
    
    private void setupDemoMode() {
        demoHandler = new Handler(Looper.getMainLooper());
        demoRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Demo mode activated - launching AI gameplay");
                isDemoMode = true;
                startGame(true);
            }
        };
        startDemoTimer();
    }
    
    private void startDemoTimer() {
        if (demoHandler != null && demoRunnable != null) {
            demoHandler.removeCallbacks(demoRunnable);
            demoHandler.postDelayed(demoRunnable, DEMO_DELAY_MS);
        }
    }
    
    private void cancelDemoMode() {
        if (demoHandler != null && demoRunnable != null) {
            demoHandler.removeCallbacks(demoRunnable);
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Reset demo timer on any touch
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startDemoTimer();
        }
        return super.dispatchTouchEvent(event);
    }

    private void startGame(boolean demoMode) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("DEMO_MODE", demoMode);
        intent.putExtra("DEMO_SCARY_LEVEL", 5); // Demo uses scary level 5 for excitement
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update high score when returning from game
        int highScore = prefs.getInt("highScore", 0);
        highScoreText.setText(String.format(getString(R.string.high_score), highScore));
        
        // Resume thunder if stopped
        if (thunderPlayer != null && !thunderPlayer.isPlaying()) {
            thunderPlayer.start();
        } else if (thunderPlayer == null) {
            playThunderLoop();
        }
        
        // Restart demo timer when returning to menu
        startDemoTimer();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Keep thunder playing in background
        // Cancel demo timer when leaving menu
        cancelDemoMode();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop and release thunder when app is destroyed
        if (thunderPlayer != null) {
            thunderPlayer.release();
            thunderPlayer = null;
        }
        // Cleanup demo handler
        cancelDemoMode();
    }
    
    private void playThunderLoop() {
        try {
            if (thunderPlayer != null) {
                thunderPlayer.release();
            }
            thunderPlayer = MediaPlayer.create(this, R.raw.thunder);
            if (thunderPlayer != null) {
                thunderPlayer.setVolume(0.5f, 0.5f); // Softer in menu
                thunderPlayer.setLooping(true); // Loop continuously!
                thunderPlayer.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing thunder loop", e);
        }
    }
}
