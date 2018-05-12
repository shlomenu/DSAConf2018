package concurrentQueue;

// This algorithm is directly imitating--when not transcribing--Herlihy and Shavit

import java.util.concurrent.atomic.AtomicReference;
import java.util.EmptyStackException;


public class LocklessQueue<T> {

	AtomicReference<QueueNode<T>> tail, head;
	
	public LocklessQueue() {
		tail = new AtomicReference<QueueNode<T>>();
		head = tail;
	}
	
	public void enqueue(T value) {
		// create new node
		QueueNode<T> node = new QueueNode<T>(value);
		while (true) {
			// get tail and tail.next
			QueueNode<T> _tail = tail.get();
			QueueNode<T> _next = _tail.next.get();
			if (_tail == tail.get()) { // if tail has not been modified
				if (_next == null) { // if _next was null
					if (_tail.next.compareAndSet(_next, node)) {
						// if next is still null
						// set tail.next to node
						tail.compareAndSet(_tail, node);
						// advance the tail pointer up one to the new node
						return;
					}
				} else { // if next was not null, then the tail 
						 // is not accurately located, so advance it
					tail.compareAndSet(_tail, _next);
				}
	
			}
		}
	}
	
	public T dequeue() throws EmptyStackException {
		while (true) {
			// get head, tail, and head.next
			QueueNode<T> _head = head.get();
			QueueNode<T> _tail = tail.get();
			QueueNode<T> _next = _head.next.get();
			if (_head == head.get()) { // if _head is still head
				if (_head == _tail) { // if head is tail
					if (_next == null) { // and tail cannot be advanced
						// then there is nothing on the stack
						throw new EmptyStackException();
					}
					// if not, advance it where another thread was interrupted
					tail.compareAndSet(_tail, _next);
				} else { // if there is a value between head and tail
					T _value = _next.value; // grab it
					// and try to shift head up one to sit on top of the removed value
					if (head.compareAndSet(_head, _next)) {
						return _value;
					}
				}
			}
		}
	}
}
	

