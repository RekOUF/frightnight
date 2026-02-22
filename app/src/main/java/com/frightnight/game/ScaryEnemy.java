package com.frightnight.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * Scary enemy that chases the player
 * Uses simple AI to patrol and hunt
 */
public class ScaryEnemy {
    
    private ModelInstance bodyInstance;
    private ModelInstance headInstance;
    private Vector3 position;
    private Vector3 velocity;
    private TerrainSystem terrain;
    
    // AI state
    private enum State {
        PATROLLING,
        CHASING,
        ATTACKING
    }
    
    private State state = State.PATROLLING;
    private Vector3 patrolTarget;
    private float speed = 3.5f;
    private float chaseSpeed = 6f;
    private float detectionRange = 25f;
    private float attackRange = 2.5f;
    
    // Animation
    private float bobPhase = 0f;
    private float bobAmount = 0.3f;
    
    /**
     * Create a scary enemy (zombie/monster)
     */
    public ScaryEnemy(ModelBuilder modelBuilder, TerrainSystem terrain, float x, float z) {
        this.terrain = terrain;
        float y = terrain.getHeightAt(x, z) + 1.5f; // Eye height
        this.position = new Vector3(x, y, z);
        this.velocity = new Vector3();
        
        // Pick random patrol target
        pickNewPatrolTarget();
        
        // Create creepy enemy model
        createEnemyModel(modelBuilder);
    }
    
    /**
     * Create scary zombie/monster model
     */
    private void createEnemyModel(ModelBuilder modelBuilder) {
        // Grayish-green zombie skin
        Material zombieMaterial = new Material(
            ColorAttribute.createDiffuse(0.3f, 0.35f, 0.25f, 1f)
        );
        
        // Dark evil eyes
        Material eyeMaterial = new Material(
            ColorAttribute.createDiffuse(0.8f, 0.1f, 0.1f, 1f), // Red glowing eyes
            ColorAttribute.createEmissive(0.6f, 0.0f, 0.0f, 1f) // Emit red light
        );
        
        // Body (hunched over)
        Model bodyModel = modelBuilder.createBox(
            0.8f, 1.5f, 0.6f, // Roughly human-sized
            zombieMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        bodyInstance = new ModelInstance(bodyModel);
        
        // Head (slightly oversized for creepy effect)
        Model headModel = modelBuilder.createSphere(
            0.5f, 0.6f, 0.5f,
            12, 10,
            zombieMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        headInstance = new ModelInstance(headModel);
        
        updateTransforms();
    }
    
    /**
     * Update enemy AI and position
     */
    public void update(float delta, Vector3 playerPosition) {
        float distanceToPlayer = position.dst(playerPosition);
        
        // Update state based on distance to player
        if (distanceToPlayer < attackRange) {
            state = State.ATTACKING;
        } else if (distanceToPlayer < detectionRange) {
            state = State.CHASING;
        } else {
            state = State.PATROLLING;
        }
        
        // Behavior based on state
        switch (state) {
            case PATROLLING:
                patrol(delta);
                break;
            case CHASING:
                chase(delta, playerPosition);
                break;
            case ATTACKING:
                attack(delta, playerPosition);
                break;
        }
        
        // Update bobbing animation
        bobPhase += delta * 4f;
        
        // Update transforms
        updateTransforms();
    }
    
    /**
     * Patrol behavior - wander around
     */
    private void patrol(float delta) {
        if (patrolTarget == null || position.dst(patrolTarget) < 2f) {
            pickNewPatrolTarget();
        }
        
        // Move toward patrol target
        velocity.set(patrolTarget).sub(position).nor().scl(speed * delta);
        position.add(velocity);
        
        // Update height based on terrain
        position.y = terrain.getHeightAt(position.x, position.z) + 1.5f;
    }
    
    /**
     * Chase player
     */
    private void chase(float delta, Vector3 playerPosition) {
        // Move toward player (faster)
        velocity.set(playerPosition).sub(position).nor().scl(chaseSpeed * delta);
        position.add(velocity);
        
        // Update height
        position.y = terrain.getHeightAt(position.x, position.z) + 1.5f;
        
        Gdx.app.log("FrightNight", "Enemy chasing player! Distance: " + position.dst(playerPosition));
    }
    
    /**
     * Attack player
     */
    private void attack(float delta, Vector3 playerPosition) {
        // Lunge toward player
        velocity.set(playerPosition).sub(position).nor().scl(chaseSpeed * 1.5f * delta);
        position.add(velocity);
        
        // Update height
        position.y = terrain.getHeightAt(position.x, position.z) + 1.5f;
        
        // TODO: Trigger game over or damage player
        Gdx.app.log("FrightNight", "Enemy ATTACKING!");
    }
    
    /**
     * Pick new random patrol target
     */
    private void pickNewPatrolTarget() {
        float x = position.x + (float)(Math.random() * 40 - 20);
        float z = position.z + (float)(Math.random() * 40 - 20);
        
        // Keep within bounds
        x = Math.max(-70, Math.min(70, x));
        z = Math.max(-70, Math.min(70, z));
        
        float y = terrain.getHeightAt(x, z);
        patrolTarget = new Vector3(x, y, z);
    }
    
    /**
     * Update model transforms
     */
    private void updateTransforms() {
        // Body position with bobbing
        float bob = (float)Math.sin(bobPhase) * bobAmount;
        bodyInstance.transform.setToTranslation(position.x, position.y + bob, position.z);
        
        // Rotate to face movement direction
        if (velocity.len() > 0.1f) {
            float angle = (float)Math.toDegrees(Math.atan2(velocity.x, -velocity.z));
            bodyInstance.transform.rotate(Vector3.Y, angle);
        }
        
        // Head position (above body)
        headInstance.transform.setToTranslation(position.x, position.y + 1.0f + bob, position.z);
        
        // Match body rotation
        if (velocity.len() > 0.1f) {
            float angle = (float)Math.toDegrees(Math.atan2(velocity.x, -velocity.z));
            headInstance.transform.rotate(Vector3.Y, angle);
        }
    }
    
    /**
     * Check if enemy caught player
     */
    public boolean hasReachedPlayer(Vector3 playerPosition) {
        return position.dst(playerPosition) < attackRange && state == State.ATTACKING;
    }
    
    public ModelInstance getBodyInstance() {
        return bodyInstance;
    }
    
    public ModelInstance getHeadInstance() {
        return headInstance;
    }
    
    public Vector3 getPosition() {
        return position;
    }
    
    public State getState() {
        return state;
    }
    
    public void dispose() {
        bodyInstance.model.dispose();
        headInstance.model.dispose();
    }
}
