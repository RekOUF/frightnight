package com.frightnight.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Animated grass field that sways in the wind
 * Creates atmosphere and realism
 */
public class WindGrassField {
    
    private Array<GrassPatch> grassPatches;
    private float windPhase = 0f;
    private Vector3 windDirection = new Vector3(1, 0, 0.5f).nor();
    
    /**
     * Create grass field across the terrain
     */
    public WindGrassField(ModelBuilder modelBuilder, TerrainSystem terrain, ForestPath path) {
        grassPatches = new Array<>();
        
        // Create grass patches across terrain (avoiding path)
        for (int i = 0; i < 150; i++) {
            float x = (float)(Math.random() * 160 - 80);
            float z = (float)(Math.random() * 160 - 80);
            
            // Don't place grass on path
            if (path != null && path.isOnPath(new Vector3(x, 0, z), 1.5f)) {
                continue;
            }
            
            float y = terrain.getHeightAt(x, z);
            
            GrassPatch patch = new GrassPatch(modelBuilder, x, y, z);
            grassPatches.add(patch);
        }
        
        Gdx.app.log("FrightNight", "Created " + grassPatches.size + " grass patches");
    }
    
    /**
     * Update grass animation (wind effect)
     */
    public void update(float delta) {
        windPhase += delta * 0.8f; // Wind speed
        
        // Update each grass patch
        for (GrassPatch patch : grassPatches) {
            patch.update(windPhase, windDirection);
        }
    }
    
    /**
     * Get all grass instances for rendering
     */
    public Array<ModelInstance> getInstances() {
        Array<ModelInstance> instances = new Array<>();
        for (GrassPatch patch : grassPatches) {
            instances.add(patch.getInstance());
        }
        return instances;
    }
    
    public void dispose() {
        for (GrassPatch patch : grassPatches) {
            patch.dispose();
        }
    }
    
    /**
     * Individual grass patch (cluster of grass blades)
     */
    private static class GrassPatch {
        private ModelInstance instance;
        private Vector3 basePosition;
        private float swayAmount;
        private float phaseOffset;
        
        public GrassPatch(ModelBuilder modelBuilder, float x, float y, float z) {
            this.basePosition = new Vector3(x, y, z);
            this.swayAmount = 0.1f + (float)(Math.random() * 0.15f);
            this.phaseOffset = (float)(Math.random() * Math.PI * 2);
            
            // Create grass cluster (small vertical quad) - SUPER BRIGHT GREEN!
            Material grassMaterial = new Material(
                ColorAttribute.createDiffuse(0.5f, 0.9f, 0.4f, 1f) // Ultra bright green
            );
            
            // Simple grass blade model (thin box)
            Model grassModel = modelBuilder.createBox(
                0.15f, 0.6f, 0.02f, // Thin vertical blade
                grassMaterial,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
            );
            
            instance = new ModelInstance(grassModel);
            instance.transform.setToTranslation(basePosition.x, basePosition.y + 0.3f, basePosition.z);
        }
        
        /**
         * Update grass swaying in wind
         */
        public void update(float windPhase, Vector3 windDirection) {
            // Calculate sway based on wind phase
            float sway = (float)Math.sin(windPhase + phaseOffset) * swayAmount;
            
            // Reset to base position
            instance.transform.setToTranslation(
                basePosition.x + windDirection.x * sway,
                basePosition.y + 0.3f,
                basePosition.z + windDirection.z * sway
            );
            
            // Slight rotation for more natural look
            instance.transform.rotate(Vector3.X, sway * 15f);
        }
        
        public ModelInstance getInstance() {
            return instance;
        }
        
        public void dispose() {
            instance.model.dispose();
        }
    }
}
