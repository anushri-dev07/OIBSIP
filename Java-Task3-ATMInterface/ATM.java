import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Console ATM user interface. Drives the Account/Bank/Transaction logic
 * behind a text menu. Handles authentication (3 attempts), check balance,
 * withdraw, fast cash, deposit, transfer, transaction history, mini
 * statement, account summary, change PIN, logout and exit. Also saves
 * receipts to a file and persists bank data across restarts.
 */
public class ATM {
    private final Bank bank;
    private Account currentAccount;
    private boolean running;

    private static final String RECEIPT_FILE = "receipts.txt";
    public static final int MAX_ATTEMPTS = 3;

    public ATM() {
        Bank loaded = DataStore.load();
        this.bank = loaded != null ? loaded : new Bank();
        this.running = true;
    }

    public void start() {
        printHeader("JAVA ATM SYSTEM", "Welcome!");

        while (running) {
            if (login()) {
                runSession();
            }
            if (running) {
                System.out.println("\nPlease try logging in again.");
            }
        }

        System.out.println("\n========================================");
        System.out.println("  Thank you for using Java ATM!");
        System.out.println("  Goodbye!");
        System.out.println("========================================");
    }

    // ------------------- Authentication -------------------

    private boolean login() {
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            System.out.println("\n--- Login ---");
            System.out.print("Enter User ID: ");
            String userId = Input.readLine().trim();

            if (!bank.accountExists(userId)) {
                attempts++;
                System.out.println("Invalid User ID. Attempts remaining: " + (MAX_ATTEMPTS - attempts));
                continue;
            }

            System.out.print("Enter PIN: ");
            String pin = Input.readLine().trim();

            Account account = bank.getAccount(userId);
            if (account.authenticate(pin)) {
                currentAccount = account;
                System.out.println("\nLogin successful! Welcome, " + account.getName() + "!");
                return true;
            }

            attempts++;
            System.out.println("Incorrect PIN. Attempts remaining: " + (MAX_ATTEMPTS - attempts));
        }

        System.out.println("\nAccount locked after " + MAX_ATTEMPTS + " incorrect attempts.");
        System.out.println("Please contact the bank.");
        return false;
    }

    // ------------------- Main Menu -------------------

    private void runSession() {
        boolean loggedIn = true;

        while (loggedIn) {
            printHeader("ATM SYSTEM", "Welcome, " + currentAccount.getName() + "!");
            System.out.println("  1. Check Balance");
            System.out.println("  2. Withdraw");
            System.out.println("  3. Deposit");
            System.out.println("  4. Transfer");
            System.out.println("  5. Fast Cash");
            System.out.println("  6. Transaction History");
            System.out.println("  7. Mini Statement");
            System.out.println("  8. Account Summary");
            System.out.println("  9. Change PIN");
            System.out.println(" 10. Logout");
            System.out.println(" 11. Exit");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            String choice = Input.readLine().trim();

            switch (choice) {
                case "1":
                    checkBalance();
                    break;
                case "2":
                    withdraw();
                    break;
                case "3":
                    deposit();
                    break;
                case "4":
                    transfer();
                    break;
                case "5":
                    fastCash();
                    break;
                case "6":
                    transactionHistory();
                    break;
                case "7":
                    miniStatement();
                    break;
                case "8":
                    accountSummary();
                    break;
                case "9":
                    changePin();
                    break;
                case "10":
                    loggedIn = false;
                    currentAccount = null;
                    System.out.println("\nLogged out successfully.");
                    break;
                case "11":
                    loggedIn = false;
                    running = false;
                    currentAccount = null;
                    DataStore.save(bank);
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1-11.");
            }
        }
    }

    // ------------------- Operations -------------------

    private void checkBalance() {
        System.out.println("\n========================================");
        System.out.println("          CHECK BALANCE");
        System.out.println("========================================");
        System.out.println("Account : " + currentAccount.getName()
                + " (" + currentAccount.getUserId() + ")");
        System.out.println("Balance : " + Account.formatMoney(currentAccount.getBalance()));
        System.out.println("========================================");
    }

    private void withdraw() {
        System.out.println("\n--- Withdraw ---");
        System.out.println("Available Balance: " + Account.formatMoney(currentAccount.getBalance()));
        System.out.println("Remaining daily withdrawal limit: "
                + Account.formatMoney(currentAccount.getRemainingDailyWithdrawal()));

        double amount = Input.readNonNegativeAmount("Enter amount to withdraw (or 0 to cancel): ");
        if (amount == 0) {
            System.out.println("Withdrawal cancelled.");
            return;
        }
        doWithdrawal(amount);
    }

    private void fastCash() {
        System.out.println("\n--- Fast Cash ---");
        System.out.println("Quick amounts: 100, 500, 1000, 2000");
        System.out.println("Available Balance: " + Account.formatMoney(currentAccount.getBalance()));
        System.out.println("Remaining daily withdrawal limit: "
                + Account.formatMoney(currentAccount.getRemainingDailyWithdrawal()));

        double amount = Input.readNonNegativeAmount("Enter a fast cash amount: ");
        if (amount == 0) {
            System.out.println("Operation cancelled.");
            return;
        }
        doWithdrawal(amount);
    }

    private void doWithdrawal(double amount) {
        if (amount > currentAccount.getBalance()) {
            System.out.println("Insufficient Funds!");
            return;
        }

        if (amount > currentAccount.getRemainingDailyWithdrawal()) {
            System.out.println("Amount exceeds daily withdrawal limit of "
                    + Account.formatMoney(Account.DAILY_WITHDRAWAL_LIMIT) + "!");
            return;
        }

        Transaction txn = currentAccount.withdraw(amount);
        if (txn == null) {
            System.out.println("Withdrawal failed. Please try again.");
            return;
        }
        printReceipt(txn, "Remaining Balance: " + Account.formatMoney(currentAccount.getBalance()));
        DataStore.save(bank);
    }

    private void deposit() {
        System.out.println("\n--- Deposit ---");
        double amount = Input.readNonNegativeAmount("Enter amount to deposit (or 0 to cancel): ");
        if (amount == 0) {
            System.out.println("Deposit cancelled.");
            return;
        }

        Transaction txn = currentAccount.deposit(amount);
        printReceipt(txn, "New Balance: " + Account.formatMoney(currentAccount.getBalance()));
        DataStore.save(bank);
    }

    private void transfer() {
        System.out.println("\n--- Transfer ---");
        System.out.println("Available Balance: " + Account.formatMoney(currentAccount.getBalance()));
        bank.displayAccountList();

        System.out.print("Enter recipient account ID (or 0 to cancel): ");
        String recipientId = Input.readLine().trim();

        if (recipientId.equals("0")) {
            System.out.println("Transfer cancelled.");
            return;
        }

        if (!bank.accountExists(recipientId)) {
            System.out.println("Recipient account not found.");
            return;
        }

        if (recipientId.equals(currentAccount.getUserId())) {
            System.out.println("You cannot transfer to your own account.");
            return;
        }

        double amount = Input.readNonNegativeAmount("Enter amount to transfer (or 0 to cancel): ");
        if (amount == 0) {
            System.out.println("Transfer cancelled.");
            return;
        }

        if (amount > currentAccount.getBalance()) {
            System.out.println("Insufficient Funds!");
            return;
        }

        Account recipient = bank.getAccount(recipientId);
        Transaction txn = currentAccount.transfer(recipient, amount);
        if (txn == null) {
            System.out.println("Transfer failed. Please try again.");
            return;
        }
        printReceipt(txn, "To: " + recipient.getName() + " (" + recipient.getUserId() + ")\n"
                + "New Balance: " + Account.formatMoney(currentAccount.getBalance()));
        DataStore.save(bank);
    }

    private void transactionHistory() {
        System.out.println("\n========================================");
        System.out.println("        TRANSACTION HISTORY");
        System.out.println("========================================");

        if (currentAccount.getTransactions().isEmpty()) {
            System.out.println("  No transactions found.");
        } else {
            int index = 1;
            for (Transaction t : currentAccount.getTransactions()) {
                System.out.println("  " + index + ". " + t);
                index++;
            }
        }
        System.out.println("========================================");
    }

    private void miniStatement() {
        System.out.println("\n========================================");
        System.out.println("          MINI STATEMENT");
        System.out.println("========================================");
        System.out.println("Account : " + currentAccount.getName()
                + " (" + currentAccount.getUserId() + ")");
        System.out.println("----------------------------------------");

        if (currentAccount.getTransactions().isEmpty()) {
            System.out.println("  No transactions yet.");
        } else {
            for (Transaction t : currentAccount.getRecentTransactions(5)) {
                System.out.println("  " + t);
            }
        }
        System.out.println("----------------------------------------");
        System.out.println("Current Balance: "
                + Account.formatMoney(currentAccount.getBalance()));
        System.out.println("========================================");
    }

    private void accountSummary() {
        System.out.println("\n" + currentAccount.getAccountSummary());
    }

    private void changePin() {
        System.out.println("\n--- Change PIN ---");
        System.out.print("Enter current PIN: ");
        String currentPin = Input.readLine().trim();

        System.out.print("Enter new PIN (4 digits): ");
        String newPin = Input.readLine().trim();

        System.out.print("Confirm new PIN: ");
        String confirmPin = Input.readLine().trim();

        if (!newPin.equals(confirmPin)) {
            System.out.println("New PINs do not match. PIN not changed.");
            return;
        }

        if (currentAccount.changePin(currentPin, newPin)) {
            System.out.println("PIN changed successfully.");
            DataStore.save(bank);
        } else {
            System.out.println("PIN change failed. Check that the current PIN is correct "
                    + "and the new PIN has exactly 4 digits.");
        }
    }

    // ------------------- Display helpers -------------------

    private void printReceipt(Transaction txn, String footer) {
        String receipt = txn.toReceipt(currentAccount.getUserId()) + footer + "\n";
        System.out.println("\n" + receipt);
        appendToFile(RECEIPT_FILE, "================================\n" + receipt);
    }

    private void appendToFile(String file, String content) {
        try {
            Files.writeString(Path.of(file), content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Could not write to " + file + ": " + e.getMessage());
        }
    }

    private void printHeader(String title, String subtitle) {
        System.out.println("\n========================================");
        System.out.println("  " + title);
        System.out.println("========================================");
        if (subtitle != null && !subtitle.isEmpty()) {
            System.out.println("  " + subtitle);
            System.out.println("========================================");
        }
    }
}