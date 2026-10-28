import java.math.BigDecimal;
import java.util.Scanner;

interface Transactable {
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount);
}

interface InterestBearing {
    BigDecimal interestRate();
    default BigDecimal yearlyInterest(BigDecimal balance) {
        return balance.signum() > 0 ? balance.multiply(interestRate()) : BigDecimal.ZERO;
    }
}

@FunctionalInterface
interface WithdrawRule {
    boolean permits(BigDecimal resultingBalance);
}

interface Premium {}

class Account implements Transactable, InterestBearing, Premium {
    private BigDecimal balance;
    private final WithdrawRule rule;

    Account(BigDecimal initialBalance, WithdrawRule rule) {
        this.balance = initialBalance;
        this.rule = rule;
    }

    @Override
    public void deposit(BigDecimal amount) {
        if (amount.signum() <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }
        balance = balance.add(amount);
        System.out.println("Deposited $" + amount + ". New Balance: $" + balance);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        if (amount.signum() <= 0) {
            System.out.println("Withdrawal amount must be positive.");
            return;
        }
        BigDecimal result = balance.subtract(amount);
        if (!rule.permits(result)) {
            System.out.println("Withdrawal rule REJECTED transaction! Resulting balance $" + result + " not permitted.");
            return;
        }
        balance = result;
        System.out.println("Withdrawn $" + amount + ". New Balance: $" + balance);
    }

    @Override
    public BigDecimal interestRate() {
        return new BigDecimal("0.05"); 
    }

    BigDecimal balance() {
        return balance;
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
        System.out.println("=== Practical 06: Interfaces, Default Methods & Lambda Rules ===");

        try (Scanner scanner = new Scanner(System.in)) {
            String balStr = prompt(scanner, "Enter initial balance", "100.00");
            BigDecimal initial = new BigDecimal(balStr);

            System.out.println("\nSelect Withdrawal Rule (Lambda Strategy):");
            System.out.println("1: Strict (No overdraft: balance >= 0)");
            System.out.println("2: Flexible (Overdraft allowed down to -100: balance >= -100)");
            String ruleChoice = prompt(scanner, "Enter rule option (1 or 2)", "1");

            WithdrawRule rule = "2".equals(ruleChoice)
                    ? (result -> result.compareTo(new BigDecimal("-100")) >= 0)
                    : (result -> result.signum() >= 0);

            Account account = new Account(initial, rule);
            System.out.println("Account configured. Initial Balance: $" + account.balance());

            String depStr = prompt(scanner, "\nEnter amount to deposit", "50.00");
            account.deposit(new BigDecimal(depStr));

            String withStr = prompt(scanner, "Enter amount to withdraw", "120.00");
            account.withdraw(new BigDecimal(withStr));

            System.out.println("\n--- Final Account Status ---");
            System.out.println("Final Balance:   $" + account.balance());
            System.out.println("Yearly Interest: $" + account.yearlyInterest(account.balance()) + " (at 5% per annum)");
        }
    }
}
