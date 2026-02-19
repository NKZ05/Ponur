package io.github.pingur;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Brick {

    private float x, y;
    private float width, height;
    private boolean destroyed;

    public Brick(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.destroyed = false;
    }

    // Vérifie collision avec la balle
    public boolean checkCollision(Ball ball) {
        if (destroyed) return false;

        float ballX = ball.getX();
        float ballY = ball.getY();
        float r = ball.getRadius();

        if (ballX + r > x && ballX - r < x + width &&
            ballY + r > y && ballY - r < y + height) {
            destroyed = true;
            return true;
        }
        return false;
    }

    public void renderShape(ShapeRenderer sr) {
        if (!destroyed) {
            sr.rect(x, y, width, height);
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
