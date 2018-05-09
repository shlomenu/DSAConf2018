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
		
		public char next() {
			char result = _string.charAt(_index);
			_index++;
			return result;
		}
	}

	public static void main(String[] args) throws IOException, InvalidArithmeticExpressionException {
		@SuppressWarnings("rawtypes")
		ArrayList<Symbol> expression = new ArrayList<Symbol>();
//		Scanner scanner = new Scanner(System.in);
//		System.out.println("Working directory: " +
//				System.getProperty("user.dir"));
//		String filename = scanner.next();
//		scanner.close();
		String source = new String(Files.readAllBytes(Paths.get("arithmetic.txt")));
		Iterator it = new Iterator(source);
		expressionBuilder(it, expression, 0);
		
		ArrayList<Operation> operations = new ArrayList<Operation>();
		for (int l = 0; l < expression.size(); l++) {
			operations.add(null);
		}
		
		ReentrantLock expressionsLock = new ReentrantLock();
		ReentrantLock operationsLock = new ReentrantLock();
		Condition expressionsCondition = expressionsLock.newCondition();
		Condition operationsCondition = operationsLock.newCondition();
		
		Thread[] threads = new Thread[3];
		for (int j = 0; j < threads.length; j++) {
			threads[j] = new Thread(new Runnable() {
				
				volatile int priorityLevel = 1;
				EliminationBackoffStack<Operation> stack = new EliminationBackoffStack<Operation>(3);
				
				@SuppressWarnings({ "rawtypes", "null", "unchecked" })
				public void run() {
					while (!expression.isEmpty()) {
					opLoop: 
						while (!expression.isEmpty()) {
							
							Symbol<Boolean> boolSymbol = new Symbol<Boolean>(false, 0);
								
							for (int i = 0; i < expression.size(); i++) {
								int myPriorityLevel = priorityLevel;
								if (expression.get(i) == null) {
									if (i + 1 == expression.size()) {
										break opLoop;
									} else {
										continue;
									}
								} 
									
								Symbol _symbol = (Symbol) expression.get(i);
									
								if (_symbol.getClass() == boolSymbol.getClass()) {
									if (_symbol._priority == myPriorityLevel) {
												
										Operation op = new Operation(null, (boolean)_symbol._field, null, _symbol._priority);
											
										while (!expressionsLock.tryLock()) {
											try {
												expressionsCondition.wait(3);
											} catch (InterruptedException ex) {
												return;
											}
										}
											
										if (_symbol == expression.get(i)) {
											expression.set(i, null);
											expressionsLock.unlock();
											expressionsCondition.signalAll();													
											while (!operationsLock.tryLock()) {
												try {
													operationsCondition.wait(3);
												} catch (InterruptedException ex) {
													expression.set(i, _symbol);
													priorityLevel = _symbol._priority;
													return;
												}
											}
											operations.set(i, op);
											if (myPriorityLevel > 1) {
												Operation curr;
												for (int k = 0; k < operations.size(); k++) {
													curr = operations.get(k);
													if (curr == null) {
														continue;
													}
													if (curr._priority == op._priority - 1) {
														if (k < i) {
															curr._operand2 = new Operand(op, op._priority);
														} 
														if (i < k) {
															curr._operand1 = new Operand(op, op._priority);
														}
													}
												}
											}
											operationsLock.unlock();
											operationsCondition.signalAll();
											stack.push(op);
										} else {
											continue opLoop;
										}
										
									}
								}
									
								if (i + 1 == expression.size()) {
									break opLoop;
								}
							}
						}
					intLoop:
						while (!expression.isEmpty()) {	
								
							Symbol<Integer> intSymbol = new Symbol<Integer>(0, 0);
									
							for (int i = 0; i < expression.size(); i++) {
								int myPriorityLevel = priorityLevel;
								if (expression.get(i) == null) {
									if (i + 1 == expression.size()) {
										break intLoop;
									} else {
										continue;
									}
								} 
								
								Symbol _symbol = (Symbol) expression.get(i);
							
								if (_symbol.getClass() == intSymbol.getClass()) {
									if (_symbol._priority == myPriorityLevel) {
											
										Operand oprd = new Operand(((Symbol<Integer>)_symbol)._field, _symbol._priority);
											
										while (!expressionsLock.tryLock()) {
											try {
												expressionsCondition.wait(3);
											} catch (InterruptedException ex) {
												return;
											}
										}
										
										if (_symbol == expression.get(i)) {
											expression.set(i, null);
											expressionsLock.unlock();
											expressionsCondition.signalAll();
											while (!operationsLock.tryLock()) {
												try {
													operationsCondition.wait(3);
												} catch (InterruptedException ex) {
													expression.set(i, _symbol);
													priorityLevel = _symbol._priority;
													return;
												}
											}
											if (operations.get(i-1)._priority == oprd._priority) {
												operations.get(i-1)._operand2 = oprd;
											} 
											if (operations.get(i+1)._priority == oprd._priority) {
												operations.get(i+1)._operand1 = oprd;
											}
											operationsLock.unlock();
											operationsCondition.signalAll();
										} else {
											continue intLoop;
										}
										
									}
								}
								
								if (i+1 == expression.size()) {
									priorityLevel++;
									break intLoop;
								}
							}
						}
						
					}
			
					Operation me;
					while (true) {
						try {
							me = stack.pop();
							while (me._operand1.getField() == null || me._operand2.getField() == null) { }
							if (me._operator == true) {
								me._field = me._operand1.getField() * me._operand2.getField();
							} else {
								me._field = me._operand1.getField() + me._operand2.getField();
							}
						} catch (EmptyStackException ex) {
							return;
						}
					}
			
				}
			});
		}
		
		for (Thread thread : threads) {
			thread.run();
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	private static void expressionBuilder(Iterator it, ArrayList<Symbol> list, int priority) throws InvalidArithmeticExpressionException {
		if (it.hasNext()) {
			char next = it.next();
			outer: while (it.hasNext()) {
				if (next == ' ') {
					while (next == ' ') {
						if (it.hasNext()) {
							next = it.next();
						} else {
							break outer;
						}
					}
				} else if (next == '(') {
					expressionBuilder(it, list, priority + 1);
					if (priority > 0) {
						next = it.next();
					}
				} else if (Character.isDigit(next)) {
					String s = "" + next;
					while (it.hasNext()) {
						char _next = it.next();
						if (!Character.isDigit(_next)) {
							next = _next;
							break;
						}
						s += _next;
					}
					list.add(new Symbol<Integer>(Integer.valueOf(s), priority));
					
				} else if (next == '+') {
					list.add(new Symbol<Boolean>(false, priority));
					next = it.next();
				} else if (next == '*') {
					list.add(new Symbol<Boolean>(true, priority));
					next = it.next();
				} else if (next == ')') {
					return;
				} else {
					throw new InvalidArithmeticExpressionException();
				}
			}
		}
	}
	
}
