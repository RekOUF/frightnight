package com.frightnight.game;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * Animated bird flying through the sky
 * Creates atmosphere and life in the horror environment
 */
public class FlyingBird {
    
    private ModelInstance body;
    private ModelInstance leftWing;
    private ModelInstance rightWing;
    
    private Vector3 position;
    private Vector3 velocity;
    private Vector3 targetPosition;
    
    private float wingFlapTimer = 0f;
    private float wingFlapSpeed = 3f; // Flaps per second
    private float wingAngle = 0f;
    private float maxWingAngle = 45f;
    
    private float flightSpeed;
    private float flightHeight;
    private float circleRadius;
    private float circleAngle;
    private boolean isCircling;
    
    /**
     * Create a flying bird
     * @param modelBuilder Shared model builder
     * @param startX Starting X position
     * @param startY Starting Y position (height)
     * @param startZ Starting Z position
     * @param isDark True for dark/crow-like bird, false for lighter bird
     */
    public FlyingBird(ModelBuilder modelBuilder, float startX, float startY, float startZ, boolean isDark) {
        this.position = new Vector3(startX, startY, startZ);
        this.velocity = new Vector3();
        
        // Random flight characteristics
        this.flightSpeed = 8f + (float)(Math.random() * 6f);
        this.flightHeight = startY;
        this.circleRadius = 20f + (float)(Math.random() * 30f);
        this.circleAngle = (float)(Math.random() * 360);
        this.isCircling = Math.random() > 0.5; // 50% chance of circling vs straight flight
        
        // Choose random distant target for straight-flying birds
        if (!isCircling) {
            targetPosition = new Vector3(
                (float)(Math.random() * 200 - 100),
                startY + (float)(Math.random() * 10 - 5),
                (float)(Math.random() * 200 - 100)
            );
        }
        
        // Bird color (dark crows for horror atmosphere)
        float r = isDark ? 0.1f : 0.3f;
        float g = isDark ? 0.1f : 0.25f;
        float b = isDark ? 0.12f : 0.2f;
        
        Material birdMaterial = new Material(
            ColorAttribute.createDiffuse(r, g, b, 1f)
        );
        
        // Create body (small ellipsoid)
        Model bodyModel = modelBuilder.createSphere(
            0.4f, 0.8f, 0.3f, // Elongated body
            8, 6,
            birdMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        body = new ModelInstance(bodyModel);
        
        // Create wings (flat boxes)
        Model wingModel = modelBuilder.createBox(
            2.5f, 0.1f, 0.8f, // Wide, flat wings
            birdMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        leftWing = new ModelInstance(wingModel);
        rightWing = new ModelInstance(wingModel);
        
        updateTransforms();
    }
    
    /**
     * Update bird position and wing animation
     */
    public void update(float delta) {
        // Update wing flapping animation
        wingFlapTimer += delta * wingFlapSpeed;
        wingAngle = (float)Math.sin(wingFlapTimer * Math.PI * 2) * maxWingAngle;
        
        // Update flight path
        if (isCircling) {
            // Circular flight pattern (like birds of prey)
            circleAngle += delta * (flightSpeed / circleRadius) * 20f;
            if (circleAngle >= 360) circleAngle -= 360;
            
            position.x = (float)Math.cos(Math.toRadians(circleAngle)) * circleRadius;
            position.z = (float)Math.sin(Math.toRadians(circleAngle)) * circleRadius;
            position.y = flightHeight + (float)Math.sin(Math.toRadians(circleAngle * 2)) * 3f; // Undulating
            
            // Face direction of movement
            velocity.set(
                -(float)Math.sin(Math.toRadians(circleAngle)),
                0,
                (float)Math.cos(Math.toRadians(circleAngle))
            );
        } else {
            // Straight flight toward target
            if (targetPosition != null) {
                velocity.set(targetPosition).sub(position).nor().scl(flightSpeed * delta);
                position.add(velocity);
                
                // Add slight undulation
                position.y = flightHeight + (float)Math.sin(wingFlapTimer * 2) * 1.5f;
                
                // If reached target, pick new target
                if (position.dst(targetPosition) < 5f) {
                    targetPosition.set(
                        (float)(Math.random() * 200 - 100),
                        flightHeight + (float)(Math.random() * 10 - 5),
                        (float)(Math.random() * 200 - 100)
                    );
                }
            }
        }
        
        // Wrap around world if bird flies too far
        if (position.x > 150) position.x = -150;
        if (position.x < -150) position.x = 150;
        if (position.z > 150) position.z = -150;
        if (position.z < -150) position.z = 150;
        
        updateTransforms();
    }
    
    /**
     * Update transform matrices for body and wings
     */
    private void updateTransforms() {
        // Update body position and rotation
        body.transform.idt();
        body.transform.setToTranslation(position);
        
        // Rotate body to face flight direction
        if (velocity.len() > 0) {
            float yaw = (float)Math.toDegrees(Math.atan2(velocity.x, -velocity.z));
            body.transform.rotate(Vector3.Y, yaw);
        }
        
        // Position and rotate left wing
        leftWing.transform.idt();
        leftWing.transform.setToTranslation(position);
        if (velocity.len() > 0) {
            float yaw = (float)Math.toDegrees(Math.atan2(velocity.x, -velocity.z));
            leftWing.transform.rotate(Vector3.Y, yaw);
        }
        leftWing.transform.translate(-1.2f, 0, 0); // Offset to left
        leftWing.transform.rotate(0, 0, 1, wingAngle); // Flap
        
        // Position and rotate right wing
        rightWing.transform.idt();
        rightWing.transform.setToTranslation(position);
        if (velocity.len() > 0) {
            float yaw = (float)Math.toDegrees(Math.atan2(velocity.x, -velocity.z));
            rightWing.transform.rotate(Vector3.Y, yaw);
        }
        rightWing.transform.translate(1.2f, 0, 0); // Offset to right
        rightWing.transform.rotate(0, 0, 1, -wingAngle); // Flap opposite direction
    }
    
    /**
     * Get body instance for rendering
     */
    public ModelInstance getBody() {
        return body;
    }
    
    /**
     * Get left wing instance for rendering
     */
    public ModelInstance getLeftWing() {
        return leftWing;
    }
    
    /**
     * Get right wing instance for rendering
     */
    public ModelInstance getRightWing() {
        return rightWing;
    }
    
    /**
     * Cleanup bird models
     */
    public void dispose() {
        body.model.dispose();
        leftWing.model.dispose();
        rightWing.model.dispose();
    }
    
    public Vector3 getPosition() {
        return position;
    }
}
