import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<T extends Comparable<T>> implements ListInterface<T> {
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
    public MyLinkedList() {
    	head = new Node<>();
    	numItems = 0;
    }

    public MyLinkedList(T firstItem) {
    	this();
    	this.add(firstItem);
    }

    public final Iterator<T> iterator() {
    	return new MyLinkedListIterator<T>(this);
    }

	@Override
	public boolean isEmpty() {
		return numItems == 0;
	}

	@Override
	public boolean has(T target) {
		for (T item: this) {
			if (item.equals(target)) return true;
		}
		return false;
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
	public T last() {
		return head.getPrev().getItem();
	}

	@Override
	public void add(T item) {
		head.getPrev().insertNext(item);
		++numItems;
	}

	@Override
	public void insert(T ins) {
		if (this.isEmpty()) {
			this.add(ins);
			return;
		}

		Node<T> curr = this.head.getNext();

		if (this.last().compareTo(ins) < 0) {
			this.add(ins);
			return;
		}

		while (curr != this.head) {
			int compare = curr.getItem().compareTo(ins);
			
			if (compare > 0) {
				curr.insertPrev(ins);
				++numItems;
				return;
			}
			else if (compare == 0) return;
			else curr = curr.getNext();
		}
	}

	@Override
	public void remove(T del) {
		Node<T> curr = this.head.getNext();

		while (curr != this.head) {
			if (curr.getItem().equals(del)) {
				curr.getPrev().removeNext();
				--numItems;
				return;
			}
			
			curr = curr.getNext();
		}
	}

	@Override
	public void removeAll() {
		head.setNext(head);
		head.setPrev(head);
		numItems = 0;
	}
}

class MyLinkedListIterator<T extends Comparable<T>> implements Iterator<T> {
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
		prev.removeNext();
		list.numItems -= 1;
		curr = prev;
		prev = null;
	}
}