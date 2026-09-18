# Java ATM Interface — Console Application

A console-based ATM machine simulation developed in Java using Object-Oriented Programming (OOP).

This project was created for **Oasis Infobyte Java Development — Task 3** and implements all the required ATM operations along with additional banking features.

## Oasis Infobyte Task 3 — Required Features

- User ID and PIN authentication
- Maximum 3 incorrect login attempts
- Transaction History
- Withdraw money with balance validation
- Deposit money
- Transfer money between accounts
- Balance validation before withdrawal and transfer
- Insufficient Funds handling
- Transactions stored using `ArrayList`
- Multiple Java classes following OOP principles
- Quit option with a goodbye message

## Additional Features

- Check Balance
- Fast Cash for quick withdrawals
- Mini Statement showing the last 5 transactions
- Change PIN
- Unique transaction IDs
- Date and time for transactions
- Formatted transaction receipts
- Daily withdrawal limit of ₹20,000
- Multiple bank accounts
- Account Summary
- Logout option
- Strong input validation
- Self-transfer prevention
- Invalid recipient validation
- Negative and zero amount validation
- Transaction receipt generation
- Data persistence across application restarts

## Technologies Used

- Java
- Object-Oriented Programming
- ArrayList
- HashMap
- File I/O / Serialization
- Java Collections Framework

## Classes

| Class | Responsibility |
|---|---|
| `Main` | Entry point of the application |
| `ATM` | Handles the console interface, login and ATM operations |
| `Bank` | Manages multiple accounts and validates account existence |
| `Account` | Stores account details, balance, PIN and transaction history |
| `Transaction` | Stores transaction details such as ID, type, amount and timestamp |
| `Input` | Handles safe and validated user input |
| `DataStore` | Handles saving and restoring ATM data |

## How to Run

### Requirements

- JDK 8 or later
- Windows operating system for the provided `run.bat` file

### Option 1 — Using run.bat

Double-click:

`run.bat`

The application will compile and start automatically.

### Option 2 — Using the Command Line

Compile:

```bash
javac Main.java ATM.java Bank.java Account.java Transaction.java Input.java DataStore.java
