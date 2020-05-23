public class ListNode<T> implements ListNodeInterface<T> {
	private T item;
	private ListNode<T> prev;
	private ListNode<T> next;

	public ListNode() {
		this.item = null;
		this.prev = this;
		this.next = this;
	}

	public ListNode(T obj) {
		this.item = obj;
		this.prev = this;
		this.next = this;
	}
	
	@Override
	public final T getItem() {
		return item;
	}
	
	@Override
	public final void setItem(T item) {
		this.item = item;
	}

	@Override
	public final void setPrev(ListNode<T> prev) {
		this.prev = prev;
	}

	@Override
	public final void setNext(ListNode<T> next) {
		this.next = next;
	}

	@Override
	public final ListNode<T> getPrev() {
		return this.prev;
	}

	@Override
	public final ListNode<T> getNext() {
		return this.next;
	}

	@Override
	public final boolean isEmpty() {
		return this.item == null;
	}
}