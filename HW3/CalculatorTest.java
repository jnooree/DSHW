import java.io.*;
import java.util.*;
import java.util.regex.*;

public class CalculatorTest
{
	private static final Pattern OVERALL_PATTERN = 
			Pattern.compile("^\\s*([0-9\\+\\-\\*\\/\\%\\^ \\t\\(\\)]+)\\s*$");
	private static final String ERROR_MSG = "ERROR";

	public CalculatorTest() {}

	public static void main(String args[]) throws Exception
	{
		//System.out.println(Runtime.version()); //debug

		try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
		{
			while(true)
			{
				try
				{
					String input = br.readLine();

					if(input.equalsIgnoreCase("q"))
						break;

					command(input);
				}
				catch(IllegalArgumentException e)
				{
					System.out.println(ERROR_MSG);
				}
			}
		}
	}

	public static void command(String input) throws IllegalArgumentException
	{
		Matcher expressionMatcher = OVERALL_PATTERN.matcher(input);

    	if(!expressionMatcher.matches())
    	{
    		throw new IllegalArgumentException();
    	}
    	else
    	{
    		List<String> postExpr = PostFixer.convert(expressionMatcher.group(1));
    		String answer = PostCalculator.calculate(postExpr);
    		
    		System.out.println(String.join(" ", postExpr));
    		System.out.println(answer);
    	}
	}
}

class Processor
{
	private final Pattern NUMR_PATTERN =
			Pattern.compile("[peos]+((\\d+)\\s*)");
	private final Pattern LPAR_PATTERN =
			Pattern.compile("[peos]+((\\()\\s*)");
	private final Pattern EXPN_PATTERN =
			Pattern.compile("[nd]((\\^)\\s*)");
	private final Pattern OPER_PATTERN = 
			Pattern.compile("[nd](([\\+\\-\\*\\/\\%])\\s*)");
	private final Pattern RPAR_PATTERN = 
			Pattern.compile(".*(p+?)[neods]*n+d*((\\))\\s*)");
	private final Pattern SIGN_PATTERN = 
			Pattern.compile("[pos]((\\-)\\s*)");
	private final List<Pattern> PATTERNS_LIST = 
			Arrays.asList(NUMR_PATTERN, LPAR_PATTERN, EXPN_PATTERN, 
						  OPER_PATTERN, RPAR_PATTERN, SIGN_PATTERN);

	private final Pattern DONE_PATTERN = Pattern.compile("^[npoeds]+$");

	private final List<List<String>> PRIORITY_LIST =
			Arrays.asList(Arrays.asList("^"), Arrays.asList("~"),
						  Arrays.asList("*", "/", "%"), Arrays.asList("+", "-"));
	private final List<String> BINOPR_LIST = 
			Arrays.asList("^", "*", "/", "%", "+", "-");
	private final String UNARY_OPERT = "~";

	public Processor() {}

	public int patternCounts()
	{
		return PATTERNS_LIST.size();
	}

	public Pattern getPattern(int i)
	{
		return PATTERNS_LIST.get(i);
	}

	public Matcher getMatcher(int i, String str)
	{
		return getPattern(i).matcher(str);
	}

	public boolean isEndConv(String str)
	{
		return DONE_PATTERN.matcher(str).matches();
	}

	public boolean isLowEq(String str1, String str2) throws IllegalArgumentException
	{
		for(List<String> currPriorityList: PRIORITY_LIST)
		{
			if(currPriorityList.contains(str2)) return true;
			else if(currPriorityList.contains(str1)) return false;
		}

		throw new IllegalArgumentException();
	}

	public boolean isUnaryOpr(String operator)
	{
		return UNARY_OPERT.equals(operator);
	}

	public boolean isBinOpr(String operator)
	{
		return BINOPR_LIST.contains(operator);
	}

	public static String replaceGroup(Matcher matcher, String expression,
									  int groupToReplace, String replacement)
	{
	    StringBuilder repBuilder = new StringBuilder(expression);

	    repBuilder.replace(matcher.start(groupToReplace),
	    				   matcher.end(groupToReplace), replacement);

	    return repBuilder.toString();
	}
}

class PostFixer
{	
	public PostFixer() {}

	public static List<String> convert(String inExpr) 
			throws IllegalArgumentException
	{
		List<String> result = new ArrayList<String>();
		EvalHelper helper = new EvalHelper();

		inExpr = "p" + inExpr + ")";

		while(true)
		{
			List<String> tmpResult = helper.getResult(inExpr);
			if(!tmpResult.isEmpty()) result.addAll(tmpResult);
			
			inExpr = helper.getNextTerm(inExpr);

			//System.out.println(String.join(" ", result)); //debug
			//System.out.println(inExpr); //debug
			//System.out.println(); //debug

			Processor prcs = new Processor();

			if(prcs.isEndConv(inExpr))
			{
				if(helper.isDone()) break;
				else throw new IllegalArgumentException();
			}
			else
			{
				if(helper.isDone()) throw new IllegalArgumentException();
			}
		}

		return result;
	}

	private static class EvalHelper extends PostFixer
	{
		private Stack<String> optStack;
		private List<Matcher> matcherList;

		EvalHelper()
		{
			this.optStack = new Stack<>();
			this.optStack.push("(");
		}

		boolean isDone()
		{
			if(this.optStack.empty()) return true;
			else return false;
		}

		List<String> getResult(String inExpr) throws IllegalArgumentException
		{
			Processor prcs = new Processor();
			this.matcherList = new ArrayList<>();

			//System.out.println(Arrays.toString(optStack.toArray())); //debug

			for(int i=0; i<prcs.patternCounts(); i++)
			{
				Matcher matcher = prcs.getMatcher(i, inExpr);
				this.matcherList.add(matcher);

				if(matcher.find())
				{
					MatchTerms currTerm = new MatchTerms(matcher, i);

					return makePostfix(currTerm, inExpr);
				}
			}

			throw new IllegalArgumentException();
		}

		String getNextTerm(String inExpr)
		{
			final List<String> REPL_LIST = 
					Arrays.asList("n", "p", "e", "o", "", "s");

			int matchCtg = this.matcherList.size() - 1;
			Matcher matcher = this.matcherList.get(matchCtg);

			if (matchCtg == 4) inExpr = Processor.replaceGroup(matcher, inExpr, 2, "d");

			return Processor.replaceGroup(matcher, inExpr, 1, REPL_LIST.get(matchCtg));
		}

		private List<String> makePostfix(MatchTerms currTerm, String inExpr)
				throws IllegalArgumentException
		{
			List<String> result = new ArrayList<>();

			if(currTerm.isCatg(0)) //numeral
			{
				result.add(currTerm.getMatch(2));
			}
			else if(currTerm.isCatg(1)) //left parenthesis
			{
				this.optStack.push("(");
			}
			else if(currTerm.isCatg(2)) //exponential
			{
				this.optStack.push("^");
			}
			else if(currTerm.isCatg(3)) //binary operators
			{
				result.addAll(popUntil(currTerm, 0));
				this.optStack.push(currTerm.getTerm());
			}
			else if(currTerm.isCatg(4)) //right parenthesis
			{
				result.addAll(popUntil("(", 1));
				this.optStack.pop();
			}
			else if(currTerm.isCatg(5)) //sign
			{
				this.optStack.push("~");
			}
			else
			{
				throw new IllegalArgumentException();
			}

			return result;
		}

		private List<String> popUntil(Terms target, int mode)
				throws IllegalArgumentException
		{
			List<String> result = new ArrayList<>();

			while(targetNotFound(target, mode))
			{
			    result.add(this.optStack.pop());
			}

			return result;
		}

		private List<String> popUntil(String targetStr, int mode) 
				throws IllegalArgumentException
		{
			Terms term = new Terms(targetStr);
			
			return popUntil(term, mode);
		}

		private boolean targetNotFound(Terms target, int mode) 
				throws IllegalArgumentException
		{
			if(mode == 0) //pop if the target operator has lower or equal priority
			{
				return target.isLowEq(this.optStack.peek());
			}
			else if(mode == 1) //pop if the target operator has not found
			{
				return !this.optStack.peek().equals(target.getTerm());
			}

			throw new IllegalArgumentException();
		}
	}
}

class PostCalculator
{
	public PostCalculator() {}

	public static String calculate(List<String> postExpr) throws IllegalArgumentException
	{
		Stack<Long> calcStack = new Stack<>();

		for(String termStr: postExpr)
		{
			calcStack = calcCore(termStr, calcStack);

			//System.out.println(termStr); //debug
			//System.out.println(Arrays.toString(calcStack.toArray())); //debug
		}
		
		try
		{
			return String.valueOf(calcStack.pop());
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException();
		}
	}

	private static Stack<Long> calcCore(String termStr, Stack<Long> calcStack) throws IllegalArgumentException
	{
		try
		{
			Processor prcs = new Processor();

			if(prcs.isUnaryOpr(termStr))
			{
				calcStack.push(-1 * calcStack.pop());
			}
			else if(prcs.isBinOpr(termStr))
			{
				Operators operator = new Operators(termStr);
				
				long tmp = operator.operate(calcStack.pop(), calcStack.pop());
				calcStack.push(tmp);
			}
			else
			{
				calcStack.push(Long.parseLong(termStr));
			}

			return calcStack;
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException();
		}
	}
}

class Terms
{
	private String termStr;
	private Processor prcs = new Processor();

	public Terms()
	{
		this.termStr = "";
	}

	public Terms(String termStr)
	{
		this.termStr = termStr;
	}

	public void setTerm(String termStr)
	{
		this.termStr = termStr;
	}

	public String getTerm()
	{
		return this.termStr;
	}

	public boolean equals(String termStr)
	{
		return this.termStr.equals(termStr) ? true : false;
	}

	public boolean equals(Terms termToComp)
	{
		return this.equals(termToComp.getTerm()) ? true : false;
	}

	public boolean isLowEq(String termStr) throws IllegalArgumentException
	{
		if(this.equals(termStr)) return true;
		else
		{
			return prcs.isLowEq(this.termStr, termStr);
		}
	}

	public boolean isHighEq(String termStr) throws IllegalArgumentException
	{
		Terms term = new Terms(termStr);

		return term.isLowEq(this.termStr);
	}
}

class MatchTerms extends Terms
{
	private Matcher matcher;
	private int matchCtg;

	public MatchTerms(Matcher matcher, int matchCtg)
	{
		this.matcher = matcher;

		this.matchCtg = matchCtg;
		
		if(this.matchCtg == 4) setTerm(")");
		else setTerm(matcher.group(2));
	}

	public Matcher getMatch()
	{
		return this.matcher;
	}

	public String getMatch(int groupNum)
	{
		return this.matcher.group(groupNum);
	}

	public int getCatg()
	{
		return this.matchCtg;
	}

	public boolean isCatg(int category)
	{
		return this.matchCtg == category ? true : false;
	}
}

class Operators extends Terms
{
	public Operators(String termStr)
	{
		setTerm(termStr);
	}

	public boolean equals(Operators optrToComp)
	{
		return this.equals(optrToComp.getTerm()) ? true : false;
	}

	public long operate(long num1, long num2) throws IllegalArgumentException
	{
		if(this.equals("^") && num1 >= 0) return (long) Math.pow(num2, num1);
		else if(this.equals("*")) return num2 * num1;
		else if(this.equals("/") && num1 != 0) return num2 / num1;
		else if(this.equals("%") && num1 != 0) return num2 % num1;
		else if(this.equals("+")) return num2 + num1;
		else if(this.equals("-")) return num2 - num1;
		else throw new IllegalArgumentException();
	}
}