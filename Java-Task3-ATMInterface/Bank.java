import java.util.HashMap;
import java.util.Map;

public class Bank {
    private final HashMap<String, Account> accounts;

    public Bank() {
        accounts = new HashMap<>();
        initializeAccounts();
    }

    public Bank(HashMap<String, Account> existing) {
        this.accounts = existing == null ? new HashMap<>() : existing;
    }

    private void initializeAccounts() {
        addAccount("1001", "Anushri", "1234", 5000.00);
        addAccount("1002", "Raj", "5678", 3200.50);
        addAccount("1003", "Priya", "9012", 10000.00);
        addAccount("1004", "Vikram", "3456", 750.25);
        addAccount("1005", "Sneha", "7890", 25000.00);
    }

    public void addAccount(String userId, String name, String pin, double initialBalance) {
        accounts.put(userId, new Account(userId, name, pin, initialBalance));
    }

    public Account getAccount(String userId) {
        return accounts.get(userId);
    }

    public boolean accountExists(String userId) {
        return accounts.containsKey(userId);
    }

    public HashMap<String, Account> getAllAccounts() {
        return accounts;
    }

    public void displayAccountList() {
        System.out.println("Available recipient accounts:");
        for (Map.Entry<String, Account> entry : accounts.entrySet()) {
            System.out.println("  " + entry.getKey() + " - " + entry.getValue().getName());
        }
    }
}
