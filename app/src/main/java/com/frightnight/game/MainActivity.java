package com.frightnight.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button playButton;
    private TextView highScoreText;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("FrightNightPrefs", Context.MODE_PRIVATE);
        
        playButton = findViewById(R.id.playButton);
        highScoreText = findViewById(R.id.highScoreText);

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
