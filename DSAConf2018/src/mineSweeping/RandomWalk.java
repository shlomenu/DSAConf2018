package mineSweeping;

// Data Structures & Algorithms
// Spring 2018
// HW2: bfs/dfs traversal


import java.util.ArrayList;
import java.util.Random;


/**
 * random walk traversal for grid
 * 
 * @author Michael Siff
 */
public class RandomWalk implements TraverserInterface {

    private static Random _randomizer = new Random();

    private int _columns;
    private int _rows;
    private Pair<Integer, Integer> _last;
    private ArrayList<Pair<Integer, Integer>> _choices;
    
    public RandomWalk(int columns, int rows, Pair<Integer, Integer> origin) {
        _columns = columns;
        _rows = rows;
        _last = origin;
        _choices = new ArrayList<Pair<Integer, Integer>>();
    }
    
    public boolean hasNext() {
        return true;
    }

    public Pair<Integer, Integer> next() {
        _choices.clear();
        int x = _last.first();
        int y = _last.second();
        if (x > 0) {
            _choices.add(new Pair<Integer, Integer>(x - 1, y));
        }
        if (y > 0) {
            _choices.add(new Pair<Integer, Integer>(x, y - 1));
        }
        if (x < _columns - 1) {
            _choices.add(new Pair<Integer, Integer>(x + 1, y));
        }
        if (y < _rows - 1) {
            _choices.add(new Pair<Integer, Integer>(x, y + 1));
        }
        _last = _choices.get(_randomizer.nextInt(_choices.size()));
        return _last;
    }

}
