import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class Account implements Comparable<Account> {
    final String number;
    final String owner;
    double balance;

    Account(String number, String owner, double balance) {
        this.number = number;
        this.owner = owner;
        this.balance = balance;
    }

    @Override
    public int compareTo(Account other) {
        return this.number.compareTo(other.number); 
    }

    @Override
    public String toString() {
        return String.format("Account[No: %-8s | Owner: %-12s | Balance: $%.2f]", number, owner, balance);
    }
}

class AccountStore {
    private final Map<String, Account> accounts = new HashMap<>();
    private final List<String> history = new ArrayList<>();
    private final ConcurrentHashMap<String, Account> sharedMap = new ConcurrentHashMap<>();

    void add(Account account) {
        accounts.put(account.number, account);
        sharedMap.put(account.number, account);
    }

    Account get(String number) {
        return accounts.get(number);
    }

    List<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    List<Account> sortedByBalance() {
        return accounts.values().stream()
                .sorted(Comparator.comparingDouble(a -> a.balance))
                .toList();
    }

    void record(String transaction) {
        history.add(transaction);
    }

    List<String> history() {
        return List.copyOf(history);
    }

    int sharedMapSize() {
        return sharedMap.size();
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
        System.out.println("=== Practical 12: Java Collections Framework & Stream Sorting ===");

        AccountStore store = new AccountStore();

        store.add(new Account("MB1201", "Asha", 900.0));
        store.add(new Account("MB1202", "Ravi", 1200.0));
        store.record("MB1201 INITIAL_DEPOSIT 900");
        store.record("MB1202 INITIAL_DEPOSIT 1200");

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.println("\n----------------- Collections Menu -----------------");
                System.out.println("1: Add New Account (HashMap)");
                System.out.println("2: View Accounts Sorted by Account Number (TreeSet / Comparable)");
                System.out.println("3: View Accounts Sorted by Balance (Stream / Comparator)");
                System.out.println("4: Search Account by Number (Map lookup)");
                System.out.println("5: Add & View Transaction History (List & ConcurrentHashMap)");
                System.out.println("0: Exit");
                System.out.print("Enter choice (0-5): ");

                int choice;
                try {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;
                    choice = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid choice. Please enter a number between 0 and 5.");
                    continue;
                }

                switch (choice) {
                    case 1 -> {
                        System.out.println("\n--- Add New Account ---");
                        String accNum = prompt(scanner, "Enter Account Number", "MB1203");
                        String owner = prompt(scanner, "Enter Account Owner", "Kavita");
                        double balance = 500.0;
                        try {
                            balance = Double.parseDouble(prompt(scanner, "Enter Opening Balance", "500.0"));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid balance format, using default 500.0.");
                        }

                        Account account = new Account(accNum, owner, balance);
                        store.add(account);
                        store.record(accNum + " OPEN_ACCOUNT $" + balance);
                        System.out.println("Successfully added: " + account);
                    }
                    case 2 -> {
                        System.out.println("\n--- Accounts Sorted by Account Number (TreeSet / Comparable) ---");
                        TreeSet<Account> byAccNumber = new TreeSet<>(store.getAll());
                        if (byAccNumber.isEmpty()) {
                            System.out.println("No accounts found.");
                        } else {
                            byAccNumber.forEach(acc -> System.out.println("  " + acc));
                        }
                    }
                    case 3 -> {
                        System.out.println("\n--- Accounts Sorted by Balance Ascending (Comparator & Streams) ---");
                        List<Account> byBalance = store.sortedByBalance();
                        if (byBalance.isEmpty()) {
                            System.out.println("No accounts found.");
                        } else {
                            byBalance.forEach(acc -> System.out.println("  " + acc));
                        }
                    }
                    case 4 -> {
                        System.out.println("\n--- Search Account in HashMap ---");
                        String searchNum = prompt(scanner, "Enter Account Number to find", "MB1201");
                        Account found = store.get(searchNum);
                        if (found != null) {
                            System.out.println("Account Found: " + found);
                        } else {
                            System.out.printf("No account found with number '%s'.%n", searchNum);
                        }
                    }
                    case 5 -> {
                        System.out.println("\n--- Transaction History (ArrayList) & Concurrent Storage ---");
                        String newNote = prompt(scanner, "Enter a new transaction log note (or press Enter to skip)", "");
                        if (!newNote.isEmpty()) {
                            store.record(newNote);
                            System.out.println("Recorded transaction log.");
                        }

                        System.out.println("\nAll Transaction Records:");
                        List<String> history = store.history();
                        for (int i = 0; i < history.size(); i++) {
                            System.out.printf("  [%d] %s%n", (i + 1), history.get(i));
                        }
                        System.out.println("ConcurrentHashMap synchronized count: " + store.sharedMapSize());
                    }
                    case 0 -> {
                        System.out.println("Exiting Practical 12. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Unknown option. Please choose between 0 and 5.");
                }
            }
        }
    }
}
