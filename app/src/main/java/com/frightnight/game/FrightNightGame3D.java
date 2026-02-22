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
    private Model moonModel;
    
    // Texture manager
    private TextureManager textureManager;
    
    // New environmental systems
    private TerrainSystem terrain;
    private Array<RealisticTree> realisticTrees;
    private ForestPath forestPath;
    private WindGrassField windGrass;
    private PlayerShadow playerShadow;
    private Array<ScaryEnemy> enemies;
    
    // Atmospheric effects
    private Array<VolumetricCloud> volumetricClouds;
    private LightningSystem lightningSystem;
    private Array<FlyingBird> birds;
    
    // First-person controller
    public FirstPersonController fpsController;
    
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
            Vector3 startPosition = new Vector3(0, 5f, 10f); // Higher up to see terrain
            fpsController = new FirstPersonController(camera, startPosition);
            Gdx.app.log("FrightNight", "Camera starting at: " + startPosition);
            fpsController.setLookSensitivity(0.15f); // Adjust for mobile
            Gdx.app.log("FrightNight", "Camera and FPS controller created successfully");
            
            // Model batch for 3D rendering
            Gdx.app.log("FrightNight", "Creating ModelBatch...");
            modelBatch = new ModelBatch();
            Gdx.app.log("FrightNight", "ModelBatch created successfully");
        
            // Environment with dusk lighting (warmer, brighter)
            Gdx.app.log("FrightNight", "Creating environment...");
            environment = new Environment();
            // Strong ambient light so we can SEE the terrain!
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.7f, 1f));
            
            // Strong directional light from above
            DirectionalLight mainLight = new DirectionalLight();
            mainLight.set(0.8f, 0.8f, 0.9f, -0.3f, -1f, -0.2f); // Bright white light from above
            environment.add(mainLight);
            
            // Moon light (cool glow)
            DirectionalLight moonLight = new DirectionalLight();
            moonLight.set(0.5f, 0.5f, 0.6f, 0.3f, -0.5f, 0.2f);
            environment.add(moonLight);
            Gdx.app.log("FrightNight", "Environment created successfully");
            
            // Initialize model builder
            Gdx.app.log("FrightNight", "Creating ModelBuilder...");
            modelBuilder = new ModelBuilder();
            instances = new Array<>();
            Gdx.app.log("FrightNight", "ModelBuilder created successfully");
            
            // Load textures
            Gdx.app.log("FrightNight", "Loading textures...");
            textureManager = new TextureManager();
            Gdx.app.log("FrightNight", "Textures loaded successfully");
            
            // Build the 3D world
            Gdx.app.log("FrightNight", "Building world...");
            buildWorld();
            Gdx.app.log("FrightNight", "World built successfully with " + instances.size + " instances");
            
            // Initialize controls
            Gdx.app.log("FrightNight", "Creating controls...");
            joystick = new TouchJoystick();
            Gdx.app.log("FrightNight", "Controls created successfully");
            
            // Initialize atmospheric effects
            Gdx.app.log("FrightNight", "Creating atmospheric effects...");
            lightningSystem = new LightningSystem();
            Gdx.app.log("FrightNight", "Lightning system created");
            
            Gdx.input.setInputProcessor(new GameInputProcessor(this));
            Gdx.app.log("FrightNight", "=== Game initialization complete ===");
            
        } catch (Exception e) {
            Gdx.app.error("FrightNight", "FATAL ERROR during initialization: " + e.getMessage(), e);
            isGameOver = true;
        }
    }
    
    private void buildWorld() {
        try {
            // Create large TEXTURED GROUND PLANE with grass texture
            Gdx.app.log("FrightNight", "Creating textured ground plane...");
            Material groundMaterial = new Material(
                com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute.createDiffuse(textureManager.getGrassTexture())
            );
            Model groundPlane = modelBuilder.createBox(300f, 0.5f, 300f, groundMaterial,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
            ModelInstance ground = new ModelInstance(groundPlane);
            ground.transform.setToTranslation(0, -0.5f, 0); // Flat at ground level
            instances.add(ground);
            Gdx.app.log("FrightNight", "Textured ground plane created (300x300) with grass texture");
            
            // Create realistic terrain with hills and valleys
            Gdx.app.log("FrightNight", "Creating terrain system...");
            terrain = new TerrainSystem(modelBuilder);
            instances.add(terrain.getTerrainInstance());
            // Add distant mountains
            for (ModelInstance mountain : terrain.getMountainInstances()) {
                instances.add(mountain);
            }
            Gdx.app.log("FrightNight", "Terrain system created with mountains");
            Gdx.app.log("FrightNight", "Terrain bounds: 200x200 units, height variation: ~8 units");
            
            // Create moon
            Gdx.app.log("FrightNight", "Creating moon model...");
            Material moonMaterial = new Material(ColorAttribute.createDiffuse(1.0f, 0.95f, 0.8f, 1));
            moonMaterial.set(ColorAttribute.createEmissive(0.9f, 0.9f, 0.7f, 1)); // Glowing
            moonModel = modelBuilder.createSphere(8f, 8f, 8f, 20, 20, moonMaterial,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            Gdx.app.log("FrightNight", "Moon model created");
            
            // Moon in the sky
            Gdx.app.log("FrightNight", "Creating moon instance...");
            ModelInstance moon = new ModelInstance(moonModel);
            moon.transform.setToTranslation(40f, 50f, -80f); // Far away in the sky
            instances.add(moon);
            
            // Create volumetric clouds
            Gdx.app.log("FrightNight", "Creating volumetric clouds...");
            volumetricClouds = new Array<>();
            for (int i = 0; i < 12; i++) {
                float cloudX = (float)(Math.random() * 140 - 70);
                float cloudY = 30f + (float)(Math.random() * 25); // High in the sky
                float cloudZ = -40f - (float)(Math.random() * 80);
                float scale = 0.8f + (float)(Math.random() * 0.6f); // Varying sizes
                
                VolumetricCloud cloud = new VolumetricCloud(modelBuilder, cloudX, cloudY, cloudZ, scale);
                volumetricClouds.add(cloud);
                
                // Add cloud parts to instances for rendering
                for (ModelInstance part : cloud.getInstances()) {
                    instances.add(part);
                }
            }
            Gdx.app.log("FrightNight", "Created " + volumetricClouds.size + " volumetric clouds");
            
            // Create flying birds
            Gdx.app.log("FrightNight", "Creating flying birds...");
            birds = new Array<>();
            for (int i = 0; i < 6; i++) {
                float birdX = (float)(Math.random() * 120 - 60);
                float birdY = 25f + (float)(Math.random() * 30); // Various heights
                float birdZ = (float)(Math.random() * 120 - 60);
                boolean isDark = Math.random() > 0.3; // 70% dark crows for horror
                
                FlyingBird bird = new FlyingBird(modelBuilder, birdX, birdY, birdZ, isDark);
                birds.add(bird);
                
                // Add bird parts to instances
                instances.add(bird.getBody());
                instances.add(bird.getLeftWing());
                instances.add(bird.getRightWing());
            }
            Gdx.app.log("FrightNight", "Created " + birds.size + " flying birds");
            Gdx.app.log("FrightNight", "Ground instance added");
            
            // Create realistic forest with branches
            Gdx.app.log("FrightNight", "Creating realistic forest...");
            realisticTrees = new Array<>();
            int numTrees = 25;
            for (int i = 0; i < numTrees; i++) {
                float x = (float)(Math.random() * 140 - 70);
                float z = (float)(Math.random() * 140 - 70);
                
                // Avoid center spawn area
                if (Math.abs(x) < 15 && Math.abs(z) < 15) continue;
                
                float y = terrain.getHeightAt(x, z);
                boolean isScary = Math.random() < 0.7f; // 70% scary trees
                
                RealisticTree tree = new RealisticTree(modelBuilder, x, y, z, isScary);
                realisticTrees.add(tree);
                
                // Add all tree parts to instances
                for (ModelInstance part : tree.getParts()) {
                    instances.add(part);
                }
            }
            Gdx.app.log("FrightNight", "Created " + realisticTrees.size + " realistic trees");
            
            // Create winding forest path with texture
            Gdx.app.log("FrightNight", "Creating forest path...");
            forestPath = new ForestPath(modelBuilder, terrain, textureManager.getPathTexture());
            for (ModelInstance pathSegment : forestPath.getPathSegments()) {
                instances.add(pathSegment);
            }
            Gdx.app.log("FrightNight", "Forest path created with texture");
            
            // Create wind-animated grass field
            Gdx.app.log("FrightNight", "Creating wind grass field...");
            windGrass = new WindGrassField(modelBuilder, terrain, forestPath);
            for (ModelInstance grassPatch : windGrass.getInstances()) {
                instances.add(grassPatch);
            }
            Gdx.app.log("FrightNight", "Wind grass created");
            
            // Create player shadow from moonlight
            Gdx.app.log("FrightNight", "Creating player shadow...");
            playerShadow = new PlayerShadow(modelBuilder);
            instances.add(playerShadow.getInstance());
            Gdx.app.log("FrightNight", "Player shadow created");
            
            // Create scary enemies (only if scaryLevel > 0)
            Gdx.app.log("FrightNight", "Creating scary enemies...");
            enemies = new Array<>();
            if (scaryLevel > 0) {
                int numEnemies = 2 + scaryLevel / 3; // 2-5 enemies based on scary level
                for (int i = 0; i < numEnemies; i++) {
                    float x = (float)(Math.random() * 100 - 50);
                    float z = (float)(Math.random() * 100 - 50);
                    
                    // Avoid spawning too close to player
                    if (Math.abs(x) < 20 && Math.abs(z) < 20) continue;
                    
                    ScaryEnemy enemy = new ScaryEnemy(modelBuilder, terrain, x, z);
                    enemies.add(enemy);
                    
                    instances.add(enemy.getBodyInstance());
                    instances.add(enemy.getHeadInstance());
                }
                Gdx.app.log("FrightNight", "Created " + enemies.size + " scary enemies!");
            }
            
            Gdx.app.log("FrightNight", "Forest created: " + instances.size + " instances so far");
            
        } catch (Exception e) {
            Gdx.app.error("FrightNight", "Error building world: " + e.getMessage(), e);
            isGameOver = true;
        }
    }
    
    public void render() {
        try {
            float delta = Gdx.graphics.getDeltaTime();
            update(delta);
            
            // Dark BLUE sky (not purple!)
            Gdx.gl.glClearColor(0.02f, 0.05f, 0.15f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            
            if (isGameOver || modelBatch == null || camera == null || instances == null) {
                return;
            }
            
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            
            // Render 3D world
            modelBatch.begin(camera);
            for (ModelInstance instance : instances) {
                if (instance != null) {
                    modelBatch.render(instance, environment);
                }
            }
            modelBatch.end();
            
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            
            // Render lightning effects (screen flash)
            if (lightningSystem != null) {
                lightningSystem.renderFlash();
            }
            
            // Render joystick UI
            if (joystick != null) {
                joystick.render();
            }
        } catch (Exception e) {
            Gdx.app.error("FrightNight", "Error in render: " + e.getMessage(), e);
        }
    }
    
    private void update(float delta) {
        if (isGameOver) return;
        if (joystick == null || fpsController == null) return;
        
        // Get joystick movement input
        Vector3 movement = joystick.getMovement();
        
        if (movement.len() > 0.1f) {
            float speed = playerSpeed * (isRunning ? runMultiplier : 1f);
            
            // Move using FPS controller
            fpsController.move(movement.x, movement.y, speed, delta);
            
            // Clamp position to world boundaries (fence area)
            fpsController.clampPosition(-85f, 85f, -85f, 85f);
        }
        
        // Update camera position and rotation
        fpsController.update();
        
        // Update atmospheric effects
        if (lightningSystem != null) {
            lightningSystem.update(delta, fpsController.getPosition());
        }
        
        // Update volumetric clouds (drifting)
        if (volumetricClouds != null) {
            for (VolumetricCloud cloud : volumetricClouds) {
                cloud.update(delta);
            }
        }
        
        // Update flying birds
        if (birds != null) {
            for (FlyingBird bird : birds) {
                bird.update(delta);
            }
        }
        
        // Update wind grass animation
        if (windGrass != null) {
            windGrass.update(delta);
        }
        
        // Update player shadow position
        if (playerShadow != null && fpsController != null && terrain != null) {
            playerShadow.update(fpsController.getPosition(), terrain);
        }
        
        // Update scary enemies AI
        if (enemies != null && fpsController != null) {
            for (ScaryEnemy enemy : enemies) {
                enemy.update(delta, fpsController.getPosition());
                
                // Check if enemy caught player
                if (enemy.hasReachedPlayer(fpsController.getPosition())) {
                    Gdx.app.log("FrightNight", "GAME OVER - Enemy caught you!");
                    gameOver();
                }
            }
        }
        
        // Update player height to match terrain
        if (terrain != null && fpsController != null) {
            Vector3 pos = fpsController.getPosition();
            float terrainHeight = terrain.getHeightAt(pos.x, pos.z);
            pos.y = terrainHeight + 1.7f; // Eye level above terrain
        }
        
        // Update score if in scary mode
        if (scaryLevel > 0) {
            score += delta * 10;
        }
    }
    
    public void setRunning(boolean running) {
        isRunning = running;
    }
    
    public void toggleHiding() {
        isHiding = !isHiding;
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
        
        if (textureManager != null) {
            textureManager.dispose();
        }
        
        if (moonModel != null) moonModel.dispose();
        
        // Dispose atmospheric effects
        if (volumetricClouds != null) {
            for (VolumetricCloud cloud : volumetricClouds) {
                cloud.dispose();
            }
            volumetricClouds.clear();
        }
        
        if (lightningSystem != null) {
            lightningSystem.dispose();
        }
        
        if (birds != null) {
            for (FlyingBird bird : birds) {
                bird.dispose();
            }
            birds.clear();
        }
        
        if (joystick != null) {
            joystick.dispose();
        }
        
        // Dispose new environmental systems
        if (terrain != null) {
            terrain.dispose();
        }
        
        if (realisticTrees != null) {
            for (RealisticTree tree : realisticTrees) {
                tree.dispose();
            }
            realisticTrees.clear();
        }
        
        if (forestPath != null) {
            forestPath.dispose();
        }
        
        if (windGrass != null) {
            windGrass.dispose();
        }
        
        if (playerShadow != null) {
            playerShadow.dispose();
        }
        
        if (enemies != null) {
            for (ScaryEnemy enemy : enemies) {
                enemy.dispose();
            }
            enemies.clear();
        }
    }
}
