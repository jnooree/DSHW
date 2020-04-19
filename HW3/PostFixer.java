import java.util.*;
import java.util.regex.*;

public class PostFixer {
	private static final Pattern DONE_PATTERN = Pattern.compile("^[noeds]+$");

	public PostFixer() {}

	public static List<Terms> convert(String inExpr) throws IllegalArgumentException {
		List<Terms> result = new ArrayList<Terms>();
		EvalHelper helper = new EvalHelper(inExpr);

		while (true) {
			List<Terms> tmpResult = helper.getTerms();
			inExpr = helper.step();

			if (!tmpResult.isEmpty()) result.addAll(tmpResult);

			if (isEndConv(inExpr)) {
				if (helper.isDone()) break;
				else throw new IllegalArgumentException();
			}
			else if (helper.isDone()) throw new IllegalArgumentException();
		}

		return result;
	}

	private static boolean isEndConv(String inExpr) {
		return DONE_PATTERN.matcher(inExpr).matches();
	}

	private static class EvalHelper {
		private static final Pattern NUMR = Pattern.compile("[peos](?<repl>(?<term>\\d+)\\s*)");
		private static final Pattern LPAR = Pattern.compile("[peos](?<repl>(?<term>\\()\\s*)");
		private static final Pattern RPAR = Pattern.compile("(?<lpar>p)[neods]*n+d*(?<repl>(?<term>\\))\\s*)");
		private static final Pattern SIGN = Pattern.compile("[pos](?<repl>(?<term>\\-)\\s*)");
		private static final Pattern EXPN = Pattern.compile("[nd](?<repl>(?<term>\\^)\\s*)");
		private static final Pattern OPER = Pattern.compile("[nd](?<repl>(?<term>[\\+\\-\\*\\/\\%])\\s*)");
		private static final List<Pattern> PATTERNS_LIST = Arrays.asList(NUMR, LPAR, RPAR, SIGN, EXPN, OPER);
		private static final List<String> REPL_LIST = Arrays.asList("n", "p", "d", "s", "e", "o");

		private ConvStack oprStack;
		private String inExpr;
		private Matcher currMatcher;
		private int currType;

		EvalHelper(String inExpr) {
			 this.oprStack = new ConvStack();
			 this.inExpr = "p" + inExpr + ")";
		}

		boolean isDone() {
			return oprStack.empty();
		}

		List<Terms> getTerms() throws IllegalArgumentException {
			for (int i=0; i<PATTERNS_LIST.size(); i++) {
				currMatcher = PATTERNS_LIST.get(i).matcher(inExpr);

				if (currMatcher.find()) {
					currType = i;
					return this.toPostfix();
				}
			}

			throw new IllegalArgumentException();
		}

		String step() {
			inExpr = replaceGroup("repl", REPL_LIST.get(currType));
			if (currType == 2) inExpr = replaceGroup("lpar", "");

			return inExpr;
		}

		private List<Terms> toPostfix() {
			List<Terms> result = new ArrayList<>();
			Terms currTerm = Terms.toTerm(currMatcher.group("term"), currType);

			//numeral
			if (currType == 0) {
				result.add(currTerm);
			}
			//left parenthesis, sign, exponential
			else if (currType == 1 || currType == 3 || currType == 4) {
				oprStack.push(currTerm);
			}
			else {
				List<Terms> tmp = oprStack.popTo(currTerm);
				if (!tmp.isEmpty()) result.addAll(tmp);
				
				if (currType == 2) oprStack.pop(); //right parenthesis
				else oprStack.push(currTerm); //binary operators
			}

			return result;
		}

		private String replaceGroup(String group, String repl) {
			StringBuilder repBuilder = new StringBuilder(inExpr);

			repBuilder.replace(currMatcher.start(group), currMatcher.end(group), repl);

			return repBuilder.toString();
		}
	}
}

