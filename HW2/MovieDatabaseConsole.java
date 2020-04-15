import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.NoSuchElementException;


public class MovieDatabaseConsole {
	/*
	 * @formatter:off
	 * 
	 * This project is contributed by the following people (in alphabetical order). 
	 * ipkn <ipknhama AT gmail DOT com>
	 * shurain <shurain AT gmail DOT com> 
	 * stania <stania.pe.kr AT gmail DOT com> 
	 * wookayin <wookayin AT gmail DOT com>
	 * 
	 * @formatter:on
	 */

	/**
	 * This method is the starting point of your program.
	 * 
	 * 이 메소드에서는 프로그램의 큰 흐름을 서술한다.
	 * 
	 * 여러차례 입력-처리를 수행하기 위한 반복문과 함께,
	 * 그 안에서 매 입력마다 행해져야 할 행위를 서술한다. 
	 * 
	 * 우리는 입력을 표준 입력(System.in) 으로부터 받을 것이고, 처리 결과는 표준 출력(System.out)
	 * 으로, 오류 메시지는 표준 에러(System.err)에 기록할 것이다.
	 * 
	 * 표준 입출력과 표준 에러를 묶어서 관례에 따라 Console 이라고 부르자. 
	 * 
	 * 그리고 명령의 종류와 명령의 인자(arguments)를 보관하고 데이터베이스를 조작하며, 그 결과를
	 * Console에 출력하는 역할을 담당하는 ConsoleCommand 클래스를 정의하자.
	 * 
	 * 이 메소드에서는 표준 입력으로부터 문자열을 읽어서 해석(parse)하여
	 * 입력에 맞는 ConsoleCommand 타입의 인스턴스를 생성한다.
	 * 
	 * ConsoleCommand가 자기 할 일을 수행하는 과정에서 예외상황이 발생할 경우, 각 예외 상황들을
	 * 처리하는 방법 또한 이 메소드에서 규정한다.
	 * 
	 * @param args
	 *            an array of arguments supplied from the command line
	 */
	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		MovieDB db = new MovieDB();

		String input = null;
		while (true) {
			try {
				// 표준 입력으로부터 한 줄을 입력받는다.
				input = br.readLine().trim();

				if (input.isEmpty())
					continue;

				if (input.toUpperCase().equals("QUIT"))
					break;

				// 입력을 해석한다.
				ConsoleCommand command = parse(input);
				command.apply(db);
			} catch (CommandParseException e) {
				System.err.printf("command parse failure: %s [cmd=%s, input=%s]\n", e.getMessage(), e.getCommand(), e.getInput());
				e.printStackTrace(System.err);
			} catch (CommandNotFoundException e) {
				System.err.printf("command not found: %s\n", e.getCommand());
				e.printStackTrace(System.err);
			} catch (Exception e) {
				System.err.printf("unexpected exception with input: [%s]\n", input);
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * {@code input}을 해석(parse)하여 ConsoleCommand 객체를 생성해 반환한다.
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private static ConsoleCommand parse(String input) throws Exception {
		// 우선 어떤 종류의 ConsoleCommand 를 생성할 것인지 결정한다.
		ConsoleCommand command = null;

		if (input.startsWith("INSERT")) {
			command = new InsertCmd();
		} else if (input.startsWith("DELETE")) {
			command = new DeleteCmd();
		} else if (input.startsWith("SEARCH")) {
			command = new SearchCmd();
		} else if (input.startsWith("PRINT")) {
			command = new PrintCmd();
		} else {
			throw new CommandNotFoundException(input);
		}

		/*
		 * ConsoleCommand의 종류가 결정되었으니 입력을 각 ConsoleCommand 의 방식에 맞춰
		 * 해석(parse)한다.
		 */
		// command variable should not be null here by throwing exception.
		command.parse(input);

		// command variable should always be valid here
		// because parse method above throws CommandParseException when arguments are invalid.
		return command;
	}

}

interface ConsoleCommand {
	/**
	 * input 을 해석하는 공통 인터페이스.
	 * @param input {@code String} 타입의 입력 문자열
	 * @throws CommandParseException 입력 규칙에 맞지 않는 입력이 들어올 경우 발생
	 */
	void parse(String input) throws CommandParseException;

	/**
	 * 명령을 MovieDB 에 적용하고 결과를 출력하는 인터페이스를 정의한다.
	 * @param db 조작할 DB 인스턴스
	 * @throws Exception 일반 오류
	 */
	void apply(MovieDB db) throws Exception;
}

/******************************************************************************
 * 명령들의 해석 규칙이 동일하므로, 코드 중복을 없애기 위한 추상 클래스.
 */
abstract class AbstractConsoleCommand implements ConsoleCommand {
	/**
	 * 공통 명령 해석 규칙을 담고 있다. {@code input} 을 분해하여 String[] 으로 만들고, 
	 * {@link AbstractConsoleCommand.parseArguments} 로 인자를 전달한다.
	 * 
	 * 만약 어떤 명령이 별도의 해석 규칙이 필요한 경우 이 메소드를 직접 오버라이드하면 된다. 
	 */
	@Override
	public void parse(String input) throws CommandParseException {
		if (input.isEmpty()) throw new CommandParseException("no input");

		String[] args = input.split(" *% *%? *");
		parseArguments(args);
	}

	/**
	 * {@link AbstractConsoleCommand.parse} 메소드에서 분해된 문자열 배열(String[]) 을 이용해 
	 * 인자를 해석하는 추상 메소드. 
	 * 
	 * 자식 클래스들은 parse 메소드가 아니라 이 메소드를 오버라이드하여
	 * 각 명령에 맞는 규칙으로 인자를 해석한다.
	 *   
	 * @param args 규칙에 맞게 분해된 명령 인자
	 * @throws CommandParseException args가 명령의 규약에 맞지 않을 경우
	 */
	protected abstract void parseArguments(String[] args) throws CommandParseException;
}

/******************************************************************************
 * 아래부터 각 명령어별로 과제 스펙에 맞는 구현을 한다.
 */

/******************************************************************************
 * DELETE %GENRE% %MOVIE% 
 */
class DeleteCmd extends AbstractConsoleCommand {
	private String genre;
	private String movie;

	@Override
	public void parseArguments(String[] args) throws CommandParseException {
		if (args.length != 3)
			throw new CommandParseException(
					"DELETE", Arrays.toString(args), "insufficient argument");
		this.genre = args[1];
		this.movie = args[2];
	}

	@Override
	public void apply(MovieDB db) throws Exception {
		db.delete(new MovieDBItem(genre, movie));
	}
}

/******************************************************************************
 * INSERT %GENRE% %MOVIE% 
 */
class InsertCmd extends AbstractConsoleCommand {
	private String genre;
	private String movie;

	@Override
	protected void parseArguments(String[] args) throws CommandParseException {
		if (args.length != 3)
			throw new CommandParseException(
					"INSERT", Arrays.toString(args), "insufficient argument");
		this.genre = args[1];
		this.movie = args[2];
	}

	@Override
	public void apply(MovieDB db) throws Exception {
		db.insert(new MovieDBItem(genre, movie));
	}
}

/******************************************************************************
 * PRINT 
 */
class PrintCmd extends AbstractConsoleCommand {
	@Override
	protected void parseArguments(String[] args) throws CommandParseException {
		if (args.length != 1)
			throw new CommandParseException(
					"PRINT", Arrays.toString(args), "unnecessary argument(s)");
	}

	@Override
	public void apply(MovieDB db) throws Exception {
		MyLinkedList<MovieDBItem> result = new MyLinkedList<>();

		try {
			result = db.items();
		}
		catch (NullPointerException e) {
			ConsoleWriter.println("EMPTY");
			return;
		}

		for (MovieDBItem item: result) {
			ConsoleWriter.writeln("(%s, %s)", item.getGenre(), item.getTitle());
		}
		ConsoleWriter.flush();
	}
}

/******************************************************************************
 * SEARCH %TERM% 
 */
class SearchCmd extends AbstractConsoleCommand {
	private String term;

	@Override
	protected void parseArguments(String[] args) throws CommandParseException {
		if (args.length != 2)
			throw new CommandParseException(
					"SEARCH", Arrays.toString(args), "insufficient argument");
		this.term = args[1];
	}

	@Override
	public void apply(MovieDB db) throws Exception {
		MyLinkedList<MovieDBItem> result = new MyLinkedList<>();

		try {
			result = db.search(term);
		} catch (NoSuchElementException e) {
			ConsoleWriter.println("EMPTY");
			return;
		}

		for (MovieDBItem item: result) {
			ConsoleWriter.writeln("(%s, %s)", item.getGenre(), item.getTitle());
		}
		ConsoleWriter.flush();
	}
}

class ConsoleWriter {
	private static BufferedWriter consoleWriter = new BufferedWriter(new OutputStreamWriter(System.out));

	public ConsoleWriter() {}

	public static void writef(String s, Object... arg) throws Exception {
		consoleWriter.write(String.format(s, arg));
	}

	public static void writeln(String s, Object... arg) throws Exception {
		ConsoleWriter.writef(s + "\n", arg);
	}

	public static void flush() throws Exception {
		consoleWriter.flush();
	}

	public static void println(String s, Object... arg) throws Exception {
		ConsoleWriter.writeln(s + "\n", arg);
		ConsoleWriter.flush();
	}
}


/**
 * Genre, Title 을 관리하는 영화 데이터베이스.
 * 
 * MyLinkedList 를 사용해 각각 Genre와 Title에 따라 내부적으로 정렬된 상태를  
 * 유지하는 데이터베이스이다. 
 */
class MovieDB {
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

        if (result.isEmpty()) throw new NoSuchElementException();
        return result;
    }
    
    public TitleList items() throws NullPointerException {
        TitleList result = new TitleList();

        for (TitleList titleList: allGenres) {
            for (MovieDBItem item: titleList) {
                result.add(item);
            }
        }

        if (result.isEmpty()) throw new NullPointerException();
        return result;
    }
}

/******************************************************************************
 * MovieDB의 인터페이스에서 공통으로 사용하는 클래스.
 */
class MovieDBItem implements Comparable<MovieDBItem> {
    private final String genre;
    private final String title;

    public MovieDBItem(String genre, String title) {
        if (genre == null) throw new NullPointerException("genre");
        if (title == null) throw new NullPointerException("title");

        this.genre = genre;
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int compareTo(MovieDBItem other) {
        int genreCompare = this.getGenre().compareTo(other.getGenre());
        
        if (genreCompare != 0) return genreCompare;
        else return this.getTitle().compareTo(other.getTitle());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        MovieDBItem other = (MovieDBItem) obj;
        if (genre == null) {
            if (other.genre != null)
                return false;
        } else if (!genre.equals(other.genre))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((genre == null) ? 0 : genre.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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


interface ListInterface<T extends Comparable<T>> extends Iterable<T> {
	public boolean isEmpty();

	public int size();

	public T first();

	public T last();

	public void add(T item);

	public void insert(T item);

	public void remove(T item);
}

//Node for circular doubly linked list
interface NodeInterface<T> {
    public T getItem();
    
    public void setItem(T item);

    public void setPrev(Node<T> prev);
    
    public void setNext(Node<T> next);

    public Node<T> getPrev();

    public Node<T> getNext();
    
    public void insertNext(T obj);

    public void insertPrev(T obj);
    
    public void removeNext();
}


class MyLinkedList<T extends Comparable<T>> implements ListInterface<T> {
	// dummy head
	Node<T> head;
	int numItems;
    /**
     * {@code Iterable<T>}를 구현하여 iterator() 메소드를 제공하는 클래스의 인스턴스는
     * 다음과 같은 자바 for-each 문법의 혜택을 볼 수 있다.
     * 
     * <pre>
     *  for (T item: iterable) {
     *  	item.someMethod();
     *  }
     * </pre>
     * 
     * @see PrintCmd#apply(MovieDB)
     * @see SearchCmd#apply(MovieDB)
     * @see java.lang.Iterable#iterator()
     */
    public MyLinkedList() {
    	head = new Node<>();
    	numItems = 0;
    }

    public MyLinkedList(T firstItem) {
    	this();
    	this.add(firstItem);
    }

    public final Iterator<T> iterator() {
    	return new MyLinkedListIterator<T>(this);
    }

	@Override
	public boolean isEmpty() {
		return numItems == 0;
	}

	@Override
	public int size() {
		return numItems;
	}

	@Override
	public T first() {
		return head.getNext().getItem();
	}

	@Override
	public T last() {
		return head.getPrev().getItem();
	}

	@Override
	public void add(T item) {
		head.getPrev().insertNext(item);
		++numItems;
	}

	@Override
	public void insert(T ins) {
		if (this.isEmpty()) {
			this.add(ins);
			return;
		}

		if (this.last().compareTo(ins) < 0) {
			this.add(ins);
			return;
		}

		Node<T> curr = this.head.getNext();
		while (curr != this.head) {
			int compare = curr.getItem().compareTo(ins);
			
			if (compare > 0) {
				curr.insertPrev(ins);
				++numItems;
				return;
			} else if (compare == 0) {
				return;
			} else {
				curr = curr.getNext();
			}
		}
	}

	@Override
	public void remove(T del) {
		Node<T> curr = this.head.getNext();

		while (curr != this.head) {
			if (curr.getItem().equals(del)) {
				curr.getPrev().removeNext();
				--numItems;
				return;
			}
			
			curr = curr.getNext();
		}
	}
}

class MyLinkedListIterator<T extends Comparable<T>> implements Iterator<T> {
	// FIXME implement this
	// Implement the iterator for MyLinkedList.
	// You have to maintain the current position of the iterator.
	private MyLinkedList<T> list;
	private Node<T> curr;
	private Node<T> prev;

	public MyLinkedListIterator(MyLinkedList<T> list) {
		this.list = list;
		this.curr = list.head;
		this.prev = null;
	}

	@Override
	public boolean hasNext() {
		return curr.getNext() != list.head;
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException();

		prev = curr;
		curr = curr.getNext();

		return curr.getItem();
	}

	@Override
	public void remove() {
		if (prev == null)
			throw new IllegalStateException("next() should be called first");
		if (curr == null)
			throw new NoSuchElementException();
		prev.removeNext();
		list.numItems -= 1;
		curr = prev;
		prev = null;
	}
}


class Node<T> implements NodeInterface<T> {
    private T item;
    private Node<T> prev;
    private Node<T> next;

    public Node() {
        this.item = null;
        this.prev = this;
        this.next = this;
    }

    public Node(T obj, Node<T> prev, Node<T> next) {
        this.item = obj;
        this.prev = prev;
        this.next = next;
    }
    
    @Override
    public final T getItem() {
    	return item;
    }
    
    @Override
    public final void setItem(T item) {
    	this.item = item;
    }
    
    @Override
    public final void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    @Override
    public final void setNext(Node<T> next) {
    	this.next = next;
    }

    @Override
    public Node<T> getPrev() {
        return this.prev;
    }
    
    @Override
    public Node<T> getNext() {
    	return this.next;
    }
    
    @Override
    public final void insertNext(T obj) {
        Node<T> newNode = new Node<>(obj, this, this.getNext());

        this.getNext().setPrev(newNode);
        this.setNext(newNode);
    }

    @Override
    public final void insertPrev(T obj) {
        Node<T> newNode = new Node<>(obj, this.getPrev(), this);

        this.getPrev().setNext(newNode);
        this.setPrev(newNode);
    }
    
    @Override
    public final void removeNext() {
        Node<T> nnNode = this.getNext().getNext();

		this.setNext(nnNode);
        nnNode.setPrev(this);
    }
}

/******************************************************************************
 * 아래의 코드는 ConsoleCommand 에서 사용하는 익셉션들의 모음이다. 
 * 필요하면 수정해도 좋으나 수정하지 않아도 된다. 
 *****************************************************************************/

/******************************************************************************
 * ConsoleCommand 처리 중에 발생하는 익셉션의 상위 클래스이다. 
 * {@code throws} 구문이나 {@code catch} 구문을 간단히 하는데 사용된다.  
 */
@SuppressWarnings("serial")
class ConsoleCommandException extends Exception {
	public ConsoleCommandException(String msg) {
		super(msg);
	}

	public ConsoleCommandException(String msg, Throwable cause) {
		super(msg, cause);
	}
}

/******************************************************************************
 * 명령 파싱 과정에서 발견된 오류상황을 서술하기 위한 예외 클래스 
 */
@SuppressWarnings("serial")
class CommandParseException extends ConsoleCommandException {
	private String command;
	private String input;

	public CommandParseException(String cause) {
		super(cause, null);
		this.command = "";
		this.input = "";
	}

	public CommandParseException(String cmd, String input, String cause) {
		super(cause, null);
		this.command = cmd;
		this.input = input;
	}

	public String getCommand() {
		return command;
	}

	public String getInput() {
		return input;
	}

}

/******************************************************************************
 * 존재하지 않는 명령을 사용자가 요구하는 경우를 서술하기 위한 예외 클래스 
 */
class CommandNotFoundException extends ConsoleCommandException {
	private String command;

	public CommandNotFoundException(String command) {
		super(String.format("input command: %s", command));
		this.command = command;
	}

	private static final long serialVersionUID = 1L;

	public String getCommand() {
		return command;
	}
}
