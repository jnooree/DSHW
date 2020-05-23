public interface ListNodeInterface<T> {
	public T getItem();
	
	public void setItem(T item);
	
	public void setPrev(ListNode<T> prev);

	public void setNext(ListNode<T> next);

	public ListNode<T> getPrev();

	public ListNode<T> getNext();

	public boolean isEmpty();
}