import java.util.List;
import java.util.Objects;

public class CompilationEngine {
    private List<Token> tokens;
    private int currentIndex = 0;
    public CompilationEngine(List<Token> tokens) {
        this.tokens = tokens;
    }

    public String compile() {
        return compileClass();
//        while(hasNext()) {
//            if (token.isTerminal()) {
//                return compileNonTerminal();
//            } else {
//                currentIndex++;
//                return "<" + token.getTokenType().getDisplayName() + ">" + token.getToken() + "</" + token.getTokenType().getDisplayName() + ">" + "\n";
//            }
//        }
    }

    public void advance(){
        currentIndex++;
    }

    public boolean hasNext(){
        return currentIndex < tokens.size();
    }

    public String getNextToken(){
        System.out.println("Getting next token => " + tokens.get(currentIndex).getToken());
        String tok  =  tokens.get(currentIndex).getToken();
        advance();
        return tok;
    }

    public String peek(){
        System.out.println("Peeking at index:"+currentIndex + " found => " + tokens.get(currentIndex).getToken());
        return tokens.get(currentIndex).getToken();
    }

    public Token peekToken(){
        System.out.println("Peeking at index:"+currentIndex);
        return tokens.get(currentIndex);
    }
    public String compileClass() {
        //class Game{
        String xml = "<class>\n\t";
        xml += "<keyword>"+getNextToken()+"</keyword>\n\t<identifier>"+getNextToken()+"</identifier>\n\t<symbol>"+getNextToken()+"</symbol>\n";
        while(hasNext()) {
            System.out.println("next token after class Game:"+ peek());
            if (peek().equals("static") || peek().equals("field")) {
                xml += compileClassVarDec();
            }else if(peek().equals("method") || peek().equals("function") || peek().equals("constructor")) {
                System.out.println("Now compiling subroutineDec: "+peek());
                xml += compileSubroutineDec();
            }else{
                //} for class close
                xml += "<symbol>"+ getNextToken()+ "</symbol>\n</class>";
            }
        }
        return xml;
    }

    public String compileClassVarDec() {
        //field Ball ball,ball1;
        StringBuilder xml = new StringBuilder("<classVarDec>\n\t");
        //field | static
        xml.append("<keyword>" + getNextToken() + "</keyword>\n\t");
        //type
        xml = pushType(xml);
        //varName
        xml.append("<identifier>" + getNextToken() + "</identifier>\n\t");
        while(peek().equals(",")){
            xml.append("<symbol>" + getNextToken() + "</symbol>\n\t");
            xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        }
        //;
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n</classVarDec>\n");
        return xml.toString();
    }

    public String compileSubroutineDec() {
        //constructor(keyword) Ball(keyword) new(identifier) '('(symbol) parameterList  ')' (symbol) {(symbol)
        StringBuilder xml = new StringBuilder("<subroutineDec>\n\t");
        //constructor | method | function
        xml.append("<keyword>").append(getNextToken()).append("</keyword>\n\t");
        //(void | type)
        xml = pushType(xml);
        //new(
        xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t").append("<symbol>").append(getNextToken()).append("</symbol>\n");
        //parameterList )
        System.out.println("=== First parameter list ==== "+ peek());
        xml.append(compileParameterList()).append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        System.out.println("==== Subroutine body now ======");
        xml.append(compileSubroutineBody());
        xml.append("</subroutineDec>\n");
        return xml.toString();
    }

    public String compileParameterList() {
        StringBuilder xml = new StringBuilder("<parameterList>\n\t");
        if(peek().equals(")")){
            return xml.append("</parameterList>\n\t").toString();
        }
        pushType(xml);
        xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        while(peek().equals(",")){
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            xml = pushType(xml);
            xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        }
        xml.append("</parameterList>\n\t").toString();
       return xml.toString();
    }
    public StringBuilder pushType(StringBuilder xml){
        if(JackConstants.isKeyword(peek())){
            xml.append("<keyword>").append(getNextToken()).append("</keyword>\n\t");
        }else{
            xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        }
        return xml;
    }
    public String compileSubroutineBody() {
        //{
        StringBuilder xml = new StringBuilder("<subroutineBody>\n\t<symbol>").append(getNextToken()).append("</symbol>\n\t");
        System.out.println("==== starting with variables ====");
        while(peek().equals("var")){
            xml.append(compileVarDec());
        }
        System.out.println("==== Now onto statements ====");
        xml.append(compileStatements());
        //}
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        xml.append("</subroutineBody>\n");
        return xml.toString();
    }

    public String compileVarDec() {

        StringBuilder xml = new StringBuilder("<varDec>\n\t");
        //var
        xml.append("<keyword>").append(getNextToken()).append("</keyword>\n\t");
        //first var type a
        xml = pushType(xml);
        xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        //, var b, var c
        while(peek().equals(",")){
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        }
        //;
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n");
        //close
        xml.append("</varDec>\n\t");
        return xml.toString();
    }

    public String compileStatements() {
         if(peek().equals("}")){
             return "";
         }
         StringBuilder xml = new StringBuilder("<statements>\n\t");
         while(!peek().equals("}")){
             System.out.println("=== Another statement ====\n"+ peek());
             switch(peek()){
                 case "let":
                     System.out.println("Compiling letstatement:"+ peek());
                     xml.append(compileLetStatement());
                     break;
                 case "if":
                     System.out.println("Compiling ifstatement:"+ peek());
                     xml.append(compileIfOrWhileStatement());
                     break;
                 case "while":
                     System.out.println("Compiling whileStatement:"+ peek());
                     xml.append(compileIfOrWhileStatement());
                     break;
                 case "do":
                     System.out.println("Compiling dostatement:"+ peek());
                     xml.append(compileDoStatement());
                     break;
                 case "return":
                     System.out.println("Compiling returnstatement:"+ peek());
                     xml.append(compileReturnStatement());
                     break;
                 default:
                     System.out.println("This is not handled for some reason:"+peek());
                     throw new RuntimeException("My bad will handle statement type in future!");
             }
         }
         xml.append("</statements>\n");
         return xml.toString();
    }

    public String compileLetStatement(){
        //let varName [expression]? = expression ;
        StringBuilder xml = new StringBuilder("<letStatement>\n\t");
        //let
        xml.append("<keyword>").append(getNextToken()).append("</keyword>\n\t");
        //varName
        xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        //[expression]?
        if(peek().equals("[")){
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            xml.append(compileExpression());
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        }
        //=
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        //expression
        System.out.println("\n======Now compiling expression =====\n");
        xml.append(compileExpression());
        //;
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        xml.append("</letStatement>\n");
        return xml.toString();
    }

    public  String compileIfOrWhileStatement(){
        //if(expression){ statements }
        boolean isIf= peek().equals("if");
        StringBuilder xml = new StringBuilder(isIf ? "<ifStatement>\n\t":"<whileStatement>\n\t");
        //if or while
        xml.append("<keyword>").append(getNextToken()).append("</keyword>\n\t");
        //(
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        xml.append(compileExpression());
        //)
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        //{
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        xml.append(compileStatements());
        //}
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");

        if(isIf && peek().equals("else")){
            //else
            xml.append("<keyword>").append(getNextToken()).append("</keyword>\n\t");
            //{
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            xml.append(compileStatements());
            //}
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        }
        xml.append(isIf ? "</ifStatement>\n\t" : "<whileStatement>\n\t");
        return xml.toString();
    }

    public String compileDoStatement(){
        StringBuilder xml = new StringBuilder("<doStatement>\n\t");
        //do
        xml.append("<keyword>").append(getNextToken()).append("</keyword>\n\t");
        //subroutineName or className
        xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        if(peek().equals(".")){
            //.
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            //subroutineName
            xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        }
        //(
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        //expressionList
        if(peek().equals(")")){
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        }else{
            xml.append(compileExpressionList());
            //)
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        }
        //;
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        xml.append("</doStatement>\n");
        return xml.toString();
    }

    public String compileReturnStatement(){
        StringBuilder xml = new StringBuilder("<returnStatement>\n\t");
        //return
        xml.append("<keyword>").append(getNextToken()).append("</keyword>\n\t");
        if(peek().equals(";")){
            //;
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        }else{
            System.out.println(" ===== \nexpression handling in return statement \n ======");
            xml.append(compileExpression());
            //;
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        }
        xml.append("</returnStatement>\n");
        return xml.toString();
    }
   //* : zero or many
    public String compileExpression(){
        //term ( op term)*
        StringBuilder xml = new StringBuilder("<expression>\n\t");
        //term
        xml.append(compileTerm());
        while(JackConstants.isOperator(peek())){
            //op
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            xml.append(compileTerm());
        }
        xml.append("</expression>\n\t");
        return xml.toString();
    }

    public String compileTerm(){
        System.out.println(" ====== \ncompiling term in progress \n ======" + peek());
        StringBuilder xml = new StringBuilder("<term>\n\t");
        if(peekToken().getTokenType() == TokenType.NUMBER_CONSTANT){
            xml.append("<integerConstant>").append(getNextToken()).append("</integerConstant>\n\t");
        }else if(peekToken().getTokenType() == TokenType.STRING_CONSTANT){
            xml.append("<stringConstant>").append(getNextToken()).append("</stringConstant>\n\t");
        }else if(peekToken().getTokenType() == TokenType.KEYWORD){
            xml.append("<keyword>").append(getNextToken()).append("</keyword>\n\t");
        }else if(peekToken().getTokenType() == TokenType.IDENTIFIER){
            //we have cases here: identifier, identifier[expression], identifier(expressionList), identifier.identifier(expressionList)
            xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
            if(peek().equals("[")){
                //[
                xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
                xml.append(compileExpression());
                //]
                xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            }else if(peek().equals("(")){
                //(
                xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
                xml.append(compileExpressionList());
                //)
                xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            }else if(peek().equals(".")){
                //.
                xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
                xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
                //(
                xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
                if(peek().equals(")")){
                    xml.append("<expressionList></expressionList>\n\t");
                    //)
                    xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
                }else {
                    xml.append(compileExpressionList());
                    //)
                    xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
                }
            }
        }else if(Objects.equals(peekToken().getToken(), "-") || Objects.equals(peekToken().getToken(), "~")){
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            xml.append(compileTerm());
        }else if(peek().equals("(")){
            //)
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            xml.append(compileExpression());
            //)
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        } else{
            throw new RuntimeException("My bad will handle expression's syntax token in future!");
        }
        xml.append("</term>\n\t");
        return xml.toString();
    }

    public String compileExpressionList(){
        StringBuilder xml = new StringBuilder("<expressionList>\n\t");
        xml.append(compileExpression());
         while (peek().equals(",")){
            //,
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            xml.append(compileExpression());
        };
        xml.append("</expressionList>\n\t");
        return xml.toString();
    }
}

