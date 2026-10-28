import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Account {
    private double balance;
    private final boolean useSynchronization;
    private final Object lock = new Object();

    Account(double initialBalance, boolean useSynchronization) {
        this.balance = initialBalance;
        this.useSynchronization = useSynchronization;
    }

    void deposit(double amount) {
        if (useSynchronization) {
            synchronized (lock) {
                balance += amount;
            }
        } else {
            // Unsynchronized: thread interference / race condition occurs here
            balance += amount;
        }
    }

    void withdraw(double amount) {
        if (useSynchronization) {
            synchronized (lock) {
                if (balance >= amount) {
                    balance -= amount;
                }
            }
        } else {
            // Unsynchronized: thread interference / race condition occurs here
            if (balance >= amount) {
                balance -= amount;
            }
        }
    }

    double balance() {
        if (useSynchronization) {
            synchronized (lock) {
                return balance;
            }
        } else {
            return balance;
        }
    }
}

class AccountWorker implements Runnable {
    private final Account account;
    private final int operations;

    AccountWorker(Account account, int operations) {
        this.account = account;
        this.operations = operations;
    }

    @Override
    public void run() {
        for (int i = 0; i < operations; i++) {
            account.deposit(1.0);
            account.withdraw(1.0);
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

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Practical 09: Multithreading, Race Conditions & Synchronization ===");

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\nSelect Concurrency Mode to Test:");
            System.out.println("  1: WITHOUT Synchronization (Unsafe - triggers Race Condition / Unsuccessful)");
            System.out.println("  2: WITH Synchronization    (Safe   - thread-safe / Successful)");
            String modeChoice = prompt(scanner, "Enter selection (1 or 2)", "1");

            boolean useSync = "2".equals(modeChoice);

            double initialBalance = Double.parseDouble(prompt(scanner, "Enter initial account balance", "1000.0"));
            int threadCount = Integer.parseInt(prompt(scanner, "Enter number of concurrent threads (2-20)", "10"));
            int opsPerThread = Integer.parseInt(prompt(scanner, "Enter iterations per thread (e.g. 5000-50000)", "10000"));

            threadCount = Math.clamp(threadCount, 2, 20);
            opsPerThread = Math.max(100, opsPerThread);

            Account account = new Account(initialBalance, useSync);

            System.out.printf("%n[SETUP] Mode: %s | %d Threads | %d Iterations/Thread%n",
                    (useSync ? "SYNCHRONIZED (Thread-Safe)" : "UNSYNCHRONIZED (Race Condition Prone)"),
                    threadCount, opsPerThread);
            System.out.println("Initial Balance: $" + initialBalance);
            System.out.println("Starting threads...\n");

            List<Thread> workers = new ArrayList<>();
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < threadCount; i++) {
                Thread worker = new Thread(new AccountWorker(account, opsPerThread), "Worker-" + (i + 1));
                workers.add(worker);
                worker.start();
            }

            for (Thread worker : workers) {
                worker.join();
            }

            long elapsed = System.currentTimeMillis() - startTime;
            double finalBalance = account.balance();

            System.out.println("All threads finished in " + elapsed + " ms.");
            System.out.println("--------------------------------------------------");
            System.out.println("Initial Balance:  $" + initialBalance);
            System.out.println("Final Balance:    $" + finalBalance);
            System.out.println("Discrepancy:      $" + (finalBalance - initialBalance));
            System.out.println("--------------------------------------------------");

            if (finalBalance == initialBalance) {
                System.out.println(">>> RESULT: SUCCESSFUL! <<<");
                System.out.println("Balance is perfectly consistent. Synchronization maintained thread safety.");
            } else {
                System.out.println(">>> RESULT: UNSUCCESSFUL (RACE CONDITION DETECTED)! <<<");
                System.out.println("Data corruption occurred because multiple threads modified the balance simultaneously without locks.");
                System.out.println("To fix this, re-run and select Mode 2 (WITH Synchronization).");
            }
        }
    }
}
