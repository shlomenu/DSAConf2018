package mineSweeping;

// Data Structures & Algorithms
// Spring 2018
// HW2: bfs/dfs traversal


/**
 * interface for two-dimensional grid visitation set
 * 
 * @author Michael Siff
 */
public interface GridSetInterface {

    /**
     * Returns if specified cell has been visited, assuming valid
     * coordinates. Throws IndexOutOfBoundsException for invalid
     * coordinates.
     * @param x column coordinate
     * @param x row coordinate
     * @return whether specified cell has been visited
     */
    boolean test(int x, int y);

    /**
     * Marks specified cell as having been visited. Throws
     * IndexOutOfBoundsException for invalid coordinates.
     * @param x column coordinate
     * @param x row coordinate
     */
    void set(int x, int y);

}
