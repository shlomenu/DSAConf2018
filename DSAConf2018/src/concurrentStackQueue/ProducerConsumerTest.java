package concurrentStackQueue;

public class ProducerConsumerTest {
	
	static LocklessQueue<Integer> queue = new LocklessQueue<Integer>();
	static EliminationBackoffStack<Integer> stack = new EliminationBackoffStack<Integer>(40);
	
	public void main(String[] args) {
		Thread[] threads = new Thread[5];
		
		for (int i = 0; i < threads.length; i++) {
			if (i % 2 == 1) {
				threads[i] = new QueueTest();
			} else {
				threads[i] = new StackTest();
			}
		}
		
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
	}
	
	private class QueueTest extends Thread {
		public void run() {
			
		}
	}
	
	private class StackTest extends Thread {
		public void run() {
			
		}
	}
	
}
