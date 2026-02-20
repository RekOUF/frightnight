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

public class FrightNightGame3D implements ApplicationListener {
    
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;
    private Array<ModelInstance> instances;
    private ModelBuilder modelBuilder;
    
    // Cached models (reuse for multiple instances!)
    private Model groundModel;
    private Model treeTrunkModel;
    private Model treeLeavesModel;
    private Model fencePostModel;
    
    // Camera modes
    private boolean isFirstPerson = true;
    private Vector3 playerPosition;
    private Vector3 playerDirection;
    private float playerYaw = 0f;
    
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
        try {
            Gdx.app.log("FrightNight", "=== Starting game initialization ===");
            
            // Initialize camera
            Gdx.app.log("FrightNight", "Creating camera...");
            camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            playerPosition = new Vector3(0, 1.7f, 0);
            playerDirection = new Vector3(0, 0, -1);
            camera.position.set(playerPosition);
            camera.lookAt(playerPosition.x, playerPosition.y, playerPosition.z - 1);
            camera.near = 0.1f;
            camera.far = 300f;
            camera.update();
            Gdx.app.log("FrightNight", "Camera created successfully");
            
            // Model batch for 3D rendering
            Gdx.app.log("FrightNight", "Creating ModelBatch...");
            modelBatch = new ModelBatch();
            Gdx.app.log("FrightNight", "ModelBatch created successfully");
        
            // Environment with night lighting
            Gdx.app.log("FrightNight", "Creating environment...");
            environment = new Environment();
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.15f, 1f));
            
            DirectionalLight moonLight = new DirectionalLight();
            moonLight.set(0.8f, 0.8f, 0.9f, -0.3f, -1f, -0.2f);
            environment.add(moonLight);
            Gdx.app.log("FrightNight", "Environment created successfully");
            
            // Initialize model builder
            Gdx.app.log("FrightNight", "Creating ModelBuilder...");
            modelBuilder = new ModelBuilder();
            instances = new Array<>();
            Gdx.app.log("FrightNight", "ModelBuilder created successfully");
            
            // Build the 3D world
            Gdx.app.log("FrightNight", "Building world...");
            buildWorld();
            Gdx.app.log("FrightNight", "World built successfully with " + instances.size + " instances");
            
            // Initialize controls
            Gdx.app.log("FrightNight", "Creating controls...");
            joystick = new TouchJoystick();
            cameraController = new CameraController();
            Gdx.app.log("FrightNight", "Controls created successfully");
            
            Gdx.input.setInputProcessor(new GameInputProcessor(this));
            Gdx.app.log("FrightNight", "=== Game initialization complete ===");
            
        } catch (Exception e) {
            Gdx.app.error("FrightNight", "FATAL ERROR during initialization: " + e.getMessage(), e);
            isGameOver = true;
        }
    }
    
    private void buildWorld() {
        try {
            // Create reusable models ONCE
            Gdx.app.log("FrightNight", "Creating ground model...");
            Material groundMaterial = new Material(ColorAttribute.createDiffuse(0.06f, 0.18f, 0.06f, 1));
            groundModel = modelBuilder.createBox(200f, 0.1f, 200f, groundMaterial,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            Gdx.app.log("FrightNight", "Ground model created");
            
            Gdx.app.log("FrightNight", "Creating tree trunk model...");
            Material treeTrunkMaterial = new Material(ColorAttribute.createDiffuse(0.1f, 0.05f, 0.02f, 1));
            treeTrunkModel = modelBuilder.createCylinder(0.5f, 4f, 0.5f, 8, treeTrunkMaterial,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            Gdx.app.log("FrightNight", "Tree trunk model created");
            
            Gdx.app.log("FrightNight", "Creating tree leaves model...");
            Material leavesMaterial = new Material(ColorAttribute.createDiffuse(0.02f, 0.15f, 0.02f, 1));
            treeLeavesModel = modelBuilder.createSphere(2.5f, 2.5f, 2.5f, 10, 10, leavesMaterial,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            Gdx.app.log("FrightNight", "Tree leaves model created");
            
            Gdx.app.log("FrightNight", "Creating fence post model...");
            Material fenceMaterial = new Material(ColorAttribute.createDiffuse(0.15f, 0.1f, 0.08f, 1));
            fencePostModel = modelBuilder.createBox(0.3f, 3f, 0.3f, fenceMaterial,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            Gdx.app.log("FrightNight", "Fence post model created");
            
            // Ground plane
            Gdx.app.log("FrightNight", "Creating ground instance...");
            ModelInstance ground = new ModelInstance(groundModel);
            ground.transform.setToTranslation(0, -0.05f, 0);
            instances.add(ground);
            Gdx.app.log("FrightNight", "Ground instance added");
            
            // Create forest instances
            Gdx.app.log("FrightNight", "Creating forest...");
            createForest();
            Gdx.app.log("FrightNight", "Forest created: " + instances.size + " instances so far");
            
            // Create fence instances
            Gdx.app.log("FrightNight", "Creating fence...");
            createFence();
            Gdx.app.log("FrightNight", "Fence created: " + instances.size + " total instances");
            
        } catch (Exception e) {
            Gdx.app.error("FrightNight", "Error building world: " + e.getMessage(), e);
            isGameOver = true;
        }
    }
    
    private void createForest() {
        for (int i = 0; i < 20; i++) {
            float x = (float) (Math.random() * 140 - 70);
            float z = (float) (Math.random() * 140 - 70);
            
            if (Math.abs(x) < 10 && Math.abs(z) < 10) continue;
            
            ModelInstance trunk = new ModelInstance(treeTrunkModel);
            trunk.transform.setToTranslation(x, 2f, z);
            instances.add(trunk);
            
            ModelInstance leaves = new ModelInstance(treeLeavesModel);
            leaves.transform.setToTranslation(x, 5f, z);
            instances.add(leaves);
        }
    }
    
    private void createFence() {
        float fenceDistance = 80f;
        int postsPerSide = 12;
        
        for (int i = 0; i < postsPerSide; i++) {
            float t = (float) i / (postsPerSide - 1);
            float pos = -fenceDistance + t * (2 * fenceDistance);
            
            createFencePost(pos, -fenceDistance);
            createFencePost(pos, fenceDistance);
            createFencePost(fenceDistance, pos);
            createFencePost(-fenceDistance, pos);
        }
    }
    
    private void createFencePost(float x, float z) {
        ModelInstance post = new ModelInstance(fencePostModel);
        post.transform.setToTranslation(x, 1.5f, z);
        instances.add(post);
    }
    
    public void render() {
        try {
            float delta = Gdx.graphics.getDeltaTime();
            update(delta);
            
            Gdx.gl.glClearColor(0.04f, 0.04f, 0.12f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            
            if (isGameOver || modelBatch == null || camera == null || instances == null) {
                return;
            }
            
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            
            modelBatch.begin(camera);
            for (ModelInstance instance : instances) {
                if (instance != null) {
                    modelBatch.render(instance, environment);
                }
            }
            modelBatch.end();
            
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            
            if (joystick != null) {
                joystick.render();
            }
            if (cameraController != null) {
                cameraController.render();
            }
        } catch (Exception e) {
            Gdx.app.error("FrightNight", "Error in render: " + e.getMessage(), e);
        }
    }
    
    private void update(float delta) {
        if (isGameOver) return;
        if (joystick == null) return;
        
        Vector3 movement = joystick.getMovement();
        
        if (movement.len() > 0.1f) {
            float speed = playerSpeed * (isRunning ? runMultiplier : 1f);
            
            Vector3 forward = new Vector3(playerDirection).nor();
            Vector3 right = new Vector3(forward).crs(Vector3.Y).nor();
            
            Vector3 moveDir = new Vector3();
            moveDir.add(forward.scl(movement.y));
            moveDir.add(right.scl(movement.x));
            moveDir.nor().scl(speed * delta);
            
            playerPosition.add(moveDir);
            
            playerPosition.x = Math.max(-85, Math.min(85, playerPosition.x));
            playerPosition.z = Math.max(-85, Math.min(85, playerPosition.z));
        }
        
        updateCamera();
        
        if (scaryLevel > 0) {
            score += delta * 10;
        }
    }
    
    private void updateCamera() {
        if (isFirstPerson) {
            camera.position.set(playerPosition);
            camera.lookAt(
                playerPosition.x + playerDirection.x,
                playerPosition.y + playerDirection.y,
                playerPosition.z + playerDirection.z
            );
        } else {
            Vector3 cameraOffset = new Vector3(playerDirection).scl(-5f);
            cameraOffset.y = 3f;
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
        
        if (groundModel != null) groundModel.dispose();
        if (treeTrunkModel != null) treeTrunkModel.dispose();
        if (treeLeavesModel != null) treeLeavesModel.dispose();
        if (fencePostModel != null) fencePostModel.dispose();
        
        if (joystick != null) {
            joystick.dispose();
        }
        if (cameraController != null) {
            cameraController.dispose();
        }
    }
}
