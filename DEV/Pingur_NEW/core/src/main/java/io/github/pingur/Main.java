package io.github.pingur;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Input;

public class Main extends ApplicationAdapter {

    private ShapeRenderer shapeRenderer;
    private PlayerPaddle paddle;
    private Ball ball;
    private Brick[][] bricks;
    private int ROWS = 8;
    private int COLUMNS = 12;
    private int score = 0;
    private int lives = 3;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private boolean paused = false;
    private boolean gameOver = false;


    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();

        //HUD
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.2f);
    layout = new GlyphLayout();

        // Initialise le jeu
        resetGame();
    }

    private void resetGame() {
    
        paddle = new PlayerPaddle(Gdx.graphics.getWidth());
        ball = new Ball(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);

        createBricks();

        // Menu
        score = 0;
        lives = 3;
        paused = false;
        gameOver = false;
    }

    // Relance du jeu
    private void createBricks() {
        bricks = new Brick[ROWS][COLUMNS];

        float fixedBrickWidth = 40f; // largeur cible
        float fixedBrickHeight = 14f; // hauteur fixe
        float spacing = 2f; // espacement entre briques

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float totalRequiredWidth = COLUMNS * fixedBrickWidth + (COLUMNS - 1) * spacing;
        float effectiveBrickWidth = fixedBrickWidth;
        if (totalRequiredWidth > screenW - 20f) { 
            effectiveBrickWidth = (screenW - 20f - (COLUMNS - 1) * spacing) / COLUMNS;
            if (effectiveBrickWidth < 8f) effectiveBrickWidth = 8f;
        }

        float gridWidth = COLUMNS * effectiveBrickWidth + (COLUMNS - 1) * spacing;
        float startX = (screenW - gridWidth) / 2f;
        float topMargin = 80f;
        float startY = screenH - topMargin;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                float x = startX + j * (effectiveBrickWidth + spacing);
                float y = startY - i * (fixedBrickHeight + spacing) - fixedBrickHeight;
                bricks[i][j] = new Brick(
                        x,
                        y,
                        effectiveBrickWidth - 2,
                        fixedBrickHeight - 2
                );
            }
        }
    }

    // Nouvelle vague
    private void startNextWave() {
        createBricks();
        ball = new Ball(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        paused = false;
    }



    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        // Pause
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;
        }

        // Restart
        if (gameOver && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
            return;
        }

        // Clear
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!paused && !gameOver) {
            paddle.update(delta);
            ball.update(delta);

            // Collision
            if (ball.getY() - ball.getRadius() < paddle.getY() + paddle.getHeight() &&
                    ball.getY() - ball.getRadius() > paddle.getY() &&
                    ball.getX() > paddle.getX() &&
                    ball.getX() < paddle.getX() + paddle.getWidth()) {
                ball.invertY();
            }
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        paddle.renderShape(shapeRenderer);
        ball.renderShape(shapeRenderer);

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Brick b = bricks[i][j];
                b.renderShape(shapeRenderer);

                if (!paused && !gameOver && b.checkCollision(ball)) {
                    ball.invertY();
                    score += 10; // Points
                }
            }
        }

        shapeRenderer.end();

        // Perte de vie
        if (!paused && !gameOver && ball.getY() - ball.getRadius() < 0) {
            lives--;
            if (lives > 0) {
                ball = new Ball(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
            } else {
                System.out.println("GAME OVER ! Score: " + score);
                gameOver = true;
            }
        }

        // HUD
        batch.begin();

        // Nombre de briques restantes
        int bricksLeft = countRemainingBricks();

        if (bricksLeft == 0 && !gameOver) {
            startNextWave();
            bricksLeft = countRemainingBricks();
        }

        layout.setText(font, "Score: " + score);
        font.draw(batch, layout, 10, Gdx.graphics.getHeight() - 10);

        String livesText = "Lives: " + lives;
        layout.setText(font, livesText);
        float livesX = Gdx.graphics.getWidth() - layout.width - 10;
        font.draw(batch, livesText, livesX, Gdx.graphics.getHeight() - 10);

        String bricksText = "Briques: " + bricksLeft;
        layout.setText(font, bricksText);
        float bx = (Gdx.graphics.getWidth() - layout.width) / 2f;
        font.draw(batch, bricksText, bx, Gdx.graphics.getHeight() - 10);



        if (paused) {
            String pausedText = "PAUSE (P pour reprendre)";
            layout.setText(font, pausedText);
            float px = (Gdx.graphics.getWidth() - layout.width) / 2f;
            font.draw(batch, pausedText, px, Gdx.graphics.getHeight() / 2f);
        }

        if (gameOver) {
            String go = "GAME OVER - appuyez sur R pour rejouer";
            layout.setText(font, go);
            float gx = (Gdx.graphics.getWidth() - layout.width) / 2f;
            font.draw(batch, go, gx, Gdx.graphics.getHeight() / 2f - 30);
        }

        batch.end();
    }

    private int countRemainingBricks() {
        int left = 0;
        if (bricks == null) return 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Brick b = bricks[i][j];
                if (b != null && !b.isDestroyed()) left++;
            }
        }
        return left;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}
