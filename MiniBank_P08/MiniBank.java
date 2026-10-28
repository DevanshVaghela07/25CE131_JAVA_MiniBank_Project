import java.math.BigDecimal;
import java.util.Scanner;

class BankException extends Exception {
    BankException(String message) { super(message); }
}

class InvalidAmountException extends BankException {
    InvalidAmountException() { super("Transaction failed: Amount must be strictly positive."); }
}

class InsufficientFundsException extends BankException {
    InsufficientFundsException() { super("Transaction failed: Insufficient funds in source account."); }
}

class Account {
    final String number;
    private BigDecimal balance;

    Account(String number, String balance) {
        this.number = number;
        this.balance = new BigDecimal(balance);
    }

    synchronized void withdraw(BigDecimal amount) throws BankException {
        if (amount.signum() <= 0) throw new InvalidAmountException();
        if (balance.compareTo(amount) < 0) throw new InsufficientFundsException();
        balance = balance.subtract(amount);
    }

    synchronized void deposit(BigDecimal amount) throws BankException {
        if (amount.signum() <= 0) throw new InvalidAmountException();
        balance = balance.add(amount);
    }

    synchronized BigDecimal balance() { return balance; }

    static void transfer(Account from, Account to, BigDecimal amount) throws BankException {
        System.out.println("Initiating transfer of $" + amount + " from " + from.number + " to " + to.number + "...");

        Account first = from.number.compareTo(to.number) < 0 ? from : to;
        Account second = first == from ? to : from;

        synchronized (first) {
            synchronized (second) {
                BigDecimal oldFrom = from.balance();
                BigDecimal oldTo = to.balance();
                try {
                    from.withdraw(amount);
                    to.deposit(amount);
                    System.out.println("Transfer successful!");
                } catch (BankException | RuntimeException exception) {

                    from.balance = oldFrom;
                    to.balance = oldTo;
                    System.out.println("Rollback executed due to error.");
                    throw exception;
                } finally {
                    System.out.println("Transfer transaction completed.");
                }
            }
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
        System.out.println("=== Practical 08: Custom Exceptions & Atomic Account Transfer ===");

        try (Scanner scanner = new Scanner(System.in)) {
            String balA = prompt(scanner, "Enter initial balance for Account A", "100.00");
            String balB = prompt(scanner, "Enter initial balance for Account B", "20.00");

            Account a = new Account("AccA", balA);
            Account b = new Account("AccB", balB);

            System.out.println("\nInitial State: Account A: $" + a.balance() + " | Account B: $" + b.balance());

            String transStr = prompt(scanner, "\nEnter transfer amount from A to B (e.g. 40 or 150)", "40.00");
            try {
                BigDecimal amount = new BigDecimal(transStr);
                Account.transfer(a, b, amount);
            } catch (BankException exception) {
                System.out.println("CAUGHT EXCEPTION: " + exception.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount format entered.");
            }

            System.out.println("\nFinal State:   Account A: $" + a.balance() + " | Account B: $" + b.balance());
        }
    }
}
