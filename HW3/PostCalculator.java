import java.util.*;
import java.util.regex.*;

class PostCalculator {
	public PostCalculator() {}

	public static String calculate(List<Terms> postExpr) throws IllegalArgumentException {
		Stack<Long> calcStack = new Stack<>();

		for (Terms term: postExpr) {
			if (!term.isOperator()) {
				calcStack.push(term.getVal());
			} else if (term.isBinOpr()) {
				long tmp = term.operate(calcStack.pop(), calcStack.pop());
				calcStack.push(tmp);
			} else {
				calcStack.push(-1 * calcStack.pop());
			}
		}
		
		try {
			return String.valueOf(calcStack.pop());
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}
}