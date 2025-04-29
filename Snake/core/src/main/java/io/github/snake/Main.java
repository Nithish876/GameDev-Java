package io.github.snake;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.LinkedList;
import java.util.Random;

public class Main extends ApplicationAdapter {

    private ShapeRenderer shapeRenderer;
    private LinkedList<Point> snake;
    private Point food;
    private Direction currentDirection;
    private float timer;
    private final float MOVE_INTERVAL = 0.15f; // Snake speed
    private final int GRID_SIZE = 25;
    private final int SCREEN_WIDTH = 1200;
    private final int SCREEN_HEIGHT = 800;
    private Random random;

    private boolean gameOver;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        snake = new LinkedList<>();
        snake.add(new Point(GRID_SIZE * 5, GRID_SIZE * 5)); // Starting point
        currentDirection = Direction.RIGHT;
        random = new Random();
        spawnFood();
    }

    @Override
    public void render() {
        handleInput();

        if (!gameOver) {
            timer += Gdx.graphics.getDeltaTime();
            if (timer >= MOVE_INTERVAL) {
                moveSnake();
                timer = 0;
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw food
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(food.x, food.y, GRID_SIZE, GRID_SIZE);

        // Draw snake
        shapeRenderer.setColor(Color.GREEN);
        for (Point p : snake) {
            shapeRenderer.rect(p.x, p.y, GRID_SIZE, GRID_SIZE);
        }

        shapeRenderer.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && currentDirection != Direction.DOWN) {
            currentDirection = Direction.UP;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && currentDirection != Direction.UP) {
            currentDirection = Direction.DOWN;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && currentDirection != Direction.RIGHT) {
            currentDirection = Direction.LEFT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && currentDirection != Direction.LEFT) {
            currentDirection = Direction.RIGHT;
        }
    }

    private void moveSnake() {
        Point head = snake.getFirst();
        Point newHead = new Point(head.x, head.y);

        switch (currentDirection) {
            case UP: newHead.y += GRID_SIZE; break;
            case DOWN: newHead.y -= GRID_SIZE; break;
            case LEFT: newHead.x -= GRID_SIZE; break;
            case RIGHT: newHead.x += GRID_SIZE; break;
        }

        // Check collision with walls
        if (newHead.x < 0 || newHead.x >= SCREEN_WIDTH || newHead.y < 0 || newHead.y >= SCREEN_HEIGHT) {
            gameOver = true;
            return;
        }

        // Check collision with itself
        for (Point p : snake) {
            if (p.equals(newHead)) {
                gameOver = true;
                return;
            }
        }

        snake.addFirst(newHead);

        // Check food collision
        if (newHead.equals(food)) {
            spawnFood();
        } else {
            snake.removeLast(); // Move by popping the tail if no food eaten
        }
    }

    private void spawnFood() {
        int maxX = SCREEN_WIDTH / GRID_SIZE;
        int maxY = SCREEN_HEIGHT / GRID_SIZE;
        food = new Point(random.nextInt(maxX) * GRID_SIZE, random.nextInt(maxY) * GRID_SIZE);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public boolean equals(Object o) {
            if (o instanceof Point) {
                Point p = (Point) o;
                return p.x == this.x && p.y == this.y;
            }
            return false;
        }
    }
}
 