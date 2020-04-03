import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Arrays;


public class BigInteger
{
    private static final int MAX_INP = 100;
    private static final int MAX_RES = MAX_INP * 2;
    private static final int[] ZERO_ASARRAY = new int[MAX_INP];

    private static final String QUIT_COMMAND = "quit";
    private static final String MSG_INVALID_INPUT = "입력이 잘못되었습니다.";

	private static final Pattern EXPRESSION_PATTERN = Pattern.compile("^\\s*([\\+\\-]?)\\s*([0-9]+)\\s*([\\+\\-\\*])\\s*([\\+\\-]?)\\s*([0-9]+)\\s*$");

    private String sign;
    private int exp;
    private int[] num;


    public BigInteger(int arrSize, int digit) //One digit into BigInteger
    {
        this.sign = (Integer.signum(digit) == -1) ? "-" : "+";
        this.exp = 1;
        
        this.num = new int[arrSize];
        this.num[arrSize - 1] = digit;
    }

    public BigInteger(String sign, String intString) //Parse integer-as-string into BigInteger
    {
        this.sign = sign.equals("-") ? "-" : "+";

        this.exp = intString.length();

        this.num = new int[MAX_INP];
        for(int i=0; i<this.exp; i++)
        {
            this.num[i + (MAX_INP - this.exp)] = intString.charAt(i) - '0';
        }
    }

    public BigInteger(BigInteger big) 
    {
        this.sign = big.sign;
        this.exp = big.exp;
        this.num = big.num;
    }


    public BigInteger operate(String operator, BigInteger big) //Choose operation
    {
        if(operator.equals("*"))
        {
            if(Arrays.equals(num, ZERO_ASARRAY) || Arrays.equals(big.num, ZERO_ASARRAY))
            {
                return new BigInteger(1, 0);
            }
            else
            {
                return multiply(big);
            }
        }
        else if((sign.equals("+") && operator.equals(big.sign)) || (sign.equals("-") && !operator.equals(big.sign)))
        {
            return add(big);
        }
        else
        {
            return subtractPrep(big);
        }
    }


    public BigInteger add(BigInteger big) //Add two BigIntegers
    {
        BigInteger result = new BigInteger(MAX_RES, 0);
        int carry = 0;

        result.sign = sign;

        result.exp = (exp >= big.exp) ? exp : big.exp;

        for(int i=MAX_RES - 1, tmp = 0; i>=(MAX_RES - result.exp); i--)
        {
            tmp = num[num.length - (MAX_RES - i)] + big.num[big.num.length - (MAX_RES - i)] + carry;
            
            carry = tmp / 10;
            result.num[i] = tmp % 10;
        }

        result.addLastCarry(carry);
        return result;
    }

    public void addLastCarry(int carry) //Process last carry
    {
        if(carry != 0)
        {
            int arrSize = this.num.length;

            this.exp++;
            this.num[arrSize - this.exp] = carry;
        }
    }


    private String detSign() //Determine sign of final result
    {
        if(sign.equals("-"))
        {
            return "+";
        }
        else
        {
            return "-";
        }
    }

    public BigInteger subtractPrep(BigInteger big) //Preprocessing for subtraction:
    {                                              //mainly choosing which of the
        if(exp > big.exp)                          //numbers is bigger (in absolute size)
        {
            return subtractPostp(big);
        }
        else if(exp < big.exp)
        {
            big.sign = detSign();
            return big.subtractPostp(this);
        }
        else
        {
            for(int i = (MAX_INP - exp); i<MAX_INP; i++)
            {
                if(num[i] > big.num[i])
                {
                    return subtractPostp(big);
                }
                else if(num[i] < big.num[i])
                {
                    big.sign = detSign();
                    return big.subtractPostp(this);
                }
            }

            return new BigInteger(1, 0); //If two numbers are equal
        }
    }

    private void subtractCore(BigInteger big, int i, int carry) //Recursion part;
    {                                                           //actually subtracts
        int tmp = num[i] - big.num[i] + carry;

        if(tmp < 0)
        {
            num[i] = 10 + tmp;
            subtractCore(big, i - 1, -1);
        }
        else if(i == (MAX_INP - exp))
        {
            num[i] = tmp;
        }
        else
        {
            num[i] = tmp;
            subtractCore(big, i - 1, 0);
        }
    }

    private BigInteger subtractPostp(BigInteger big) //Post-processing: determining exponential part
    {
        BigInteger result = new BigInteger(this);

        result.subtractCore(big, MAX_INP - 1, 0);
  
        int realExp = 0;
        for(; result.num[realExp] == 0; realExp++);
        result.exp = MAX_INP - realExp;

        return result;
    }

  
    public BigInteger multiply(BigInteger big) //Multiply two BigIntegers
    {
        BigInteger result = new BigInteger(MAX_RES, 0);

        for(int i=(MAX_INP - 1); i>=(MAX_INP - exp); i--)
        {
            BigInteger tmpResult = new BigInteger(MAX_INP + 1, 0);
            int carry = 0;

            tmpResult.exp = big.exp;

            for(int j=(MAX_INP - 1), tmp = 0; j>=(MAX_INP - big.exp); j--)
            {
                tmp = (num[i] * big.num[j]) + carry;
                
                carry = tmp / 10;
                tmpResult.num[j + 1] = tmp % 10;
            }

            tmpResult.addLastCarry(carry);

            result = result.add(tmpResult.multiply10s((MAX_INP - 1) - i));
        }

        result.sign = (sign.equals(big.sign)) ? "+" : "-";
        return result;
    }

    private BigInteger multiply10s(int zeros) //Multiply 10's by adjusting indices
    {
        BigInteger result = new BigInteger(MAX_RES, 0);

        for(int i=(MAX_INP + 1 - exp); i<=MAX_INP; i++)
        {
            result.num[i - zeros + (MAX_INP - 1)] = num[i];
        }

        result.exp = exp + zeros;

        return result;
    }


    @Override
    public String toString() //Output
    {
        String answer = "";
        if(sign.equals("-")) answer = answer.concat(sign);

        int arrSize = num.length;
        for(int i=(arrSize - exp); i<arrSize; i++)
        {
            answer = answer.concat(Integer.toString(num[i]));
        }

        return answer;
    }


	public static void main(String[] args) throws Exception
	{
        try(InputStreamReader isr = new InputStreamReader(System.in))
        {
            try(BufferedReader reader = new BufferedReader(isr))
            {
                boolean done = false;

                while(!done)
                {
                    String input = reader.readLine(); //Get input
  
                    try
                    {
                        done = processInput(input);
                    }
                    catch(IllegalArgumentException e)
                    {
                        System.err.println(MSG_INVALID_INPUT); //Handle mismatch error
                    }
                }
            }
        }
    }


    private static boolean isQuitCmd(String input)
    {
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }

    static boolean processInput(String input) throws IllegalArgumentException
    //Check if the input is QUIT_COMMAND
    {
        boolean quit = isQuitCmd(input);
  
        if (quit)
        {
            return true;
        }
        else
        {
            BigInteger result = evaluate(input);   //Actual processing part
            System.out.println(result.toString()); //Actual output part

            return false;
        }
    }

    static BigInteger evaluate(String input) throws IllegalArgumentException //Parse input
    {
        Matcher expressionMatcher = EXPRESSION_PATTERN.matcher(input);
        
        if(!expressionMatcher.matches())
        {
            throw new IllegalArgumentException(); //Raise error when mismatch - handle @ line 290
        }
        else
        {
            String operator = expressionMatcher.group(3);

            BigInteger num1 = new BigInteger(expressionMatcher.group(1), expressionMatcher.group(2));
            BigInteger num2 = new BigInteger(expressionMatcher.group(4), expressionMatcher.group(5)); 

            return num1.operate(operator, num2);
        }
    } 
} 