import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BigInteger_eg
{
    public static final String QUIT_COMMAND = "quit";
    public static final String MSG_INVALID_INPUT = "입력이 잘못되었습니다.";
  
    // implement this
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile("");
  
  
    public BigInteger_eg(int i)
    {
    }
  
    public BigInteger_eg(int[] num1)
    {
    }
  
    public BigInteger_eg(String s)
    {
    }
  
    public BigInteger_eg add(BigInteger_eg big)
    {
    }
  
    public BigInteger_eg subtract(BigInteger_eg big)
    {
    }
  
    public BigInteger_eg multiply(BigInteger_eg big)
    {
    }
  
    @Override
    public String toString()
    {
    }
  
    static BigInteger_eg evaluate(String input) throws IllegalArgumentException
    {
        // implement here
        // parse input
        // using regex is allowed
  
        // One possible implementation
        // BigInteger_eg num1 = new BigInteger_eg(arg1);
        // BigInteger_eg num2 = new BigInteger_eg(arg2);
        // BigInteger_eg result = num1.add(num2);
        // return result;
    }
  
    public static void main(String[] args) throws Exception
    {
        try (InputStreamReader isr = new InputStreamReader(System.in))
        {
            try (BufferedReader reader = new BufferedReader(isr))
            {
                boolean done = false;
                while (!done)
                {
                    String input = reader.readLine();
  
                    try
                    {
                        done = processInput(input);
                    }
                    catch (IllegalArgumentException e)
                    {
                        System.err.println(MSG_INVALID_INPUT);
                    }
                }
            }
        }
    }
  
    static boolean processInput(String input) throws IllegalArgumentException
    {
        boolean quit = isQuitCmd(input);
  
        if (quit)
        {
            return true;
        }
        else
        {
            BigInteger_eg result = evaluate(input);
            System.out.println(result.toString());
  
            return false;
        }
    }
  
    static boolean isQuitCmd(String input)
    {
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}
