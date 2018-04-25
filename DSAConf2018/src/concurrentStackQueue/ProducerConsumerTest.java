package concurrentStackQueue;

public class ProducerConsumerTest {
	
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
		static LocklessQueue queue = new LocklessQueue<Integer>();
		public void run() {
			
		}
	}
	
	private class StackTest extends Thread {
		
		public void run() {
			
		}
	}
	
}
