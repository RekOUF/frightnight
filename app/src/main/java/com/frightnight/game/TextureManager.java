package com.frightnight.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages game textures
 */
public class TextureManager implements Disposable {
    
    private Texture grassTexture;
    private Texture rockTexture;
    private Texture pathTexture;
    
    public TextureManager() {
        loadTextures();
    }
    
    private void loadTextures() {
        try {
            Gdx.app.log("TextureManager", "Loading textures...");
            
            // Load grass texture
            grassTexture = new Texture(Gdx.files.internal("grass.png"));
            grassTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            grassTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            Gdx.app.log("TextureManager", "Grass texture loaded: " + grassTexture.getWidth() + "x" + grassTexture.getHeight());
            
            // Load rock texture
            rockTexture = new Texture(Gdx.files.internal("rock.png"));
            rockTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            rockTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            Gdx.app.log("TextureManager", "Rock texture loaded: " + rockTexture.getWidth() + "x" + rockTexture.getHeight());
            
            // Load path texture
            pathTexture = new Texture(Gdx.files.internal("rockandpad.png"));
            pathTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            pathTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            Gdx.app.log("TextureManager", "Path texture loaded: " + pathTexture.getWidth() + "x" + pathTexture.getHeight());
            
        } catch (Exception e) {
            Gdx.app.error("TextureManager", "Error loading textures: " + e.getMessage(), e);
        }
    }
    
    public Texture getGrassTexture() {
        return grassTexture;
    }
    
    public Texture getRockTexture() {
        return rockTexture;
    }
    
    public Texture getPathTexture() {
        return pathTexture;
    }
    
    @Override
    public void dispose() {
        if (grassTexture != null) grassTexture.dispose();
        if (rockTexture != null) rockTexture.dispose();
        if (pathTexture != null) pathTexture.dispose();
    }
}
