public interface ListInterface<T> extends Iterable<T> {
	public void add(T item);

	public T first();

	public T last();

	public boolean isEmpty();
}