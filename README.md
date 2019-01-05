##### Eli Whitehouse
##### Data Structures & Algorithms
##### Spring 2018
##### Michael Siff

# Concurrent Data Structures: Stacks and Queues
							
1. 	Over the course of this semester, I carefully studied chapters 1-11 of Herlihy 
	and Shavit's The Art of Multiprocessor Programming.  In the course of that study I 
	learned about the challenges and limitations of mutual exclusions; the utility of 
	different definitions of the correctness of concurrent objects (e.g. sequential 
	consistency, quiescent consistency, serializability, and linearizability); the 
	relationship between the use of atomicity and the creation of consensus; lock 
	construction (including how to design a reentrant lock from any non-reentrant lock);
	monitors and conditions; course, fine, lazy, optimistic, and non-blocking forms of 
	synchronization; concurrent queues, and concurrent stacks.  
	
2.	The purpose of ProducerConsumerTest is to demonstrate an inherent scalability problem
	with a rather naive implmentation of a non-blocking queue.  The problem is that while 
	in theory every thread will eventually succeed in modifying the queue, the throughput 
	is ultimately quite bad.  This is because the attempts to modify the head and tail 
	pointers quickly become sequential bottlenecks in the face of contention.  concurrentStack
	attempts to fix this problem.  
	
3.	The idea behind the EliminationBackoffStack is that instead of simply spinning on the 
	sequential bottlenecks at the ends of the underlying linked list, one creates a place 
	where, when contention is high, many threads can meet to push and pop with each other
	simultaneously.  This mechanism works due to the basic observation that a push and a
	pop on a stack cancel, and their calles can be linearized (treated mathematically as 
	occurring atomically) at the moment the item is exchanged.  It is also possible to make 
	an elimination queue.  This implementation was not presented by Herlihy and Shavit.  Several
	references were provided, which I did track down and read.  I did not find the pseudocode
	sufficiently clarifying to attempt an execution of the algorithm for a queue.  The key 
	difference for FIFO ordering is that pushes and pops to a queue can only cancel if the 
	push has sufficiently "aged" relative to the pop.  A pop can only eliminate a push if the 
	push happened sufficiently prior to the pop to have, if it were allowed onto the much 
	contended queue, traveled all the way to the top priority spot via seniority.  
	
4.	While it is quite easy to imagine real concurrent uses for queues, it is much harder to 
	picture a genuine use case for a concurrent stack.  The issue is that a stack is the 
	opposite of fair; it buries the longer-waiting result in favor of a newer one.  Inspired
	(very loosely!!!) by Taura K., Matsuoka S., and Yonezawa A.'s paper, An Efficient 
	Implementation Scheme of Concurrent Object-Oriented Langugaes on Stock Multicomputers, I 
	realized that a concurrent stack would be useful for distributing tasks that themselves 
	have dependencies.  Thus, some can be evaluated, but others depend of the results of 
	other computations.  Essentially, if there is recursion among the tasks, a concurrent
	stack is useful because it generally prioritizes the operation that is most likely to
	be doable.  I thought that a simple version of such a recursive problem would be parsing
	nested additions and multiplications from infix notation.  This is accomplished in 
	several steps in concurrentArithmetic.concurrentParser.  First, a single-threaded 
	routine is called to actually read the string and transform it into an AtomicReferenceArray
	of Symbol objects.  These symbol objects are merely an internally manipulable version of 
	exactly the information contained in the string.  Each symbol object contains a generic 
	field.  This field is an integer when it is minted from a numeric character, and is a 
	boolean (true for multiplication and false for addition) when a + or * sign is encountered.
	Symbol objects contain a priority field (which is a synonym for the degree of nesting 
	of an expression.  Perhaps unintuitively, they also contain the default (unique) hashCode 
	of the Symbol object that "operates" on them.  This is not important for Symbol<Integer>'s;
	they are, by the definition of the infix notation of a binary operator, located immediately
	next to their operator.  This is necessary for transmitting results from more nested to 
	less nested computations consistently.  Because the Symbols are kept in an array, there 
	is no such nice decree about how to determine what really is the "parent" operation of 
	another Operation object.  Depending on how populated and lopsided the expression is, parent 
	and child Operation objects can be as close or far from each other as they want to be.  
	There is no hueristic method.  The Operation object contains a boolean for addition or 
	multiplication and two operands.  The Operand object provides a shared interface so that 
	both the value of an integer and the value of an intermediate computation can be queried 
	with one function, getField().  
	
	After the array of Symbol objects, called expressions, is built and an equally
	sized Operation array is allocated, each thread performs the following routine.  
	Beginning at priority level one, loop through the array of symbols to find an operation.  
	After successfully comparing and setting this value to null in expressions, set it in 
	operations, decrement the number of total symbols remaining to be processed, 
	and search for the object in the operations array with a matching _token field 
	(which are created from Object hashcodes, since they are required to be unique).  Once found,
	create an Operand wrapper around that Operation object and attach it on the appropriate 
	side of the binary operation. (This order does not matter mathematically, merely 
	procedurally).  If a thread reaches the end of this for loop with a consistent 
	priority level throughout (another thread did not change it), then the array does 
	not have anything of the current priority level in it, and the second half of the loop 
	is performed: finding the integer operands at this level.  Since integers are the "leaves"
	of the computation, they are always directly adjacent to their operations, and the same 
	hashcode checking is not necessary.  If the priority level was adjusted mid 
	traversal, then control flows back to finding operations at the new level.  Only if there 
	are no more unprocessed symbols, as determined by an AtomicInteger, will the outermost loop be 
	broken.  Each thread then pops Operations objects until the single priority 1 Operation 
	object is found, and its result printed.  
	
	smallArithmetic.txt contains a trivial expression.  arithmetic.txt is a moderately large 
	expression, and reallyBigArithmetic.txt is several times the size of arithmetic.txt.  
	lopsidedArithmetic.txt is like arithmetic.txt, but lopsided, as the name suggests.  
	
	For a reason I truly have not been able to understand, on very large (reallyBigArithmetic.txt)
	and occasionally on more lopsided (lopsidedArithmetic.txt) expressions, concurrentParser will 
	get into infinite loops.  It could also occasionally happens that everything gets popped off 
	the stack, and the call terminates, but no final result is printed.  This second problem 
	seems to mostly have gone away at some stage.  As for the infinite loops, I have done my 
	best to locate them, and as far as I can tell, they seem to occur when a thread in the 
	operations loop cannot find where to attach its operation and checks infinitely.  This could 
	happen when an expression is malformed.  concurrentParser is only guaranteed to terminate when
	the expression is a tree.  It cannot dynamically assess this because there is no way to tell 
	whether the necessary result will never arrive or is merely pending another thread's progress 
	elsewhere.  Either way, I have observed the same infinite loop with properly formed expressions.  
	
	It is clear that somewhere, there is a recursive connection that is never being made.  The only
	way this could possibly happen is if one thread raises the priority level so as to somehow 
	exclude a different thread from discovering an unprocessed symbol.  However, the exit conditions 
	of the loops at the heart of concurrent parser absolutely forbid this.  It is only possible to 
	raise the priority level after doing a full traversal on that priority level and finding no 
	operations and no operands to process.  I have tried many things to fix it: giving each thread 
	their own priority level (though it is quite wasteful); setting one initial priority level for 
	both opLoop and oprdLoop, so that it is even forbidden that priority level can be changed by 
	a different thread between the loops.  With any possible inconsistency, it is still true that 
	for one thread to have advanced priority level in what seems like a premature way, a proper 
	check eventually preceded it.  Even if many threads are spinning for a long time, it should 
	be impossible that the operation they are waiting on is not eventually put in place by the 
	thread that originally took it.  If you can see the problem, I would be very grateful.
	
	Nonetheless, on all but reallyBigArithmetic.txt, the program runs to completion with a correct 
	answer either almost all or all of the time.  If it seems not to be termination, it is because 
	it isn't.  For the sake of your computer's fan, do not be hesitant to terminate it yourself.  
	
		
