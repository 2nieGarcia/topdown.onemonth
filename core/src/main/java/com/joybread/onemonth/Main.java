package com.joybread.onemonth;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ApplicationAdapter {
    FitViewport viewport;
    SpriteBatch batch;

    // Character properties
    Texture bucketTexture;
    Vector2 bucketPosition;
    float bucketSpeed = 200f;

    // Shooting properties
    Vector2 shootStart;
    Vector2 shootEnd;
    boolean isShooting;

    @Override
    public void create() {
        viewport = new FitViewport(800, 480); // Set viewport size
        batch = new SpriteBatch();

        // Load the bucket texture
        bucketTexture = new Texture("bucket.png");

        // Initialize bucket position at the center of the screen
        bucketPosition = new Vector2(
                viewport.getWorldWidth() / 2 - bucketTexture.getWidth() / 2,
                viewport.getWorldHeight() / 2 - bucketTexture.getHeight() / 2
        );

        // Initialize shooting variables
        shootStart = new Vector2();
        shootEnd = new Vector2();
        isShooting = false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Update viewport on resize
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
        // Movement with W, A, S, D
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            bucketPosition.y += bucketSpeed * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            bucketPosition.y -= bucketSpeed * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            bucketPosition.x -= bucketSpeed * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            bucketPosition.x += bucketSpeed * Gdx.graphics.getDeltaTime();
        }

        // Shooting with mouse
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            isShooting = true;
            shootStart.set(bucketPosition.x + bucketTexture.getWidth() / 2, bucketPosition.y + bucketTexture.getHeight() / 2);
            shootEnd.set(Gdx.input.getX(), viewport.getWorldHeight() - Gdx.input.getY()); // Convert screen coordinates to world coordinates
        }
    }

    private void logic() {
        // Keep the bucket within the screen bounds
        bucketPosition.x = Math.max(0, Math.min(viewport.getWorldWidth() - bucketTexture.getWidth(), bucketPosition.x));
        bucketPosition.y = Math.max(0, Math.min(viewport.getWorldHeight() - bucketTexture.getHeight(), bucketPosition.y));
    }

    private void draw() {
        // Clear the screen
        ScreenUtils.clear(Color.BLACK);

        // Set up the viewport and batch
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Draw the bucket
        batch.draw(bucketTexture, bucketPosition.x, bucketPosition.y);

        // Draw the shooting line if shooting
        if (isShooting) {
            batch.end(); // End the current batch to switch to line rendering
            Gdx.gl.glLineWidth(2); // Set line thickness
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            batch.begin();
            drawLine(shootStart, shootEnd, Color.RED);
            isShooting = false; // Reset shooting state
        }

        batch.end();
    }

    private void drawLine(Vector2 start, Vector2 end, Color color) {
        // Draw a line using the SpriteBatch
        batch.setColor(color);
        batch.draw(bucketTexture, start.x, start.y, end.x, end.y);
        batch.setColor(Color.WHITE); // Reset color
    }

    @Override
    public void dispose() {
        // Clean up resources
        batch.dispose();
        bucketTexture.dispose();
    }
}