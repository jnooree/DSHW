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
    		List<Terms> postExpr = PostFixer.convert(expressionMatcher.group(1));
    		String answer = PostCalculator.calculate(postExpr);

    		String postExprStr = "";
    		for(Terms term: postExpr)
    		{
    			postExprStr = postExprStr + " " + term.getTerm();
    		}
    		
    		System.out.println(postExprStr.substring(1));
    		System.out.println(answer);
    	}
	}
}

class PostFixer
{
	public PostFixer() {}

	public static List<Terms> convert(String inExpr) throws IllegalArgumentException
	{
		inExpr = "(" + inExpr + ")";
		inExpr = inExpr.replaceAll("\\s+", "");
		List<Terms> inFixList = split(inExpr, 0);

		/*for(Terms term: inFixList)  //debug
		{
			System.out.println(term.getTerm());
		}*/
		
		ConvHelper convInFix = new ConvHelper(inFixList);
		return convInFix.toPostFix();
	}

	private static List<Terms> split(String inExpr, int parCounts) throws IllegalArgumentException
	{
		List<Terms> result = new ArrayList<>();
		RegexHelper rxInExpr = new RegexHelper(inExpr);

		System.out.println(inExpr); //debug

		if(inExpr.charAt(0) == '-')
		{
			result.add(new Operators("~", 1));
			result.addAll(split(inExpr.substring(1), parCounts));
		}
		else if(inExpr.charAt(0) == '(')
		{
			result.add(new Terms("("));
			result.addAll(split(inExpr.substring(1), ++parCounts));
		}
		else if(rxInExpr.isOper())
		{
			result.addAll(rxInExpr.getOper());
			result.addAll(split(rxInExpr.getInExpr(), parCounts));
		}
		else if(rxInExpr.isRpar(parCounts))
		{
			result.addAll(rxInExpr.getRpar());
			String tmpExpr = rxInExpr.getInExpr();

			if(tmpExpr.equals(""))
			{
				if(parCounts == 1) return result;
				else throw new IllegalArgumentException();
			}
			else
			{
				result.addAll(split(tmpExpr, --parCounts));
			}
		}
		else throw new IllegalArgumentException();

		return result;
	}

	private static class RegexHelper extends PostFixer
	{
		private final Pattern OPER_PATTERN = Pattern.compile("^(\\d+)([\\^\\+\\-\\*\\/\\%]).+");
		private final Pattern RPAR_PATTERN = Pattern.compile("^(\\d*)(\\))");

		private String inExpr;
		private Matcher operMatcher;
		private Matcher rParMatcher;

		RegexHelper() {}

		RegexHelper(String inExpr)
		{
			this.inExpr = inExpr;

			operMatcher = OPER_PATTERN.matcher(inExpr);
			rParMatcher = RPAR_PATTERN.matcher(inExpr);
		}

		String getInExpr()
		{
			return inExpr;
		}

		boolean isOper()
		{
			operMatcher.reset();
			return operMatcher.find();
		}

		boolean isRpar()
		{
			rParMatcher.reset();
			return rParMatcher.find();
		}

		boolean isRpar(int parCounts)
		{
			if(parCounts <= 0) return false;
			else return isRpar();
		}

		List<Terms> getOper()
		{
			List<Terms> result = new ArrayList<>();

			operMatcher.reset();
			operMatcher.find();

			result.add(new Terms(operMatcher.group(1)));
			result.add(new Operators(operMatcher.group(2)));

			inExpr = inExpr.substring(operMatcher.end(2));

			return result;
		}

		List<Terms> getRpar()
		{
			List<Terms> result = new ArrayList<>();

			rParMatcher.reset();
			rParMatcher.find();

			if(!rParMatcher.group(1).equals(""))
			{
				result.add(new Terms(rParMatcher.group(1)));
			}
			result.add(new Terms(")"));

			rParMatcher.reset();
			if(rParMatcher.matches())
			{
				inExpr = "";
			}
			else
			{
				rParMatcher.find();
				inExpr = inExpr.substring(rParMatcher.end(2));
			}

			return result;
		}
	}

	private static class ConvHelper extends PostFixer
	{
		private Stack<Terms> oprStack;
		private List<Terms> inTermsList;

		ConvHelper()
		{
			oprStack = new Stack<>();
		}

		ConvHelper(List<Terms> inTermsList)
		{
			oprStack = new Stack<>();
			this.inTermsList = inTermsList;
		}

		List<Terms> toPostFix()
		{
			List<Terms> result = new ArrayList<>();

			for(Terms term: inTermsList)
			{
				if(term.equals("(") || term.equals("^") || term.equals("~"))
				{
					oprStack.push(term);
				}
				else if(term.isOperator())
				{
					result.addAll(popUntil(term));
					oprStack.push(term);
				}
				else if(term.equals(")"))
				{
					result.addAll(popUntil("("));
					oprStack.pop();
				}
				else
				{
					result.add(term);
				}
			}

			return result;
		}

		private List<Terms> popUntil(Terms term)
		{
			List<Terms> result = new ArrayList<>();

			while(term.getPriority() >= oprStack.peek().getPriority())
			{
				result.add(oprStack.pop());
			}

			return result;
		}

		private List<Terms> popUntil(String termStr)
		{
			List<Terms> result = new ArrayList<>();

			while(!oprStack.peek().equals(termStr))
			{
			    result.add(oprStack.pop());
			}

			return result;
		}
	}
}

class PostCalculator
{
	public PostCalculator() {}

	public static String calculate(List<Terms> postExpr) throws IllegalArgumentException
	{
		Stack<Long> calcStack = new Stack<>();

		for(Terms term: postExpr)
		{
			calcStack = calcCore(term, calcStack);

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

	private static Stack<Long> calcCore(Terms term, Stack<Long> calcStack) throws IllegalArgumentException
	{
		try
		{
			if(term.isUnaryOpr())
			{
				calcStack.push(-1 * calcStack.pop());
			}
			else if(term.isBinOpr())
			{				
				long tmp = term.operate(calcStack.pop(), calcStack.pop());
				calcStack.push(tmp);
			}
			else
			{
				calcStack.push(Long.parseLong(term.getTerm()));
			}
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException();
		}

		return calcStack;
	}
}


class Terms
{
	private String termStr;

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

	public boolean equals(Terms term)
	{
		return this.equals(term.getTerm()) ? true : false;
	}

	public int getPriority()
	{
		return 100;
	}

	public boolean isOperator()
	{
		return false;
	}

	public boolean isUnaryOpr()
	{
		return false;
	}

	public boolean isBinOpr()
	{
		return false;
	}

	public long operate(long num1, long num2) throws IllegalArgumentException
	{
		return 0;
	}
}

class Operators extends Terms
{
	private final String UNARY_OPERT = "~";
	private final List<String> BINOPR_LIST = Arrays.asList("^", "*", "/", "%", "+", "-");
	private final List<List<String>> PRIORITY_LIST =
			Arrays.asList(Arrays.asList("^"), Arrays.asList("~"),
						  Arrays.asList("*", "/", "%"), Arrays.asList("+", "-"));

	private int priority;

	public Operators(String oprStr)
	{
		setTerm(oprStr);

		for(int i=0; i<PRIORITY_LIST.size(); i++)
		{
			if(PRIORITY_LIST.get(i).contains(oprStr))
			{
				priority = i;
				break;
			}
		}
	}

	public Operators(String oprStr, int priority)
	{
		setTerm(oprStr);
		this.priority = priority;
	}

	@Override
	public int getPriority()
	{
		return priority;
	}

	@Override
	public boolean isOperator()
	{
		return true;
	}

	@Override
	public boolean isUnaryOpr()
	{
		return UNARY_OPERT.equals(getTerm());
	}

	@Override
	public boolean isBinOpr()
	{
		return BINOPR_LIST.contains(getTerm());
	}

	@Override
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