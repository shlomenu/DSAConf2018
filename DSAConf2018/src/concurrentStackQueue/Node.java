package concurrentStackQueue;

import java.util.concurrent.atomic.AtomicReference;

public class Node<T> {
	
	public T value;
	public AtomicReference<Node<T>> next;
	
	public Node(T _value) {
		_value = value;
		next = new AtomicReference<Node<T>>(null);
	}
	
}
