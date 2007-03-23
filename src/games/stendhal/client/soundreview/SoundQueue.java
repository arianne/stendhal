package games.stendhal.client.soundreview;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SoundQueue<T> {

	ConcurrentLinkedQueue<T> clq;

	public SoundQueue() {
		clq = new ConcurrentLinkedQueue<T>();
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return clq.isEmpty();
	}

	public boolean offer(T o) {
		// TODO Auto-generated method stub
		return clq.offer(o);
	}

	public T peek() {
		// TODO Auto-generated method stub
		return clq.peek();
	}

	public T poll() {
		// TODO Auto-generated method stub
		return clq.poll();
	}

	public void clear() {
		// TODO Auto-generated method stub
		clq.clear();
	}

}
