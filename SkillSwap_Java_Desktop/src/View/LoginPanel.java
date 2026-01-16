package View;

import Models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPanel extends JFrame {
    // Components for login
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;
    private JComboBox<String> loginRoleBox;

    // Components for signup
    private JTextField signupNameField;
    private JTextField signupEmailField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmPasswordField;
    private JComboBox<String> signupRoleBox;
    private JTextField signupPhoneField;

    // Panels
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private BackgroundPanel backgroundPanel;

    public LoginPanel() {
        setTitle("SkillSwap SA - Learn. Teach. Earn. Together.");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create background panel
        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // Create card layout for login/signup
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);

        // Create login and signup panels
        JPanel loginPanel = createLoginPanel();
        JPanel signupPanel = createSignupPanel();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(signupPanel, "SIGNUP");

        backgroundPanel.add(mainPanel, BorderLayout.CENTER);
        setContentPane(backgroundPanel);

        // Start with login panel
        cardLayout.show(mainPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Welcome Back!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Center form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(Color.WHITE);
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        loginEmailField = new JTextField(20);
        styleTextField(loginEmailField);
        formPanel.add(loginEmailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(Color.WHITE);
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        loginPasswordField = new JPasswordField(20);
        styleTextField(loginPasswordField);
        formPanel.add(loginPasswordField, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel roleLabel = new JLabel("I want to:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleLabel.setForeground(Color.WHITE);
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        String[] roles = {"Learn Only", "Teach Only", "Learn & Teach"};
        loginRoleBox = new JComboBox<>(roles);
        styleComboBox(loginRoleBox);
        formPanel.add(loginRoleBox, gbc);

        // Forgot password link
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel forgotLabel = new JLabel("<html><u>Forgot password?</u></html>");
        forgotLabel.setForeground(new Color(173, 216, 230));
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(LoginPanel.this,
                        "Reset password feature coming soon!\nContact admin for assistance.",
                        "Forgot Password",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        formPanel.add(forgotLabel, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Login button
        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn, new Color(46, 204, 113));
        loginBtn.addActionListener(e -> handleLogin());
        buttonPanel.add(loginBtn);

        // Switch to signup
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setOpaque(false);
        JLabel noAccountLabel = new JLabel("Don't have an account? ");
        noAccountLabel.setForeground(Color.WHITE);
        JLabel signupLink = new JLabel("<html><u><b>Sign up here</b></u></html>");
        signupLink.setForeground(new Color(255, 215, 0));
        signupLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(mainPanel, "SIGNUP");
            }
        });
        switchPanel.add(noAccountLabel);
        switchPanel.add(signupLink);
        buttonPanel.add(switchPanel);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Header with back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Back button
        JLabel backLabel = new JLabel("← Back to Login");
        backLabel.setForeground(new Color(173, 216, 230));
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(mainPanel, "LOGIN");
            }
        });

        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 215, 0));

        headerPanel.add(backLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Scrollable form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Full Name
        gbc.gridx = 0; gbc.gridy = row;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        signupNameField = new JTextField(20);
        styleTextField(signupNameField);
        formPanel.add(signupNameField, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(Color.WHITE);
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        signupEmailField = new JTextField(20);
        styleTextField(signupEmailField);
        formPanel.add(signupEmailField, gbc);
        row++;

        // Phone
        gbc.gridx = 0; gbc.gridy = row;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        phoneLabel.setForeground(Color.WHITE);
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        signupPhoneField = new JTextField(20);
        styleTextField(signupPhoneField);
        formPanel.add(signupPhoneField, gbc);
        row++;

        // Password
        gbc.gridx = 0; gbc.gridy = row;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(Color.WHITE);
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        signupPasswordField = new JPasswordField(20);
        styleTextField(signupPasswordField);
        formPanel.add(signupPasswordField, gbc);
        row++;

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = row;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmLabel.setForeground(Color.WHITE);
        formPanel.add(confirmLabel, gbc);

        gbc.gridx = 1;
        signupConfirmPasswordField = new JPasswordField(20);
        styleTextField(signupConfirmPasswordField);
        formPanel.add(signupConfirmPasswordField, gbc);
        row++;

        // Role
        gbc.gridx = 0; gbc.gridy = row;
        JLabel roleLabel = new JLabel("I want to:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleLabel.setForeground(Color.WHITE);
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        String[] roles = {"Learn Only", "Teach Only", "Learn & Teach"};
        signupRoleBox = new JComboBox<>(roles);
        styleComboBox(signupRoleBox);
        formPanel.add(signupRoleBox, gbc);
        row++;

        // Terms checkbox
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JCheckBox termsCheck = new JCheckBox("I agree to the Terms & Conditions");
        termsCheck.setOpaque(false);
        termsCheck.setForeground(Color.WHITE);
        termsCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(termsCheck, gbc);
        row++;

        // Subjects (optional)
        gbc.gridx = 0; gbc.gridy = row;
        JLabel subjectsLabel = new JLabel("Subjects (comma separated):");
        subjectsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subjectsLabel.setForeground(Color.WHITE);
        formPanel.add(subjectsLabel, gbc);

        gbc.gridx = 1;
        JTextField subjectsField = new JTextField(20);
        styleTextField(subjectsField);
        formPanel.add(subjectsField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Signup button
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton signupBtn = new JButton("Create Account");
        styleButton(signupBtn, new Color(52, 152, 219));
        signupBtn.addActionListener(e -> handleSignup(subjectsField.getText(), termsCheck.isSelected()));
        buttonPanel.add(signupBtn, BorderLayout.CENTER);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void handleLogin() {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        String selectedRole = (String) loginRoleBox.getSelectedItem();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both email and password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Map UI role to database role
        String dbRole = mapRoleToDB(selectedRole);

        System.out.println("Attempting login for: " + email);
        System.out.println("Selected role (UI): " + selectedRole);
        System.out.println("Database role: " + dbRole);

        try (Connection conn = ConnectionBD.getConnection()) {
            // FIXED: Added phone and subjects to the SELECT query
            String query = "SELECT user_id, full_name, email, role, credit_balance, phone, subjects FROM users WHERE email = ? AND password = ? AND role = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password); // Plain password comparison
            stmt.setString(3, dbRole);

            System.out.println("Executing query: " + query);
            System.out.println("Parameters: email=" + email + ", password=[HIDDEN], role=" + dbRole);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String fullName = rs.getString("full_name");
                double credits = rs.getDouble("credit_balance");
                String role = rs.getString("role");
                String phone = rs.getString("phone");
                String subjects = rs.getString("subjects");
                String userEmail = rs.getString("email"); // Renamed to avoid conflict

                // DEBUG: Print retrieved values
                System.out.println("Retrieved user data:");
                System.out.println("  User ID: " + userId);
                System.out.println("  Full Name: " + fullName);
                System.out.println("  Email: " + userEmail);
                System.out.println("  Role: " + role);
                System.out.println("  Credits: " + credits);
                System.out.println("  Phone: " + phone);
                System.out.println("  Subjects: " + subjects);

                // Check if role is null
                if (role == null) {
                    JOptionPane.showMessageDialog(this,
                            "User role is not set in database. Please contact administrator.",
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create User object
                User user = new User(userId, fullName, userEmail, role, credits);
                user.setPhone(phone);
                user.setSubjects(subjects);

                // DEBUG: Verify User object
                System.out.println("Created User object:");
                System.out.println("  User role: " + user.getRole());
                System.out.println("  Is mentor: " + user.isMentor());
                System.out.println("  Is learner: " + user.isLearner());

                // Show success message
                String message = String.format(
                        "<html><div style='text-align: center;'>"
                                + "<h3>Welcome back, %s!</h3>"
                                + "<p>Role: <b>%s</b></p>"
                                + "<p>Credits: <b>%.2f</b> 🪙</p>"
                                + "</div></html>",
                        fullName, formatRole(role), credits
                );

                JOptionPane.showMessageDialog(this, message, "Login Successful", JOptionPane.INFORMATION_MESSAGE);

                // Open dashboard based on role
                openDashboard(user);

            } else {
                System.out.println("No user found with these credentials");

                // Check if user exists with different role
                String checkQuery = "SELECT role FROM users WHERE email = ? AND password = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, email);
                checkStmt.setString(2, password);
                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next()) {
                    String actualRole = checkRs.getString("role");
                    System.out.println("User exists but with different role: " + actualRole);
                    JOptionPane.showMessageDialog(this,
                            String.format("Account exists but as a %s. Please select '%s' role.",
                                    formatRole(actualRole), mapDBToUIRole(actualRole)),
                            "Wrong Role Selected",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    System.out.println("No user found with this email/password combination");
                    JOptionPane.showMessageDialog(this,
                            "Invalid email or password. Please try again.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(User user) {
        // Debug: Check user object
        System.out.println("Opening dashboard for user:");
        System.out.println("  User object: " + user);
        System.out.println("  User role: " + user.getRole());
        System.out.println("  Is mentor: " + user.isMentor());
        System.out.println("  Is learner: " + user.isLearner());

        this.dispose(); // Close login window

        SwingUtilities.invokeLater(() -> {
            String role = user.getRole();

            // Handle null or empty role
            if (role == null || role.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "User role is not defined. Please contact administrator.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                new LoginPanel().setVisible(true); // Go back to login
                return;
            }

            // Check role and open appropriate dashboard
            if (user.isMentor() && !user.isLearner()) {
                // Mentor only - open mentor dashboard
                System.out.println("Opening Mentor Dashboard");
                new MentorDashboard(user).setVisible(true);
            } else if (user.isLearner() && !user.isMentor()) {
                // Learner only - open learner dashboard
                System.out.println("Opening Learner Dashboard (placeholder)");
                JOptionPane.showMessageDialog(null,
                        "Learner dashboard coming soon!\nFor now, using combined dashboard.",
                        "Coming Soon",
                        JOptionPane.INFORMATION_MESSAGE);
                new CombinedDashboard(user).setVisible(true);
            } else if ("both".equals(role)) {
                // Both roles - open combined dashboard
                System.out.println("Opening Combined Dashboard");
                new CombinedDashboard(user).setVisible(true);
            } else {
                // Unknown role
                System.out.println("Unknown role: " + role);
                JOptionPane.showMessageDialog(null,
                        "Unknown user role: " + role,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                new LoginPanel().setVisible(true);
            }
        });
    }

    private void handleSignup(String subjects, boolean agreedToTerms) {
        // Get form data
        String fullName = signupNameField.getText().trim();
        String email = signupEmailField.getText().trim();
        String phone = signupPhoneField.getText().trim();
        String password = new String(signupPasswordField.getPassword());
        String confirmPassword = new String(signupConfirmPasswordField.getPassword());
        String selectedRole = (String) signupRoleBox.getSelectedItem();
        String dbRole = mapRoleToDB(selectedRole);

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields (Name, Email, Password).",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match. Please try again.",
                    "Password Mismatch",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters long.",
                    "Weak Password",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!agreedToTerms) {
            JOptionPane.showMessageDialog(this,
                    "You must agree to the Terms & Conditions to create an account.",
                    "Terms Not Accepted",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if email already exists
        try (Connection conn = ConnectionBD.getConnection()) {
            // Check for existing email
            String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                        "This email is already registered. Please use a different email or try logging in.",
                        "Email Already Exists",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Insert new user
            String insertQuery = "INSERT INTO users (email, full_name, role, password, phone, subjects, credit_balance) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, email);
            insertStmt.setString(2, fullName);
            insertStmt.setString(3, dbRole);
            insertStmt.setString(4, password); // Store plain password
            insertStmt.setString(5, phone.isEmpty() ? null : phone);
            insertStmt.setString(6, subjects.isEmpty() ? null : subjects);
            insertStmt.setDouble(7, 50.00); // Starting credits

            int rowsAffected = insertStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the new user ID
                String getIdQuery = "SELECT user_id FROM users WHERE email = ?";
                PreparedStatement getIdStmt = conn.prepareStatement(getIdQuery);
                getIdStmt.setString(1, email);
                ResultSet newUserRs = getIdStmt.executeQuery();

                int userId = -1;
                if (newUserRs.next()) {
                    userId = newUserRs.getInt("user_id");
                }

                // Add welcome bonus transaction
                String transactionQuery = "INSERT INTO credit_transactions (user_id, amount, transaction_type, description) VALUES (?, ?, ?, ?)";
                PreparedStatement transStmt = conn.prepareStatement(transactionQuery);
                transStmt.setInt(1, userId);
                transStmt.setDouble(2, 50.00);
                transStmt.setString(3, "bonus");
                transStmt.setString(4, "Welcome bonus for new account");
                transStmt.executeUpdate();

                // Success message
                String message = String.format(
                        "<html><div style='text-align: center;'>"
                                + "<h3>🎉 Welcome to SkillSwap SA!</h3>"
                                + "<p>Account created successfully for <b>%s</b></p>"
                                + "<p>Role: <b>%s</b></p>"
                                + "<p>Starting Credits: <b>50.00</b> 🪙</p>"
                                + "<p>You can now login with your credentials.</p>"
                                + "</div></html>",
                        fullName, formatRole(dbRole)
                );

                JOptionPane.showMessageDialog(this, message, "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

                // Clear form and switch to login
                clearSignupForm();
                cardLayout.show(mainPanel, "LOGIN");

            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to create account. Please try again.",
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSignupForm() {
        signupNameField.setText("");
        signupEmailField.setText("");
        signupPhoneField.setText("");
        signupPasswordField.setText("");
        signupConfirmPasswordField.setText("");
        signupRoleBox.setSelectedIndex(0);
    }

    private String mapRoleToDB(String uiRole) {
        switch (uiRole) {
            case "Learn Only": return "learner";
            case "Teach Only": return "mentor";
            case "Learn & Teach": return "both";
            default: return "learner";
        }
    }

    private String mapDBToUIRole(String dbRole) {
        switch (dbRole) {
            case "learner": return "Learn Only";
            case "mentor": return "Teach Only";
            case "both": return "Learn & Teach";
            default: return "Learn Only";
        }
    }

    private String formatRole(String dbRole) {
        switch (dbRole) {
            case "learner": return "Learner 📚";
            case "mentor": return "Mentor 👨‍🏫";
            case "both": return "Learner & Mentor 🔄";
            default: return dbRole;
        }
    }

    private void openDashboard(int userId, String userName, String role, double credits) {
        // Placeholder for dashboard
        JOptionPane.showMessageDialog(this,
                String.format("Dashboard would open for:\nName: %s\nID: %d\nRole: %s\nCredits: %.2f",
                        userName, userId, role, credits),
                "Opening Dashboard",
                JOptionPane.INFORMATION_MESSAGE);

        // TODO: Implement actual dashboard
        // DashboardFrame dashboard = new DashboardFrame(userId, userName, role, credits);
        // dashboard.setVisible(true);
        // this.dispose();
    }

    // Style methods (same as before)
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(255, 255, 255, 200));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setOpaque(true);
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }

    // Background Panel (same as before)
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        private Image blurredImage;

        public BackgroundPanel() {
            try {
                URL imageUrl = new URL("https://images.unsplash.com/photo-1497633762265-9d179a990aa6?q=80&w=2073&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
                backgroundImage = ImageIO.read(imageUrl);
                blurredImage = blurImage(backgroundImage);
            } catch (IOException e) {
                System.err.println("Could not load background image: " + e.getMessage());
                backgroundImage = createFallbackBackground();
                blurredImage = backgroundImage;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (blurredImage != null) {
                int width = getWidth();
                int height = getHeight();
                g.drawImage(blurredImage, 0, 0, width, height, this);

                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 180),
                        0, height, new Color(25, 52, 65, 220)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);
            }
        }

        private Image blurImage(Image image) {
            BufferedImage bufferedImage = new BufferedImage(
                    image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            int kernelSize = 5;
            float blurStrength = 1/(float)(kernelSize * kernelSize);
            float[] blurKernel = new float[kernelSize * kernelSize];
            java.util.Arrays.fill(blurKernel, blurStrength);

            ConvolveOp blurOp = new ConvolveOp(new Kernel(kernelSize, kernelSize, blurKernel));
            return blurOp.filter(bufferedImage, null);
        }

        private Image createFallbackBackground() {
            BufferedImage image = new BufferedImage(500, 600, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0, 119, 73),
                    500, 600, new Color(255, 184, 28)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, 500, 600);

            g2d.setColor(new Color(220, 36, 31, 50));
            for (int i = 0; i < 500; i += 40) {
                for (int j = 0; j < 600; j += 40) {
                    g2d.fillOval(i, j, 20, 20);
                }
            }

            g2d.dispose();
            return image;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginPanel frame = new LoginPanel();
            frame.setVisible(true);
        });
    }
}