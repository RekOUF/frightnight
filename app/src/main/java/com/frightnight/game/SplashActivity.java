package com.frightnight.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Splash screen shown on app startup
 * Displays splash, checks for updates, plays thunder sound
 */
public class SplashActivity extends Activity {
    
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private MediaPlayer thunderPlayer;
    private boolean versionChecked = false;
    private boolean splashComplete = false;
    private String updateVersion = null;
    private String updateUrl = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Play thunder sound
        playThunderSound();
        
        // Check for updates asynchronously
        checkForUpdates();
        
        // Wait for splash duration
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashComplete = true;
                proceedIfReady();
            }
        }, SPLASH_DURATION);
    }
    
    private void playThunderSound() {
        try {
            thunderPlayer = MediaPlayer.create(this, R.raw.thunder);
            if (thunderPlayer != null) {
                thunderPlayer.setVolume(0.3f, 0.3f); // Soft thunder
                thunderPlayer.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing thunder sound", e);
        }
    }
    
    private void checkForUpdates() {
        VersionChecker.checkForUpdate(new VersionChecker.VersionCheckListener() {
            @Override
            public void onVersionChecked(boolean updateAvailable, String latestVersion, String downloadUrl) {
                versionChecked = true;
                if (updateAvailable) {
                    updateVersion = latestVersion;
                    updateUrl = downloadUrl;
                }
                proceedIfReady();
            }
            
            @Override
            public void onError(String error) {
                Log.d(TAG, "Version check error: " + error);
                versionChecked = true;
                proceedIfReady();
            }
        });
    }
    
    private void proceedIfReady() {
        if (splashComplete && versionChecked) {
            if (updateVersion != null) {
                showUpdateDialog(updateVersion, updateUrl);
            } else {
                goToMainActivity();
            }
        }
    }
    
    private void showUpdateDialog(final String version, final String downloadUrl) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(SplashActivity.this)
                        .setTitle("Update Beschikbaar!")
                        .setMessage("Een nieuwe versie (v" + version + ") is beschikbaar op GitHub!\n\n" +
                                "Download de nieuwste versie voor nieuwe features en bug fixes.")
                        .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                                startActivity(browserIntent);
                                goToMainActivity();
                            }
                        })
                        .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goToMainActivity();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }
    
    private void goToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thunderPlayer != null) {
            thunderPlayer.release();
            thunderPlayer = null;
        }
    }
}
