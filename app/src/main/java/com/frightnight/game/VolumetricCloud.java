package com.frightnight.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Creates realistic volumetric clouds using multiple overlapping spheres
 * with varying sizes and transparency for a puffy, atmospheric effect
 */
public class VolumetricCloud {
    
    private Array<ModelInstance> cloudParts;
    private Vector3 position;
    private float driftSpeed;
    private float driftAngle;
    
    /**
     * Create a volumetric cloud at specified position
     * @param modelBuilder The shared model builder
     * @param x World X position
     * @param y World Y position (height)
     * @param z World Z position
     * @param scale Overall cloud size multiplier
     */
    public VolumetricCloud(ModelBuilder modelBuilder, float x, float y, float z, float scale) {
        this.position = new Vector3(x, y, z);
        this.cloudParts = new Array<>();
        this.driftSpeed = 0.5f + (float)(Math.random() * 1.5f); // Random drift speed
        this.driftAngle = (float)(Math.random() * 360); // Random drift direction
        
        // Create cloud material with transparency
        Material cloudMaterial = new Material(
            ColorAttribute.createDiffuse(0.7f, 0.6f, 0.7f, 1f), // Purple-tinted dusk clouds
            new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.6f) // Semi-transparent
        );
        
        // Create main cloud body with multiple overlapping spheres
        int numPuffs = 8 + (int)(Math.random() * 6); // 8-14 puffs per cloud
        
        for (int i = 0; i < numPuffs; i++) {
            // Random offset for each puff
            float offsetX = (float)(Math.random() * 12 - 6) * scale;
            float offsetY = (float)(Math.random() * 4 - 2) * scale;
            float offsetZ = (float)(Math.random() * 8 - 4) * scale;
            
            // Random size for each puff (creates irregular shape)
            float puffSize = (2f + (float)(Math.random() * 3f)) * scale;
            
            // Random transparency variation
            float alpha = 0.5f + (float)(Math.random() * 0.3f);
            Material puffMaterial = new Material(
                ColorAttribute.createDiffuse(0.7f, 0.6f, 0.7f, 1f),
                new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, alpha)
            );
            
            // Create sphere puff
            Model puffModel = modelBuilder.createSphere(
                puffSize, puffSize, puffSize * 0.8f, // Slightly flattened
                12, 10,
                puffMaterial,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
            );
            
            ModelInstance puff = new ModelInstance(puffModel);
            puff.transform.setToTranslation(
                position.x + offsetX,
                position.y + offsetY,
                position.z + offsetZ
            );
            
            cloudParts.add(puff);
        }
    }
    
    /**
     * Update cloud position (drifting slowly across sky)
     */
    public void update(float delta) {
        // Drift slowly across sky
        float moveX = (float)Math.cos(Math.toRadians(driftAngle)) * driftSpeed * delta;
        float moveZ = (float)Math.sin(Math.toRadians(driftAngle)) * driftSpeed * delta;
        
        position.x += moveX;
        position.z += moveZ;
        
        // Update all puff positions
        for (int i = 0; i < cloudParts.size; i++) {
            ModelInstance puff = cloudParts.get(i);
            Vector3 currentPos = new Vector3();
            puff.transform.getTranslation(currentPos);
            currentPos.x += moveX;
            currentPos.z += moveZ;
            puff.transform.setTranslation(currentPos);
        }
        
        // Wrap around world boundaries
        if (position.x > 150) position.x = -150;
        if (position.x < -150) position.x = 150;
        if (position.z > 150) position.z = -150;
        if (position.z < -150) position.z = 150;
    }
    
    /**
     * Get all cloud part instances for rendering
     */
    public Array<ModelInstance> getInstances() {
        return cloudParts;
    }
    
    /**
     * Cleanup cloud models
     */
    public void dispose() {
        for (ModelInstance part : cloudParts) {
            part.model.dispose();
        }
        cloudParts.clear();
    }
    
    public Vector3 getPosition() {
        return position;
    }
}
