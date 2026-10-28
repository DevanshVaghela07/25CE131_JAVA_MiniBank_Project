import java.util.Scanner;
import java.util.regex.Pattern;

enum TransactionType { DEPOSIT, WITHDRAW }

record Command(String accountNumber, TransactionType type, double amount) {}

class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern MOBILE = Pattern.compile("^\\+?[0-9]{10,15}$");

    static boolean email(String s) {
        return s != null && EMAIL.matcher(s).matches();
    }

    static boolean mobile(String s) {
        return s != null && MOBILE.matcher(s).matches();
    }
}

class CommandParser {
    static Command parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty command provided.");
        }
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid format. Expected: <accountNumber> <DEPOSIT|WITHDRAW> <amount>");
        }
        try {
            String acc = parts[0];
            TransactionType type = TransactionType.valueOf(parts[1].toUpperCase());
            double amount = Double.parseDouble(parts[2]);
            if (amount <= 0) {
                throw new IllegalArgumentException("Transaction amount must be positive.");
            }
            return new Command(acc, type, amount);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Command parse error: " + exception.getMessage(), exception);
        }
    }
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

    public static void main(String[] args) {
        System.out.println("=== Practical 04: Regex Validation & Command Parsing ===");

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("\n--- Part 1: Command Parser ---");
            System.out.println("Format expected: <accountNumber> <DEPOSIT|WITHDRAW> <amount>");
            String cmdInput = prompt(scanner, "Enter command to parse", "MB401 DEPOSIT 250.00");
            try {
                Command command = CommandParser.parse(cmdInput);
                System.out.println("Successfully parsed Command: " + command);
            } catch (IllegalArgumentException e) {
                System.out.println("Failed: " + e.getMessage());
            }


            System.out.println("\n--- Part 2: Email Validation ---");
            String emailInput = prompt(scanner, "Enter email address to validate", "user@minibank.com");
            boolean isEmailValid = Validator.email(emailInput);
            System.out.printf("Email '%s' is %s.%n", emailInput, isEmailValid ? "VALID" : "INVALID");


            System.out.println("\n--- Part 3: Mobile Number Validation ---");
            String mobileInput = prompt(scanner, "Enter mobile number to validate", "+919876543210");
            boolean isMobileValid = Validator.mobile(mobileInput);
            System.out.printf("Mobile '%s' is %s.%n", mobileInput, isMobileValid ? "VALID" : "INVALID");
        }
    }
}
