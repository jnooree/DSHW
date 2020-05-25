import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.UnsupportedEncodingException;

public class HashTable<T extends Comparable<T>> implements HashTableInterface<T> {
	private static final int TABLE_SIZE = 100;
	private ArrayList<AVLTreeInterface<T>> table;

	public HashTable() {
		table = new ArrayList<>(TABLE_SIZE);

		for (int i = 0; i < TABLE_SIZE; i++)
			table.add(new AVLTree<T>());
	}

	@Override
	public void clear() {
		for (AVLTreeInterface<T> hashItem: table) {
			hashItem.clear();
		}
	}

	@Override
	public void insert(T item) throws UnsupportedEncodingException {
		table.get(hash(item)).insert(item);
	}

	@Override
	public MyList<T> search(T key) throws UnsupportedEncodingException {
		return table.get(hash(key)).search(key);
	}

	@Override
	public List<T> getItems(int hash) {
		return table.get(hash).getAll();
	}

	public static final <T> int hash(T key) throws UnsupportedEncodingException {
		int hashCode = 0;
		for (byte c: key.toString().getBytes("US-ASCII")) {
			hashCode += (int) c;
		}
		return hashCode % TABLE_SIZE;
	}
}