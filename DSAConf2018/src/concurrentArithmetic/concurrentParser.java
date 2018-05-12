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
	
	public static final int NUMBER_OF_THREADS = 1;

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException {
		// Fetching string from given file
		Scanner scanner = new Scanner(System.in);
		System.out.println("Working directory: " + System.getProperty("user.dir"));
//		System.out.print("File to parse: ");
		String filename = "reallyBigArithmetic.txt"; //scanner.next();
		String source = new String(Files.readAllBytes(Paths.get(filename)));
		
		// Build atomic Symbol array and Operation array, some bookkeeping integers, and initialize stack 
		AtomicReferenceArray<Symbol> expressions = expressionBuilder(new Iterator(source), new ArrayList<Symbol>(), 1);
		AtomicReferenceArray<Operation> operations = new AtomicReferenceArray<Operation>(expressions.length());
		EliminationBackoffStack<Operation> stack = new EliminationBackoffStack<Operation>(NUMBER_OF_THREADS);
		AtomicInteger priorityLevel = new AtomicInteger(1);
		AtomicInteger number_unprocessed_symbols = new AtomicInteger(expressions.length());
		
//		System.out.print("[");
//		for (int j = 0; j < expressions.length(); j++) {
//			System.out.println("{ " + expressions.get(j)._field + ", " + expressions.get(j)._priority + ", " + expressions.get(j)._token + ", " + expressions.get(j).hashCode() + "}");
//		}
//		System.out.println("]");
		
		Thread[] threads = new Thread[NUMBER_OF_THREADS];
		for (int j = 0; j < threads.length; j++) {
			threads[j] = new Thread(new Runnable() { 
				
				public void run() {
					while (number_unprocessed_symbols.get() > 0) { 
					opLoop: 
						while (true) { // This loop prepares the operations of each priority level
							int initialPriorityLevel = priorityLevel.get();
							for (int i = 0; i < expressions.length(); i++) {
								Symbol symbol = expressions.get(i);
								if (symbol != null) {
									if (symbol._field.getClass() == Boolean.class && symbol._priority == initialPriorityLevel) {
										if (expressions.compareAndSet(i, symbol, null)) {
											Operation op = new Operation(null, (boolean) symbol._field, null, symbol._priority, symbol._parentToken, symbol._childToken);
											System.out.println("Here is my new Operation: " + op._operator + " priority " + op._priority); 
											operations.set(i, op);
											System.out.println("operations: " + operations.get(i)._operator + " " + operations.get(i)._priority);
											number_unprocessed_symbols.getAndDecrement();
											System.out.println("I have " + number_unprocessed_symbols.get() + " unprocessed symbols");
											stack.push(op);
											if (op._priority > 1) {
												Operation curr;
												while (true) {
													for (int t = 0; t < operations.length(); t++) {
														if (operations.get(t) != null) {
															System.out.print(operations.get(t)._operator + " ");
														} else {
															System.out.print(operations.get(t) + " ");
														}
													}
													System.out.println();
													System.out.println("My priority is " + op._priority);
													for (int k = 0; k < operations.length(); k++) {
														System.out.println("Operand loop index: " + k);
														curr = operations.get(k);
														if (curr != null) {
															System.out.println("What is curr._priority? It is " + curr._priority + " and what is op_.priority " + op._priority);
															System.out.println("What is curr._childToken? It is " + curr._childToken + ". And what is op._parentToken? " + op._parentToken);
														}
															
														if (curr == null) {
															System.out.println("Curr was null");
															continue; 
														} else if (curr._childToken == op._parentToken) { 
															System.out.println("Currently examining a priority " + curr._priority + " operation");
															if (k < i) { // operation is right of parent operation
																System.out.println("I was left of the operation");
																if (curr._rightOperand != null) {
																	System.out.println("Drats! left of the operation was null");
//																	System.out.println("It is " + curr._operator + " with operand " + curr._rightOperand.getField());
																	continue;
																} else {
																	curr._rightOperand = new Operand(op, op._priority);
																	System.out.println("I was left of the operation");

																}
															} 
															if (i < k) { // operation is left of parent operation
																if (curr._leftOperand != null) {
																	System.out.println("Drats! right of the operation was null");
//																	System.out.println("It is " + curr._operator + " with operand " + curr._rightOperand.getField());
																	continue;
																} else {
																	curr._leftOperand = new Operand(op, op._priority);
																	System.out.println("I was right of the operation");
																}
															}
															System.out.println("I didn't match either condition");
															// Begin new traversal of expressions
															continue opLoop;
														}
														
													}
												}
											}
										}
									}
								} System.out.println("I'm trapped in this horrible condition");
								if (initialPriorityLevel != priorityLevel.get()) {
									initialPriorityLevel = priorityLevel.get();
								} else if (i + 1 == expressions.length()) {
									break opLoop;
								} 
								
							}
						}
					oprdLoop:
						while (true) { // This loop processes the operands of each priority level	
//							System.out.println("I'm in the operand loop");
							int initialPriorityLevel = priorityLevel.get();
//							System.out.println("This is my priority level: " + initialPriorityLevel);
							for (int i = 0; i < expressions.length(); i++) {
								Symbol symbol = expressions.get(i);
								if (symbol != null) {
									if (symbol._field.getClass() == Integer.class && symbol._priority == initialPriorityLevel) {
										if (expressions.compareAndSet(i, symbol, null)) {
											number_unprocessed_symbols.getAndDecrement();
											Operand oprd = new Operand((Integer)symbol._field, symbol._priority);
											Operation minus = null, plus = null;
											boolean minusLevel = true, plusLevel = true;
											while (true) {
												System.out.println("I'm stuck in the operand assignment loop");
												if (i > 0 && minusLevel) {
													minus = operations.get(i-1);
												}
												if (i < operations.length() - 1 && plusLevel) {
													plus = operations.get(i+1);
												}
												if (minus != null) {
													if (minus._priority == oprd._priority) {
														minus._rightOperand = oprd;
														break;
													} else {
														minusLevel = false;
													}
												}
												if (plus != null) {
													if (plus._priority == oprd._priority) {
														plus._leftOperand = oprd;
														break;
													} else {
														plusLevel = false;
													}
												}
											}
											continue oprdLoop;
										}
									}
								} 
								if (initialPriorityLevel != priorityLevel.get()) {
									break oprdLoop;
								} else if (i + 1 == expressions.length()) {
									priorityLevel.getAndIncrement();
									break oprdLoop;
								} 
							}
						}
					}
					System.out.println("I reached the popping loop");
					// This is the popping loop
					Operation mine;
					while (true) { 
						try {
							System.out.println("I popped");
							mine = stack.pop();
							System.out.println("The priority of my popped operation is " + mine._priority);
							if (mine._leftOperand != null) {
								System.out.println("My left operand is " + mine._leftOperand.getField());
							}
							if (mine._rightOperand != null) { 
								System.out.println("My right operand is " + mine._rightOperand.getField());
							}
							int count = 0;
							while (mine._leftOperand == null || mine._rightOperand == null) {
								count++;
//								if (count > 1000) { return; }
//								System.out.println("I'm spinning 1");/*spin*/
								}
							while (mine._leftOperand.getField() == null || mine._rightOperand.getField() == null) {
								count++;
//								if (count > 10000) {return; }
//								System.out.println("I'm spinning 2");/*spin*/
								}
							// Because each operation's field must be calculated by a thread after it is popped,
							// the recursion that would, ideally, be perfectly preserved by the stack, is necessary
							if (mine._operator) { // if multiplication operation
								mine._field = mine._leftOperand.getField() * mine._rightOperand.getField();
							} else { // if addition operation
								mine._field = mine._leftOperand.getField() + mine._rightOperand.getField();
							}
							System.out.println("My result is " + mine._field);
							if (mine._priority == 1) {
								System.out.println("The final result is : " + mine._field);
								return;
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
			thread.start();
			
		}
		scanner.close();
		
		
		
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
	
	// This method just conveniently returns the expression in an AtomicReferenceArray
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static AtomicReferenceArray<Symbol> expressionBuilder(Iterator it, ArrayList<Symbol> list, int priority) throws InvalidArithmeticExpressionException {
		expressionBuilderHelper(it, list, priority, (new Object()).hashCode());
		return new AtomicReferenceArray(list.toArray());
		
	}
	
	// This method takes an iterator over an infixed, fully parenthesized (meaning in readme) 
	// arithmetic expression and returns a list with symbol objects
	@SuppressWarnings("rawtypes")
	private static void expressionBuilderHelper(Iterator it, ArrayList<Symbol> list, int priority, int token) throws InvalidArithmeticExpressionException {
		Symbol<Boolean> symbol = new Symbol<Boolean>(null, priority, token, 0);
		int myToken = symbol.hashCode();
		symbol._childToken = myToken;
		System.out.println("My priority " + priority);
		System.out.println("I received the hashCode " + token);
		System.out.println("I gave my children the hashCode " + myToken);
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
					expressionBuilderHelper(it, list, priority + 1, myToken);
					if (it.hasNext()) { // if priority == 1 there may be nothing next
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
					list.add(new Symbol<Integer>(Integer.valueOf(s), priority, token, myToken)); 
					
				} else if (next == '+') { // addition represented as false boolean Symbol object
					symbol._field = false;
					list.add(symbol);
					next = it.next();
				} else if (next == '*') { // multiplication represented as true boolean Symbol object
					symbol._field = true;
					list.add(symbol);
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
