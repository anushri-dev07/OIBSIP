import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Saves and restores the whole Bank (accounts, balances, transactions)
 * to a local file so data survives program restarts.
 */
public class DataStore {
    private static final String FILE = "atmdata.dat";

    private DataStore() {
    }

    /** Returns a restored Bank, or null if no saved data exists / cannot be read. */
    @SuppressWarnings("unchecked")
    public static Bank load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE))) {
            Object obj = in.readObject();
            if (obj instanceof HashMap) {
                HashMap<String, Account> accounts = (HashMap<String, Account>) obj;
                for (Account a : accounts.values()) {
                    for (Transaction t : a.getTransactions()) {
                        Transaction.syncCounter(t.getTransactionId());
                    }
                }
                return new Bank(accounts);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No saved data found - starting with demo accounts.");
        }
        return null;
    }

    public static void save(Bank bank) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))) {
            out.writeObject(bank.getAllAccounts());
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }
}