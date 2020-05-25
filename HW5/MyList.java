public interface MyList<T> extends Iterable<T> {
	public boolean isEmpty();

	public void add(T item);

	public T firstItem();

	public T lastItem();
}