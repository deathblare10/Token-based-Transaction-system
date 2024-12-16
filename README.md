# Token-based-Transaction-system

### Introduction 
This **Token based Transaction system** project is a robust, Java-based application designed to manage users, track their token balances, and facilitate secure transactions. It provides an interactive command-line interface that supports different roles, including **Regular Users** and **Admin Users**, offering distinct features and functionalities for each. The system emphasizes security, accountability, and ease of use, ensuring a smooth and controlled environment for managing digital tokens.

---

### Key Objectives of the Project

The primary objectives of this project are:

1. **Secure Token Transactions**:
   - Enable users to transfer tokens to others securely.
   - Validate transactions with secure authentication using transaction passwords.

2. **User Role Management**:
   - Differentiate between Regular Users and Admin Users with tailored permissions.
   - Provide administrative capabilities like user account deactivation, token modification, and password resets.

3. **Activity Monitoring**:
   - Track user activity to ensure they remain logged in only while active.
   - Automatically log out inactive users after a defined threshold.

4. **Scalable Design**:
   - Allow for easy extension of features or additional roles in the future.
   - Maintain high standards for code clarity, modularity, and maintainability.

---

### Features of the Token based Transaction system

#### **1. User Roles**
- **Regular Users**:
  - Perform basic operations like checking balances, viewing transaction history, and making payments.
- **Admin Users**:
  - Manage other users by modifying their token balances, deactivating/activating accounts, and resetting transaction passwords.
  - Log all administrative actions for accountability.

#### **2. Authentication and Security**
- **Login Password**:
  - Used for logging into the system, ensuring access is restricted to authorized users.
- **Transaction Password**:
  - Adds an additional layer of security for token transfers.
  - Enforces strong password policies (minimum 8 characters, including uppercase, lowercase, digit, and special character).

#### **3. Token Management**
- Users start with an initial balance of 100 tokens.
- Tokens can be transferred securely between users, with strict validation for amounts and limits.
- Admins can adjust token balances of any user as needed.

#### **4. Inactivity Handling**
- Users are automatically logged out after a period of inactivity (e.g., 100 seconds).
- Regular checks are performed to enforce this feature, ensuring system security.

#### **5. Transaction History**
- Users can view a detailed history of their token transactions, including:
  - Date and time of the transaction.
  - Amount sent or received.
  - Counterparty involved in the transaction.
  - Transaction type (e.g., payment).
  - Status (e.g., successful).

#### **6. Administrative Actions**
Admins can:
- Modify token balances of users.
- Deactivate or activate user accounts.
- Reset transaction passwords for any user.
- Log all actions performed for auditing and accountability.

---

### Key Functional Components

1. **User Management**:
   - Users are managed as objects of either `RegularUser` or `AdminUser` classes.
   - Each user has attributes like `username`, `tokens`, `transaction history`, and `isLocked` status.

2. **Transaction Handling**:
   - Transactions are represented by the `Transaction` class, capturing details such as date, amount, counterpart username, and status.
   - Token transfers involve real-time updates to sender and recipient balances while maintaining logs for future reference.

3. **Authentication**:
   - Login credentials are verified at the time of login.
   - Failed login attempts are monitored, and accounts are locked after multiple failed attempts.

4. **Admin Functions**:
   - Admin users are equipped with additional tools to manage the system.
   - Logs of administrative actions are maintained for review.

5. **Inactivity Logout**:
   - Tracks the last activity time for each logged-in user.
   - Logs out users automatically if they exceed the defined inactivity threshold.

---

### How the System Works

#### **1. User Registration**
- Users register by providing a username, a login password, and a transaction password.
- Password strength is validated to ensure secure credentials.
- Users specify whether they are registering as an Admin or Regular User.

#### **2. Login**
- Users log in by entering their credentials.
- The system verifies the username and password.
- Accounts are locked after multiple failed login attempts for security.

#### **3. Post-Login Options**
- Based on the logged-in user's role:
  - Regular Users can:
    - Make payments by transferring tokens to another user.
    - Check their token balance.
    - View their transaction history.
  - Admin Users can:
    - Modify token balances for any user.
    - Deactivate or activate user accounts.
    - Reset transaction passwords.
    - View and log their administrative actions.

#### **4. Secure Payments**
- Payments require the user to authenticate using their transaction password.
- The system validates the amount against available balance and defined transaction limits.
- Transactions are logged for both sender and recipient.

#### **5. Logout and Inactivity**
- Users can log out manually.
- Inactivity triggers automatic logout after a defined period, maintaining session security.

---

### Example Scenarios

#### **Scenario 1: Regular User**
- A user named Alice registers as a Regular User.
- Alice logs in and transfers 20 tokens to another user, Bob.
- The transaction appears in both Alice's and Bob's transaction histories.

#### **Scenario 2: Admin Actions**
- An admin named John logs in and deactivates Bob's account due to suspected misuse.
- John modifies Alice's token balance by adding 50 tokens as a reward.
- John resets Alice's transaction password at her request.

#### **Scenario 3: Security**
- A malicious user attempts to log in to Bob's account but fails three times.
- Bob's account is locked, and further access attempts are denied until unlocked by an admin.

---

### Advantages of the System

1. **Security**:
   - Robust authentication ensures the safety of user accounts and transactions.
   - Strong password enforcement and inactivity-based auto-logout add layers of protection.

2. **Usability**:
   - Clear, interactive command-line interface guides users through available actions.

3. **Accountability**:
   - Comprehensive logging of transactions and admin actions ensures transparency.

4. **Flexibility**:
   - The design allows for easy extension, including adding new user roles or transaction types.

5. **Scalability**:
   - The modular architecture can support larger user bases with minimal adjustments.

---

This **Token based Transaction system** is ideal for environments requiring secure, token-based interactions, such as employee reward systems, digital currency management, or closed-network marketplaces. It ensures a balance of security, usability, and extensibility, making it a versatile solution for modern token management needs.
