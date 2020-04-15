public interface ListInterface<T extends Comparable<T>> extends Iterable<T> {
	public boolean isEmpty();

	public int size();

	public T first();

	public T last();

	public void add(T item);

	public void insert(T item);

	public void remove(T item);
}