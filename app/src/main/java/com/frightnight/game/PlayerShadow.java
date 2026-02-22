package com.frightnight.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * Player shadow cast by moonlight
 * Creates realism and immersion
 */
public class PlayerShadow {
    
    private ModelInstance shadowInstance;
    private Vector3 moonDirection; // Direction FROM moon TO ground
    
    /**
     * Create player shadow
     */
    public PlayerShadow(ModelBuilder modelBuilder) {
        // Moon is at position (40, 50, -80), so direction is roughly:
        // From player toward opposite of moon
        moonDirection = new Vector3(-40, -50, 80).nor();
        
        // Create dark semi-transparent circle for shadow
        Material shadowMaterial = new Material(
            ColorAttribute.createDiffuse(0.02f, 0.02f, 0.02f, 1f),
            new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.6f)
        );
        
        // Flat ellipse for shadow
        Model shadowModel = modelBuilder.createCylinder(
            1.2f, 0.01f, 0.8f, // Slightly oval shape
            16,
            shadowMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        
        shadowInstance = new ModelInstance(shadowModel);
    }
    
    /**
     * Update shadow position based on player position and terrain
     */
    public void update(Vector3 playerPosition, TerrainSystem terrain) {
        // Calculate shadow offset from moon direction
        // Shadow is cast in opposite direction of moon
        Vector3 shadowOffset = new Vector3(moonDirection.x, 0, moonDirection.z).nor();
        shadowOffset.scl(0.5f); // Shadow slightly offset from player center
        
        // Shadow position on ground
        float shadowX = playerPosition.x + shadowOffset.x;
        float shadowZ = playerPosition.z + shadowOffset.z;
        float shadowY = terrain.getHeightAt(shadowX, shadowZ) + 0.02f; // Just above terrain
        
        shadowInstance.transform.setToTranslation(shadowX, shadowY, shadowZ);
        
        // Rotate shadow based on terrain slope (for realism)
        // This would require more complex terrain normal calculation
        // For now, keep it simple and flat
    }
    
    public ModelInstance getInstance() {
        return shadowInstance;
    }
    
    public void dispose() {
        shadowInstance.model.dispose();
    }
}
