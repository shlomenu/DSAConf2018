package concurrentMineSweeper;


import java.util.ArrayList;


/**
 * generic queues using java.util.ArrayList (solution)
 * 
 * @author Michael Siff
 */
public class ArrayListQueue<T> {

    private static final int INITIAL_CAP = 5;
    private static final int GROWTH_MULTIPLIER = 2;
    
    private ArrayList<T> _array;
    private ArrayList<T> _scratch;
    private int _front;
    private int _back;
    private int _cap;
    private int _size;

    
    /**
     * Constructs an empty queue.
     */
    public ArrayListQueue() {
        _array = new ArrayList<T>();
        _scratch = new ArrayList<T>();
        _front = 0;
        _back = 0;
        _size = 0;
        _cap = INITIAL_CAP;
        _pad();
    }

    
    /**
     * Returns true if queue is empty, false otherwise.
     * @return whether queue is empty
     */
    public boolean isEmpty() {
        return _size == 0;
    }

    
    /**
     * Appends specified element to back of queue.
     * @param element to be added onto back of queue
     */
    public void enqueue(T element) {
        if (_size == _cap) {
            // if queue is currently full
            if (_front > 0) {
                // use scratch array to record all elements at back (before front)
                // then move all elements down so front is now 0
                // then copy scratch elements onto back
                _scratch.clear();
                for (int i = 0; i < _front; i++) {
                    _scratch.add(_array.get(i));
                }
                int j = 0;
                for (int i = _front; i < _cap; i++) {
                    _array.set(j, _array.get(i));
                    j++;
                }
                for (T x : _scratch) {
                    _array.set(j, x);
                    j++;
                }
                _front = 0;
            }
            _back = _size;
            _cap *= GROWTH_MULTIPLIER;
            _pad();
        }
        _array.set(_back, element);
        _back = (_back + 1) % _cap;
        _size++;
    }

    
    /**
     * Removes and returns element currently sitting at front of queue,
     * assuming it is not empty. (If empty, it generates a "queue empty"
     * error.)
     * @return element at front of queue that is removed
     */
    public T dequeue() {
        if (!isEmpty()) {
            T x = _array.get(_front);
            _front = (_front + 1) % _cap;
            _size--;
            return x;
        } else {
            throw new RuntimeException("queue empty");
        }
    }


    private void _pad() {
        while (_array.size() < _cap) {
            _array.add(null);
        }
    }

}
