package concurrentArithmetic;

public class Operand {
	
	private final Object _field;
	final int _priority;
	
	public Operand(Integer x, int priority) {
		_field = x;
		_priority = priority;
	}
	
	public Operand(Operation x, int priority) {
		_field = x;
		_priority = priority;
	}
	
	public Integer getField() {
		Integer y = 0;
		if (_field.getClass() == y.getClass()) {
			return ((Integer)_field);
		} else {
			return ((Operation)_field)._field;
		}
	}
}
