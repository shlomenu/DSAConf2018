package concurrentStackQueue;

import java.util.concurrent.atomic.AtomicReference;

public class EliminationBackoffStack<T> {
	
	AtomicReference<Node> top = new AtomicReference<Node>(null);
	static final int capacity = 100;
	EliminationArray<T> eliminationArray = new EliminationArray<T>(capacity);
	
	
	
}
