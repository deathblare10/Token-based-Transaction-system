import java.util.*;
import java.time.*;

abstract class User {
    String username;
    String loginPassword;
    String transactionPassword;
    int tokens;
    LocalDateTime lastActivityTime;
    List<Transaction> transactionHistory;
    boolean isLocked;

    User(String username, String loginPassword, String transactionPassword) {
        this.username = username;
        this.loginPassword = loginPassword;
        this.transactionPassword = transactionPassword;
        this.tokens = 100;  // Initial tokens
        this.lastActivityTime = LocalDateTime.now();
        this.transactionHistory = new ArrayList<>();
        this.isLocked = false;
    }

    abstract void displayUserType();

    void resetLastActivityTime() {
        this.lastActivityTime = LocalDateTime.now();
    }

    boolean isInactive() {
        return Duration.between(lastActivityTime, LocalDateTime.now()).getSeconds() > TokenSystem.INACTIVITY_THRESHOLD;
    }
}

class RegularUser extends User {

    RegularUser(String username, String loginPassword, String transactionPassword) {
        super(username, loginPassword, transactionPassword);
    }

    @Override
    void displayUserType() {
        System.out.println("Regular User: " + username);
    }

    void checkBalance() {
        System.out.println("Your token balance: " + tokens);
    }

    void displayTransactionHistory() {
        System.out.println("Transaction History:");
        for (Transaction transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }

    boolean makePayment(Scanner scanner, User recipient) {
        System.out.print("Enter your transaction password for verification: ");
        String transactionPassword = scanner.nextLine();
        if (!this.transactionPassword.equals(transactionPassword)) {
            System.out.println("Incorrect transaction password. Payment canceled.");
            return false;
        }

        System.out.print("Enter the amount of tokens to transfer: ");
        int amount = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (amount <= 0 || amount > this.tokens || amount > TokenSystem.TRANSACTION_LIMIT) {
            System.out.println("Invalid amount, exceeds transaction limit, or insufficient tokens.");
            return false;
        }

        this.tokens -= amount;
        recipient.tokens += amount;

        // Log the transaction
        transactionHistory.add(new Transaction(LocalDateTime.now(), -amount, recipient.username, "Payment", "Successful"));
        recipient.transactionHistory.add(new Transaction(LocalDateTime.now(), amount, this.username, "Payment", "Successful"));

        System.out.println("Payment successful!");
        return true;
    }
}

class AdminUser extends User {

    AdminUser(String username, String loginPassword, String transactionPassword) {
        super(username, loginPassword, transactionPassword);
    }

    @Override
    void displayUserType() {
        System.out.println("Admin User: " + username);
    }

    void modifyTokens(User user, int amount) {
        user.tokens += amount;
        System.out.println("Tokens modified successfully for " + user.username + " by " + amount + " tokens.");
        TokenSystem.logAdminAction(username, "Modified tokens for " + user.username + " by " + amount + " tokens.");
    }

    void deactivateUser(User user) {
        user.isLocked = true;
        System.out.println("User " + user.username + " has been deactivated.");
        TokenSystem.logAdminAction(username, "Deactivated user " + user.username + ".");
    }

    void activateUser(User user) {
        user.isLocked = false;
        System.out.println("User " + user.username + " has been activated.");
        TokenSystem.logAdminAction(username, "Activated user " + user.username + ".");
    }

    void resetPassword(User user) {
        String newTransactionPassword = TokenSystem.promptForPassword(new Scanner(System.in), "Enter a new transaction password for " + user.username + ": ");
        user.transactionPassword = newTransactionPassword;
        System.out.println("Transaction password reset for " + user.username + ".");
        TokenSystem.logAdminAction(username, "Reset transaction password for " + user.username + ".");
    }
}

class Transaction {
    LocalDateTime date;
    int amount;
    String counterpartUsername;
    String type;
    String status;

    Transaction(LocalDateTime date, int amount, String counterpartUsername, String type, String status) {
        this.date = date;
        this.amount = amount;
        this.counterpartUsername = counterpartUsername;
        this.type = type;
        this.status = status;
    }

    @Override
    public String toString() {
        return date + ": " + (amount < 0 ? "Sent " : "Received ") + Math.abs(amount) + " tokens to/from " + counterpartUsername + " | Status: " + status;
    }
}

public class TokenSystem {
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int TRANSACTION_LIMIT = 50;
    public static final int INACTIVITY_THRESHOLD = 100; // in seconds
    public static final int MAX_TRANSACTION_HISTORY = 100;

    private static List<User> users = new ArrayList<>();
    private static User loggedInUser = null;
    private static int failedLoginAttempts = 0;
    private static List<String> adminActionLog = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            // Check for inactivity
            if (loggedInUser != null && loggedInUser.isInactive()) {
                System.out.println("You have been logged out due to inactivity.");
                loggedInUser = null;
            }

            if (loggedInUser == null) {
                System.out.println("\n1. Register\n2. Login\n3. Quit");
            } else if (loggedInUser instanceof AdminUser adminUser) {
                System.out.println("\n1. Modify Tokens\n2. Deactivate User\n3. Activate User\n4. Reset Password\n5. Logout");
            } else {
                System.out.println("\n1. Make Payment\n2. Check Balance\n3. Transaction History\n4. Logout");
            }

            System.out.print("Enter your choice: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please try again.");
                scanner.next();  // Clear invalid input
                continue;
            }

            choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            if (loggedInUser == null) {
                switch (choice) {
                    case 1 -> registerUser(scanner);
                    case 2 -> loginUser(scanner);
                    case 3 -> System.exit(0);
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } else if (loggedInUser instanceof AdminUser adminUser) {
                switch (choice) {
                    case 1 -> adminModifyTokens(scanner, adminUser);
                    case 2 -> adminDeactivateUser(scanner, adminUser);
                    case 3 -> adminActivateUser(scanner, adminUser);
                    case 4 -> adminResetPassword(scanner, adminUser);
                    case 5 -> {
                        System.out.println("Logged out.");
                        loggedInUser = null;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } else if (loggedInUser instanceof RegularUser regularUser) {
                switch (choice) {
                    case 1 -> makePayment(scanner, regularUser);
                    case 2 -> regularUser.checkBalance();
                    case 3 -> regularUser.displayTransactionHistory();
                    case 4 -> {
                        System.out.println("Logged out.");
                        loggedInUser = null;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    private static void registerUser(Scanner scanner) {
        System.out.println("Register User");
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        String loginPassword = promptForPassword(scanner, "Enter your login password: ");
        String transactionPassword = promptForPassword(scanner, "Enter your transaction password: ");

        System.out.print("Are you an admin? (yes/no): ");
        String userType = scanner.nextLine().trim().toLowerCase();

        User newUser;
        if (userType.equals("yes")) {
            newUser = new AdminUser(username, loginPassword, transactionPassword);
        } else {
            newUser = new RegularUser(username, loginPassword, transactionPassword);
        }

        users.add(newUser);
        System.out.println("Registration successful!");
    }

    private static void loginUser(Scanner scanner) {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your login password: ");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.username.equals(username) && user.loginPassword.equals(password)) {
                if (user.isLocked) {
                    System.out.println("This account is locked. Please contact an admin to unlock.");
                    return;
                }
                loggedInUser = user;
                user.resetLastActivityTime();
                System.out.println("Login successful!");
                failedLoginAttempts = 0; // Reset on successful login
                return;
            }
        }
        failedLoginAttempts++;
        System.out.println("Login failed. User not found or incorrect password.");
        if (failedLoginAttempts >= 3) {
            lockUserAccount(username);
        }
    }

    private static void lockUserAccount(String username) {
        User user = users.stream()
                .filter(u -> u.username.equals(username))
                .findFirst()
                .orElse(null);
        if (user != null) {
            user.isLocked = true;
            System.out.println("Account locked due to multiple failed login attempts.");
        }
    }

    private static void makePayment(Scanner scanner, RegularUser sender) {
        System.out.print("Enter recipient's username: ");
        String recipientUsername = scanner.nextLine();

        User recipient = users.stream()
                .filter(u -> u.username.equals(recipientUsername))
                .findFirst()
                .orElse(null);

        if (recipient == null) {
            System.out.println("Recipient not found.");
            return;
        }

        sender.makePayment(scanner, recipient);
    }

    private static void adminModifyTokens(Scanner scanner, AdminUser adminUser) {
        System.out.print("Enter username to modify tokens: ");
        String username = scanner.nextLine();

        User user = users.stream()
                .filter(u -> u.username.equals(username))
                .findFirst()
                .orElse(null);

        if (user != null) {
            System.out.print("Enter the amount of tokens to add (positive) or remove (negative): ");
            int amount = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            adminUser.modifyTokens(user, amount);
        } else {
            System.out.println("User not found.");
        }
    }

    private static void adminDeactivateUser(Scanner scanner, AdminUser adminUser) {
        System.out.print("Enter username to deactivate: ");
        String username = scanner.nextLine();

        User user = users.stream()
                .filter(u -> u.username.equals(username))
                .findFirst()
                .orElse(null);

        if (user != null) {
            adminUser.deactivateUser(user);
        } else {
            System.out.println("User not found.");
        }
    }

    private static void adminActivateUser(Scanner scanner, AdminUser adminUser) {
        System.out.print("Enter username to activate: ");
        String username = scanner.nextLine();

        User user = users.stream()
                .filter(u -> u.username.equals(username))
                .findFirst()
                .orElse(null);

        if (user != null) {
            adminUser.activateUser(user);
        } else {
            System.out.println("User not found.");
        }
    }

    private static void adminResetPassword(Scanner scanner, AdminUser adminUser) {
        System.out.print("Enter username to reset password: ");
        String username = scanner.nextLine();

        User user = users.stream()
                .filter(u -> u.username.equals(username))
                .findFirst()
                .orElse(null);

        if (user != null) {
            adminUser.resetPassword(user);
        } else {
            System.out.println("User not found.");
        }
    }

    static void logAdminAction(String adminUsername, String action) {
        adminActionLog.add(LocalDateTime.now() + " - " + adminUsername + ": " + action);
    }

    public static String promptForPassword(Scanner scanner, String prompt) {  // Changed to public
        String password;
        do {
            System.out.print(prompt);
            password = scanner.nextLine();
            if (!isPasswordStrong(password)) {
                System.out.println("Weak password. Must be at least " + MIN_PASSWORD_LENGTH + " characters with uppercase, lowercase, digit, and special character.");
            }
        } while (!isPasswordStrong(password));
        return password;
    }

    private static boolean isPasswordStrong(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
