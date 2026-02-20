package com.frightnight.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
                startGame();
            }
        });
    }

    private void startGame() {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update high score when returning from game
        int highScore = prefs.getInt("highScore", 0);
        highScoreText.setText(String.format(getString(R.string.high_score), highScore));
    }
}
