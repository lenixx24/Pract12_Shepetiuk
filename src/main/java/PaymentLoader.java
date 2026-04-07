import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PaymentLoader {
    public record LoadResult(List<Payment> payments, int invalidLines){}
    public static List<Payment> load(Path csv){
        List<Payment> payments=new ArrayList<>();
        try(BufferedReader br = Files.newBufferedReader(csv)){
            String line;
            while((line = br.readLine())!=null){
                if(line.isBlank()) continue;
                String[] parts = line.split(",");
                if(parts.length!=4) continue;
                try{
                String id= parts[0].trim();
                String email = parts[1].trim();
                PaymentStatus status = PaymentStatus.valueOf(parts[2].trim().toUpperCase());
                long cents = Long.parseLong(parts[3].trim());
                payments.add(new Payment(id, email, status, cents));
                }
                catch (IllegalArgumentException _){

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return payments;
    }
    public static LoadResult loadWithStats(Path csv){
        List<Payment> payments=new ArrayList<>();
        int invalidLines=0;
        try(BufferedReader br = Files.newBufferedReader(csv)){
            String line;
            while((line = br.readLine())!=null){
                if(line.isBlank()) {
                    invalidLines++;
                    continue;
                }
                String[] parts = line.split(",");
                if(parts.length!=4){
                    invalidLines++;
                    continue;
                }
                try{
                    String id= parts[0].trim();
                    String email = parts[1].trim();
                    PaymentStatus status = PaymentStatus.valueOf(parts[2].trim().toUpperCase());
                    long cents = Long.parseLong(parts[3].trim());
                    payments.add(new Payment(id, email, status, cents));
                }
                catch (IllegalArgumentException e){
                    invalidLines++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new LoadResult(payments, invalidLines);
    }

    static void main() {
        try {
            Path fileCsv = Files.createFile(Path.of("payments.csv"));
            Files.writeString(fileCsv, """
                    id,email,status,amountCents
                    1,john.doe@example.com,PAID,1500
                    2,jane.doe@example.com,NEW,500
                    invalid_row123
                    3,bad.status@example.com,UNKNOWN_STATUS,200
                    4,bob.smith@example.com,FAILED,350
                    
                    """);
            List<Payment> payments = load(fileCsv);
            System.out.println(payments);
            LoadResult result = loadWithStats(fileCsv);
            System.out.println("With stats:");
            System.out.println("Valid: " + result.payments().size());
            System.out.println("Invalid lines: " + result.invalidLines());

            System.out.println("Valid payments");
            result.payments().forEach(System.out::println);
            Files.deleteIfExists(fileCsv);
            Path fileRes = Path.of("report.txt");

           PaymentReportWriter.writeReport(fileRes, result.payments(), result.invalidLines());
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
