package com.frightnight.game;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker {
    private static final String TAG = "VersionChecker";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/RekOUF/frightnight/releases/latest";
    private static final String CURRENT_VERSION = "2.3";
    
    public interface VersionCheckListener {
        void onVersionChecked(boolean updateAvailable, String latestVersion, String downloadUrl);
        void onError(String error);
    }
    
    public static void checkForUpdate(VersionCheckListener listener) {
        new CheckVersionTask(listener).execute();
    }
    
    private static class CheckVersionTask extends AsyncTask<Void, Void, VersionResult> {
        private VersionCheckListener listener;
        
        CheckVersionTask(VersionCheckListener listener) {
            this.listener = listener;
        }
        
        @Override
        protected VersionResult doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                URL url = new URL(GITHUB_API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String latestVersion = jsonResponse.getString("tag_name").replace("v", "");
                    String downloadUrl = jsonResponse.getString("html_url");
                    
                    boolean updateAvailable = compareVersions(CURRENT_VERSION, latestVersion) < 0;
                    
                    return new VersionResult(true, updateAvailable, latestVersion, downloadUrl, null);
                } else {
                    return new VersionResult(false, false, null, null, "HTTP Error: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error checking version", e);
                return new VersionResult(false, false, null, null, e.getMessage());
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        
        @Override
        protected void onPostExecute(VersionResult result) {
            if (listener != null) {
                if (result.success) {
                    listener.onVersionChecked(result.updateAvailable, result.latestVersion, result.downloadUrl);
                } else {
                    listener.onError(result.error);
                }
            }
        }
    }
    
    private static int compareVersions(String version1, String version2) {
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");
        
        int length = Math.max(v1Parts.length, v2Parts.length);
        for (int i = 0; i < length; i++) {
            int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;
            
            if (v1Part < v2Part) return -1;
            if (v1Part > v2Part) return 1;
        }
        return 0;
    }
    
    private static class VersionResult {
        boolean success;
        boolean updateAvailable;
        String latestVersion;
        String downloadUrl;
        String error;
        
        VersionResult(boolean success, boolean updateAvailable, String latestVersion, 
                     String downloadUrl, String error) {
            this.success = success;
            this.updateAvailable = updateAvailable;
            this.latestVersion = latestVersion;
            this.downloadUrl = downloadUrl;
            this.error = error;
        }
    }
}
