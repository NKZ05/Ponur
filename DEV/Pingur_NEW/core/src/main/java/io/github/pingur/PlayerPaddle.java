package io.github.pingur;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PlayerPaddle {

    private float x, y;
    private float width, height;
    private float speed;

    public PlayerPaddle(int screenWidth) {
        width = 100f;
        height = 20f;
        x = screenWidth / 2f - width / 2f;
        y = 50f;
        speed = 400f;
    }

    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += speed * delta;
        }

        // Empêche la raquette de sortir de l'écran
        if (x < 0) {
            x = 0;
        }
        if (x + width > Gdx.graphics.getWidth()) {
            x = Gdx.graphics.getWidth() - width;
        }
    }


    public void renderShape(ShapeRenderer sr) {
        sr.rect(x, y, width, height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

}
