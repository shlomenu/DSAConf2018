package concurrentArithmetic;

// Symbol<Boolean>'s, which represent additions or multiplications, 
// are rendered as Operation objects, each containing a reference to
// its two operands

public class Operation {

	public Operand _leftOperand;
	public boolean _operator;
	public Operand _rightOperand;
	public final int _priority, _parentToken, _childToken;
	public Integer _field;
	
	public Operation(Operand operand1, boolean operator, Operand operand2, int priority, int parentToken, int childToken) {
		_leftOperand = operand1;
		_operator = operator;
		_rightOperand = operand2;
		_priority = priority;
		_parentToken = parentToken;
		_childToken = childToken;
	}	
}

