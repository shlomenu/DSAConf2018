package concurrentQueue;

import java.util.concurrent.atomic.AtomicReference;

public class QueueNode<T> {
	
	public T value;
	public AtomicReference<QueueNode<T>> next;
	
	public QueueNode(T _value) {
		_value = value;
		next = new AtomicReference<QueueNode<T>>(null);
	}
	
}
