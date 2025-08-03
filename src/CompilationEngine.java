import java.util.List;
import java.util.Objects;

public class CompilationEngine {
    private List<Token> tokens;
    private int currentIndex = 0;
    private int indentLevel = 0;
    private static final String INDENT = "  "; // 2 spaces for indentation
    
    public CompilationEngine(List<Token> tokens) {
        this.tokens = tokens;
    }

    public String compile() {
        return compileClass();
    }

    private String getIndent() {
        return INDENT.repeat(indentLevel);
    }
    
    private String formatElement(String tagName, String content) {
        return getIndent() + "<" + tagName + "> " + content + " </" + tagName + ">\n";
    }
    
    private String openTag(String tagName) {
        String result = getIndent() + "<" + tagName + ">\n";
        indentLevel++;
        return result;
    }
    
    private String closeTag(String tagName) {
        indentLevel--;
        return getIndent() + "</" + tagName + ">\n";
    }
    
    private String emptyElement(String tagName) {
        return getIndent() + "<" + tagName + ">\n" + getIndent() + "</" + tagName + ">\n";
    }

    public void advance(){
        currentIndex++;
    }

    public boolean hasNext(){
        return currentIndex < tokens.size();
    }

    public String getNextToken(){
        String tok = tokens.get(currentIndex).getToken();
        advance();
        return tok;
    }

    public String peek(){
        return tokens.get(currentIndex).getToken();
    }

    public Token peekToken(){
        return tokens.get(currentIndex);
    }
    
    public String compileClass() {
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("class"));
        
        // class keyword
        xml.append(formatElement("keyword", getNextToken()));
        // class name
        xml.append(formatElement("identifier", getNextToken()));
        // {
        xml.append(formatElement("symbol", getNextToken()));
        
        while(hasNext()) {
            if (peek().equals("static") || peek().equals("field")) {
                xml.append(compileClassVarDec());
            } else if(peek().equals("method") || peek().equals("function") || peek().equals("constructor")) {
                xml.append(compileSubroutineDec());
            } else {
                // } for class close
                xml.append(formatElement("symbol", getNextToken()));
                break;
            }
        }
        
        xml.append(closeTag("class"));
        return xml.toString();
    }

    public String compileClassVarDec() {
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("classVarDec"));
        
        // field | static
        xml.append(formatElement("keyword", getNextToken()));
        // type
        xml.append(pushType());
        // varName
        xml.append(formatElement("identifier", getNextToken()));
        
        while(peek().equals(",")){
            xml.append(formatElement("symbol", getNextToken()));
            xml.append(formatElement("identifier", getNextToken()));
        }
        
        // ;
        xml.append(formatElement("symbol", getNextToken()));
        xml.append(closeTag("classVarDec"));
        return xml.toString();
    }

    public String compileSubroutineDec() {
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("subroutineDec"));
        
        // constructor | method | function
        xml.append(formatElement("keyword", getNextToken()));
        // (void | type)
        xml.append(pushType());
        // subroutine name
        xml.append(formatElement("identifier", getNextToken()));
        // (
        xml.append(formatElement("symbol", getNextToken()));
        // parameterList
        xml.append(compileParameterList());
        // )
        xml.append(formatElement("symbol", getNextToken()));
        // subroutine body
        xml.append(compileSubroutineBody());
        
        xml.append(closeTag("subroutineDec"));
        return xml.toString();
    }

    public String compileParameterList() {
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("parameterList"));
        
        if(!peek().equals(")")){
            xml.append(pushType());
            xml.append(formatElement("identifier", getNextToken()));
            
            while(peek().equals(",")){
                xml.append(formatElement("symbol", getNextToken()));
                xml.append(pushType());
                xml.append(formatElement("identifier", getNextToken()));
            }
        }
        
        xml.append(closeTag("parameterList"));
        return xml.toString();
    }
    
    public String pushType(){
        if(JackConstants.isKeyword(peek())){
            return formatElement("keyword", getNextToken());
        } else {
            return formatElement("identifier", getNextToken());
        }
    }
    
    public String compileSubroutineBody() {
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("subroutineBody"));
        
        // {
        xml.append(formatElement("symbol", getNextToken()));
        
        // variable declarations
        while(peek().equals("var")){
            xml.append(compileVarDec());
        }
        
        // statements
        xml.append(compileStatements());
        
        // }
        xml.append(formatElement("symbol", getNextToken()));
        xml.append(closeTag("subroutineBody"));
        return xml.toString();
    }

    public String compileVarDec() {
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("varDec"));
        
        // var
        xml.append(formatElement("keyword", getNextToken()));
        // type
        xml.append(pushType());
        // varName
        xml.append(formatElement("identifier", getNextToken()));
        
        // (, varName)*
        while(peek().equals(",")){
            xml.append(formatElement("symbol", getNextToken()));
            xml.append(formatElement("identifier", getNextToken()));
        }
        
        // ;
        xml.append(formatElement("symbol", getNextToken()));
        xml.append(closeTag("varDec"));
        return xml.toString();
    }

    public String compileStatements() {
        if(peek().equals("}")){
            return "";
        }
        
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("statements"));
        
        while(!peek().equals("}")){
            switch(peek()){
                case "let":
                    xml.append(compileLetStatement());
                    break;
                case "if":
                    xml.append(compileIfStatement());
                    break;
                case "while":
                    xml.append(compileWhileStatement());
                    break;
                case "do":
                    xml.append(compileDoStatement());
                    break;
                case "return":
                    xml.append(compileReturnStatement());
                    break;
                default:
                    throw new RuntimeException("Unexpected statement type: " + peek());
            }
        }
        
        xml.append(closeTag("statements"));
        return xml.toString();
    }

    public String compileLetStatement(){
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("letStatement"));
        
        // let
        xml.append(formatElement("keyword", getNextToken()));
        // varName
        xml.append(formatElement("identifier", getNextToken()));
        
        // [expression]?
        if(peek().equals("[")){
            xml.append(formatElement("symbol", getNextToken()));
            xml.append(compileExpression());
            xml.append(formatElement("symbol", getNextToken()));
        }
        
        // =
        xml.append(formatElement("symbol", getNextToken()));
        // expression
        xml.append(compileExpression());
        // ;
        xml.append(formatElement("symbol", getNextToken()));
        
        xml.append(closeTag("letStatement"));
        return xml.toString();
    }

    public String compileIfStatement(){
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("ifStatement"));
        
        // if
        xml.append(formatElement("keyword", getNextToken()));
        // (
        xml.append(formatElement("symbol", getNextToken()));
        // expression
        xml.append(compileExpression());
        // )
        xml.append(formatElement("symbol", getNextToken()));
        // {
        xml.append(formatElement("symbol", getNextToken()));
        // statements
        xml.append(compileStatements());
        // }
        xml.append(formatElement("symbol", getNextToken()));

        if(peek().equals("else")){
            // else
            xml.append(formatElement("keyword", getNextToken()));
            // {
            xml.append(formatElement("symbol", getNextToken()));
            // statements
            xml.append(compileStatements());
            // }
            xml.append(formatElement("symbol", getNextToken()));
        }
        
        xml.append(closeTag("ifStatement"));
        return xml.toString();
    }
    
    public String compileWhileStatement(){
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("whileStatement"));
        
        // while
        xml.append(formatElement("keyword", getNextToken()));
        // (
        xml.append(formatElement("symbol", getNextToken()));
        // expression
        xml.append(compileExpression());
        // )
        xml.append(formatElement("symbol", getNextToken()));
        // {
        xml.append(formatElement("symbol", getNextToken()));
        // statements
        xml.append(compileStatements());
        // }
        xml.append(formatElement("symbol", getNextToken()));
        
        xml.append(closeTag("whileStatement"));
        return xml.toString();
    }

    public String compileDoStatement(){
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("doStatement"));
        
        // do
        xml.append(formatElement("keyword", getNextToken()));
        // subroutineName or className
        xml.append(formatElement("identifier", getNextToken()));
        
        if(peek().equals(".")){
            // .
            xml.append(formatElement("symbol", getNextToken()));
            // subroutineName
            xml.append(formatElement("identifier", getNextToken()));
        }
        
        // (
        xml.append(formatElement("symbol", getNextToken()));
        // expressionList
        xml.append(compileExpressionList());
        // )
        xml.append(formatElement("symbol", getNextToken()));
        // ;
        xml.append(formatElement("symbol", getNextToken()));
        
        xml.append(closeTag("doStatement"));
        return xml.toString();
    }

    public String compileReturnStatement(){
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("returnStatement"));
        
        // return
        xml.append(formatElement("keyword", getNextToken()));
        
        if(!peek().equals(";")){
            xml.append(compileExpression());
        }
        
        // ;
        xml.append(formatElement("symbol", getNextToken()));
        xml.append(closeTag("returnStatement"));
        return xml.toString();
    }

    public String compileExpression(){
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("expression"));
        
        // term
        xml.append(compileTerm());
        
        while(JackConstants.isOperator(peek())){
            // op
            xml.append(formatElement("symbol", getNextToken()));
            xml.append(compileTerm());
        }
        
        xml.append(closeTag("expression"));
        return xml.toString();
    }

    public String compileTerm(){
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("term"));
        
        if(peekToken().getTokenType() == TokenType.NUMBER_CONSTANT){
            xml.append(formatElement("integerConstant", getNextToken()));
        } else if(peekToken().getTokenType() == TokenType.STRING_CONSTANT){
            xml.append(formatElement("stringConstant", getNextToken()));
        } else if(peekToken().getTokenType() == TokenType.KEYWORD){
            xml.append(formatElement("keyword", getNextToken()));
        } else if(peekToken().getTokenType() == TokenType.IDENTIFIER){
            // identifier cases: identifier, identifier[expression], identifier(expressionList), identifier.identifier(expressionList)
            xml.append(formatElement("identifier", getNextToken()));
            
            if(peek().equals("[")){
                // [
                xml.append(formatElement("symbol", getNextToken()));
                xml.append(compileExpression());
                // ]
                xml.append(formatElement("symbol", getNextToken()));
            } else if(peek().equals("(")){
                // (
                xml.append(formatElement("symbol", getNextToken()));
                xml.append(compileExpressionList());
                // )
                xml.append(formatElement("symbol", getNextToken()));
            } else if(peek().equals(".")){
                // .
                xml.append(formatElement("symbol", getNextToken()));
                xml.append(formatElement("identifier", getNextToken()));
                // (
                xml.append(formatElement("symbol", getNextToken()));
                xml.append(compileExpressionList());
                // )
                xml.append(formatElement("symbol", getNextToken()));
            }
        } else if(Objects.equals(peekToken().getToken(), "-") || Objects.equals(peekToken().getToken(), "~")){
            // unary operators
            xml.append(formatElement("symbol", getNextToken()));
            xml.append(compileTerm());
        } else if(peek().equals("(")){
            // (
            xml.append(formatElement("symbol", getNextToken()));
            xml.append(compileExpression());
            // )
            xml.append(formatElement("symbol", getNextToken()));
        } else {
            throw new RuntimeException("Unexpected token in term: " + peek());
        }
        
        xml.append(closeTag("term"));
        return xml.toString();
    }

    public String compileExpressionList(){
        StringBuilder xml = new StringBuilder();
        xml.append(openTag("expressionList"));
        
        if(!peek().equals(")")){
            xml.append(compileExpression());
            
            while (peek().equals(",")){
                // ,
                xml.append(formatElement("symbol", getNextToken()));
                xml.append(compileExpression());
            }
        }
        
        xml.append(closeTag("expressionList"));
        return xml.toString();
    }
}
