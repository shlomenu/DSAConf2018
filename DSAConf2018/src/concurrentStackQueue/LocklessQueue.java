package concurrentStackQueue;

import java.util.concurrent.atomic.AtomicReference;
import java.util.EmptyStackException;

public class LocklessQueue<T> {

	AtomicReference<Node<T>> tail, head;
	
	public LocklessQueue() {
		tail = new AtomicReference<Node<T>>();
		head = tail;
	}
	
	public void enqueue(T value) {
		Node<T> node = new Node<T>(value);
		while (true) {
			Node<T> last = tail.get();
			Node<T> next = last.next.get();
			if (last == tail.get()) {
				if (next == null) {
					if (last.next.compareAndSet(next, node)) {
						tail.compareAndSet(last, node);
						return;
					}
				} else {
					tail.compareAndSet(last, next);
				}
	
			}
			
		
		}
	}
	
	public T dequeue() throws EmptyStackException {
		while (true) {
			Node<T> _head = head.get();
			Node<T> _tail = tail.get();
			Node<T> next = _head.next.get();
			if (_head == head.get()) {
				if (_head == _tail) {
					if (next == null) {
						throw new EmptyStackException();
					}
				}
				tail.compareAndSet(_tail, next);
			} else {
				T _value = next.value;
				if (head.compareAndSet(_head, next)) {
					return _value;
				}
				
			}
		}
	}
	
}
	

