import java.util.Arrays;
import java.util.List;

public class JackConstants {
    private static final List<String> symbols = Arrays.asList(
            "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=", "~"
    );
    private static final List<String> operators = Arrays.asList("+","-","/","*","&gt;","&lt;","&amp;","|","=");

    private static final List<String> keywords = Arrays.asList(
            "class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean",
            "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return"
    );

   private static final List<String> start_non_terminals = Arrays.asList("class", "function", "method", "if", "else", "while", "return", "do","field","var");

   public static boolean starts_non_terminal(String tokenValue){
       return  start_non_terminals.contains(tokenValue);
   }

    public static boolean isSymbol(String symbol){
        return symbols.contains(symbol);
    }

    public static boolean isKeyword(String keyword){
        return keywords.contains(keyword);
    }

    public static boolean isOperator(String operator){
        return operators.contains(operator);
    }
}
