package com.frightnight.game;

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
 * Realistic terrain system with hills, valleys, and distant mountains
 * Creates a dynamic 3D landscape with height variation
 */
public class TerrainSystem {
    
    private Model terrainModel;
    private ModelInstance terrainInstance;
    private Array<ModelInstance> mountainInstances;
    
    private int gridSize = 50; // 50x50 grid
    private float cellSize = 4f; // Each cell is 4 units
    private float[][] heightMap;
    
    /**
     * Create terrain with procedural hills and valleys
     */
    public TerrainSystem(ModelBuilder modelBuilder) {
        mountainInstances = new Array<>();
        
        // Generate height map with rolling hills
        generateHeightMap();
        
        // Build terrain mesh
        buildTerrainMesh(modelBuilder);
        
        // Create distant mountains
        createDistantMountains(modelBuilder);
    }
    
    /**
     * Generate procedural height map with rolling hills
     */
    private void generateHeightMap() {
        heightMap = new float[gridSize + 1][gridSize + 1];
        
        // Create rolling hills using multiple sine waves (Perlin-like)
        for (int x = 0; x <= gridSize; x++) {
            for (int z = 0; z <= gridSize; z++) {
                float height = 0;
                
                // Multiple octaves of sine waves for natural terrain
                height += Math.sin(x * 0.1f) * Math.cos(z * 0.1f) * 4f; // Large hills
                height += Math.sin(x * 0.3f + 17) * Math.cos(z * 0.3f + 23) * 2f; // Medium bumps
                height += Math.sin(x * 0.5f + 42) * Math.cos(z * 0.5f + 31) * 1f; // Small detail
                
                // Add some randomness
                height += (float)(Math.random() * 0.5 - 0.25);
                
                // Flatten the center area slightly (where player starts)
                float centerX = gridSize / 2f;
                float centerZ = gridSize / 2f;
                float distToCenter = (float)Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                if (distToCenter < 8) {
                    height *= distToCenter / 8f; // Gradual flatten
                }
                
                heightMap[x][z] = height;
            }
        }
    }
    
    /**
     * Build 3D terrain mesh from height map
     */
    private void buildTerrainMesh(ModelBuilder modelBuilder) {
        modelBuilder.begin();
        
        // Dark grass material with slight variation
        Material terrainMaterial = new Material(
            ColorAttribute.createDiffuse(0.12f, 0.22f, 0.10f, 1f)
        );
        
        MeshPartBuilder meshBuilder = modelBuilder.part(
            "terrain",
            GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked,
            terrainMaterial
        );
        
        // Build grid of triangles
        for (int x = 0; x < gridSize; x++) {
            for (int z = 0; z < gridSize; z++) {
                // Get corner heights
                float h00 = heightMap[x][z];
                float h10 = heightMap[x + 1][z];
                float h01 = heightMap[x][z + 1];
                float h11 = heightMap[x + 1][z + 1];
                
                // Convert to world coordinates
                float worldX = (x - gridSize / 2f) * cellSize;
                float worldZ = (z - gridSize / 2f) * cellSize;
                
                Vector3 v00 = new Vector3(worldX, h00, worldZ);
                Vector3 v10 = new Vector3(worldX + cellSize, h10, worldZ);
                Vector3 v01 = new Vector3(worldX, h01, worldZ + cellSize);
                Vector3 v11 = new Vector3(worldX + cellSize, h11, worldZ + cellSize);
                
                // Slight color variation based on height
                Color c00 = getTerrainColor(h00);
                Color c10 = getTerrainColor(h10);
                Color c01 = getTerrainColor(h01);
                Color c11 = getTerrainColor(h11);
                
                // First triangle
                meshBuilder.setColor(c00);
                meshBuilder.vertex(v00.x, v00.y, v00.z);
                meshBuilder.setColor(c10);
                meshBuilder.vertex(v10.x, v10.y, v10.z);
                meshBuilder.setColor(c01);
                meshBuilder.vertex(v01.x, v01.y, v01.z);
                
                // Second triangle
                meshBuilder.setColor(c10);
                meshBuilder.vertex(v10.x, v10.y, v10.z);
                meshBuilder.setColor(c11);
                meshBuilder.vertex(v11.x, v11.y, v11.z);
                meshBuilder.setColor(c01);
                meshBuilder.vertex(v01.x, v01.y, v01.z);
            }
        }
        
        terrainModel = modelBuilder.end();
        terrainInstance = new ModelInstance(terrainModel);
    }
    
    /**
     * Get terrain color based on height (darker in valleys, lighter on hills)
     */
    private Color getTerrainColor(float height) {
        // ULTRA BRIGHT - must be visible!
        float brightness = 0.5f + height * 0.1f; // Much higher base
        brightness = Math.max(0.5f, Math.min(0.9f, brightness)); // Very bright range
        return new Color(brightness * 0.7f, brightness, brightness * 0.5f, 1f); // Bright green
    }
    
    /**
     * Create distant mountains on the horizon
     */
    private void createDistantMountains(ModelBuilder modelBuilder) {
        // Create 8 mountain peaks around the perimeter
        for (int i = 0; i < 8; i++) {
            float angle = (float)(i * 45); // Every 45 degrees
            float distance = 180f + (float)(Math.random() * 40); // Far away
            
            float x = (float)Math.cos(Math.toRadians(angle)) * distance;
            float z = (float)Math.sin(Math.toRadians(angle)) * distance;
            float height = 30f + (float)(Math.random() * 40); // 30-70 units tall
            float width = 20f + (float)(Math.random() * 30);
            
            // Dark mountain material
            Material mountainMaterial = new Material(
                ColorAttribute.createDiffuse(0.08f, 0.08f, 0.12f, 1f) // Very dark blue-gray
            );
            
            // Create cone-shaped mountain
            Model mountainModel = modelBuilder.createCone(
                width, height, width,
                16, // Segments
                mountainMaterial,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
            );
            
            ModelInstance mountain = new ModelInstance(mountainModel);
            mountain.transform.setToTranslation(x, height / 2f - 5f, z); // Half submerged
            mountainInstances.add(mountain);
        }
    }
    
    /**
     * Get height at specific world position (for placing objects)
     */
    public float getHeightAt(float worldX, float worldZ) {
        // Convert world coords to grid coords
        float gridX = (worldX / cellSize) + gridSize / 2f;
        float gridZ = (worldZ / cellSize) + gridSize / 2f;
        
        // Clamp to grid bounds
        int x0 = Math.max(0, Math.min(gridSize - 1, (int)gridX));
        int z0 = Math.max(0, Math.min(gridSize - 1, (int)gridZ));
        
        // Simple bilinear interpolation
        float fx = gridX - x0;
        float fz = gridZ - z0;
        
        int x1 = Math.min(gridSize, x0 + 1);
        int z1 = Math.min(gridSize, z0 + 1);
        
        float h00 = heightMap[x0][z0];
        float h10 = heightMap[x1][z0];
        float h01 = heightMap[x0][z1];
        float h11 = heightMap[x1][z1];
        
        float h0 = h00 + (h10 - h00) * fx;
        float h1 = h01 + (h11 - h01) * fx;
        
        return h0 + (h1 - h0) * fz;
    }
    
    public ModelInstance getTerrainInstance() {
        return terrainInstance;
    }
    
    public Array<ModelInstance> getMountainInstances() {
        return mountainInstances;
    }
    
    public void dispose() {
        if (terrainModel != null) {
            terrainModel.dispose();
        }
        for (ModelInstance mountain : mountainInstances) {
            mountain.model.dispose();
        }
    }
}
