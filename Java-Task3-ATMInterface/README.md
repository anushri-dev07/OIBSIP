# Java ATM Interface — Console Application

A console-based ATM machine simulation built in Java, using Object-Oriented
Design with multiple classes. Meets the Oasis Infobyte Task 3 requirements.

## Oasis Infobyte Task 3 — Required Features
- **User ID + PIN authentication**, access denied after 3 incorrect attempts
- **Transaction History** — log of all transactions in the current session
- **Withdraw** — prompt for amount, validate sufficient balance, update balance, log transaction
- **Deposit** — prompt for amount, update balance, log transaction
- **Transfer** — prompt for recipient account ID + amount, validate balance, update both accounts, log transaction
- **Quit** — goodbye message and exit
- **Balance validation** before any withdrawal/transfer — "Insufficient Funds"
- Transactions stored in an **ArrayList**, displayed in Transaction History
- **5 required classes**: `ATM`, `Account`, `Transaction`, `Bank`, `Main`

## Additional Features
- Check Balance screen
- Fast Cash — quick withdrawals (100, 500, 1000, 2000)
- Mini Statement — shows last 5 transactions
- Change PIN (new menu option)
- Receipts are appended to `receipts.txt` so you can review/print them
- Data persistence — balances and history are saved to `atmdata.dat`,
  so a restart continues where you left off
- Daily withdrawal limit (₹20,000) with per-day tracking
- Unique transaction IDs (e.g. TXN1001) + date/time per transaction
- Formatted receipts after withdrawal/deposit/transfer
- Account Summary (holder, balance, transaction count, daily-limit usage)
- Multiple named accounts managed by the Bank
- Logout (return to login) and Exit
- Proper input validation (negative/zero/invalid amounts rejected, self-transfer blocked)

## Classes
| Class          | Responsibility                                                         |
|----------------|------------------------------------------------------------------------|
| `Main`         | Entry point — starts the ATM                                          |
| `ATM`          | Console UI: login/menu, and runs all user-facing transactions         |
| `Bank`         | Stores multiple accounts in a HashMap, validates account existence    |
| `Account`      | Encapsulates ID, name, PIN, balance, ArrayList history, daily limit   |
| `Transaction`  | Encapsulates ID, type, amount, balance-after, timestamp, status        |
| `Input`        | Helper for safe, validated console number input                        |
| `DataStore`    | Saves/restores the whole Bank to `atmdata.dat` (persistence)          |

## How to Run
Requires JDK 8+.

Compile:
```
javac Main.java ATM.java Bank.java Account.java Transaction.java Input.java DataStore.java
```

Run:
```
java Main
```

To start fresh (reset demo data), delete `atmdata.dat` in this folder.

## Demo Accounts
| User ID | Name    | PIN | Initial Balance |
|---------|---------|-----|-----------------|
| 1001    | Anushri | 1234| ₹5000.00        |
| 1002    | Raj     | 5678| ₹3200.50        |
| 1003    | Priya   | 9012| ₹10000.00       |
| 1004    | Vikram  | 3456| ₹750.25         |
| 1005    | Sneha   | 7890| ₹25000.00       |

## Project Structure
```
ATM Interface/
├── Main.java
├── ATM.java
├── Bank.java
├── Account.java
├── Transaction.java
├── Input.java
├── DataStore.java
├── run.bat
└── README.md
```
