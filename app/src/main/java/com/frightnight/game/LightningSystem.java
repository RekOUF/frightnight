package com.frightnight.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

/**
 * Dramatic lightning and thunder system for horror atmosphere
 * Creates distant lightning bolts with screen flashes and thunder sounds
 */
public class LightningSystem {
    
    private ShapeRenderer shapeRenderer;
    private Sound thunderSound;
    private Sound lightningCrackSound;
    
    // Lightning state
    private boolean isFlashing = false;
    private float flashIntensity = 0f;
    private float flashDuration = 0.2f;
    private float flashTimer = 0f;
    
    // Lightning bolt
    private boolean showBolt = false;
    private float boltTimer = 0f;
    private float boltDuration = 0.15f;
    private Vector3 boltStart;
    private Vector3 boltEnd;
    
    // Timing
    private float timeSinceLastStrike = 0f;
    private float nextStrikeIn = 8f; // First strike in 8 seconds
    
    private ModelInstance lightningBolt;
    private Model boltModel;
    
    public LightningSystem() {
        shapeRenderer = new ShapeRenderer();
        
        // Load thunder sound (if available)
        try {
            thunderSound = Gdx.audio.newSound(Gdx.files.internal("thunder.ogg"));
            Gdx.app.log("FrightNight", "Thunder sound loaded successfully");
        } catch (Exception e) {
            Gdx.app.log("FrightNight", "Thunder sound not found (optional): " + e.getMessage());
            thunderSound = null;
        }
        
        // Try to load lightning crack sound
        try {
            lightningCrackSound = Gdx.audio.newSound(Gdx.files.internal("lightning.ogg"));
            Gdx.app.log("FrightNight", "Lightning sound loaded successfully");
        } catch (Exception e) {
            Gdx.app.log("FrightNight", "Lightning sound not found (optional): " + e.getMessage());
            lightningCrackSound = null;
        }
        
        boltStart = new Vector3();
        boltEnd = new Vector3();
    }
    
    /**
     * Update lightning timing and effects
     */
    public void update(float delta, Vector3 playerPosition) {
        timeSinceLastStrike += delta;
        
        // Time for a lightning strike?
        if (timeSinceLastStrike >= nextStrikeIn) {
            triggerLightning(playerPosition);
            timeSinceLastStrike = 0f;
            nextStrikeIn = 5f + (float)(Math.random() * 10f); // Next strike in 5-15 seconds
        }
        
        // Update flash effect
        if (isFlashing) {
            flashTimer += delta;
            
            // Fade out flash
            float progress = flashTimer / flashDuration;
            flashIntensity = 1f - progress;
            
            if (flashTimer >= flashDuration) {
                isFlashing = false;
                flashIntensity = 0f;
                flashTimer = 0f;
            }
        }
        
        // Update bolt visibility
        if (showBolt) {
            boltTimer += delta;
            if (boltTimer >= boltDuration) {
                showBolt = false;
                boltTimer = 0f;
            }
        }
    }
    
    /**
     * Trigger a lightning strike
     */
    private void triggerLightning(Vector3 playerPosition) {
        // Create lightning bolt in the distance
        float angle = (float)(Math.random() * 360);
        float distance = 80f + (float)(Math.random() * 50f); // 80-130 units away
        
        boltStart.set(
            playerPosition.x + (float)Math.cos(Math.toRadians(angle)) * distance,
            40f + (float)(Math.random() * 20f), // High in sky
            playerPosition.z + (float)Math.sin(Math.toRadians(angle)) * distance
        );
        
        boltEnd.set(
            boltStart.x + (float)(Math.random() * 10 - 5),
            0f, // Ground level
            boltStart.z + (float)(Math.random() * 10 - 5)
        );
        
        // Trigger visual effects
        isFlashing = true;
        flashTimer = 0f;
        flashIntensity = 0.8f;
        
        showBolt = true;
        boltTimer = 0f;
        
        // Play sounds
        if (lightningCrackSound != null) {
            lightningCrackSound.play(0.4f); // Lightning crack (loud)
        }
        
        // Thunder comes slightly after (speed of sound delay)
        if (thunderSound != null) {
            // Delay thunder based on distance (0.3-0.8 seconds)
            float thunderDelay = 0.3f + (distance / 200f);
            new Thread(() -> {
                try {
                    Thread.sleep((long)(thunderDelay * 1000));
                    if (thunderSound != null) {
                        thunderSound.play(0.5f + (float)(Math.random() * 0.3f)); // Rumbling thunder
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        
        Gdx.app.log("FrightNight", "Lightning strike at distance: " + distance);
    }
    
    /**
     * Render lightning effects
     */
    public void renderFlash() {
        if (isFlashing && flashIntensity > 0) {
            // Draw white flash overlay
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 1, flashIntensity * 0.6f); // White flash
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
            
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }
    
    /**
     * Render lightning bolt (if visible)
     */
    public void renderBolt() {
        if (showBolt) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            
            // Draw jagged lightning bolt
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setProjectionMatrix(Gdx.graphics.getWidth() > 0 ? 
                shapeRenderer.getProjectionMatrix() : shapeRenderer.getProjectionMatrix());
            
            // Draw main bolt (bright white/blue)
            shapeRenderer.setColor(0.8f, 0.9f, 1f, 0.9f);
            
            // Create jagged bolt path with segments
            int segments = 8;
            Vector3 current = new Vector3(boltStart);
            
            for (int i = 0; i < segments; i++) {
                float t = (float)(i + 1) / segments;
                Vector3 next = new Vector3(boltStart).lerp(boltEnd, t);
                
                // Add random jagged offset
                next.x += (float)(Math.random() * 4 - 2);
                next.z += (float)(Math.random() * 4 - 2);
                
                // Draw thick line segment (simulated with multiple thin lines)
                for (int thickness = 0; thickness < 3; thickness++) {
                    float offset = (thickness - 1) * 0.3f;
                    // Line drawing would be done in 3D space, but for now we'll just log it
                    // Actual rendering would need camera projection
                }
                
                current.set(next);
            }
            
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }
    
    public boolean isFlashing() {
        return isFlashing;
    }
    
    public float getFlashIntensity() {
        return flashIntensity;
    }
    
    /**
     * Cleanup resources
     */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (thunderSound != null) {
            thunderSound.dispose();
        }
        if (lightningCrackSound != null) {
            lightningCrackSound.dispose();
        }
        if (boltModel != null) {
            boltModel.dispose();
        }
    }
}
