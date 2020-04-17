import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.NoSuchElementException;

/******************************************************************************
 * 명령들의 해석 규칙이 동일하므로, 코드 중복을 없애기 위한 추상 클래스.
 */
public abstract class ConsoleCommand {
	/**
	 * input 을 해석하는 공통 인터페이스.
	 * @param input {@code String} 타입의 입력 문자열
	 * @throws CommandParseException 입력 규칙에 맞지 않는 입력이 들어올 경우 발생
	 *
	 * 공통 명령 해석 규칙을 담고 있다. {@code input} 을 분해하여 String[] 으로 만들고, 
	 * {@link ConsoleCommand.parseArguments} 로 인자를 전달한다.
	 * 
	 * 만약 어떤 명령이 별도의 해석 규칙이 필요한 경우 이 메소드를 직접 오버라이드하면 된다. 
	 */
	public static ConsoleCommand parse(String input) throws CommandParseException {
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
			throw new CommandParseException(input);
		}

		String[] args = input.split(" *% *%? *");
		command.parseArguments(args);
		return command;
	}

	/**
	 * {@link ConsoleCommand.parse} 메소드에서 분해된 문자열 배열(String[]) 을 이용해 
	 * 인자를 해석하는 추상 메소드. 
	 * 
	 * 자식 클래스들은 parse 메소드가 아니라 이 메소드를 오버라이드하여
	 * 각 명령에 맞는 규칙으로 인자를 해석한다.
	 *   
	 * @param args 규칙에 맞게 분해된 명령 인자
	 * @throws CommandParseException args가 명령의 규약에 맞지 않을 경우
	 */
	protected abstract void parseArguments(String[] args) throws CommandParseException;

	public abstract void apply(MovieDB db) throws Exception;
}

/******************************************************************************
 * 아래부터 각 명령어별로 과제 스펙에 맞는 구현을 한다.
 */
/******************************************************************************
 * DELETE %GENRE% %MOVIE% 
 */
class DeleteCmd extends ConsoleCommand {
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
class InsertCmd extends ConsoleCommand {
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
class PrintCmd extends ConsoleCommand {
	@Override
	protected void parseArguments(String[] args) throws CommandParseException {
		if (args.length != 1)
			throw new CommandParseException(
					"PRINT", Arrays.toString(args), "unnecessary argument(s)");
	}

	@Override
	public void apply(MovieDB db) throws Exception {
		MyLinkedList<MovieDBItem> result = db.items();

		if (result.isEmpty()) {
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
class SearchCmd extends ConsoleCommand {
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
		MyLinkedList<MovieDBItem> result = db.search(term);

		if (result.isEmpty()) {
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
		ConsoleWriter.writeln(s, arg);
		ConsoleWriter.flush();
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