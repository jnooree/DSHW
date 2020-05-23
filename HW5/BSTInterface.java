import java.util.List;

public interface BSTInterface<T extends Comparable<T>> {
	public void clear();

	public boolean isEmpty();

	public AVLTreeNode<T> root();

	public void insert(T item);

	public ListInterface<T> search(T key);

	public List<T> getItems();
}