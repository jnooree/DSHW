import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedList<T> implements ListInterface<T> {
	ListNode<T> head; // dummy head

    public LinkedList() {
    	head = new ListNode<>();
    }

    public LinkedList(T firstItem) {
    	this();
    	add(firstItem);
    }

    public final Iterator<T> iterator() {
    	return new LinkedListIterator<T>(this);
    }

	@Override
	public void add(T item) {
		ListNode<T> newItem = new ListNode<>(item);
		newItem.setNext(head);
		newItem.setPrev(head.getPrev());

		head.getPrev().setNext(newItem);
		head.setPrev(newItem);
	}

	@Override
	public T first() {
		return head.getNext().getItem();
	}

	@Override
	public T last() {
		return head.getPrev().getItem();
	}

	@Override
	public boolean isEmpty() {
		return head.getNext() == head;
	}

	@Override
	public String toString() {
		String result = "[";
		for (T item: this) {
			result += item.toString() + ", ";
		}
		return result.substring(0, result.length() - 2) + "]";
	}
}

class LinkedListIterator<T> implements Iterator<T> {
	private LinkedList<T> list;
	private ListNode<T> curr;
	private ListNode<T> prev;

	public LinkedListIterator(LinkedList<T> list) {
		this.list = list;
		this.curr = list.head;
		this.prev = null;
	}

	@Override
	public boolean hasNext() {
		return curr.getNext() != list.head;
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException();

		prev = curr;
		curr = curr.getNext();

		return curr.getItem();
	}
}