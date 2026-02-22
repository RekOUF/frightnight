package com.frightnight.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Winding path through the forest
 * Creates a visible trail for player to follow
 */
public class ForestPath {
    
    private Array<Vector3> pathPoints;
    private Array<ModelInstance> pathSegments;
    private float pathWidth = 2.5f;
    
    /**
     * Create a winding path through the terrain
     */
    public ForestPath(ModelBuilder modelBuilder, TerrainSystem terrain, Texture pathTexture) {
        pathPoints = new Array<>();
        pathSegments = new Array<>();
        
        // Generate winding path
        generatePathPoints();
        
        // Build path mesh segments with texture
        buildPath(modelBuilder, terrain, pathTexture);
    }
    
    /**
     * Generate path control points (winding through forest)
     */
    private void generatePathPoints() {
        // Start near player spawn
        pathPoints.add(new Vector3(-20, 0, -20));
        
        // Create winding path with curves
        float x = -20;
        float z = -20;
        
        for (int i = 0; i < 15; i++) {
            // Add some curve to the path
            float angle = (float)(Math.random() * 60 - 30); // -30 to +30 degrees
            float distance = 8f + (float)(Math.random() * 8f); // 8-16 units
            
            x += (float)Math.cos(Math.toRadians(angle)) * distance;
            z += (float)Math.sin(Math.toRadians(angle)) * distance;
            
            // Keep within reasonable bounds
            x = Math.max(-70, Math.min(70, x));
            z = Math.max(-70, Math.min(70, z));
            
            pathPoints.add(new Vector3(x, 0, z));
        }
    }
    
    /**
     * Build path mesh from control points with texture
     */
    private void buildPath(ModelBuilder modelBuilder, TerrainSystem terrain, Texture pathTexture) {
        Material pathMaterial = new Material(
            TextureAttribute.createDiffuse(pathTexture)
        );
        
        // Create path segments between each pair of points
        for (int i = 0; i < pathPoints.size - 1; i++) {
            Vector3 p1 = pathPoints.get(i);
            Vector3 p2 = pathPoints.get(i + 1);
            
            // Calculate path height from terrain - RAISED ABOVE GROUND
            p1.y = terrain.getHeightAt(p1.x, p1.z) + 0.15f; // Higher above terrain
            p2.y = terrain.getHeightAt(p2.x, p2.z) + 0.15f;
            
            // Calculate path direction
            Vector3 direction = new Vector3(p2).sub(p1);
            float length = direction.len();
            direction.nor();
            
            // Calculate perpendicular for path width
            Vector3 perpendicular = new Vector3(-direction.z, 0, direction.x).nor();
            
            // Create quad for path segment
            modelBuilder.begin();
            MeshPartBuilder meshBuilder = modelBuilder.part(
                "path_" + i,
                GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
                pathMaterial
            );
            
            // Path vertices (slightly wider quad)
            Vector3 v1 = new Vector3(p1).add(new Vector3(perpendicular).scl(pathWidth / 2));
            Vector3 v2 = new Vector3(p1).add(new Vector3(perpendicular).scl(-pathWidth / 2));
            Vector3 v3 = new Vector3(p2).add(new Vector3(perpendicular).scl(pathWidth / 2));
            Vector3 v4 = new Vector3(p2).add(new Vector3(perpendicular).scl(-pathWidth / 2));
            
            // Normal pointing up
            Vector3 normal = new Vector3(0, 1, 0);
            
            // Texture coordinates (repeat texture along path)
            float uRepeat = length / 5f; // Repeat every 5 units
            
            // First triangle (v1, v2, v3)
            MeshPartBuilder.VertexInfo vi1 = new MeshPartBuilder.VertexInfo().setPos(v1).setNor(normal).setUV(0, 0);
            MeshPartBuilder.VertexInfo vi2 = new MeshPartBuilder.VertexInfo().setPos(v2).setNor(normal).setUV(1, 0);
            MeshPartBuilder.VertexInfo vi3 = new MeshPartBuilder.VertexInfo().setPos(v3).setNor(normal).setUV(0, uRepeat);
            meshBuilder.triangle(vi1, vi2, vi3);
            
            // Second triangle (v2, v4, v3)
            MeshPartBuilder.VertexInfo vi4 = new MeshPartBuilder.VertexInfo().setPos(v4).setNor(normal).setUV(1, uRepeat);
            meshBuilder.triangle(vi2, vi4, vi3);
            
            Model segmentModel = modelBuilder.end();
            ModelInstance segment = new ModelInstance(segmentModel);
            pathSegments.add(segment);
        }
    }
    
    /**
     * Get the nearest path point to a position
     */
    public Vector3 getNearestPathPoint(Vector3 position) {
        Vector3 nearest = pathPoints.first();
        float minDist = position.dst(nearest);
        
        for (Vector3 point : pathPoints) {
            float dist = position.dst(point);
            if (dist < minDist) {
                minDist = dist;
                nearest = point;
            }
        }
        
        return new Vector3(nearest);
    }
    
    /**
     * Check if position is on or near path
     */
    public boolean isOnPath(Vector3 position, float tolerance) {
        for (int i = 0; i < pathPoints.size - 1; i++) {
            Vector3 p1 = pathPoints.get(i);
            Vector3 p2 = pathPoints.get(i + 1);
            
            // Distance from point to line segment
            float dist = distanceToSegment(position, p1, p2);
            if (dist <= pathWidth / 2 + tolerance) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Calculate distance from point to line segment
     */
    private float distanceToSegment(Vector3 point, Vector3 lineStart, Vector3 lineEnd) {
        Vector3 line = new Vector3(lineEnd).sub(lineStart);
        float lineLength = line.len();
        line.nor();
        
        Vector3 toPoint = new Vector3(point).sub(lineStart);
        float projection = toPoint.dot(line);
        
        if (projection <= 0) {
            return toPoint.len();
        } else if (projection >= lineLength) {
            return new Vector3(point).sub(lineEnd).len();
        } else {
            Vector3 closest = new Vector3(lineStart).add(new Vector3(line).scl(projection));
            return new Vector3(point).sub(closest).len();
        }
    }
    
    public Array<ModelInstance> getPathSegments() {
        return pathSegments;
    }
    
    public Array<Vector3> getPathPoints() {
        return pathPoints;
    }
    
    public void dispose() {
        for (ModelInstance segment : pathSegments) {
            segment.model.dispose();
        }
    }
}
