// Barath Tirumala

import java.util.concurrent.atomic.*;
import java.util.Random;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.MILLIS;

class Node<T> {
	Node<T> next;
	T val;
	
	public Node(T _val, Node<T> next) {
		this.next = next;
		this.val = _val;
	}
}

public class StackWithSize<T> {
	AtomicReference<Node<T>> head;
    AtomicInteger numOps;
    AtomicInteger numSize;
	
	public StackWithSize() {
		head = new AtomicReference<Node<T>>();
        this.numOps = new AtomicInteger(0);
        this.numSize = new AtomicInteger(0);
	}

	public boolean push(T val) {
		boolean success = false;
		while (!success) {
			Node<T> curHead = head.get();
			Node<T> newHead = new Node<T>(val, curHead);
			success = head.compareAndSet(curHead, newHead);
        };
        this.numOps.incrementAndGet();
        this.numSize.incrementAndGet();
		return success;

	}
	
	public T pop() {
		Node<T> newHead = null;
		Node<T> curHead = null;
		boolean success = false;
		while (!success) {
			curHead = head.get();
			newHead = curHead.next;
            success = head.compareAndSet(curHead, newHead);
        }
        this.numOps.incrementAndGet();
        this.numSize.decrementAndGet();
		return curHead.val;
	}
	
	public int getNumOps(){
		return this.numOps.get();
	}

    public int getSize(){
		return this.numSize.get();
	}
}

class Main {
	public static void main(String[] args) throws InterruptedException {
		int numThreads = 4;
		StackWithSize<Integer> StackWithSize = new StackWithSize<>();
		Thread[] threads = new Thread[numThreads];

		for(int i = 0; i<50000; i++){
			StackWithSize.push(i);
		}
		System.out.println("Populated initial StackWithSize");
		System.out.println("Time Start");
		LocalTime startTime = LocalTime.now();
		for(int i = 0; i<numThreads; i++){
			threads[i] = new Thread(new runMain(StackWithSize, i));
			threads[i].start();
        }

		for(int i = 0; i<numThreads; i++){
            threads[i].join();
		}
		
		System.out.println("Time End");
		LocalTime endTime = LocalTime.now();

		String timeelapsed = String.valueOf(MILLIS.between(startTime,endTime));
		System.out.println("Time: " +timeelapsed + " ms");

        System.out.println("Number of Operations: " + StackWithSize.getNumOps());
        System.out.println("Size of Stack: " + StackWithSize.getSize());
	}
}

class runMain implements Runnable {
	StackWithSize<Integer> StackWithSize;
	Random rd;
	int ID;

	public runMain(StackWithSize StackWithSize, int ID){
		this.StackWithSize = StackWithSize;
		this.rd = new Random();
		this.ID = ID;
	}

	@Override
	public void run() {
		System.out.println("thread " + this.ID + " started");
		for(int i = 0; i<150000; i++){
            boolean check = rd.nextBoolean(); 
			if(check){
				StackWithSize.push(i);
			}
			else{
				StackWithSize.pop();
			}
        }
	}
}