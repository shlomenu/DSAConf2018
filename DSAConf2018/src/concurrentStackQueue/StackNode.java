package concurrentStackQueue;

public class StackNode<T> {

	public T value;
	public StackNode<T> next;
	
	public StackNode(T _value) {
		value = _value;
		next = null;
	}
	
}
