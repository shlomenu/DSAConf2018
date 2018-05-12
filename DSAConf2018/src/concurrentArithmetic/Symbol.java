package concurrentArithmetic;

// This class is used to create an internal representation of arithmetic 
// expressions evaluated by concurrentParser.  It stores the identity of 
// the symbol (addition, multiplication, or integer value), and this symbol's
// level of nesting within the original expression

public class Symbol<T> {
	
	final int _priority;
	T _field;
	int _parentToken, _childToken;
	
	public Symbol(T x, int priority, int parentToken, int childToken) {
		_field = x;
		_priority = priority;
		_parentToken = parentToken;
		_childToken = childToken;
	}
	
}
