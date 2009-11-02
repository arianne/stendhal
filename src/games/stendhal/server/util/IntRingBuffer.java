package games.stendhal.server.util;

/**
 * a ring buffer for ints.
 * 
 * Note: This class is optimized for performance and memory usage.
 *       That's why it is used in stead of a linked list with throw-away
 *       Integer objects.
 *
 * @author hendrik
 */
public class IntRingBuffer {
	private int head = 0; 
	private int tail = 0; 

	private int[] ringBuffer = new int[10];

	/**
	 * adds an item to the buffer
	 *
	 * @param item item
	 * @return true, if the item was added succesfully; false, if the buffer is full 
	 */
	public boolean add(int item) {
		int next = (head + 1) % ringBuffer.length;
		if (next == tail) {
			return false;
		}
		ringBuffer[next] = item;
		head = next;
		return true;
	}

	/**
	 * removes the oldest items from the buffer
	 *
	 * @return true, if the item was removed succesfully; false, if the buffer was empty 
	 */
	public boolean removeOldest() {
		if (head == tail) {
			return false;
		}
		tail = (tail + 1) % ringBuffer.length;
		return true;
	}
}
