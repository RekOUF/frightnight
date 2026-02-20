package com.frightnight.game;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;

/**
 * Splash screen shown on app startup
 * Displays splash, checks for updates, plays thunder sound
 */
public class SplashActivity extends Activity {
    
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private static final int PERMISSION_REQUEST_CODE = 100;
    private MediaPlayer thunderPlayer;
    private boolean versionChecked = false;
    private boolean splashComplete = false;
    private String updateVersion = null;
    private String updateUrl = null;
    private long downloadId = -1;
    private DownloadManager downloadManager;
    private BroadcastReceiver downloadReceiver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        
        // Play thunder sound
        playThunderSound();
        
        // Request permissions first
        requestStoragePermission();
        
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
        
        // Register download complete receiver
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    installApk();
                }
            }
        };
        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Storage permission granted");
            } else {
                Toast.makeText(this, "Storage permission nodig voor updates", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void playThunderSound() {
        try {
            thunderPlayer = MediaPlayer.create(this, R.raw.thunder);
            if (thunderPlayer != null) {
                thunderPlayer.setVolume(1.0f, 1.0f); // LOUD thunder!
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
                        .setTitle("Update Beschikbaar! 🔥")
                        .setMessage("Een nieuwe versie (v" + version + ") is beschikbaar!\n\n" +
                                "Download en installeer automatisch?\n\n" +
                                "De APK wordt gedownload en je krijgt een installatie prompt.")
                        .setPositiveButton("Download & Installeer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get direct APK download URL
                                String apkUrl = downloadUrl.replace("/tag/", "/download/") + "/app-debug.apk";
                                downloadAndInstallApk(apkUrl, version);
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
    
    private void downloadAndInstallApk(String url, String version) {
        try {
            // Create download request
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle("Fright Night v" + version);
            request.setDescription("Nieuwe versie aan het downloaden...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            
            // Set destination
            String fileName = "FrightNight-v" + version + ".apk";
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            
            // Allow downloading on metered network
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);
            
            // Start download
            downloadId = downloadManager.enqueue(request);
            
            Toast.makeText(this, "Download gestart! Check notificaties...", Toast.LENGTH_LONG).show();
            
            // Continue to main activity (download happens in background)
            goToMainActivity();
            
        } catch (Exception e) {
            Log.e(TAG, "Error downloading APK", e);
            Toast.makeText(this, "Download fout: " + e.getMessage(), Toast.LENGTH_LONG).show();
            goToMainActivity();
        }
    }
    
    private void installApk() {
        try {
            // Query download manager for file
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor cursor = downloadManager.query(query);
            
            if (cursor.moveToFirst()) {
                int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (statusIndex >= 0) {
                    int status = cursor.getInt(statusIndex);
                    
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        if (uriIndex >= 0) {
                            String uriString = cursor.getString(uriIndex);
                            Uri fileUri = Uri.parse(uriString);
                            
                            // Install APK
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                // Use FileProvider for Android 7+
                                File file = new File(fileUri.getPath());
                                Uri contentUri = FileProvider.getUriForFile(this, 
                                        getPackageName() + ".provider", file);
                                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } else {
                                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                            }
                            
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error installing APK", e);
            Toast.makeText(this, "Installatie fout: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
        if (downloadReceiver != null) {
            try {
                unregisterReceiver(downloadReceiver);
            } catch (Exception e) {
                // Receiver not registered
            }
        }
    }
}
