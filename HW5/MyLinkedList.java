import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<T> implements MyList<T> {
	ListNode<T> head; // dummy head

	public MyLinkedList() {
		head = new ListNode<>();
	}

	public MyLinkedList(T firstItem) {
		this();
		add(firstItem);
	}

	@Override
	public final Iterator<T> iterator() {
		return new MyLinkedListIterator<T>(this);
	}

	@Override
	public boolean isEmpty() {
		return head.getNext() == head;
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
	public T firstItem() {
		return head.getNext().getItem();
	}

	@Override
	public T lastItem() {
		return head.getPrev().getItem();
	}

	@Override
	public String toString() {
		if (this.isEmpty()) return "[]";

		String result = "[";
		for (T item: this) {
			result += item.toString() + ", ";
		}
		return result.substring(0, result.length() - 2) + "]";
	}
}

class MyLinkedListIterator<T> implements Iterator<T> {
	private MyLinkedList<T> list;
	private ListNode<T> curr;
	private ListNode<T> prev;

	public MyLinkedListIterator(MyLinkedList<T> list) {
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

	@Override
	public void remove() {
		if (prev == null)
			throw new IllegalStateException("next() should be called first");
		if (curr == null)
			throw new NoSuchElementException();
		curr.remove();
		curr = prev;
		prev = null;
	}
}