import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public class JackAnalyzer {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java JackAnalyzer <file_or_directory>");
            return;
        }

        Path inputPath = Paths.get(args[0]);

        if (Files.isDirectory(inputPath)) {
            List<Path> jackFiles = Files.walk(inputPath)
                    .filter(path -> path.toString().endsWith(".jack"))
                    .collect(Collectors.toList());

            for (Path jackFile : jackFiles) {
                processJackFile(jackFile);
            }
        } else if (Files.isRegularFile(inputPath) && inputPath.toString().endsWith(".jack")) {
            processJackFile(inputPath);
        } else {
            System.out.println("Invalid file or directory input.");
        }
    }

    private static void processJackFile(Path jackFile) throws IOException {
        JackTokenizer tokenizer = new JackTokenizer(jackFile);
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();

        String outputFileName = jackFile.getFileName().toString().replace(".jack", ".xml");
        Path outputPath = jackFile.getParent().resolve(outputFileName);

        Files.writeString(outputPath, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        CompilationEngine engine = new CompilationEngine(tokens);
        String xmlCode = engine.compile();

        Files.writeString(outputPath, xmlCode, StandardOpenOption.APPEND);
        System.out.println("✅ Created: " + outputPath);
    }
}
