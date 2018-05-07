package mineSweeping;

// Data Structures & Algorithms
// Spring 2018
// HW2: bfs/dfs traversal


/** 
 * interface for generic immutable pairs
 *
 * @author Michael Siff
 */
public interface PairInterface<T, U> {

    T first();

    U second();

    boolean equals(PairInterface<T, U> otherPair);
    
    String toString();

}
