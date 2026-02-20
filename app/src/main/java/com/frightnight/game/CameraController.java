package com.frightnight.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Camera rotation controls and UI
 * Displays buttons for camera rotation and sprint
 */
public class CameraController {
    
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    
    // Button positions (right side)
    private float rightWidth = Gdx.graphics.getWidth();
    private float leftButtonX;
    private float rightButtonX;
    private float rotateButtonY;
    private float buttonSize = 60f;
    
    // Sprint button (right bottom)
    private float sprintButtonX;
    private float sprintButtonY = 120f;
    
    public CameraController() {
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        
        // Calculate button positions
        rightButtonX = Gdx.graphics.getWidth() - 80;
        leftButtonX = rightButtonX - 100;
        rotateButtonY = Gdx.graphics.getHeight() / 2f;
        sprintButtonX = Gdx.graphics.getWidth() - 80;
    }
    
    public void render() {
        // Draw rotation buttons
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 0.3f);
        
        // Left rotate button
        shapeRenderer.circle(leftButtonX, rotateButtonY, buttonSize / 2);
        
        // Right rotate button  
        shapeRenderer.circle(rightButtonX, rotateButtonY, buttonSize / 2);
        
        // Sprint button
        shapeRenderer.setColor(0, 1, 0, 0.3f); // Green
        shapeRenderer.circle(sprintButtonX, sprintButtonY, buttonSize / 2);
        
        shapeRenderer.end();
        
        // Draw button labels
        spriteBatch.begin();
        font.draw(spriteBatch, "<", leftButtonX - 10, rotateButtonY + 10);
        font.draw(spriteBatch, ">", rightButtonX - 10, rotateButtonY + 10);
        font.draw(spriteBatch, "RUN", sprintButtonX - 25, sprintButtonY + 10);
        spriteBatch.end();
    }
    
    public boolean isLeftButtonPressed(float x, float y) {
        y = Gdx.graphics.getHeight() - y;
        float dx = x - leftButtonX;
        float dy = y - rotateButtonY;
        return Math.sqrt(dx * dx + dy * dy) < buttonSize / 2;
    }
    
    public boolean isRightButtonPressed(float x, float y) {
        y = Gdx.graphics.getHeight() - y;
        float dx = x - rightButtonX;
        float dy = y - rotateButtonY;
        return Math.sqrt(dx * dx + dy * dy) < buttonSize / 2;
    }
    
    public boolean isSprintButtonPressed(float x, float y) {
        y = Gdx.graphics.getHeight() - y;
        float dx = x - sprintButtonX;
        float dy = y - sprintButtonY;
        return Math.sqrt(dx * dx + dy * dy) < buttonSize / 2;
    }
    
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
    }
}
