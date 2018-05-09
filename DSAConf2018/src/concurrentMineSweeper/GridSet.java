package concurrentMineSweeper;

/**
 * two-dimensional grid visitation set (solution)
 * 
 * @author Michael Siff
 */
public class GridSet {

    private GridCounter _grid;

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
