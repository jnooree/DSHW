import java.util.List;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;

public class MatchDB {
    private HashTable<MatchDBItem> substrings;

    public MatchDB() {
        substrings = new HashTable<>();
    }

    public void reset() {
        substrings.clear();
    }

    public void insert(MatchDBItem item) throws UnsupportedEncodingException {
        substrings.insert(item);
    }

    public List<MatchDBItem> print(int hash) {
        return substrings.getItems(hash);
    }

    public ListInterface<MatchDBItem> search(String key) throws UnsupportedEncodingException {
        ListInterface<MatchDBItem> result = substrings.search(new MatchDBItem(key));

        if (result == null) {
            return new LinkedList<>();
        } else {
            return result;
        }
    }
}