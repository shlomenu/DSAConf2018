package concurrentStackQueue;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EliminationArray<T> {
	
	private static final int quantum = 1;
	LocklessExchanger<T>[] exchangeArray;
	Random random;
	
	public EliminationArray(int capacity) {
		exchangeArray = (LocklessExchanger<T>[]) new Object[capacity];
		for (int i = 0; i < capacity; i++) {
			exchangeArray[i] = new LocklessExchanger<T>();
		}
		random = new Random();
	}
	
	public T visit(T value, int range) throws TimeoutException {
		int slot = random.nextInt(range);
		return exchangeArray[slot].exchange(value, quantum, TimeUnit.MILLISECONDS);
	}
	
}
