package concurrentStackQueue;

import java.util.concurrent.atomic.AtomicReference;
import java.util.EmptyStackException;

public class LocklessQueue<T> {

	AtomicReference<Node> tail, head;
	
	public LocklessQueue() {
		tail = new AtomicReference<Node>();
		head = tail;
	}
	
	private class Node {
		public T value;
		public AtomicReference<Node> next;
		public Node(T _value) {
			_value = value;
			next = new AtomicReference<Node>(null);
		}
	}
	public void enqueue(T value) {
		Node node = new Node(value);
		while (true) {
			Node last = tail.get();
			Node next = last.next.get();
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
			Node _head = head.get();
			Node _tail = tail.get();
			Node next = _head.next.get();
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
	

