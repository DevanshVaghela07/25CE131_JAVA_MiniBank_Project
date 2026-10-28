import java.io.*;
import java.nio.file.*;
import java.util.Scanner;
import java.util.stream.Stream;

record Account(String number, double balance) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

public class MiniBank {
    private static String prompt(Scanner scanner, String message, String defaultValue) {
        System.out.print(message + " (default '" + defaultValue + "'): ");
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            return line.isEmpty() ? defaultValue : line;
        }
        System.out.println(defaultValue);
        return defaultValue;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Practical 11: File I/O & Object Serialization ===");

        Path folder = Path.of("minibank-p11");
        Files.createDirectories(folder);

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("\n--- Part 1: Object Serialization ---");
            String accNum = prompt(scanner, "Enter Account Number to save", "MB1101");
            double balance = Double.parseDouble(prompt(scanner, "Enter Balance", "350.0"));

            Account account = new Account(accNum, balance);
            Path stateFile = folder.resolve("account.bin");

            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(stateFile))) {
                out.writeObject(account);
                System.out.println("Serialized Account object written to " + stateFile.toAbsolutePath());
            }

            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(stateFile))) {
                Account reloaded = (Account) in.readObject();
                System.out.println("Deserialized Account successfully: " + reloaded);
            }

            System.out.println("\n--- Part 2: Transaction Logging with java.nio.file ---");
            String logEntry = prompt(scanner, "Enter transaction record to append (e.g. 'DEPOSIT|100')", "DEPOSIT|100");

            Path logFile = folder.resolve("transactions.log");
            Files.writeString(logFile, logEntry + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Appended entry to " + logFile.getFileName());

            System.out.println("\n--- Current Contents of " + logFile.getFileName() + " ---");
            String allLogs = Files.readString(logFile);
            System.out.print(allLogs);

            System.out.println("\nScanning all files in directory:");
            try (Stream<Path> stream = Files.walk(folder)) {
                stream.filter(Files::isRegularFile).forEach(p -> System.out.println(" - " + p.getFileName()));
            }
        }
    }
}
