package concurrentArithmetic;

public class Operation {

	public Operand _leftOperand;
	public boolean _operator;
	public Operand _rightOperand;
	public final int _priority;
	public Integer _field;
	
	public Operation(Operand operand1, boolean operator, Operand operand2, int priority) {
		_leftOperand = operand1;
		_operator = operator;
		_rightOperand = operand2;
		_priority = priority;
	}	
}

