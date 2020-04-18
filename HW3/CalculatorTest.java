import java.io.*;
import java.util.*;
import java.util.regex.*;

public class CalculatorTest {
	private static final Pattern OVERALL_PATTERN = Pattern.compile("^\\s*([0-9\\+\\-\\*\\/\\%\\^ \\t\\(\\)]+)\\s*$");
	private static final String ERROR_MSG = "ERROR";

	public static void main(String args[]) throws Exception {
		//System.out.println(Runtime.version()); //debug

		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			while (true) {
				try {
					String input = br.readLine();

					if(input.equalsIgnoreCase("q"))
						break;

					command(input);
				} catch(IllegalArgumentException e) {
					System.out.println(ERROR_MSG);
				}
			}
		}
	}

	private static void command(String input) throws IllegalArgumentException {
		Matcher exprMatcher = OVERALL_PATTERN.matcher(input);

    	if (!exprMatcher.matches()) {
    		throw new IllegalArgumentException();
    	} else {
    		List<Terms> postExpr = PostFixer.convert(exprMatcher.group(1));
    		String answer = PostCalculator.calculate(postExpr);
    		
    		System.out.println(toString(postExpr));
    		System.out.println(answer);
    	}
	}

	private static String toString(List<Terms> postExpr) {
		String result = "";
		
		for (Terms term: postExpr) {
			result = result.concat(term.getTerm() + " ");
		}

		return result.trim();
	}
}
