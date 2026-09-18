import java.util.Scanner;

/**
 * Small helper for safe, validated console input
 * (rejects non-numeric / negative amounts and re-prompts).
 */
public class Input {
    private static final Scanner SCANNER = new Scanner(System.in);

    private Input() {
    }

    public static String readLine() {
        return SCANNER.nextLine();
    }

    /**
     * Reads a non-negative double. Loops until a valid number is entered.
     * Returns 0 to allow "cancel" via entering 0.
     */
    public static double readNonNegativeAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value < 0) {
                    System.out.println("Amount cannot be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a number.");
            }
        }
    }
}
