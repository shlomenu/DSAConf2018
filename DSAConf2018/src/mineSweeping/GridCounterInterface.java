package mineSweeping;

// Data Structures & Algorithms
// Spring 2018
// HW2: bfs/dfs traversal


/**
 * interface for two-dimensional grid of counters
 * 
 * @author Michael Siff
 */
public interface GridCounterInterface {

    /**
     * Returns number of columns in grid.
     * @return number of columns in grid
     */
    int columns();

    /**
     * Returns number of rows in grid.
     * @return number of rows in grid
     */
    int rows();
    
    /**
     * Returns number of cells in grid that are still in initial state.
     * @return number of cells in grid in initial state
     */
    int zeros();

    /**
     * Returns number of times specified cell has been incremented,
     * assuming valid coordinates. Throws IndexOutOfBoundsException for
     * invalid coordinates.
     * @param x column coordinate
     * @param x row coordinate
     * @return number of times specified cell has been incremented
     */
    int get(int x, int y);

    /**
     * Increments counter for specified cell. Throws
     * IndexOutOfBoundsException for invalid coordinates.
     * @param x column coordinate
     * @param x row coordinate
     */
    void increment(int x, int y);

}
