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
 * Realistic scary tree with actual branches and leaves
 * Creates a creepy, twisted tree structure
 */
public class RealisticTree {
    
    private Array<ModelInstance> parts;
    private Vector3 position;
    private float height;
    
    /**
     * Create a realistic tree with branches
     * @param modelBuilder Shared model builder
     * @param x World X position
     * @param y Ground height at this position
     * @param z World Z position
     * @param isScary If true, makes twisted evil-looking tree
     */
    public RealisticTree(ModelBuilder modelBuilder, float x, float y, float z, boolean isScary) {
        this.position = new Vector3(x, y, z);
        this.parts = new Array<>();
        this.height = 5f + (float)(Math.random() * 4f); // 5-9 units tall
        
        if (isScary) {
            createScaryTree(modelBuilder);
        } else {
            createNormalTree(modelBuilder);
        }
    }
    
    /**
     * Create twisted, scary tree perfect for horror atmosphere
     */
    private void createScaryTree(ModelBuilder modelBuilder) {
        // Very dark, almost black bark
        Material barkMaterial = new Material(
            ColorAttribute.createDiffuse(0.08f, 0.06f, 0.05f, 1f)
        );
        
        // Dead-looking dark leaves
        Material leafMaterial = new Material(
            ColorAttribute.createDiffuse(0.1f, 0.15f, 0.08f, 1f)
        );
        
        float trunkRadius = 0.3f + (float)(Math.random() * 0.2f);
        
        // Main trunk (slightly twisted)
        Model trunk = modelBuilder.createCylinder(
            trunkRadius * 2, height * 0.6f, trunkRadius * 2,
            8,
            barkMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        ModelInstance trunkInstance = new ModelInstance(trunk);
        trunkInstance.transform.setToTranslation(position.x, position.y + height * 0.3f, position.z);
        // Slight twist/rotation for creepy effect
        trunkInstance.transform.rotate(Vector3.Z, (float)(Math.random() * 10 - 5));
        parts.add(trunkInstance);
        
        // Create 4-7 twisted branches
        int numBranches = 4 + (int)(Math.random() * 4);
        
        for (int i = 0; i < numBranches; i++) {
            float branchHeight = height * (0.4f + (float)(Math.random() * 0.4f));
            float angle = (float)(Math.random() * 360);
            float branchLength = 1.5f + (float)(Math.random() * 1.5f);
            float branchThickness = trunkRadius * (0.3f + (float)(Math.random() * 0.2f));
            
            // Create branch
            Model branch = modelBuilder.createCylinder(
                branchThickness * 2, branchLength, branchThickness * 2,
                6,
                barkMaterial,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
            );
            
            ModelInstance branchInstance = new ModelInstance(branch);
            
            // Position at trunk
            float branchX = position.x + (float)Math.cos(Math.toRadians(angle)) * trunkRadius;
            float branchY = position.y + branchHeight;
            float branchZ = position.z + (float)Math.sin(Math.toRadians(angle)) * trunkRadius;
            
            branchInstance.transform.setToTranslation(branchX, branchY, branchZ);
            
            // Rotate branch outward (30-60 degrees up)
            branchInstance.transform.rotate(Vector3.Y, angle);
            branchInstance.transform.rotate(Vector3.X, 30 + (float)(Math.random() * 30));
            
            // Twist branch for creepy look
            branchInstance.transform.rotate(Vector3.Z, (float)(Math.random() * 20 - 10));
            
            parts.add(branchInstance);
            
            // Add small leaf clusters on branch ends
            float leafX = branchX + (float)Math.cos(Math.toRadians(angle)) * branchLength * 0.7f;
            float leafY = branchY + branchLength * 0.5f;
            float leafZ = branchZ + (float)Math.sin(Math.toRadians(angle)) * branchLength * 0.7f;
            
            Model leaves = modelBuilder.createSphere(
                0.8f, 0.8f, 0.8f,
                8, 8,
                leafMaterial,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
            );
            
            ModelInstance leafInstance = new ModelInstance(leaves);
            leafInstance.transform.setToTranslation(leafX, leafY, leafZ);
            parts.add(leafInstance);
        }
        
        // Add sparse top foliage
        Model topLeaves = modelBuilder.createSphere(
            1.2f, 1.5f, 1.2f,
            10, 8,
            leafMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        
        ModelInstance topInstance = new ModelInstance(topLeaves);
        topInstance.transform.setToTranslation(position.x, position.y + height * 0.9f, position.z);
        parts.add(topInstance);
    }
    
    /**
     * Create normal (but still dark) tree
     */
    private void createNormalTree(ModelBuilder modelBuilder) {
        // Dark bark
        Material barkMaterial = new Material(
            ColorAttribute.createDiffuse(0.15f, 0.10f, 0.08f, 1f)
        );
        
        // Dark green leaves
        Material leafMaterial = new Material(
            ColorAttribute.createDiffuse(0.12f, 0.20f, 0.12f, 1f)
        );
        
        float trunkRadius = 0.25f + (float)(Math.random() * 0.15f);
        
        // Main trunk
        Model trunk = modelBuilder.createCylinder(
            trunkRadius * 2, height * 0.7f, trunkRadius * 2,
            8,
            barkMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        ModelInstance trunkInstance = new ModelInstance(trunk);
        trunkInstance.transform.setToTranslation(position.x, position.y + height * 0.35f, position.z);
        parts.add(trunkInstance);
        
        // Create 3-5 branches
        int numBranches = 3 + (int)(Math.random() * 3);
        
        for (int i = 0; i < numBranches; i++) {
            float branchHeight = height * (0.5f + (float)(Math.random() * 0.3f));
            float angle = (float)(i * (360f / numBranches) + Math.random() * 30);
            float branchLength = 1.2f + (float)(Math.random() * 1.0f);
            
            Model branch = modelBuilder.createCylinder(
                trunkRadius, branchLength, trunkRadius,
                6,
                barkMaterial,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
            );
            
            ModelInstance branchInstance = new ModelInstance(branch);
            
            float branchX = position.x + (float)Math.cos(Math.toRadians(angle)) * trunkRadius;
            float branchY = position.y + branchHeight;
            float branchZ = position.z + (float)Math.sin(Math.toRadians(angle)) * trunkRadius;
            
            branchInstance.transform.setToTranslation(branchX, branchY, branchZ);
            branchInstance.transform.rotate(Vector3.Y, angle);
            branchInstance.transform.rotate(Vector3.X, 40 + (float)(Math.random() * 20));
            
            parts.add(branchInstance);
            
            // Leaf cluster
            float leafX = branchX + (float)Math.cos(Math.toRadians(angle)) * branchLength * 0.6f;
            float leafY = branchY + branchLength * 0.4f;
            float leafZ = branchZ + (float)Math.sin(Math.toRadians(angle)) * branchLength * 0.6f;
            
            Model leaves = modelBuilder.createSphere(
                1.0f, 1.0f, 1.0f,
                10, 8,
                leafMaterial,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
            );
            
            ModelInstance leafInstance = new ModelInstance(leaves);
            leafInstance.transform.setToTranslation(leafX, leafY, leafZ);
            parts.add(leafInstance);
        }
        
        // Top foliage
        Model topLeaves = modelBuilder.createSphere(
            1.5f, 2.0f, 1.5f,
            12, 10,
            leafMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        
        ModelInstance topInstance = new ModelInstance(topLeaves);
        topInstance.transform.setToTranslation(position.x, position.y + height * 0.85f, position.z);
        parts.add(topInstance);
    }
    
    public Array<ModelInstance> getParts() {
        return parts;
    }
    
    public Vector3 getPosition() {
        return position;
    }
    
    public void dispose() {
        for (ModelInstance part : parts) {
            part.model.dispose();
        }
        parts.clear();
    }
}
