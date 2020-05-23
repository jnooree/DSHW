import java.io.*;

public class Matching {
	private static MatchDB db = new MatchDB();

	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				String input = br.readLine();
				if (input.compareTo("QUIT") == 0) break;
				command(input);
			} catch (Exception e) {
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
	}

	private static void command(String input) throws Exception {
		ConsoleCommand command = ConsoleCommand.parse(input);
		command.apply(db);
	}
}
