import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentReportWriter {
   public static void writeReport(Path out, List<Payment> payments, int invalidLines) throws IOException {
       long paidTotalCents = payments.stream()
               .filter(p -> p.status() == PaymentStatus.PAID)
               .mapToLong(Payment::amountCents)
               .sum();

       Map<PaymentStatus, Long> statusCounts = payments.stream()
               .collect(Collectors.groupingBy(Payment::status, Collectors.counting()));

       long countNew = statusCounts.getOrDefault(PaymentStatus.NEW, 0L);
       long countPaid = statusCounts.getOrDefault(PaymentStatus.PAID, 0L);
       long countFailed = statusCounts.getOrDefault(PaymentStatus.FAILED, 0L);

       Path parentDir = out.getParent();
       if (parentDir == null) {
           parentDir = Path.of("");
       }
       Path tempFile = Files.createTempFile(parentDir, "report_tmp_", ".txt");

       try {
           try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {

               writer.write("invalidLines=" + invalidLines);
               writer.newLine();

               writer.write("paidTotalCents=" + paidTotalCents);
               writer.newLine();
               writer.write("NEW=" + countNew);
               writer.newLine();
               writer.write("PAID=" + countPaid);
               writer.newLine();
               writer.write("FAILED=" + countFailed);
               writer.newLine();
           }

           Files.move(tempFile, out,
                   StandardCopyOption.REPLACE_EXISTING,
                   StandardCopyOption.ATOMIC_MOVE);

       } catch (IOException e) {
           Files.deleteIfExists(tempFile);
           throw e;
       }
   }
}
