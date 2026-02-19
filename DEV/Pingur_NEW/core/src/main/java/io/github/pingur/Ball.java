package io.github.pingur;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Ball {

    private float x, y;
    private float radius;
    private float speedX, speedY;

    public Ball(float startX, float startY) {
        radius = 10f;          // rayon de la balle
        x = startX;
        y = startY;
        speedX = 200f;         // vitesse horizontale
        speedY = 200f;         // vitesse verticale
    }


    public void update(float delta) {
        x += speedX * delta;
        y += speedY * delta;

        // Rebond
        if (x - radius < 0) {
            x = radius;
            speedX = -speedX;
        }
        if (x + radius > com.badlogic.gdx.Gdx.graphics.getWidth()) {
            x = com.badlogic.gdx.Gdx.graphics.getWidth() - radius;
            speedX = -speedX;
        }

        if (y + radius > com.badlogic.gdx.Gdx.graphics.getHeight()) {
            y = com.badlogic.gdx.Gdx.graphics.getHeight() - radius;
            speedY = -speedY;
        }
    }

    public void renderShape(ShapeRenderer sr) {
        sr.circle(x, y, radius);
    }


    public float getX() { return x; }
    public float getY() { return y; }
    public float getRadius() { return radius; }

    public void invertY() { speedY = -speedY; }
}
