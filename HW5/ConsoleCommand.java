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
	public static final int SUBSTR_LEN = 6;

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
			while ((line = br.readLine()) != null)
				inputList.add(line);
		} catch (Exception e) {
			throw new CommandParseException("InputCmd", inputFile, e.toString());
		}
	}

	@Override
	public void apply(MatchDB db) throws Exception {
		db.reset();

		for (int i = 0; i < inputList.size(); i++) {
			String input = inputList.get(i);

			for (int j = 0; j <= (input.length() - SUBSTR_LEN); j++) {
				db.insert(new MatchDBItem(input.substring(j, j+SUBSTR_LEN), 
										  new int[] {i+1, j+1}));
			}
		}
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
		List<MatchDBItem> result = db.print(hash);

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
	private int tgtLen;

	@Override
	protected void parseArguments(String target) {
		this.target = target;
		tgtLen = target.length();
	}

	@Override
	public void apply(MatchDB db) throws Exception {
		List<MyList<MatchDBItem>> matches = new ArrayList<>();

		for (int i = SUBSTR_LEN; i < (tgtLen + SUBSTR_LEN); i += SUBSTR_LEN) {
			int endIdx = i < tgtLen ? i : tgtLen;
			int startIdx = endIdx - SUBSTR_LEN;
			
			String key = target.substring(startIdx, endIdx);
			matches.add(db.search(key));
		}

		if (matches.get(0).isEmpty()) {
			System.out.println(NO_MATCH_MSG);
			return;
		}
		
		List<int[]> result = getFullMatch(matches);
		System.out.println(idxToString(result));
	}

	private List<int[]> getFullMatch(List<MyList<MatchDBItem>> matches) {
		List<int[]> result = new ArrayList<>();
		for (MatchDBItem item: matches.get(0))
			result.add(item.getIdx());

		for (int i = 1; i < matches.size(); i++) {
			Iterator<int[]> resultIter = result.iterator();
			
			int diff;
			if ((i + 1) * SUBSTR_LEN <= tgtLen) {
				diff = i * SUBSTR_LEN;
			} else {
				diff = (i - 1) * SUBSTR_LEN + tgtLen % SUBSTR_LEN;
			}

			while (resultIter.hasNext()) {
				int[] j = resultIter.next();
				boolean remove = true;

				for (MatchDBItem k: matches.get(i)) {
					if (j[0] == k.getIdx()[0] && j[1] == (k.getIdx()[1] - diff))
						remove = false;
				}

				if (remove) resultIter.remove();
			}
		}

		return result;
	}

	private static String idxToString(List<int[]> indices) {
		if (indices.isEmpty()) return NO_MATCH_MSG;

		List<String> result = new ArrayList<>();
		for (int[] i: indices) {
			result.add("(" +
					   Arrays.stream(i)
							 .mapToObj(Integer::toString)
							 .collect(Collectors.joining(", ")) +
					   ")");
		}
		return String.join(" ", result);
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