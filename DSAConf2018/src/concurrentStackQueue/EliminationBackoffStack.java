package concurrentStackQueue;

import java.util.EmptyStackException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class EliminationBackoffStack<T> {
	
	private static int _waitingCapacity;
	AtomicReference<StackNode<T>> top;
	EliminationArray<T> eliminationArray;
	static ThreadLocal<RangePolicy> policy;
	
	public EliminationBackoffStack(int waitingCapacity) {
		top = new AtomicReference<StackNode<T>>(null);
		eliminationArray = new EliminationArray<T>(waitingCapacity);
		_waitingCapacity = waitingCapacity;
		policy = new ThreadLocal<RangePolicy>() {
			protected synchronized RangePolicy initialValue() {
				return new RangePolicy(_waitingCapacity);
			}
		};
	}
	
	public void push(T value) {
		RangePolicy rangePolicy = policy.get();
		StackNode<T> node = new StackNode<T>(value);
		while (true) {
			if (tryPush(node)) {
				return;
			} else try {
				T otherValue = eliminationArray.visit(value, rangePolicy.getRange());
				if (otherValue == null) {
					try {
						rangePolicy.recordEliminationSuccess();
					} catch (OversizedExchangerArray ex) {
						_waitingCapacity = (int) (1/2) * _waitingCapacity;
						continue;
					}
					return;
				}
			} catch (TimeoutException except) {
				try {
					rangePolicy.recordEliminationTimeout();
				} catch (OversizedExchangerArray ex) {
					_waitingCapacity = (int) (1/2) * _waitingCapacity;
				}
			}
		}
	}
	
	private boolean tryPush(StackNode<T> node) {
		StackNode<T> oldTop = top.get();
		node.next = oldTop;
		return(top.compareAndSet(oldTop, node));
	}
	
	public T pop() throws EmptyStackException {
		RangePolicy rangePolicy = policy.get();
		while (true) {
			StackNode<T> node = tryPop();
			if (node != null) {
				return node.value;
			} else try {
				T otherValue = eliminationArray.visit(null, rangePolicy.getRange());
				if (otherValue != null) {
					try {
						rangePolicy.recordEliminationSuccess();
					} catch (OversizedExchangerArray ex) { 
						_waitingCapacity = (int) (1/2) * _waitingCapacity;
						continue;
					}
					return otherValue;
				}
			} catch (TimeoutException except) {
				try {
					rangePolicy.recordEliminationTimeout();
				} catch (OversizedExchangerArray ex) {
					_waitingCapacity = (int) (1/2) * _waitingCapacity;
				}
			}
		}
	}
	
	private StackNode<T> tryPop() throws EmptyStackException {
		StackNode<T> oldTop = top.get();
		if (oldTop == null) {
			throw new EmptyStackException();
		}
		StackNode<T> newTop = oldTop.next;
		if (top.compareAndSet(oldTop, newTop)) {
			return oldTop;
		} else {
			return null;
		}
	}
	
}
