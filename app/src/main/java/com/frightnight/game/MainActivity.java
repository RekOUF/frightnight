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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
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
        
        // Check for updates on startup
        checkForUpdates();
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
    
    private void checkForUpdates() {
        VersionChecker.checkForUpdate(new VersionChecker.VersionCheckListener() {
            @Override
            public void onVersionChecked(boolean updateAvailable, String latestVersion, String downloadUrl) {
                if (updateAvailable) {
                    showUpdateDialog(latestVersion, downloadUrl);
                } else {
                    Log.d(TAG, "App is up to date");
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error checking for updates: " + error);
                // Silently fail - don't bother user with update check errors
            }
        });
    }
    
    private void showUpdateDialog(final String version, final String downloadUrl) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Update Available")
                    .setMessage("A new version (v" + version + ") is available on GitHub!\n\n" +
                               "Would you like to download it?")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Open GitHub release page in browser
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                            startActivity(browserIntent);
                        }
                    })
                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .show();
            }
        });
    }
}
