package concurrentArithmetic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import concurrentStack.EliminationBackoffStack;

@SuppressWarnings("unused")
public class concurrentParser {
	
	public static final int INITIAL_NUMBER_OF_THREADS = 3;
	
	// Java did not provide an easy way to do this via String methods
	private static class Iterator {
		String _string;
		int _index = 0;
		
		public Iterator(String str) {
			_string = str;
		}
		
		public boolean hasNext() {
			if (_index + 1 > _string.length()) {
				return false;
			} else {
				return true;
			}
		}
		
		// Just wrapper around charAt; must be used with hasNext to be safe
		public char next() {
			char result = _string.charAt(_index);
			_index++;
			return result;
		}
	}

	public static void main(String[] args) throws IOException, InvalidArithmeticExpressionException {
		// Fetching string from given file
		Scanner scanner = new Scanner(System.in);
		System.out.println("Working directory: " + System.getProperty("user.dir"));
		System.out.print("File to parse: ");
		String filename = scanner.next();
		String source = new String(Files.readAllBytes(Paths.get(filename)));
		scanner.close();
		
		// Building expressions and operations list and array (respectively)
		@SuppressWarnings("rawtypes")
		Object[] expressions = expressionBuilder(new Iterator(source), new ArrayList<Symbol>(), 1);
		Object[] operations = new Object[expressions.length];
		
		// make (default) three new threads
		Thread[] threads = new Thread[INITIAL_NUMBER_OF_THREADS];
		for (int j = 0; j < threads.length; j++) {
			
			threads[j] = new Thread(new Runnable() { 
				
				// It makes it much easier to pair up operations and operands if 
				// their order from the original expression is maintaned, so it is 
				// most efficient if expressions and operations are both just arrays,
				// one of which is being nullified as the other is being populated.  
				// We must count how many Symbol objects actually remain since the size
				// of expressions is constant
				
				volatile int priorityLevel = 1; // keeps the stack ordered
				volatile int number_unprocessed_symbols = expressions.length;
				EliminationBackoffStack<Operation> stack = new EliminationBackoffStack<Operation>(3);
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				public void run() {
					// This loop does all the pushing
					while (number_unprocessed_symbols > 0) { 
					opLoop: 
						while (true) { // This loop prepares the operations of each priority level
							
							for (int i = 0; i < expressions.length; i++) {
								
								// if priority level was not held constant through a successful iteration,
								// it is possible that operand pointers from a previous priority level 
								// would be "left behind."
								int myPriorityLevel = priorityLevel;
								
								// for-loop can only reach end if it finds no operations,
								// so it should move on to finding operands
								if (expressions[i] == null) {
									if (i + 1 == expressions.length) {
										break opLoop;
									} else {
										continue;
									}
								} 
								
								Symbol _symbol = (Symbol) expressions[i];
								
								// if current symbol contains an operation and matches current priority level
								if (_symbol._field.getClass() == Boolean.class && _symbol._priority == myPriorityLevel) {
									
									// All operations are inserted with null operands, pointers added at higher levels
									Operation op = new Operation(null, (boolean)_symbol._field, null, _symbol._priority);
									
									// Remove from expressions
									synchronized (expressions) {
										if (_symbol == expressions[i]) { // Check if some other thread go to it
											expressions[i] = null; // if not nullify
										} else { // if so traverse again
											continue opLoop;
										}
									}
									
									// Operation can be pushed as soon as it has been 
									// uniquely removed.
									stack.push(op);
									
									// Discount removed symbol
									number_unprocessed_symbols--;
								
									// add to operations
									synchronized (operations) { 
										operations[i] = op;
									}
										
									// Only level 1 operations (there should only be one) are not the operand
									// of any other operation
									if (myPriorityLevel > 1) {
										Operation curr;
										// Iterate over operations
										for (int k = 0; k < operations.length; k++) {
											curr = (Operation) operations[k];
											if (curr == null) {
												continue; // skip nulls
											} else if (curr._priority == op._priority - 1) { // there can only be one of these
												if (k < i) { // operation is right of parent operation
													curr._rightOperand = new Operand(op, op._priority);
												} 
												if (i < k) { // operation is left of parent operation
													curr._leftOperand = new Operand(op, op._priority);
												}
												// Begin new traversal of expressions
												continue opLoop;
											}
										}
										// if for some inconceivable reason there is a parent and it 
										throw new InvalidArithmeticExpressionException();
									}
										
//										while (!expressionsLock.tryLock()) {
//											try {
//												expressionsCondition.wait(3);
//											} catch (InterruptedException ex) {
//												return;
//											}
//										}
											
//										if (_symbol == expressions.get(i)) {
//											expressions.set(i, null);
//											expressionsLock.unlock();
//											expressionsCondition.signalAll();													
//											while (!operationsLock.tryLock()) {
//												try {
//													operationsCondition.wait(3);
//												} catch (InterruptedException ex) {
//													expressions.set(i, _symbol);
//													priorityLevel = _symbol._priority;
//													return;
//												}
//											}
//											operations.set(i, op);
//											if (myPriorityLevel > 1) {
//												Operation curr;
//												for (int k = 0; k < operations.size(); k++) {
//													curr = operations.get(k);
//													if (curr == null) {
//														continue;
//													}
//													if (curr._priority == op._priority - 1) {
//														if (k < i) {
//															curr._operand2 = new Operand(op, op._priority);
//														} 
//														if (i < k) {
//															curr._operand1 = new Operand(op, op._priority);
//														}
//													}
//												}
//											}
//											operationsLock.unlock();
//											operationsCondition.signalAll();
//											stack.push(op);
//										} else {
//											continue opLoop;
//										}
										
								}
								
								// if completed full traversal 
								if (i + 1 == expressions.length) {
									break opLoop;
								}
							}
						}
					oprdLoop:
						while (number_unprocessed_symbols > 0) { // This loop processes the operands of each priority level	
							for (int i = 0; i < expressions.length; i++) {
								
								int myPriorityLevel = priorityLevel;
								
								if (expressions[i] == null) {
									if (i + 1 == expressions.length) {
										priorityLevel++;
										break oprdLoop;
									} else {
										continue;
									}
								} 
								
								Symbol _symbol = (Symbol) expressions[i];
							
								if (_symbol._field.getClass() == Integer.class && _symbol._priority == myPriorityLevel) {
											
									Operand oprd = new Operand(((Symbol<Integer>)_symbol)._field, _symbol._priority);
										
									synchronized (expressions) {
										if (_symbol == expressions[i]) {
											expressions[i] = null;
										} else {
											continue oprdLoop;
										}
									}
										
									synchronized (operations) {
										if (i > 0) {
											if (((Operation)operations[i-1])._priority == oprd._priority) {
												((Operation)operations[i-1])._rightOperand = oprd;
											} 
										}
										if (i < expressions.length - 1) {
											if (((Operation)operations[i+1])._priority == oprd._priority) {
												((Operation)operations[i+1])._leftOperand = oprd;
											}
										}
									}
										
//										while (!expressionsLock.tryLock()) {
//											try {
//												expressionsCondition.wait(3);
//											} catch (InterruptedException ex) {
//												return;
//											}
//										}
//										
//										if (_symbol == expressions.get(i)) {
//											expressions.set(i, null);
//											expressionsLock.unlock();
//											expressionsCondition.signalAll();
//											while (!operationsLock.tryLock()) {
//												try {
//													operationsCondition.wait(3);
//												} catch (InterruptedException ex) {
//													expressions.set(i, _symbol);
//													priorityLevel = _symbol._priority;
//													return;
//												}
//											}
//											if (operations.get(i-1)._priority == oprd._priority) {
//												operations.get(i-1)._operand2 = oprd;
//											} 
//											if (operations.get(i+1)._priority == oprd._priority) {
//												operations.get(i+1)._operand1 = oprd;
//											}
//											operationsLock.unlock();
//											operationsCondition.signalAll();
//										} else {
//											continue intLoop;
//										}
										
								}
								
								// if passed through all Symbol<Boolean>'s and now all Symbol<Integer>'s 
								// at this priority level, raise priority level and restart pushing loop
								if (i + 1 == expressions.length) {
									priorityLevel++;
									break oprdLoop;
								}
							}
						}
						
					}
			
					// This is the popping loop
					Operation mine;
					while (true) { 
						try {
							mine = stack.pop();
							while (mine._leftOperand.getField() == null || mine._rightOperand.getField() == null) {/*spin*/}
							// Because each operation's field must be calculated by a thread after it is popped,
							// the recursion that would, ideally, be perfectly preserved by the stack, is necessary
							if (mine._operator == true) { // if multiplication operation
								mine._field = mine._leftOperand.getField() * mine._rightOperand.getField();
							} else { // if addition operation
								mine._field = mine._leftOperand.getField() + mine._rightOperand.getField();
							}
						} catch (EmptyStackException ex) { 
							return; // Each thread's run() finally ends when it pops off 
									// an empty stack.
						}
					}
			
				}
			});
		}
		
		for (Thread thread : threads) {
			thread.run();
		}
		
	}
	
	private static Object[] expressionBuilder(Iterator it, ArrayList<Symbol> list, int priority) throws InvalidArithmeticExpressionException {
		expressionBuilderHelper(it, list, priority);
		return list.toArray();
		
	}
	
	// This method takes an iterator over an infixed, fully parenthesized (meaning in readme) 
	// arithmetic expression and returns a list with symbol objects
	@SuppressWarnings("rawtypes")
	private static void expressionBuilderHelper(Iterator it, ArrayList<Symbol> list, int priority) throws InvalidArithmeticExpressionException {
		if (it.hasNext()) {
			char next = it.next();
			// Advances iterator for first character of each call/recursion
			outer: while (it.hasNext()) {
				// Each clause advances iterator individually
				if (next == ' ') {
					while (next == ' ') { // if whitespace advance to next non-space
						if (it.hasNext()) {
							next = it.next();
						} else { // or recognize that end has been reached
							break outer;
						}
					}
				} else if (next == '(') { // if open parenthesis recurse
					expressionBuilder(it, list, priority + 1);
					if (priority > 1) { // if priority == 1 there may be nothing next
						next = it.next();
					}
				} else if (Character.isDigit(next)) { // if number has started
					String s = "" + next; // add current digit
					while (it.hasNext()) { // keep concatenating digits
						char _next = it.next();
						if (!Character.isDigit(_next)) { // until whitespace (which should be there)
							next = _next;
							break;
						}
						s += _next;
					}
					// and turn it into new Symbol<Integer>
					list.add(new Symbol<Integer>(Integer.valueOf(s), priority)); 
					
				} else if (next == '+') { // addition represented as false boolean Symbol object
					list.add(new Symbol<Boolean>(false, priority));
					next = it.next();
				} else if (next == '*') { // multiplication represented as true boolean Symbol object
					list.add(new Symbol<Boolean>(true, priority));
					next = it.next();
				} else if (next == ')') { // end recursion
					return;
				} else {
					throw new InvalidArithmeticExpressionException();
				}
			}
		}
	}
	
}
