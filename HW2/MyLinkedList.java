import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<T> implements ListInterface<T> {
	// dummy head
	Node<T> head;
	int numItems;
    /**
     * {@code Iterable<T>}를 구현하여 iterator() 메소드를 제공하는 클래스의 인스턴스는
     * 다음과 같은 자바 for-each 문법의 혜택을 볼 수 있다.
     * 
     * <pre>
     *  for (T item: iterable) {
     *  	item.someMethod();
     *  }
     * </pre>
     * 
     * @see PrintCmd#apply(MovieDB)
     * @see SearchCmd#apply(MovieDB)
     * @see java.lang.Iterable#iterator()
     */
    public final Iterator<T> iterator() {
    	return new MyLinkedListIterator<T>(this);
    }

	@Override
	public boolean isEmpty() {
		return numItems == 0;
	}

	@Override
	public boolean has(T target) {
		for (T node: this) {
			if (node.equals(target)) return true;
		}
		return false;
	}

	@Override
	public T getID() {
		return head.getItem();
	}

	@Override
	public int size() {
		return numItems;
	}

	@Override
	public T first() {
		return head.getNext().getItem();
	}

	@Override
	public void add(T item) {
		this.insert(item, numItems);
	}

	@Override
	public void insert(T item, int pos) throws IndexOutOfBoundsException {
		if (pos > numItems) throw new IndexOutOfBoundsException();
		
		Node<T> last = head;
		for(int i=0; i<pos; i++) {
			last = last.getNext();
		}
		last.insertNext(item);
		++numItems;
	}

	@Override
	public void remove(int pos) throws IndexOutOfBoundsException {
		if (pos > numItems) throw new IndexOutOfBoundsException();
		
		Node<T> last = head;
		for(int i=0; i<pos; i++) {
			last = last.getNext();
		}
		last.removeNext();
		--numItems;
	}

	@Override
	public void removeAll() {
		head.setNext(null);
	}

	public T find(String target) {
		throw new UnsupportedOperationException();
	}

	public void insertSorted(T ins) {
		throw new UnsupportedOperationException();
	}

	public void removeSorted(T del) {
		throw new UnsupportedOperationException();
	}
}

class MyLinkedListIterator<T> implements Iterator<T> {
	// FIXME implement this
	// Implement the iterator for MyLinkedList.
	// You have to maintain the current position of the iterator.
	private MyLinkedList<T> list;
	private Node<T> curr;
	private Node<T> prev;

	public MyLinkedListIterator(MyLinkedList<T> list) {
		this.list = list;
		this.curr = list.head;
		this.prev = null;
	}

	@Override
	public boolean hasNext() {
		return curr.getNext() != null;
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
		prev.removeNext();
		list.numItems -= 1;
		curr = prev;
		prev = null;
	}
}