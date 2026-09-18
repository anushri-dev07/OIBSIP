import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1001);

    private final String transactionId;
    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime dateTime;
    private final String details;
    private final String status;

    public Transaction(String type, double amount, double balanceAfter, String details) {
        this(type, amount, balanceAfter, details, "SUCCESS");
    }

    public Transaction(String type, double amount, double balanceAfter, String details, String status) {
        this.transactionId = "TXN" + ID_COUNTER.getAndIncrement();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.dateTime = LocalDateTime.now();
        this.details = details;
        this.status = status;
    }

    /**
     * Advances the shared ID counter so new transactions never collide
     * with IDs restored from a saved data file.
     */
    public static void syncCounter(String transactionId) {
        if (transactionId != null && transactionId.startsWith("TXN")) {
            try {
                int num = Integer.parseInt(transactionId.substring(3));
                ID_COUNTER.updateAndGet(cur -> Math.max(cur, num + 1));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDetails() {
        return details;
    }

    public String getStatus() {
        return status;
    }

    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

    @Override
    public String toString() {
        return String.format("%s | %-14s | Amount: %s | Balance After: %s | %s | %s",
                transactionId, type, formatMoney(amount), formatMoney(balanceAfter),
                details, status);
    }

    public String toReceipt(String accountId) {
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("        TRANSACTION RECEIPT\n");
        sb.append("================================\n");
        sb.append(String.format("Transaction ID : %s%n", transactionId));
        sb.append(String.format("Type           : %s%n", type));
        sb.append(String.format("Account        : %s%n", accountId));
        sb.append(String.format("Amount         : %s%n", formatMoney(amount)));
        sb.append(String.format("Date           : %s%n",
                dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
        sb.append(String.format("Status         : %s%n", status));
        sb.append("--------------------------------\n");
        sb.append(String.format("Available Balance: %s%n", formatMoney(balanceAfter)));
        sb.append("================================\n");
        return sb.toString();
    }

    private static String formatMoney(double value) {
        return String.format("Rs.%,.2f", value);
    }
}
