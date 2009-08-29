package games.stendhal.client.gui.bag;

public class CircledCollection<T> {
	private final Node EMPTY_NODE = new Node();
	private Node head = EMPTY_NODE;
	private int size;
	private Node tail = EMPTY_NODE;
	private Node current = head;

	private class Node {

		private T value;
		private Node next;

		private Node(final T object) {
			this();
			this.value = object;
		}

		private Node() {
			this.next = this;
		}

	}

	public void add(final T object) {
		add(new Node(object));
	}

	protected void add(final Node newNode) {
		switch (size) {
		case 0:
			head = newNode;
			tail = newNode;
			current = newNode;
			break;

		default:
			newNode.next = head;
			tail.next = newNode;
			tail = newNode;
			break;
		}
		size++;
	}

	public int size() {
		return size;
	}

	public T getCurrent() {
		return current.value;
	}

	public boolean moveNext() {
		current = current.next;
		return current != head;
	}

	@SuppressWarnings("unchecked")
	public CircledCollection<T>[] newArray(final int length) {
		return new CircledCollection[length];
	}

}
