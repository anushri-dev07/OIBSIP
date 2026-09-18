import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String userId;
    private final String name;
    private String pin;
    private double balance;
    private final ArrayList<Transaction> transactions;
    private double withdrawnToday;
    private LocalDate lastWithdrawalDate;

    public static final double DAILY_WITHDRAWAL_LIMIT = 20000.00;

    public Account(String userId, String name, String pin, double initialBalance) {
        this.userId = userId;
        this.name = name;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        this.withdrawnToday = 0;
        this.lastWithdrawalDate = LocalDate.now();
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public double getWithdrawnToday() {
        resetDailyIfNeeded();
        return withdrawnToday;
    }

    public boolean authenticate(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    public boolean changePin(String currentPin, String newPin) {
        if (!authenticate(currentPin) || newPin == null || !newPin.matches("\\d{4}")) {
            return false;
        }
        this.pin = newPin;
        return true;
    }

    public ArrayList<Transaction> getRecentTransactions(int count) {
        int size = transactions.size();
        int from = Math.max(0, size - count);
        return new ArrayList<>(transactions.subList(from, size));
    }

    private void resetDailyIfNeeded() {
        LocalDate today = LocalDate.now();
        if (!lastWithdrawalDate.equals(today)) {
            withdrawnToday = 0;
            lastWithdrawalDate = today;
        }
    }

    public Transaction deposit(double amount) {
        balance += amount;
        Transaction t = new Transaction("DEPOSIT", amount, balance, "Cash deposited");
        transactions.add(t);
        return t;
    }

    public Transaction withdraw(double amount) {
        if (amount <= 0 || amount > balance
                || withdrawnToday + amount > DAILY_WITHDRAWAL_LIMIT) {
            return null;
        }
        resetDailyIfNeeded();
        if (withdrawnToday + amount > DAILY_WITHDRAWAL_LIMIT) {
            return null;
        }
        balance -= amount;
        withdrawnToday += amount;
        Transaction t = new Transaction("WITHDRAWAL", amount, balance, "Cash withdrawn");
        transactions.add(t);
        return t;
    }

    public Transaction transfer(Account recipient, double amount) {
        if (amount <= 0 || amount > balance
                || recipient == null || recipient.getUserId().equals(this.userId)) {
            return null;
        }
        balance -= amount;
        Transaction t = new Transaction("TRANSFER OUT", amount, balance,
                "To " + recipient.getName() + " (" + recipient.getUserId() + ")");
        transactions.add(t);
        recipient.balance += amount;
        recipient.transactions.add(new Transaction("TRANSFER IN", amount, recipient.balance,
                "From " + this.getName() + " (" + this.getUserId() + ")"));
        return t;
    }

    public double getRemainingDailyWithdrawal() {
        resetDailyIfNeeded();
        return DAILY_WITHDRAWAL_LIMIT - withdrawnToday;
    }

    public String getAccountSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("        ACCOUNT SUMMARY\n");
        sb.append("================================\n");
        sb.append(String.format("Account Holder : %s%n", name));
        sb.append(String.format("Account ID     : %s%n", userId));
        sb.append(String.format("Balance        : %s%n", formatMoney(balance)));
        sb.append(String.format("Transactions   : %d%n", transactions.size()));
        sb.append(String.format("Withdrawn Today: %s%n", formatMoney(withdrawnToday)));
        sb.append(String.format("Daily Limit    : %s%n", formatMoney(DAILY_WITHDRAWAL_LIMIT)));
        sb.append(String.format("Remaining Limit: %s%n", formatMoney(getRemainingDailyWithdrawal())));
        sb.append("================================\n");
        return sb.toString();
    }

    public static String formatMoney(double value) {
        return String.format("Rs.%,.2f", value);
    }
}
