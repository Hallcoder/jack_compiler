import java.util.Objects;

public class Token {
    private TokenType tokenType;
    private String token;
    private boolean isTerminal;

    public Token(TokenType tokenType, String token, boolean isTerminal) {
        this.tokenType = tokenType;
        this.token = token;
        this.isTerminal = isTerminal;
    }


    public String print(int i){
    System.out.println("Index:"+i+", Type: " + tokenType + ", Token: " + token);
    return null;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getToken() {
        if(Objects.equals(token, ">")) return "&lt;";
        if(Objects.equals(token, "<")) return "&gt;";
        if(token.charAt(0) == '"') return "&quot;";
        if(Objects.equals(token, "&")) return "&amp;";
        return token;
    }

    public boolean isTerminal() {
        return isTerminal;
    }
}
