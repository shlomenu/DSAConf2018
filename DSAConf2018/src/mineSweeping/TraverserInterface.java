package mineSweeping;

// Data Structures & Algorithms
// Spring 2018
// HW2: bfs/dfs traversal


/**
 * interface for grid traversal
 * 
 * @author Michael Siff
 */
public interface TraverserInterface {

    /**
     * Returns true if traverser has another place to go.
     * @return whether traverser has another place to go
     */
    boolean hasNext();
    
    /**
     * Returns next position traverser goes to assuming it has one;
     * otherwise throws an error.
     * @return next position traverser goes to
     */
    // get next pair assuming there is one
    Pair<Integer, Integer> next();

}
