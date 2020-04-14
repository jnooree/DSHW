import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Genre, Title 을 관리하는 영화 데이터베이스.
 * 
 * MyLinkedList 를 사용해 각각 Genre와 Title에 따라 내부적으로 정렬된 상태를  
 * 유지하는 데이터베이스이다. 
 */
public class MovieDB {
    private static MovieList allMovies = new MovieList();

    public MovieDB() {}

    public void insert(MovieDBItem item) {
        String genre = item.getGenre();
        
        try {
            allMovies.insertSorted(item);
        } catch (NoSuchElementException e) {
            allMovies.insertSorted(new TitleList(item));
        }
        System.err.printf("[trace] MovieDB: INSERT [%s] [%s]\n", item.getGenre(), item.getTitle());
    }

    public void delete(MovieDBItem item) {
        String genre = item.getGenre();

        try {
            TitleList list = allMovies.find(genre);
            list.removeSorted(item);
            if (list.size() == 0) allMovies.removeSorted(list);
        } catch (NoSuchElementException e) {}
        System.err.printf("[trace] MovieDB: DELETE [%s] [%s]\n", item.getGenre(), item.getTitle());
    }

    public MyLinkedList<MovieDBItem> search(String term) {
        // FIXME implement this
        // Search the given term from the MovieDB.
        // You should return a linked list of MovieDBItem.
        // The search command is handled at SearchCmd class.
    	
    	// Printing search results is the responsibility of SearchCmd class. 
    	// So you must not use System.out in this method to achieve specs of the assignment.
    	
        // This tracing functionality is provided for the sake of debugging.
        // This code should be removed before submitting your work.
    	System.err.printf("[trace] MovieDB: SEARCH [%s]\n", term);
    	
    	// FIXME remove this code and return an appropriate MyLinkedList<MovieDBItem> instance.
    	// This code is supplied for avoiding compilation error.   
        MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();

        return results;
    }
    
    public MyLinkedList<MovieDBItem> items() {
        // FIXME implement this
        // Search the given term from the MovieDatabase.
        // You should return a linked list of QueryResult.
        // The print command is handled at PrintCmd class.

    	// Printing movie items is the responsibility of PrintCmd class. 
    	// So you must not use System.out in this method to achieve specs of the assignment.

    	// Printing functionality is provided for the sake of debugging.
        // This code should be removed before submitting your work.
        System.err.printf("[trace] MovieDB: ITEMS\n");

    	// FIXME remove this code and return an appropriate MyLinkedList<MovieDBItem> instance.
    	// This code is supplied for avoiding compilation error.   
        MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();
        
    	return results;
    }
}

class TitleList extends MyLinkedList<MovieDBItem> implements ListInterface<MovieDBItem>, Comparable<MovieDBItem> {
    // dummy head
    public TitleList(MovieDBItem firstItem) {
        head = new Node<MovieDBItem>(firstItem);
        numItems = 1;
    }

    @Override
    public void insertItem(MovieDBItem newItem) {
        if (this.last().compareTo(newItem) < 0) this.add(newItem);

        int pos = 0;
        for (MovieDBItem item : this) {
            int compare = item.compareTo(newItem);
            if (compare > 0) break;
            else if (compare == 0) return;
            ++pos;
        }
        this.insert(newItem, pos);
    }

    @Override
    public void removeItem(MovieDBItem delItem) {
        int pos = 0;

        for (MovieDBItem item: this) {
            if(item.equals(delItem)) this.remove(pos);
            ++pos;
        }
    }
}

class MovieList extends MyLinkedList<TitleList> implements ListInterface<TitleList>  {
    public MovieList() {}

    @Override
    public TitleList find(String target) throws NoSuchElementException {
        for (TitleList genreList: this) {
            if(genreList.last().equals(target)) return genreList;
        }
        throw new NoSuchElementException();
    }

    public void insertItem(MovieDBItem newItem) throws IllegalArgumentException {
        for (TitleList list: this) {
        }
    }

    @Override
    public void insertItem(TitleList newList) throws IllegalArgumentException {
        int pos = 0;

        for (TitleList list: this) {
            int compare = list.last().compareTo(newList.last());
            if(compare > 0) break;
            else if(compare == 0) throw new IllegalArgumentException();
            ++pos;
        }
        
        this.insert(newList, pos);
    }

    @Override
    public void removeItem(TitleList delList) {
        int pos = 0;

        for (TitleList genreList: this) {
            if(genreList.getID().getGenre().equals(delList.getID().getGenre())) this.remove(pos);
            ++pos;
        }
    }
}


/*
class Item extends Node<String> implements Comparable<Item> {
	public Item(String name) {
		super(name);
	}

    public Item(String name, Item next) {
        super(name, next);
    }
	
	@Override
	public int compareTo(Item o) {
		return this.getItem().compareTo(o.getItem());
	}

	@Override
	public int hashCode() {
		final int prime = 17;

        String name = this.getItem();
        return 1 + prime * ((name == null) ? 0 : name.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Item other = (Item) obj;
        String name = this.getItem();
        String otherName = other.getItem();

        if (name == null) {
            if (otherName != null)
                return false;
        } else if (!name.equals(otherName))
            return false;
        return true;
	}
}
*/