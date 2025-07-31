public class Token {
    private TokenType tokenType;
    private String token;

    public Token(TokenType tokenType, String token) {
        this.tokenType = tokenType;
        this.token = token;
    }

    @Override
    public String toString(){
    System.out.println("Type: " + tokenType + ", Token: " + token);
    return null;
    }
}
