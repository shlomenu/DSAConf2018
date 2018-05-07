package mineSweeping;


/**
 * breadth-first grid traversal (solution)
 * 
 * @author Michael Siff
 */
public class Bfs implements TraverserInterface {

    private GridSetInterface _grid;
    private QueueInterface<Pair<Integer, Integer>> _queue;
    private int _columns;
    private int _rows;
    
    public Bfs(int columns, int rows, Pair<Integer, Integer> origin) {
        _grid = new GridSet(columns, rows);
        _queue = new ArrayListQueue<Pair<Integer, Integer>>();
        _grid.set(origin.first(), origin.second());
        _columns = columns;
        _rows = rows;
        addPoint(origin);
    }
    
    public boolean hasNext() {
        return !_queue.isEmpty();
    }

    public Pair<Integer, Integer> next() {
        Pair<Integer, Integer> p = _queue.dequeue();
        addPoint(p);
        return p;
    }

    private void addPoint(Pair<Integer, Integer> p) {
        int x = p.first();
        int y = p.second();
        if (x > 0) {
            process_point(x - 1, y);
        }
        if (y > 0) {
            process_point(x, y - 1);
        }
        if (x < _columns - 1) {
            process_point(x + 1, y);
        }
        if (y < _rows - 1) {
            process_point(x, y + 1);
        }
    }


    private void process_point(int x, int y) {
        if (!_grid.test(x, y)) {
            _queue.enqueue(new Pair<Integer, Integer>(x, y));
            _grid.set(x, y);
        }
    }
    
}
