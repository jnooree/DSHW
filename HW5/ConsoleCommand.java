import java.io.*;
import java.nio.file.*;

import java.util.*;
import java.util.stream.*;

/******************************************************************************
 * 명령들의 해석 규칙이 동일하므로, 코드 중복을 없애기 위한 추상 클래스.
 */
public abstract class ConsoleCommand {
	/**
	 * input 을 해석하는 공통 인터페이스.
	 * @param input {@code String} 타입의 입력 문자열
	 * @throws CommandParseException 입력 규칙에 맞지 않는 입력이 들어올 경우 발생
	 *
	 * 공통 명령 해석 규칙을 담고 있다.
	 * {@link ConsoleCommand.parseArguments} 로 인자를 전달한다.
	 * 
	 * 만약 어떤 명령이 별도의 해석 규칙이 필요한 경우 이 메소드를 직접 오버라이드하면 된다. 
	 */

	public static ConsoleCommand parse(String input) throws CommandParseException {
		ConsoleCommand command;

		if (input.startsWith("<")) {
			command = new InputCmd();
		} else if (input.startsWith("@")) {
			command = new PrintCmd();
		} else if (input.startsWith("?")) {
			command = new SearchCmd();
		} else {
			throw new CommandParseException(input);
		}

		command.parseArguments(input.substring(2));
		return command;
	}

	protected abstract void parseArguments(String input) throws CommandParseException;

	public abstract void apply(MatchDB db) throws Exception;
}

/******************************************************************************
 * 아래부터 각 명령어별로 과제 스펙에 맞는 구현을 한다.
 */
/******************************************************************************
 * < pathToFile
 */
class InputCmd extends ConsoleCommand {
	private List<String> inputList;

	public InputCmd() {
		inputList = new ArrayList<>();
	}

	@Override
	protected void parseArguments(String inputFile) throws CommandParseException {
		try {
			BufferedReader br = Files.newBufferedReader(Paths.get(inputFile));

			String line;
			while ((line = br.readLine()) != null) inputList.add(line);
		} catch (Exception e) {
			throw new CommandParseException("InputCmd", inputFile, e.toString());
		}
	}

	@Override
	public void apply(MatchDB db) throws Exception {
		db.reset();
		db.insert(inputList);
	}
}

/******************************************************************************
 * @ hashNum
 */
class PrintCmd extends ConsoleCommand {
	private static final String EMPTY_MESSAGE = "EMPTY";
	private int hash;

	@Override
	protected void parseArguments(String hashString) throws CommandParseException {
		try {
			hash = Integer.parseInt(hashString);
		} catch (Exception e) {
			throw new CommandParseException("PrintCmd", hashString, e.toString());
		}
	}

	@Override
	public void apply(MatchDB db) throws Exception {
		List<MatchDBItem> result = db.getItems(hash);

		if (result.isEmpty()) {
			System.out.println(EMPTY_MESSAGE);
			return;
		}

		System.out.println(result.stream()
								 .map(item -> item.toString())
								 .collect(Collectors.joining(" ")));
	}
}

/******************************************************************************
 * ? pattern
 */
class SearchCmd extends ConsoleCommand {
	private static final String NO_MATCH_MSG = "(0, 0)";
	private String target;

	@Override
	protected void parseArguments(String target) {
		this.target = target;
	}

	@Override
	public void apply(MatchDB db) throws Exception {
		MyList<MatchDBItem> result = db.search(target);
		System.out.println(idxToString(result));
	}

	private static String idxToString(MyList<MatchDBItem> result) {
		if (result.isEmpty()) return NO_MATCH_MSG;

		List<String> idxString = new ArrayList<>();
		for (MatchDBItem item: result) {
			int[] idx = item.getIdx();
			idxString.add("(" +
						  Arrays.stream(idx)
								.mapToObj(Integer::toString)
								.collect(Collectors.joining(", ")) +
						  ")");
		}
		return String.join(" ", idxString);
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