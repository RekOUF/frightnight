package com.frightnight.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * LibGDX 3D Horror RPG Game Core
 * First-person and third-person switchable camera
 * Touch joystick controls
 * Night scene with 3D environment
 */
public class FrightNightGame3D implements ApplicationListener {
    
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;
    private Array<ModelInstance> instances;
    private ModelBuilder modelBuilder;
    
    // Camera modes
    private boolean isFirstPerson = true;
    private Vector3 playerPosition;
    private Vector3 playerDirection;
    private float playerYaw = 0f; // Rotation around Y axis
    
    // Player stats
    private float playerSpeed = 5f;
    private float runMultiplier = 2f;
    private boolean isRunning = false;
    private boolean isHiding = false;
    
    // Scary level (0-10)
    private int scaryLevel = 0;
    private int score = 0;
    
    // Game state
    private boolean isGameOver = false;
    
    // Virtual joystick
    public TouchJoystick joystick;
    public CameraController cameraController;
    
    public FrightNightGame3D(int scaryLevel) {
        this.scaryLevel = scaryLevel;
    }
    
    @Override
    public void create() {
        // Initialize camera
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        playerPosition = new Vector3(0, 1.7f, 0); // Eye height ~1.7m
        playerDirection = new Vector3(0, 0, -1); // Looking forward (negative Z)
        camera.position.set(playerPosition);
        camera.lookAt(playerPosition.x, playerPosition.y, playerPosition.z - 1);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();
        
        // Model batch for 3D rendering
        modelBatch = new ModelBatch();
        
        // Environment with night lighting
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.15f, 1f)); // Very dark ambient (night)
        
        // Moonlight (directional from above/angle)
        DirectionalLight moonLight = new DirectionalLight();
        moonLight.set(0.8f, 0.8f, 0.9f, -0.3f, -1f, -0.2f); // Blueish moon light
        environment.add(moonLight);
        
        // Initialize model builder
        modelBuilder = new ModelBuilder();
        instances = new Array<>();
        
        // Build the 3D world
        buildWorld();
        
        // Initialize controls
        joystick = new TouchJoystick();
        cameraController = new CameraController();
        
        Gdx.input.setInputProcessor(new GameInputProcessor(this));
    }
    
    private void buildWorld() {
        // Ground plane (dark grass)
        Material groundMaterial = new Material(ColorAttribute.createDiffuse(0.06f, 0.18f, 0.06f, 1)); // Dark grass
        Model groundModel = modelBuilder.createBox(200f, 0.1f, 200f, groundMaterial,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance ground = new ModelInstance(groundModel);
        ground.transform.setToTranslation(0, -0.05f, 0);
        instances.add(ground);
        
        // Create trees (dark scary trees)
        createForest();
        
        // Create fence boundary
        createFence();
        
        // Sky will be rendered as dark background in render()
    }
    
    private void createForest() {
        Material treeTrunkMaterial = new Material(ColorAttribute.createDiffuse(0.1f, 0.05f, 0.02f, 1)); // Very dark brown
        Material leavesMaterial = new Material(ColorAttribute.createDiffuse(0.02f, 0.15f, 0.02f, 1)); // Very dark green
        
        // Generate 30+ trees randomly
        for (int i = 0; i < 35; i++) {
            float x = (float) (Math.random() * 160 - 80); // -80 to 80
            float z = (float) (Math.random() * 160 - 80);
            
            // Skip if too close to player spawn
            if (Math.abs(x) < 10 && Math.abs(z) < 10) continue;
            
            // Tree trunk
            Model trunkModel = modelBuilder.createCylinder(0.5f, 4f, 0.5f, 8, treeTrunkMaterial,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            ModelInstance trunk = new ModelInstance(trunkModel);
            trunk.transform.setToTranslation(x, 2f, z);
            instances.add(trunk);
            
            // Tree leaves (sphere on top)
            Model leavesModel = modelBuilder.createSphere(2.5f, 2.5f, 2.5f, 12, 12, leavesMaterial,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            ModelInstance leaves = new ModelInstance(leavesModel);
            leaves.transform.setToTranslation(x, 5f, z);
            instances.add(leaves);
        }
    }
    
    private void createFence() {
        Material fenceMaterial = new Material(ColorAttribute.createDiffuse(0.15f, 0.1f, 0.08f, 1)); // Dark wood
        
        // Fence posts around perimeter
        float fenceDistance = 90f;
        int postsPerSide = 20;
        
        for (int i = 0; i < postsPerSide; i++) {
            float t = (float) i / (postsPerSide - 1);
            float pos = -fenceDistance + t * (2 * fenceDistance);
            
            // North side
            createFencePost(pos, -fenceDistance, fenceMaterial);
            // South side
            createFencePost(pos, fenceDistance, fenceMaterial);
            // East side
            createFencePost(fenceDistance, pos, fenceMaterial);
            // West side
            createFencePost(-fenceDistance, pos, fenceMaterial);
        }
    }
    
    private void createFencePost(float x, float z, Material material) {
        Model postModel = modelBuilder.createBox(0.3f, 3f, 0.3f, material,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance post = new ModelInstance(postModel);
        post.transform.setToTranslation(x, 1.5f, z);
        instances.add(post);
    }
    
    @Override
    public void render() {
        // Update game logic
        float delta = Gdx.graphics.getDeltaTime();
        update(delta);
        
        // Clear screen with dark night sky
        Gdx.gl.glClearColor(0.04f, 0.04f, 0.12f, 1); // Very dark blue (night)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        
        // Enable depth test for 3D
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        
        // Render 3D scene
        modelBatch.begin(camera);
        for (ModelInstance instance : instances) {
            modelBatch.render(instance, environment);
        }
        modelBatch.end();
        
        // Disable depth test for 2D UI
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        
        // Render UI (joystick, etc.)
        if (joystick != null) {
            joystick.render();
        }
        if (cameraController != null) {
            cameraController.render();
        }
    }
    
    private void update(float delta) {
        if (isGameOver) return;
        
        // Safety check
        if (joystick == null) return;
        
        // Get joystick input
        Vector3 movement = joystick.getMovement();
        
        // Update player position based on joystick
        if (movement.len() > 0.1f) {
            float speed = playerSpeed * (isRunning ? runMultiplier : 1f);
            
            // Calculate movement direction relative to camera
            Vector3 forward = new Vector3(playerDirection).nor();
            Vector3 right = new Vector3(forward).crs(Vector3.Y).nor();
            
            Vector3 moveDir = new Vector3();
            moveDir.add(forward.scl(movement.y));
            moveDir.add(right.scl(movement.x));
            moveDir.nor().scl(speed * delta);
            
            playerPosition.add(moveDir);
            
            // Clamp to world bounds
            playerPosition.x = Math.max(-85, Math.min(85, playerPosition.x));
            playerPosition.z = Math.max(-85, Math.min(85, playerPosition.z));
        }
        
        // Update camera based on mode
        updateCamera();
        
        // Update score (survival time)
        if (scaryLevel > 0) {
            score += delta * 10; // 10 points per second
        }
    }
    
    private void updateCamera() {
        if (isFirstPerson) {
            // First person: camera at player position
            camera.position.set(playerPosition);
            camera.lookAt(
                playerPosition.x + playerDirection.x,
                playerPosition.y + playerDirection.y,
                playerPosition.z + playerDirection.z
            );
        } else {
            // Third person: camera behind and above player
            Vector3 cameraOffset = new Vector3(playerDirection).scl(-5f); // 5m behind
            cameraOffset.y = 3f; // 3m above
            camera.position.set(playerPosition).add(cameraOffset);
            camera.lookAt(playerPosition);
        }
        camera.update();
    }
    
    public void switchCameraMode() {
        isFirstPerson = !isFirstPerson;
    }
    
    public void setRunning(boolean running) {
        isRunning = running;
    }
    
    public void toggleHiding() {
        isHiding = !isHiding;
    }
    
    public void rotateCameraLeft(float amount) {
        playerYaw += amount;
        updatePlayerDirection();
    }
    
    public void rotateCameraRight(float amount) {
        playerYaw -= amount;
        updatePlayerDirection();
    }
    
    private void updatePlayerDirection() {
        playerDirection.set(
            (float) Math.sin(Math.toRadians(playerYaw)),
            0,
            (float) -Math.cos(Math.toRadians(playerYaw))
        ).nor();
    }
    
    public int getScore() {
        return (int) score;
    }
    
    public boolean isGameOver() {
        return isGameOver;
    }
    
    public void gameOver() {
        isGameOver = true;
    }
    
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void dispose() {
        if (modelBatch != null) {
            modelBatch.dispose();
        }
        if (instances != null) {
            for (ModelInstance instance : instances) {
                if (instance != null && instance.model != null) {
                    instance.model.dispose();
                }
            }
        }
        if (joystick != null) {
            joystick.dispose();
        }
        if (cameraController != null) {
            cameraController.dispose();
        }
    }
}
