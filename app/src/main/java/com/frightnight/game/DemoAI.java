package com.frightnight.game;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * AI controller for demo mode (attract mode)
 * Makes the game play itself to show off features
 */
public class DemoAI {
    
    private TerrainSystem terrain;
    
    // Movement state
    private Vector3 currentTarget;
    private Vector3 movementDirection;
    private float timeAtTarget = 0;
    private float exploreTimer = 0;
    private float lookTimer = 0;
    private Vector3 lookDirection;
    
    // Behavior state
    private AIState currentState = AIState.EXPLORING;
    private float stateTimer = 0;
    private boolean shouldRunFlag = false;
    private boolean shouldLookFlag = false;
    
    // Constants
    private static final float TARGET_CHANGE_INTERVAL = 3f; // Change direction every 3 seconds
    private static final float LOOK_INTERVAL = 2f; // Look around every 2 seconds
    private static final float DANGER_DISTANCE = 30f; // Run when enemy within 30 units
    private static final float SAFE_DISTANCE = 50f; // Stop running when 50 units away
    
    enum AIState {
        EXPLORING,  // Wandering around
        FLEEING,    // Running from enemy
        OBSERVING   // Stopped and looking around
    }
    
    public DemoAI(TerrainSystem terrain) {
        this.terrain = terrain;
        this.movementDirection = new Vector3(1, 0, 0);
        this.lookDirection = new Vector3();
        pickNewTarget();
    }
    
    public Vector3 update(float delta, Vector3 playerPosition, Array<ScaryEnemy> enemies) {
        stateTimer += delta;
        exploreTimer += delta;
        lookTimer += delta;
        
        // Check for nearby threats
        ScaryEnemy nearestEnemy = findNearestEnemy(playerPosition, enemies);
        float distanceToEnemy = Float.MAX_VALUE;
        
        if (nearestEnemy != null) {
            try {
                Vector3 enemyPos = nearestEnemy.getPosition();
                if (enemyPos != null && playerPosition != null) {
                    distanceToEnemy = playerPosition.dst(enemyPos);
                }
            } catch (Exception e) {
                // Ignore enemy position errors
                distanceToEnemy = Float.MAX_VALUE;
            }
        }
        
        // State machine
        switch (currentState) {
            case EXPLORING:
                if (distanceToEnemy < DANGER_DISTANCE) {
                    // Enemy too close! Start fleeing
                    currentState = AIState.FLEEING;
                    shouldRunFlag = true;
                    stateTimer = 0;
                } else if (exploreTimer > 8f) {
                    // Periodically stop to look around for atmosphere
                    currentState = AIState.OBSERVING;
                    stateTimer = 0;
                    exploreTimer = 0;
                }
                
                // Normal exploration movement
                if (exploreTimer > TARGET_CHANGE_INTERVAL) {
                    pickNewTarget();
                    exploreTimer = 0;
                }
                break;
                
            case FLEEING:
                shouldRunFlag = true;
                
                if (distanceToEnemy > SAFE_DISTANCE) {
                    // Safe now, return to exploring
                    currentState = AIState.EXPLORING;
                    shouldRunFlag = false;
                    stateTimer = 0;
                    pickNewTarget();
                } else if (nearestEnemy != null) {
                    // Run away from enemy
                    try {
                        Vector3 enemyPos = nearestEnemy.getPosition();
                        if (enemyPos != null && playerPosition != null) {
                            Vector3 fleeDirection = new Vector3(playerPosition).sub(enemyPos).nor();
                            movementDirection.set(fleeDirection.x, 0, fleeDirection.z).nor();
                        }
                    } catch (Exception e) {
                        // Ignore flee calculation errors
                    }
                }
                break;
                
            case OBSERVING:
                shouldRunFlag = false;
                
                if (stateTimer > 2f) {
                    // Done observing, continue exploring
                    currentState = AIState.EXPLORING;
                    stateTimer = 0;
                    pickNewTarget();
                } else {
                    // Stand still and look around
                    movementDirection.set(0, 0, 0);
                }
                break;
        }
        
        // Look around occasionally for cinematic effect
        if (lookTimer > LOOK_INTERVAL) {
            shouldLookFlag = true;
            lookDirection.set(
                (float)(Math.random() * 2 - 1),
                (float)(Math.random() * 0.4 - 0.2), // Slight vertical look
                (float)(Math.random() * 2 - 1)
            ).nor();
            lookTimer = 0;
        } else if (lookTimer > 0.5f) {
            shouldLookFlag = false;
        }
        
        // Navigate towards current target during exploration
        if (currentState == AIState.EXPLORING && currentTarget != null) {
            Vector3 toTarget = new Vector3(currentTarget).sub(playerPosition);
            toTarget.y = 0; // Ignore height
            
            if (toTarget.len() < 5f) {
                // Reached target, pick new one
                pickNewTarget();
            } else {
                // Move towards target
                movementDirection.set(toTarget).nor();
            }
        }
        
        return movementDirection;
    }
    
    private void pickNewTarget() {
        // Pick a random point within world bounds
        float x = (float)(Math.random() * 120 - 60);
        float z = (float)(Math.random() * 120 - 60);
        float y = terrain != null ? terrain.getHeightAt(x, z) : 0;
        
        currentTarget = new Vector3(x, y, z);
    }
    
    private ScaryEnemy findNearestEnemy(Vector3 position, Array<ScaryEnemy> enemies) {
        if (enemies == null || enemies.size == 0 || position == null) return null;
        
        ScaryEnemy nearest = null;
        float minDistance = Float.MAX_VALUE;
        
        try {
            for (ScaryEnemy enemy : enemies) {
                if (enemy == null) continue;
                
                Vector3 enemyPos = enemy.getPosition();
                if (enemyPos == null) continue;
                
                float dist = position.dst(enemyPos);
                if (dist < minDistance) {
                    minDistance = dist;
                    nearest = enemy;
                }
            }
        } catch (Exception e) {
            // Return null on any error
            return null;
        }
        
        return nearest;
    }
    
    public boolean shouldRun() {
        return shouldRunFlag;
    }
    
    public boolean shouldLookAround() {
        return shouldLookFlag;
    }
    
    public Vector3 getLookDirection() {
        return lookDirection;
    }
}
