import java.util.*;

abstract public class Terms implements Comparable<Terms> {
	private String term;

	protected Terms(String term) {
		this.term = term;
	}

	public static Terms toTerm(String termStr, int type) {
		if (type == 0) {
			return new Numerals(termStr);
		} else if (type == 1 || type == 2) {
			return new Parenthesis(termStr);
		} else if (type == 3) {
			return new Sign("~");
		} else {
			return new BinOpr(termStr);
		}
	}

	public String getTerm() {
		return this.term;
	}

	public boolean isOperator() {
		return false;
	}

	public boolean isUnaryOpr() {
		return false;
	}

	public boolean isBinOpr() {
		return false;
	}

	public long getVal() {
		throw new UnsupportedOperationException();
	}

	public long operate(long num1, long num2) {
		throw new UnsupportedOperationException();
	}
}

class Numerals extends Terms {
	private long num;

	public Numerals(String term) {
		super(term);
		this.num = Long.parseLong(term);
	}

	public long getVal() {
		return this.num;
	}

	@Override
	public int compareTo(Terms term) {
		return 0;
	}
}

class Parenthesis extends Terms {
	public Parenthesis(String term) {
		super(term);
	}

	@Override
	public int compareTo(Terms term) {
		return -1;
	}
}

class Sign extends Terms {
	public Sign(String term) {
		super(term);
	}

	@Override
	public boolean isOperator() {
		return true;
	}

	@Override
	public boolean isUnaryOpr() {
		return true;
	}

	@Override
	public int compareTo(Terms term) {
		if (!term.isOperator()) return 1;

		if (term.isUnaryOpr()) return 0;
		else if (term.getTerm().equals("^")) return -1;
		else return 1;
	}
}

class BinOpr extends Terms {
	private static final List<List<String>> PRIORITY_LIST = Arrays.asList(Arrays.asList("^"), Arrays.asList("~"), Arrays.asList("*", "/", "%"), Arrays.asList("+", "-"));

	public BinOpr(String term) {
		super(term);
	}

	@Override
	public boolean isOperator() {
		return true;
	}

	@Override
	public boolean isBinOpr() {
		return true;
	}

	@Override
	public long operate(long num1, long num2) throws IllegalArgumentException {
		if(this.getTerm().equals("^") && num1 >= 0) return (long) Math.pow(num2, num1);
		else if(this.getTerm().equals("*")) return num2 * num1;
		else if(this.getTerm().equals("/") && num1 != 0) return num2 / num1;
		else if(this.getTerm().equals("%") && num1 != 0) return num2 % num1;
		else if(this.getTerm().equals("+")) return num2 + num1;
		else if(this.getTerm().equals("-")) return num2 - num1;

		throw new IllegalArgumentException();
	}

	@Override
	public int compareTo(Terms term) throws IllegalArgumentException {
		if (!term.isOperator()) return 1;
		if (this.getTerm().equals(term.getTerm())) return 0;

		for(List<String> currPriorityList: PRIORITY_LIST)
		{
			if (currPriorityList.contains(this.getTerm())) return 1;
			else if (currPriorityList.contains(term.getTerm())) return -1;
		}

		throw new IllegalArgumentException();
	}
}