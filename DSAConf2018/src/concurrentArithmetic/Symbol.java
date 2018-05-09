package concurrentArithmetic;

public class Symbol<T> {
	
	final int _priority;
	final T _field;
	
	public Symbol(T x, int priority) {
		_field = x;
		_priority = priority;
	}
	
}
