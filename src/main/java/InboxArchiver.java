import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class InboxArchiver {
    public static void archiveTmpFiles(Path inbox, Path archive) throws IOException {
        if (!Files.exists(archive)) {
            Files.createDirectories(archive);
        }

        try (Stream<Path> paths = Files.list(inbox)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".tmp"))
                    .forEach(file -> {
                        try {
                            Path target = archive.resolve(file.getFileName());
                            Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("To archive: " + file.getFileName());
                        } catch (IOException e) {
                            System.err.println("Error with " + file.getFileName() + ": " + e.getMessage());
                        }
                    });
        }
    }
    public static void main(String[] args) {
        try {
            Path inbox = Path.of("practical-data/inbox");
            Path archive = Path.of("practical-data/archive");

            Files.createDirectories(inbox);
            Files.writeString(inbox.resolve("data1.txt"), "some text!!");
            Files.writeString(inbox.resolve("temp_data1.tmp"), "1temp data)");
            Files.writeString(inbox.resolve("temp_data2.tmp"), "2temp data(");

            System.out.println("Before archivation:");
            Files.list(inbox).forEach(System.out::println);

            System.out.println("Start archivation");
            archiveTmpFiles(inbox, archive);

            System.out.println("After archivation (inbox):");
            Files.list(inbox).forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
