import java.util.Arrays;
import java.util.List;

public class JackConstants {
    private static final List<String> symbols = Arrays.asList(
            "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=", "~"
    );

    private static final List<String> keywords = Arrays.asList(
            "class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean",
            "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return"
    );


    public static List<String> getSymbols() {
        return symbols;
    }
    public static List<String> getKeywords() {
        return keywords;
    }
    public static boolean isSymbol(String symbol){
        return symbols.contains(symbol);
    }
    public static boolean isKeyword(String keyword){
        return keywords.contains(keyword);
    }
}
