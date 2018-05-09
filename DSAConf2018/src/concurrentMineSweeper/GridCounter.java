package concurrentMineSweeper;

/**
 * two-dimensional grid of counters (solution)
 * 
 * @author Michael Siff
 */
public class GridCounter {

    private int[][] _matrix;
    private int _columns;
    private int _zeros;

    public GridCounter(int columns, int rows) {
        _matrix = new int[rows][columns];
        _zeros = rows * columns;
        for (int i = 0; i < rows; i++) {
             for (int j = 0; j < columns; j++) {
                 _matrix[i][j] = 0;
             }
        }
        _columns = columns;
    }

    public int columns() {
        return _columns;
    }

    public int rows() {
        return _matrix.length;
    }

    public int zeros() {
        return _zeros;
    }

    public int get(int x, int y) {
        if (y >= 0 && y < _matrix.length && x >= 0 && x < _columns) {
            return _matrix[y][x];
        } else {
            throw new IndexOutOfBoundsException("<" + x + ", " + y + ">");
        }
    }
        
    public void increment(int x, int y) {
        if (y >= 0 && y < _matrix.length && x >= 0 && x < _columns) {
            if (_matrix[y][x] == 0) {
                _zeros--;
            }
            _matrix[y][x]++;
        } else {
            throw new IndexOutOfBoundsException("<" + x + ", " + y + ">");
        }
    }

}
