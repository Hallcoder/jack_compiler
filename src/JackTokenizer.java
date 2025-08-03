import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
        AtomicBoolean insideComment = new AtomicBoolean(false);
        try {
            Files.lines(inputFile).forEach((line) -> {
                currentLineContent = line;
                resetPosition();

                // Remove single-line and multi-line comments
                // Handle multi-line comment start or continuation
                if (insideComment.get()) {
                    if (line.contains("*/")) {
                        insideComment.set(false);
                        currentLineContent = line.substring(line.indexOf("*/") + 2); // continue after comment
                        resetPosition();
                    } else {
                        // Entire line is in a comment block
                        return;
                    }
                }

                // Remove single-line comments
                if (currentLineContent.contains("//")) {
                    currentLineContent = currentLineContent.substring(0, currentLineContent.indexOf("//"));
                }

                // Start of a multi-line comment
                if (currentLineContent.contains("/*")) {
                    int startIdx = currentLineContent.indexOf("/*");
                    int endIdx = currentLineContent.indexOf("*/", startIdx + 2);

                    if (endIdx != -1) {
                        // Both /* and */ on same line
                        currentLineContent = currentLineContent.substring(0, startIdx) +
                                currentLineContent.substring(endIdx + 2);
                    } else {
                        insideComment.set(true);
                        currentLineContent = currentLineContent.substring(0, startIdx);
                    }
                }

                // Continue if there's nothing left to tokenize
                if (currentLineContent.trim().isEmpty()) return;

                while (currentPosition < currentLineContent.length()) {
                    char currentChar = currentLineContent.charAt(currentPosition);
                    switch (currentChar) {
                        case '\n':
                            nextLine();
                            break;
                        case '\r':
                        case '\t':
                        case ' ':
                            advance();
                            break;
                        case '"':
                            scanString();
                            break;
                        case '{': case '}': case '(': case ')': case '[': case ']':
                        case '.': case ',': case ';': case '+': case '-': case '*':
                        case '/': case '&': case '|': case '<': case '>': case '=':
                        case '~':
                            advance();
                            scanSymbol();
                            break;
                        default:
                            if (Character.isLetter(currentChar) || currentChar == '_') {
                                scanKeywordOrIdentifier();
                            } else if (Character.isDigit(currentChar)) {
                                scanNumber();
                            } else {
                                throw new RuntimeException("Invalid character: " + currentChar);
                            }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void advance(){
        currentPosition++;
//        System.out.println("Advancing position: " + currentPosition);
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
//        System.out.println("Next line: " + currentLine);
    }
    private void scanSymbol() {
        //symbol, keyword, identifier, literal(number, string)
        //Process : first filter out symbols, next keywords, next identifier, then finally check if it's a number(starts with a digit)
        // or if it's a string constant(starts with quotes)
        String tokenValue  = String.valueOf(currentLineContent.charAt(currentPosition-1));
        tokens.add(new Token(TokenType.SYMBOL, tokenValue,JackConstants.starts_non_terminal(tokenValue)));
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
//          System.out.println("Adding keyword: " + tokenValue);
          tokens.add(new Token(TokenType.KEYWORD, tokenValue.toString(),JackConstants.starts_non_terminal(tokenValue.toString())));
      }else{
//          System.out.println("Adding identifier: " + tokenValue);
          tokens.add(new Token(TokenType.IDENTIFIER, tokenValue.toString(),JackConstants.starts_non_terminal(tokenValue.toString())));
      }
    }
    private void scanNumber(){
     int i = currentPosition;
     StringBuilder tokenValue = new StringBuilder();
     while(i<currentLineContent.length()) {
         if(Character.isDigit(currentLineContent.charAt(i))) {
//             System.out.println("appending character:"+currentLineContent.charAt(i));
             tokenValue.append(currentLineContent.charAt(i));
             i++;
             advance();
         }else{
             break;
         }
      }
     tokens.add(new Token(TokenType.NUMBER_CONSTANT, tokenValue.toString(),JackConstants.starts_non_terminal(tokenValue.toString())));
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
//            System.out.println("appending character:"+c);
            advance();
        }
        advance();
        tokens.add(new Token(TokenType.STRING_CONSTANT, tokenValue.toString(),JackConstants.starts_non_terminal(tokenValue.toString())));
    }

}
