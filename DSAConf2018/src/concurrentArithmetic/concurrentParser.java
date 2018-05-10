package concurrentArithmetic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import concurrentStack.EliminationBackoffStack;


public class concurrentParser {
	
	public static final int INITIAL_NUMBER_OF_THREADS = 3;

	public static void main(String[] args) throws IOException, InvalidArithmeticExpressionException {
		// Fetching string from given file
		Scanner scanner = new Scanner(System.in);
		System.out.println("Working directory: " + System.getProperty("user.dir"));
		System.out.print("File to parse: ");
		String filename = scanner.next();
		String source = new String(Files.readAllBytes(Paths.get(filename)));
		scanner.close();
		
		// Building expressions and operations arrays (respectively)
		@SuppressWarnings("rawtypes")
		AtomicReferenceArray<Symbol> expressions = expressionBuilder(new Iterator(source), new ArrayList<Symbol>(), 1);
		AtomicReferenceArray<Operation> operations = new AtomicReferenceArray<Operation>(expressions.length());
		EliminationBackoffStack<Operation> stack = new EliminationBackoffStack<Operation>(INITIAL_NUMBER_OF_THREADS);
		AtomicInteger priorityLevel = new AtomicInteger(1);
		AtomicInteger number_unprocessed_symbols = new AtomicInteger(expressions.length());
		
		// make default number of threads (3)
		Thread[] threads = new Thread[INITIAL_NUMBER_OF_THREADS];
		for (int j = 0; j < threads.length; j++) {
			threads[j] = new Thread(new Runnable() { 
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				public void run() {
					// This loop does all the pushing
					while (number_unprocessed_symbols.get() > 0) { 
					opLoop: 
						while (true) { // This loop prepares the operations of each priority level
							int initialPriorityLevel = priorityLevel.get();
							for (int i = 0; i < expressions.length(); i++) {
								Symbol symbol = expressions.get(i);
								if (symbol != null) {
									if (symbol._field.getClass() == Boolean.class && symbol._priority == priorityLevel.get()) {
										if (expressions.compareAndSet(i, symbol, null)) {
											Operation op = new Operation(null, (boolean) symbol._field, null, symbol._priority);
											operations.set(i, op);
											number_unprocessed_symbols.getAndDecrement();
											stack.push(op);
											if (op._priority > 1) {
												Operation curr;
												for (int k = 0; k < operations.length(); k++) {
													curr = operations.get(k);
													if (curr == null) {
														continue; 
													} else if (curr._priority == op._priority - 1) { 
														if (k < i) { // operation is right of parent operation
															if (curr._rightOperand != null) {
																continue;
															} else {
																curr._rightOperand = new Operand(op, op._priority);
															}
														} 
														if (i < k) { // operation is left of parent operation
															if (curr._leftOperand != null) {
																continue;
															} else {
																curr._leftOperand = new Operand(op, op._priority);
															}
														}
														// Begin new traversal of expressions
														continue opLoop;
													}
												}
												throw new InvalidArithmeticExpressionException();
											}
										}
									}
								} 
								if (i + 1 == expressions.length() && initialPriorityLevel == priorityLevel.get()) {
									break opLoop;
								}
							}
						}
								
								
//								if (symbol == null) {
//									if (i + 1 == expressions.length() && initialPriorityLevel == priorityLevel.get()) {
//										break opLoop;
//									} else {
//										continue;
//									}
//								} else if 
								
								// if current symbol contains an operation and matches current priority level
//								if (symbol._field.getClass() == Boolean.class && symbol._priority == priorityLevel.get()) {
//									
//									// Remove from expressions
//									if (!expressions.compareAndSet(i, symbol, null)) {
//										continue opLoop;
//									}
									
									// create new Operation object with same boolean field and priority level as symbol
//									Operation op = new Operation(null, (boolean) symbol._field, null, symbol._priority);
//									operations.set(i, op); // add it ASAP
//									
//									// Discount removed symbol
//									number_unprocessed_symbols.getAndDecrement();
//									
//									// It has been uniquely removed so it can be pushed
//									stack.push(op);
//									
//									synchronized (expressions) {
//										if (_symbol == expressions[i]) { // Check if some other thread go to it
//											expressions[i] = null; // if not nullify
//										} else { // if so traverse again
//											continue opLoop;
//										}
//									}
									
//									synchronized (operations) { 
//										operations[i] = op;
//									}
										
									// Only level 1 operations (there should only be one) are 
									// not the operand of any other operation
//									if (op._priority > 1) {
//										Operation curr;
//										// Iterate operations
//										for (int k = 0; k < operations.length(); k++) {
//											curr = operations.get(k);
//											if (curr == null) {
//												continue; 
//											} else if (curr._priority == op._priority - 1) { 
//												if (k < i) { // operation is right of parent operation
//													if (curr._rightOperand != null) {
//														continue;
//													} else {
//														curr._rightOperand = new Operand(op, op._priority);
//													}
//												} 
//												if (i < k) { // operation is left of parent operation
//													if (curr._leftOperand != null) {
//														continue;
//													} else {
//														curr._leftOperand = new Operand(op, op._priority);
//													}
//												}
//												// Begin new traversal of expressions
//												continue opLoop;
//											}
//										}
										// The expressionBuilder function can check whether the input expression
										// uses the right characters in a valid order; it doesn't check if that 
										// expression is a tree.  Operation objects represent binary operations,
										// so if an expression is supplied that is not a tree, there will be some
										// Operation for which a parent cannot be found.  That check will have been
										// done if a complete traversal is performed and no Operation with null operands
										// on the appropriate side has been found.
//										throw new InvalidArithmeticExpressionException();
//									}
										
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
										
//								}
								
								// if full traversal of expressions completed without finding 
								// an unprocessed operation, move on to locating operands
//								if (i + 1 == expressions.length() && initialPriorityLevel == priorityLevel.get()) {
//									break opLoop;
//								}
//							}
//						}
					oprdLoop:
						while (true) { // This loop processes the operands of each priority level	
							int initialPriorityLevel = priorityLevel.get();
							for (int i = 0; i < expressions.length(); i++) {
								Symbol symbol = expressions.get(i);
								if (symbol != null) {
									if (symbol._field.getClass() == Integer.class && symbol._priority == priorityLevel.get()) {
										if (expressions.compareAndSet(i, symbol, null)) {
											Operand oprd = new Operand((Integer)symbol._field, symbol._priority);
											Operation minus = null, plus = null;
											boolean minusLevel = true, plusLevel = true;
											while (true) {
												if (i > 0 && minusLevel) {
													minus = operations.get(i-1);
												}
												if (i < operations.length() - 1 && plusLevel) {
													plus = operations.get(i+1);
												}
												if (minus != null) {
													if (minus._priority == oprd._priority) {
														minus._rightOperand = oprd;
													} else {
														minusLevel = false;
													}
												}
												if (plus != null) {
													if (plus._priority == oprd._priority) {
														minus._leftOperand = oprd;
													} else {
														plusLevel = false;
													}
												}
												continue oprdLoop;
											}
										}
									}
								} else {
									if (initialPriorityLevel != priorityLevel.get()) {
										break oprdLoop;
									} else if (i + 1 == expressions.length()) {
										priorityLevel.getAndIncrement();
										break oprdLoop;
									}
								} 
							}
						}
						
								
//								Symbol _symbol = (Symbol) expressions[i];
//							
//								if (_symbol._field.getClass() == Integer.class && _symbol._priority == priorityLevel) {
//											
									
										
//									synchronized (expressions) {
//										if (_symbol == expressions[i]) {
//											expressions[i] = null;
//										} else {
//											continue oprdLoop;
//										}
//									}
										
//									synchronized (operations) {
//										if (i > 0) {
//											if (((Operation)operations[i-1])._priority == oprd._priority) {
//												((Operation)operations[i-1])._rightOperand = oprd;
//											} 
//											continue oprdLoop;
//										}
//										if (i < expressions.length() - 1) {
//											if (((Operation)operations[i+1])._priority == oprd._priority) {
//												((Operation)operations[i+1])._leftOperand = oprd;
//											}
//											continue oprdLoop;
//										}
//									}
										
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
								
								// if a full traversal was completed, then either this thread 
								// was first to discover that all the operands on this level 
								// have been exhausted and the very first loop should be restarted,
								// or the priority level was changed by another thread mid-traversal,
								// and the i
								// If priorityLevel is raised mid traversal relative to some thread,
								// the myPriority level and priorityLevel will not be equal
								// traversal of operations could find no results not because there
								// are none, but because priorityLevel was changed midway through 
								// and not all members of operations were checked at the new priority
								// level.  
								// if passed through all Symbol<Boolean>'s and now all Symbol<Integer>'s 
								// at this priority level, raise priority level and restart pushing loop
//								synchronized (priorityLevel) {
//									if (initialPriorityLevel != priorityLevel) {
//										break oprdLoop;
//									} else if (i + 1 == expressions.length()) {
//										priorityLevel++;
//										break oprdLoop;
//									}
//								}
//							}
//						}
						
//					}
			
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
	
	// This method just conveniently returns the expression in an array
	@SuppressWarnings("rawtypes")
	private static AtomicReferenceArray<Symbol> expressionBuilder(Iterator it, ArrayList<Symbol> list, int priority) throws InvalidArithmeticExpressionException {
		expressionBuilderHelper(it, list, priority);
		return new AtomicReferenceArray<Symbol>((Symbol[])list.toArray());
		
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
					expressionBuilderHelper(it, list, priority + 1);
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
