import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

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
        String tok  =  tokens.get(currentIndex).getToken();
        advance();
        return tok;
    }
    public String peek(){
        System.out.println("Peeking at index:"+currentIndex);
        return tokens.get(currentIndex).getToken();
    }
    public String compileClass() {
        //class Game{
        String xml = "<class>\n\t";
        xml += "<keyword>"+getNextToken()+"</keyword>\n\t<identifier>"+getNextToken()+"</identifier>\n\t<symbol>"+getNextToken()+"</symbol>\n";
        while(hasNext()) {
            System.out.println("next token after class Game:"+ peek());
            if (peek().equals("static") || peek().equals("field")) {
                xml += compileClassVarDec();
            }else{
                xml += compileSubroutineDec();
            }
        }
        xml += "}</class>";
        return xml;
    }

    public String compileClassVarDec() {
        //field Ball ball,ball1;
        StringBuilder xml = new StringBuilder("<classVarDec>\n\t<keyword>" + getNextToken() + "</keyword>\n\t<keyword>" + getNextToken() + "</keyword>\n\t<identifier>" + getNextToken() + "</identifier>\n\t");
        while(peek().equals(",")){
            xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        }
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n</classVarDec>\n");
        return xml.toString();
    }

    public String compileSubroutineDec() {
        //constructor(keyword) Ball(keyword) new(identifier) '('(symbol) parameterList  ')' (symbol) {(symbol)
        StringBuilder xml = new StringBuilder("<subroutineDec>\n\t<keyword>").append(getNextToken()).append("</keyword>\n\t<keyword>").append(getNextToken()).append("</keyword>\n\t");
        //new(
        xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t").append("<symbol>").append(getNextToken()).append("</symbol>\n");
        //parameterList )
        xml.append(compileParamaterList()).append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        xml.append(compileSubroutineBody());
        xml.append("</subroutineDec>\n");
        return xml.toString();
    }

    public String compileParamaterList() {
        StringBuilder xml = new StringBuilder("<paramaterList>\n\t");
        if(peek().equals(")")){
            return xml.append("</paramaterList>\n\t").toString();
        }
        pushType(xml);
        xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        while(peek().equals(",")){
            xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
            xml = pushType(xml);
            xml.append("<identifier>").append(getNextToken()).append("</identifier>\n\t");
        }
        xml.append("</paramaterList>\n\t").toString();
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
        xml.append(compileVarDec());
        xml.append(compileStatements());
        xml.append("<symbol>").append(getNextToken()).append("</symbol>\n\t");
        return xml.toString();
    }

    public String compileVarDec() {
        StringBuilder xml = new StringBuilder("<varDec>\n\t");
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
         StringBuilder xml = new StringBuilder("<statements>\n\t");
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
                 throw new RuntimeException("My bad will handle statement type in future!");
         }
         return xml.toString();
    }
}

