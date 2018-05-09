package concurrentMineSweeper;

/**
 * depth-first grid traversal (solution)
 * 
 * @author Michael Siff
 */
public class Dfs implements TraverserInterface {

    private GridSet _grid;
    private ArrayListStack<Pair<Integer, Integer>> _stack;
    private int _columns;
    private int _rows;
    
    public Dfs(int columns, int rows, Pair<Integer, Integer> origin) {
        _grid = new GridSet(columns, rows);
        _stack = new ArrayListStack<Pair<Integer, Integer>>();
        _grid.set(origin.first(), origin.second());
        _columns = columns;
        _rows = rows;
        addPoint(origin);
    }
    
    public boolean hasNext() {
        return !_stack.isEmpty();
    }

    public Pair<Integer, Integer> next() {
        Pair<Integer, Integer> p = _stack.pop();
        addPoint(p);
        return p;
    }

    private void addPoint(Pair<Integer, Integer> p) {
        int x = p.first();
        int y = p.second();
        if (x > 0) {
            processPoint(x - 1, y);
        }
        if (y > 0) {
            processPoint(x, y - 1);
        }
        if (x < _columns - 1) {
            processPoint(x + 1, y);
        }
        if (y < _rows - 1) {
            processPoint(x, y + 1);
        }
    }

    private void processPoint(int x, int y) {
        if (!_grid.test(x, y)) {
            _stack.push(new Pair<Integer, Integer>(x, y));
            _grid.set(x, y);
        }
    }
    
}
