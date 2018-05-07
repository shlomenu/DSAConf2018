package mineSweeping;

// Data Structures & Algorithms
// Spring 2018
// HW2: bfs/dfs traversal


/**
 * two-dimensional grid visitation set (solution)
 * 
 * @author Michael Siff
 */
public class GridSet implements GridSetInterface {

    private GridCounterInterface _grid;

    public GridSet(int columns, int rows) {
        _grid = new GridCounter(columns, rows);
    }
    
    public boolean test(int x, int y) {
        if (y >= 0 && y < _grid.rows() && x >= 0 && x < _grid.columns()) {
            return (_grid.get(x, y) > 0);
        } else {
            throw new IndexOutOfBoundsException("<" + x + ", " + y + ">");
        }
    }

    public void set(int x, int y) {
        if (y >= 0 && y < _grid.rows() &&
            x >= 0 && x < _grid.columns() &&
            _grid.get(x, y) == 0) {
            _grid.increment(x, y);
        } else {
            throw new IndexOutOfBoundsException("<" + x + ", " + y + ">");
        }
    }

}
