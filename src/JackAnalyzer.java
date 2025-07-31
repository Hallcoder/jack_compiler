import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class JackAnalyzer {
    public static void main(String[] args) throws IOException {
        Path inputFile = Paths.get(args[0]);
        String inputFileName = inputFile.getFileName().toString();
        JackTokenizer tokenizer = new JackTokenizer(inputFile);
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();
        tokens.forEach(Token::toString);
        System.out.println("=== Done with tokenization ===");
        Path outputFile = Paths.get(inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".xml");
        CompilationEngine compilationEngine = new CompilationEngine(tokens,outputFile);
        compilationEngine.compile();
        System.out.println("Compilation finished. Yaay!");
    }
}