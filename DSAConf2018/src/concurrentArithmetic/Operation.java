package concurrentArithmetic;

public class Operation {

	public Operand _operand1;
	public boolean _operator;
	public Operand _operand2;
	public final int _priority;
	public Integer _field;
	
	public Operation(Operand operand1, boolean operator, Operand operand2, int priority) {
		_operand1 = operand1;
		_operator = operator;
		_operand2 = operand2;
		_priority = priority;
	}	
}

