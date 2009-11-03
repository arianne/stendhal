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
	private int counter = 0;

	private int[] ringBuffer;

	/**
	 * creates a new IntRingBuffer
	 *
	 * @param size size of the buffer.
	 */
	public IntRingBuffer(int size) {
		ringBuffer = new int[size];
	}

	/**
	 * Is the buffer empty?
	 *
	 * @return true, if the buffer is empty, false otherwise
	 */
	public boolean isEmpty() {
		return counter == 0;
	}

	/**
	 * Is the buffer full?
	 *
	 * @return true, if the buffer cannot accept more items, false otherwise
	 */
	public boolean isFull() {
		return (head == tail) && counter > 0;
	}

	/**
	 * adds an item to the buffer
	 *
	 * @param item item
	 * @return true, if the item was added succesfully; false, if the buffer is full 
	 */
	public boolean add(int item) {
		if (isFull()) {
			return false;
		}
		head = (head + 1) % ringBuffer.length;
		ringBuffer[head] = item;
		counter++;
		return true;
	}

	/**
	 * removes the oldest items from the buffer
	 *
	 * @return true, if the item was removed succesfully; false, if the buffer was empty 
	 */
	public boolean removeOldest() {
		if (isEmpty()) {
			return false;
		}
		tail = (tail + 1) % ringBuffer.length;
		counter--;
		return true;
	}
}
