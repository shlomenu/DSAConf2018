package concurrentStackQueue;

import java.util.concurrent.atomic.AtomicReference;
import java.util.EmptyStackException;

public class LocklessQueue<T> {

	AtomicReference<QueueNode<T>> tail, head;
	
	public LocklessQueue() {
		tail = new AtomicReference<QueueNode<T>>();
		head = tail;
	}
	
	public void enqueue(T value) {
		QueueNode<T> node = new QueueNode<T>(value);
		while (true) {
			QueueNode<T> _tail = tail.get();
			QueueNode<T> _next = _tail.next.get();
			if (_tail == tail.get()) {
				if (_next == null) {
					if (_tail.next.compareAndSet(_next, node)) {
						tail.compareAndSet(_tail, node);
						return;
					}
				} else {
					tail.compareAndSet(_tail, _next);
				}
	
			}
			
		
		}
	}
	
	public T dequeue() throws EmptyStackException {
		while (true) {
			QueueNode<T> _head = head.get();
			QueueNode<T> _tail = tail.get();
			QueueNode<T> _next = _head.next.get();
			if (_head == head.get()) {
				if (_head == _tail) {
					if (_next == null) {
						throw new EmptyStackException();
					}
					tail.compareAndSet(_tail, _next);
				} else {
					T _value = _next.value;
					if (head.compareAndSet(_head, _next)) {
						return _value;
					}
				}
			}
		}
	}
}
	

