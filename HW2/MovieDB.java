import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.NoSuchElementException;

/**
 * Genre, Title 을 관리하는 영화 데이터베이스.
 * 
 * MyLinkedList 를 사용해 각각 Genre와 Title에 따라 내부적으로 정렬된 상태를  
 * 유지하는 데이터베이스이다. 
 */
public class MovieDB {
    private MyLinkedList<TitleList> allGenres = new MyLinkedList<>();

    public MovieDB() {}

    public void insert(MovieDBItem item) {
        for (TitleList titleList: allGenres) {
            if (item.getGenre().equals(titleList.first().getGenre())) {
                titleList.insert(item);
                return;
            }
        }
        allGenres.insert(new TitleList(item));
    }

    public void delete(MovieDBItem item) {
        Iterator<TitleList> genreIterator = allGenres.iterator();

        while (genreIterator.hasNext()) {
           TitleList titleList = genreIterator.next();
           if (item.getGenre().equals(titleList.first().getGenre())) titleList.remove(item);
           if (titleList.isEmpty()) genreIterator.remove();
        }
    }

    public TitleList search(String term) throws NoSuchElementException {
        TitleList result = new TitleList();
        TitleList found = new TitleList();

        for (TitleList titleList: allGenres) {
            try {
                found = titleList.find(term);
            } catch (NoSuchElementException e) {
                continue;
            }

            for (MovieDBItem item: found) {
                result.add(item);                
            }
        }

        return result;
    }
    
    public TitleList items() throws NullPointerException {
        TitleList result = new TitleList();

        for (TitleList titleList: allGenres) {
            for (MovieDBItem item: titleList) {
                result.add(item);
            }
        }

        return result;
    }
}

class TitleList extends MyLinkedList<MovieDBItem> implements ListInterface<MovieDBItem>, Comparable<TitleList> {
    public TitleList() {
        super();
    }

    public TitleList(MovieDBItem firstItem) {
        super(firstItem);
    }

    public TitleList find(String target) throws NoSuchElementException {
        TitleList result = new TitleList();
        Pattern p = Pattern.compile(target, Pattern.LITERAL);

        for (MovieDBItem item: this) {
            if (p.matcher(item.getTitle()).find()) {
                result.add(item);
            }
        }

        if (result.isEmpty()) throw new NoSuchElementException();
        return result;
    }

    @Override
    public int compareTo(TitleList other) throws IllegalStateException {
        int genreCompare = this.first().getGenre().compareTo(other.first().getGenre());
        
        if (genreCompare != 0) return genreCompare;
        else throw new IllegalStateException("No repeating genre is accepted");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        
        TitleList other = (TitleList) obj;
        if (!this.first().equals(other.first()) || !this.last().equals(other.last()) || this.size() != other.size()) return false;

        Iterator<MovieDBItem> otherIterator = other.iterator();
        for (MovieDBItem myItem: this) {
            if (!myItem.equals(otherIterator.next())) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 1;
        result = prime * result + ((this.first() == null) ? 0 : this.first().hashCode());
        result = prime * result + ((this.last() == null) ? 0 : this.last().hashCode());
        result = prime * result + this.size();
        return result;
    }
}