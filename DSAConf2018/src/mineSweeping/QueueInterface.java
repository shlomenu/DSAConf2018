package mineSweeping;

// Data Structures & Algorithms
// Spring 2018


/**
 * Interface for generic queues.
 * 
 * @author Michael Siff
 */
public interface QueueInterface<T>
{
    /**
     * Returns true if queue is empty, false otherwise.
     * @return whether queue is empty
     */
    boolean isEmpty();

    
    /**
     * Appends specified element to back of queue.
     * @param element to be added onto back of queue
     */
    void enqueue(T element);

    
    /**
     * Removes and returns element currently sitting at front of queue,
     * assuming it is not empty. (If empty, it generates a "queue empty"
     * error.)
     * @return element at front of queue that is removed
     */
    T dequeue();
    
}
