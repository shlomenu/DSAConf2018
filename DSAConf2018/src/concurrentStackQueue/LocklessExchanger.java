package concurrentStackQueue;

import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LocklessExchanger<T> {
	
	static final int EMPTY = 0;
	static final int WAITING = 1;
	static final int BUSY = 2;
	AtomicStampedReference<T> slot = new AtomicStampedReference<T>(null, 0);
	
	public T exchange(T myItem, long timeout, TimeUnit unit) throws TimeoutException {
		long nanosecs = unit.toNanos(timeout);
		long timeBound = System.nanoTime() + nanosecs;
		int[] stampHolder = {EMPTY};
		while (true) {
			if (System.nanoTime() > timeBound) {
				throw new TimeoutException();
			}
			T yourItem = slot.get(stampHolder);
			int stamp = stampHolder[0];
			switch(stamp) {
			case EMPTY:
				if (slot.compareAndSet(yourItem, myItem, EMPTY, WAITING)) {
					while (System.nanoTime() < timeBound) {
						yourItem = slot.get(stampHolder);
						if (stampHolder[0] == BUSY) {
							slot.set(null, EMPTY);
							return yourItem;
						}
					}
					if (slot.compareAndSet(myItem,  null,  WAITING, EMPTY)) {
						throw new TimeoutException();
					} else {
						yourItem = slot.get(stampHolder);
						slot.set(null, EMPTY);
						return yourItem;
					}
				}
				break;
			case WAITING:
				if (slot.compareAndSet(yourItem, myItem, WAITING, BUSY)) {
					return yourItem;
				}
				break;
			default:
				break;
			}
		
		}
	}
}
