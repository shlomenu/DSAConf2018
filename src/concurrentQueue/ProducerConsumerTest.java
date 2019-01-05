package concurrentQueue;

import java.util.concurrent.atomic.AtomicInteger;

// This class is meant to demonstrate why 
// LocklessQueue does not scale well

public class ProducerConsumerTest {
	
	static final int NUMBER_OF_THREADS = 5;
	static LocklessQueue<String> queue = new LocklessQueue<String>();
	
	public void main(String[] args) {
		Thread[] low_load_threads = new Thread[NUMBER_OF_THREADS];
		
		AtomicInteger threadID = new AtomicInteger(1);
		AtomicInteger fooNum = new AtomicInteger(1);
		LocklessQueue<String> queue = new LocklessQueue<String>();
		
		for (Thread thread : low_load_threads) {
			thread = new Thread(new Runnable() {
				public void run() {
					int myID = threadID.getAndIncrement();
					Object[] strings = new Object[2];
					for (int j = 0; j < strings.length; j++) {
						strings[j] = new String("ThreadID: " + myID + "foo " + fooNum.getAndIncrement());
					}
					long start;
					if (myID % 2 == 0) {
						start = System.nanoTime();
						for (Object string : strings) {
							queue.enqueue((String)string);
							System.out.println("ThreadID: " + myID + " enqueued" + (String)string);

						}
						for (int k = 0; k < strings.length; k++) {
							String dequeue = queue.dequeue();
							if (dequeue.equals((String)strings[k])) {
								System.out.println("ThreadID: " + myID + " dequeued" + (String)strings[k]);
							}
						}
						long diff = System.nanoTime() - start;
						System.out.println("ThreadID: " + myID + " spent " + diff + " nanoseconds dequeueing just two Strings");
					}
					if (myID % 2 == 1) {
						start = System.nanoTime();
						for (int k = 0; k < strings.length; k++) {
							String dequeue = queue.dequeue();
							if (dequeue.equals((String)strings[k])) {
								System.out.println("ThreadID: " + myID + " dequeued" + (String)strings[k]);
							}						}
						for (Object string : strings) {
							queue.enqueue((String)string);
							System.out.println("ThreadID: " + myID + " enqueued" + (String)string);
	
						}
						long diff = System.nanoTime();
						System.out.println("ThreadID: " + myID + " spent " + diff + " nanoseconds dequeueing just two Strings");
					}
				}
			});
		}
		
		System.out.println("Beginning low load test: ");
		for (int i = 0; i < low_load_threads.length; i++) {
			low_load_threads[i].start();
		}
		
		Thread[] high_load_threads = new Thread[15];
		
		threadID.set(1);
		fooNum.set(1);
		
		for (Thread thread : high_load_threads) {
			thread = new Thread(new Runnable() {
				public void run() {
					int myID = threadID.getAndIncrement();
					Object[] strings = new Object[10];
					for (int j = 0; j < strings.length; j++) {
						strings[j] = new String("ThreadID: " + myID + "foo " + fooNum.getAndIncrement());
					}
					Long start;
					if (myID % 2 == 0) {
						start = System.nanoTime();
						for (Object string : strings) {
							queue.enqueue((String)string);
							System.out.println("ThreadID: " + myID + " enqueued" + (String)string);

						}
						for (int k = 0; k < strings.length; k++) {
							String dequeue = queue.dequeue();
							if (dequeue.equals((String)strings[k])) {
								System.out.println("ThreadID: " + myID + " dequeued" + (String)strings[k]);
							}						}
						long diff = System.nanoTime() - start;
						System.out.println("ThreadID: " + myID + " spent " + diff + " nanoseconds dequeueing just two Strings");
					}
					if (myID % 2 == 1) {
						start = System.nanoTime();
						for (int k = 0; k < strings.length; k++) {
							String dequeue = queue.dequeue();
							if (dequeue.equals((String)strings[k])) {
								System.out.println("ThreadID: " + myID + " dequeued" + (String)strings[k]);
							}						}
						for (Object string : strings) {
							queue.enqueue((String)string);
							System.out.println("ThreadID: " + myID + " enqueued" + (String)string);
	
						}
						long diff = System.nanoTime() - start;
						System.out.println("ThreadID: " + myID + " spent " + diff + " nanoseconds dequeueing just two Strings");
					}
				}
			});
		}
		
		System.out.println("Beginning high load test: ");
		for (int i = 0; i < high_load_threads.length; i++) {
			high_load_threads[i].start();
		}
		
	}
}
