import java.util.List;

public interface AVLTreeInterface<T extends Comparable<T>> {
	public void clear();

	public boolean isEmpty();

	public void insert(T item);

	public MyList<T> search(T key);

	public List<T> getAll();
}