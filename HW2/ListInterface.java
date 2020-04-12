public interface ListInterface<T> extends Iterable<T> {
	public boolean isEmpty();

	public boolean has(T item);

	public T getID();

	public int size();

	public T first();

	public void add(T item);

	public void insert(T item, int pos);

	public void remove(int pos);

	public void removeAll();
}