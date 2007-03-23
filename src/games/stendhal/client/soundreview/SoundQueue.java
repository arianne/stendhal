package games.stendhal.client.soundreview;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SoundQueue<T> {

	ConcurrentLinkedQueue<T> clq;

	public SoundQueue() {
		clq = new ConcurrentLinkedQueue<T>();
	}

	public boolean isEmpty() {
		return clq.isEmpty();
	}

	public boolean offer(T o) {
		return clq.offer(o);
	}

	public T peek() {
		return clq.peek();
	}

	public T poll() {
		return clq.poll();
	}

	public void clear() {
		clq.clear();
	}

}
