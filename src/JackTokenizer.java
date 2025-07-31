import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JackTokenizer {
    private int currentPosition = 0;
    private int currentLine = 0;
    private long totalLines = 0;
    private String currentLineContent ="";
    private Path inputFile;
    private List<Token> tokens = new ArrayList<>();
    public JackTokenizer(Path inputFile) throws IOException {
        this.inputFile = inputFile;
        this.totalLines = Files.lines(inputFile).count();
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void tokenize() {
        try{
            Files.lines(inputFile).forEach((line)->{
                currentLineContent = line;
                resetPosition();
                System.out.println("Tokenize line: " + line);
                System.out.println(currentPosition);
                while(currentPosition < currentLineContent.length()) {
                    char  currentChar = line.charAt(currentPosition);
                    switch (currentChar) {
                        case '\n':
                            System.out.println("Char now is new line:" + currentChar);
                            nextLine();
                            break;
                        case '\r':
                        case '\t':
                        case ' ':
                            System.out.println("Char now is space like:" + currentChar);
                            advance();
                            break;
                        case '"':
                            System.out.println("Char now is start of string:" + currentChar);
                            scanString();
                            break;
                        // JACK symbols
                        case '{':
                        case '}':
                        case '(':
                        case ')':
                        case '[':
                        case ']':
                        case '.':
                        case ',':
                        case ';':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '&':
                        case '|':
                        case '<':
                        case '>':
                        case '=':
                        case '~':
                            System.out.println("Char now is symbol:" + currentChar);
                            advance();
                            scanSymbol();
                            break;
                        default:
                            System.out.println("Char now is either number, keyword or identifier:" + currentChar);
                            if (Character.isLetter(currentChar) || currentChar == '_') {
                                scanKeywordOrIdentifier(); // Use keyword as placeholder but the logic will help differentiate
                            } else if (Character.isDigit(currentChar)) {
                                scanNumber();
                            } else {
                                throw new RuntimeException("Invalid character: " + currentChar);
                            }
                    }
                }

            });
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private void advance(){
        currentPosition++;
        System.out.println("Advancing position: " + currentPosition);
    }

    private void resetPosition(){
        currentPosition = 0;
    }

    private void nextLine(){
        if(currentLine+1 > totalLines){
            throw new RuntimeException("Invalid line increase!");
        }
        currentLine++;
        currentLineContent = "";
        resetPosition();
        System.out.println("Next line: " + currentLine);
    }
    private void scanSymbol() {
        //symbol, keyword, identifier, literal(number, string)
        //Process : first filter out symbols, next keywords, next identifier, then finally check if it's a number(starts with a digit)
        // or if it's a string constant(starts with quotes)
        tokens.add(new Token(TokenType.SYMBOL, String.valueOf(currentLineContent.charAt(currentPosition-1))));
    }
    public char peek(){
        if(currentPosition > currentLineContent.length()){
            throw new RuntimeException("Invalid length access");
        }
        return currentLineContent.charAt(currentPosition);
    }
    private void scanKeywordOrIdentifier(){
      int i = currentPosition;
      StringBuilder tokenValue = new StringBuilder();
      while(i<currentLineContent.length()) {
          if (currentLineContent.charAt(i) != ' ' && !JackConstants.isSymbol(String.valueOf(currentLineContent.charAt(i)))) {
              tokenValue.append(currentLineContent.charAt(i));
              i++;
              advance();
          }else{
              break;
          }
      }
      if(JackConstants.isKeyword(tokenValue.toString())){
          System.out.println("Adding keyword: " + tokenValue);
          tokens.add(new Token(TokenType.KEYWORD, tokenValue.toString()));
      }else{
          System.out.println("Adding identifier: " + tokenValue);
          tokens.add(new Token(TokenType.IDENTIFIER, tokenValue.toString()));
      }
    }
    private void scanNumber(){
     int i = currentPosition;
     StringBuilder tokenValue = new StringBuilder();
     while(i<currentLineContent.length()) {
         if(Character.isDigit(currentLineContent.charAt(i))) {
             System.out.println("appending character:"+currentLineContent.charAt(i));
             tokenValue.append(currentLineContent.charAt(i));
             i++;
             advance();
         }else{
             break;
         }
      }
     tokens.add(new Token(TokenType.NUMBER_CONSTANT, tokenValue.toString()));
    }

    private void scanString(){
        advance();
        String substr  = currentLineContent.substring(currentPosition);
        StringBuilder tokenValue = new StringBuilder();
        for(char c: substr.toCharArray()){
            if(c == '"') {
                break;
            }
            tokenValue.append(c);
            System.out.println("appending character:"+c);
            advance();
        }
        tokens.add(new Token(TokenType.STRING_CONSTANT, tokenValue.toString()));
    }
}
