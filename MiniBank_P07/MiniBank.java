import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Scanner;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Id {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Positive {}

class Account {
    @Id
    private final String accountNumber;

    @Positive
    private final BigDecimal balance;

    Account(String number, BigDecimal balance) {
        this.accountNumber = number;
        this.balance = balance;
    }

    String getAccountNumber() { return accountNumber; }
    BigDecimal getBalance() { return balance; }

    @Override
    public String toString() {
        return "Account[id=" + accountNumber + ", balance=" + balance + "]";
    }
}

class AnnotationValidator {
    static boolean report(Object object) throws IllegalAccessException {
        System.out.println("Validating annotations for: " + object);
        boolean allValid = true;

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)) {
                Object val = field.get(object);
                if (val == null || val.toString().trim().isEmpty()) {
                    System.out.println("  [VALIDATION ERROR] Field @" + field.getName() + " (@Id) cannot be empty.");
                    allValid = false;
                } else {
                    System.out.println("  [VALIDATION OK] Field @" + field.getName() + " marked with @Id: " + val);
                }
            }
            if (field.isAnnotationPresent(Positive.class)) {
                BigDecimal value = (BigDecimal) field.get(object);
                if (value == null || value.signum() <= 0) {
                    System.out.println("  [VALIDATION ERROR] Field '" + field.getName() + "' (@Positive) must be > 0. Found: " + value);
                    allValid = false;
                } else {
                    System.out.println("  [VALIDATION OK] Field '" + field.getName() + "' (@Positive) is positive: " + value);
                }
            }
        }
        return allValid;
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
        System.out.println("=== Practical 07: Custom Annotations & Reflection Validator ===");

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\nEnter account details to validate using Reflection:");
            String accId = prompt(scanner, "Enter Account ID", "MB701");
            String balStr = prompt(scanner, "Enter Balance (try positive or negative)", "-25.00");

            BigDecimal bal;
            try {
                bal = new BigDecimal(balStr);
            } catch (Exception e) {
                System.out.println("Invalid numeric input. Setting balance to 0.");
                bal = BigDecimal.ZERO;
            }

            Account userAccount = new Account(accId, bal);
            System.out.println("\n--- Reflection Validation Results ---");
            boolean valid = AnnotationValidator.report(userAccount);
            System.out.println("Overall Validation Result: " + (valid ? "PASSED" : "FAILED"));

            System.out.println("\n--- Benchmark Valid Account Test ---");
            Account validAcc = new Account("MB700", new BigDecimal("150.00"));
            AnnotationValidator.report(validAcc);
        } catch (IllegalAccessException e) {
            System.out.println("Reflection error: " + e.getMessage());
        }
    }
}
