import java.nio.file.Path;

public class PathSafety {
    public static Path safeResolve(Path base, String userInput) {
        Path absoluteBase = base.toAbsolutePath().normalize();
        Path resolvedPath = absoluteBase.resolve(userInput).normalize();

        if (!resolvedPath.startsWith(absoluteBase)) {
            throw new IllegalArgumentException("Path is out of base: " + userInput);
        }
        return resolvedPath;
    }
    public static void main(String[] args) {
        Path baseDir = Path.of("/uploads");

        try {
            String safeInput = "reports/2025.txt";
            Path result1 = safeResolve(baseDir, safeInput);
            System.out.println("Success: " + safeInput + " > " + result1);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }

        try {
            String strangeInput = "./../secret.txt";
            Path result2 = safeResolve(baseDir, strangeInput);
            System.out.println("Success: " + strangeInput + " > " + result2);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
