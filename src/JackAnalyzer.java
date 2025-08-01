import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
        tokens.forEach((token) -> {
            token.print(tokens.indexOf(token));
        });
        Path outputFile = Paths.get(inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".xml");
        if(!Files.exists(outputFile)){
            Files.createFile(outputFile);
        }else{
            Files.writeString(outputFile,"",StandardOpenOption.TRUNCATE_EXISTING);
        }
        CompilationEngine compilationEngine = new CompilationEngine(tokens);
        String code = compilationEngine.compile();
        System.out.println("Resulting xml:" + code);
        try {
            Files.write(outputFile, code.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("=== Done with tokenization ===");
        System.out.println("Compilation finished. Yaay!");
    }
}