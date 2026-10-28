import java.util.Objects;
import java.util.Scanner;

class Customer implements Cloneable {
    private String name;
    private Address address;

    Customer(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    static class Address implements Cloneable {
        String city;

        Address(String city) {
            this.city = city;
        }

        @Override
        public Address clone() {
            try {
                return (Address) super.clone();
            } catch (CloneNotSupportedException exception) {
                throw new AssertionError(exception);
            }
        }

        @Override
        public String toString() {
            return "Address{city='" + city + "'}";
        }
    }

    void setAddressCity(String newCity) {
        if (address != null) address.city = newCity;
    }

    String getAddressCity() {
        return address == null ? "None" : address.city;
    }

    String getName() { return name; }

    @Override
    public Customer clone() {
        try {
            Customer copy = (Customer) super.clone();
            copy.address = (address == null) ? null : address.clone(); 
            return copy;
        } catch (CloneNotSupportedException exception) {
            throw new AssertionError(exception);
        }
    }

    @Override
    public String toString() {
        return "Customer{name='" + name + "', " + address + "}";
    }
}

class Account {
    private final String accountNumber;

    Account(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "Account{accountNumber='" + accountNumber + "'}";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof Account account && Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
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
        System.out.println("=== Practical 03: Object Cloning, equals() & hashCode() ===");

        try (Scanner scanner = new Scanner(System.in)) {
            
            System.out.println("\n--- Part 1: Account Equality Test ---");
            String acc1Num = prompt(scanner, "Enter first account number", "MB201");
            String acc2Num = prompt(scanner, "Enter second account number", "MB201");

            Account first = new Account(acc1Num);
            Account second = new Account(acc2Num);

            System.out.println("Account 1: " + first + " (hashCode=" + first.hashCode() + ")");
            System.out.println("Account 2: " + second + " (hashCode=" + second.hashCode() + ")");
            System.out.println("Are both accounts equal? " + first.equals(second));

            System.out.println("\n--- Part 2: Deep Copy / Cloning Test ---");
            String custName = prompt(scanner, "Enter Customer Name", "Asha");
            String custCity = prompt(scanner, "Enter Customer City", "Surat");

            Customer original = new Customer(custName, new Customer.Address(custCity));
            System.out.println("Original Customer created: " + original);

            Customer cloned = original.clone();
            System.out.println("Cloned Customer created:   " + cloned);

            String newCity = prompt(scanner, "Enter new city for the Cloned Customer", "Mumbai");
            cloned.setAddressCity(newCity);

            System.out.println("\n--- Result after modifying cloned customer's city ---");
            System.out.println("Original Customer: " + original);
            System.out.println("Cloned Customer:   " + cloned);
            if (!original.getAddressCity().equals(cloned.getAddressCity())) {
                System.out.println("SUCCESS: Original city was preserved! Deep copy confirmed.");
            } else {
                System.out.println("Shallow copy detected.");
            }
        }
    }
}
