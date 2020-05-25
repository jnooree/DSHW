 import java.util.List;
 import java.io.UnsupportedEncodingException;

public interface HashTableInterface<T> {
	public void clear();

	public void insert(T item) throws UnsupportedEncodingException;

	public MyList<T> search(T key) throws UnsupportedEncodingException;

	public List<T> getItems(int hash);
}