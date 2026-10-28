import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Id {}

class Account {
    @Id
    private final String accountNumber;
    final double balance;

    Account(String number, double balance) {
        this.accountNumber = number;
        this.balance = balance;
    }

    String getAccountNumber() { return accountNumber; }
    double getBalance() { return balance; }

    @Override
    public String toString() {
        return "Account[id=" + accountNumber + ", balance=" + balance + "]";
    }
}

class Repository<T> {
    private final Map<String, T> entries = new HashMap<>();

    void save(T value) {
        entries.put(extractId(value), value);
    }

    @SafeVarargs
    final void saveAll(T... values) {
        for (T value : values) save(value);
    }

    Optional<T> findById(String id) {
        return Optional.ofNullable(entries.get(id));
    }

    List<T> findAll() {
        return List.copyOf(entries.values());
    }

    boolean delete(String id) {
        return entries.remove(id) != null;
    }

    private String extractId(T value) {
        for (Class<?> type = value.getClass(); type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);
                        return Objects.toString(field.get(value));
                    } catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                }
            }
        }
        throw new IllegalArgumentException("No @Id field found on " + value.getClass().getSimpleName());
    }
}

public class MiniBank {

    @SafeVarargs
    static <N extends Number> double average(N... values) {
        if (values.length == 0) throw new IllegalArgumentException("At least one value required.");
        double sum = 0.0;
        for (N value : values) sum += value.doubleValue();
        return sum / values.length;
    }

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
        System.out.println("=== Practical 13: Generics, Varargs & Generic Repository ===");

        Repository<Account> repository = new Repository<>();
        repository.saveAll(new Account("MB1301", 500.0), new Account("MB1302", 900.0));

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("\n--- Part 1: Generic Repository<Account> ---");
            String newId = prompt(scanner, "Enter new Account ID to add", "MB1303");
            double newBal = Double.parseDouble(prompt(scanner, "Enter Account Balance", "1250.0"));

            Account acc = new Account(newId, newBal);
            repository.save(acc);
            System.out.println("Saved into repository: " + acc);

            String searchId = prompt(scanner, "\nEnter Account ID to search", newId);
            Optional<Account> found = repository.findById(searchId);
            if (found.isPresent()) {
                System.out.println("Found: " + found.get());
            } else {
                System.out.println("Account with ID " + searchId + " not found.");
            }

            System.out.println("\nAll Accounts in Repository:");
            for (Account a : repository.findAll()) {
                System.out.println(" - " + a);
            }

            System.out.println("\n--- Part 2: Generic Method average(N... values) ---");
            String numbersInput = prompt(scanner, "Enter numbers separated by space", "10 20.5 35 40");
            try {
                String[] tokens = numbersInput.split("\\s+");
                Double[] numbers = new Double[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    numbers[i] = Double.parseDouble(tokens[i]);
                }
                double avg = average(numbers);
                System.out.printf("Calculated average of %s is: %.2f%n", Arrays.toString(numbers), avg);
            } catch (Exception e) {
                System.out.println("Error parsing numbers, testing default numbers [10, 20, 30]: average = " + average(10, 20, 30));
            }
        }
    }
}
