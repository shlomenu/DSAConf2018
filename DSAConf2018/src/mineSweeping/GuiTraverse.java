package mineSweeping;

// Data Structures & Algorithms
// Spring 2018
// HW2: bfs/dfs traversal
// visual comparison of bfs, dfs, and random walk


import java.util.HashSet;
import java.util.Random;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;


public class GuiTraverse extends Application {

    private static final int SCALE = 10;
    private static final int ROWS = 30;
    private static final int COLUMNS = 40;
    private static final int WIDTH = COLUMNS * SCALE;
    private static final int HEIGHT = ROWS * SCALE;
    private static final Color START_COLOR = Color.GREEN;
    private static final Color FIRST_VISIT_COLOR = Color.ORANGE;
    private static final Color REPEATED_VISIT_COLOR = Color.RED;

    private static Random randomizer = new Random();
    
    private HashSet<KeyCode> _pressed;
    private boolean _running;
    private boolean _started;
    private GraphicsContext _gc;
    private GridCounter _grid;
    private TraverserInterface _walker;
    
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("traversal comparison");
        Group root = new Group();
        Scene scene = new Scene(root);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        _gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        primaryStage.setScene(scene);

        _pressed = new HashSet<KeyCode>();
        _running = true;
        _started = false;

        scene.setOnKeyPressed(
            new EventHandler<KeyEvent>() {
                public void handle(KeyEvent e) {
                    _pressed.add(e.getCode());
                }
            });

        new AnimationTimer() {
            public void handle(long currentNanoTime)
            {
                if (_pressed.remove(KeyCode.Q)) {
                    stop();
                    System.exit(0);
                } else if (!_started) {
                    if (!_pressed.isEmpty()) {
                        _begin();
                        _started = true;
                    }
                } else {
                    if (_pressed.remove(KeyCode.SPACE)) {
                        _running = !_running;
                    }
                    if (_running && _grid.zeros() > 0 && _walker.hasNext()) {
                        Pair<Integer, Integer> p = _walker.next();
                        if (_grid.get(p.first(), p.second()) > 0) {
                            _paint(p.first(), p.second(), REPEATED_VISIT_COLOR);
                        } else {
                            _grid.increment(p.first(), p.second());
                            _paint(p.first(), p.second(), FIRST_VISIT_COLOR);
                        }
                    }
                }
            }
        }.start();
        
        primaryStage.show();
    }

    private void _begin() {
        int r = randomizer.nextInt(ROWS);
        int c = randomizer.nextInt(COLUMNS);
        Pair<Integer, Integer> origin = new Pair<Integer, Integer>(c, r);
       
        if (_pressed.contains(KeyCode.B)) {
            _walker = new Bfs(COLUMNS, ROWS, origin);
        } else if (_pressed.contains(KeyCode.D)) {
            _walker = new Dfs(COLUMNS, ROWS, origin);
        } else {
            _walker = new RandomWalk(COLUMNS, ROWS, origin);
        }
        _pressed.clear();

        _grid = new GridCounter(COLUMNS, ROWS);
        _grid.increment(c, r);
        _drawGrid();
        _paint(c, r, START_COLOR);
    }

    
    private void _paint(int x, int y, Color color) {
        _gc.setFill(color);
        int left = x * SCALE;
        int top = y * SCALE;
        _gc.fillRect(left+1, top+1, SCALE-1, SCALE-1);
    }

    private void _drawGrid() {
        _gc.setStroke(Color.BLACK);
        for (int top = 0; top < HEIGHT; top += SCALE) {
            for (int left = 0; left < WIDTH; left += SCALE) {
                _gc.strokeRect(left, top, SCALE, SCALE);
            }
        }
    }
}
