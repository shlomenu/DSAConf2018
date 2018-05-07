package mineSweeping;

// Data Structures & Algorithms
// Spring 2018


/**
 * Interface for generic stacks.
 * 
 * @author Michael Siff
 */
public interface StackInterface<T>
{

    /**
     * Returns true if stack is empty, false otherwise.
     * @return whether stack is empty
     */
    boolean isEmpty();
    
    /**
     * Pushes specified element onto top of stack.
     * @param element to be pushed onto top of stack
     */
    void push(T element);
    
    /**
     * Removes and returns element currently on top of stack, assuming
     * stack not empty. (If empty, generates a "stack underflow" error.)
     * @return element on top of stack that is removed
     */
    T pop();
    
}
