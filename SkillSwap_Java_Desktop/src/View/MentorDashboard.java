package View;

import Models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MentorDashboard extends JFrame {
    private User currentUser;
    private JLabel welcomeLabel;
    private JLabel creditLabel;
    private JTable sessionsTable;
    private DefaultTableModel tableModel;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Colors
    private final Color PRIMARY_COLOR = new Color(0, 119, 73); // SA Green
    private final Color SECONDARY_COLOR = new Color(255, 184, 28); // SA Gold
    private final Color ACCENT_COLOR = new Color(220, 36, 31); // SA Red
    private final Color DARK_BLUE = new Color(25, 52, 65);

    public MentorDashboard(User user) {
        this.currentUser = user;
        setTitle("SkillSwap SA - Mentor Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadUpcomingSessions();
        setVisible(true);
    }

    private void initializeUI() {
        // Main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout());

        // Create top navigation bar
        JPanel topPanel = createTopPanel();

        // Create sidebar
        JPanel sidebar = createSidebar();

        // Create main content area with card layout
        cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Create different panels for content
        JPanel dashboardPanel = createDashboardPanel();
        JPanel sessionsPanel = createSessionsPanel();
        JPanel earningsPanel = createEarningsPanel();
        JPanel profilePanel = createProfilePanel();

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(sessionsPanel, "SESSIONS");
        contentPanel.add(earningsPanel, "EARNINGS");
        contentPanel.add(profilePanel, "PROFILE");

        // Add panels to main layout
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BLUE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setPreferredSize(new Dimension(getWidth(), 60));

        // Left side: Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(DARK_BLUE);

        JLabel logoLabel = new JLabel("🎓");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        logoLabel.setForeground(SECONDARY_COLOR);

        JLabel titleLabel = new JLabel("SkillSwap Mentor");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        leftPanel.add(logoLabel);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(titleLabel);

        // Right side: User info and credits
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(DARK_BLUE);

        JPanel creditPanel = new JPanel();
        creditPanel.setBackground(new Color(52, 152, 219));
        creditPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        creditLabel = new JLabel(String.format("💰 %.2f Credits", currentUser.getCreditBalance()));
        creditLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        creditLabel.setForeground(Color.WHITE);
        creditPanel.add(creditLabel);

        JLabel userLabel = new JLabel("👤 " + currentUser.getFullName());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        rightPanel.add(userLabel);
        rightPanel.add(Box.createHorizontalStrut(20));
        rightPanel.add(creditPanel);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)));
        panel.setPreferredSize(new Dimension(220, getHeight()));

        // Sidebar header
        JLabel sidebarHeader = new JLabel("Mentor Tools");
        sidebarHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sidebarHeader.setForeground(DARK_BLUE);
        sidebarHeader.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebarHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(sidebarHeader);
        panel.add(Box.createVerticalStrut(10));

        // Navigation buttons
        String[] menuItems = {
                "📊 Dashboard",
                "📅 My Sessions",
                "💵 Earnings",
                "👤 Profile",
                "➕ Create Session",
                "🔍 Find Learners",
                "🏆 My Badges",
                "⚙️ Settings"
        };

        for (String item : menuItems) {
            JButton menuButton = createMenuButton(item);
            menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(menuButton);
            panel.add(Box.createVerticalStrut(5));
        }

        panel.add(Box.createVerticalGlue());

        // Logout button at bottom
        JButton logoutBtn = new JButton("🚪 Logout");
        styleButton(logoutBtn, ACCENT_COLOR);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> logout());

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.setBackground(new Color(245, 245, 245));
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        logoutPanel.add(logoutBtn);

        panel.add(logoutPanel);

        return panel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(DARK_BLUE);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(220, 45));

        // Make button change content panel
        button.addActionListener(e -> {
            switch (text) {
                case "📊 Dashboard":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "DASHBOARD");
                    break;
                case "📅 My Sessions":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "SESSIONS");
                    loadUpcomingSessions();
                    break;
                case "💵 Earnings":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "EARNINGS");
                    loadEarningsData();
                    break;
                case "👤 Profile":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "PROFILE");
                    loadProfileData();
                    break;
                case "➕ Create Session":
                    openCreateSessionDialog();
                    break;
                case "🔍 Find Learners":
                    openFindLearnersDialog();
                    break;
                case "🏆 My Badges":
                    showBadges();
                    break;
                case "⚙️ Settings":
                    openSettings();
                    break;
            }
        });

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(230, 240, 255));
                button.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, PRIMARY_COLOR));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
            }
        });

        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome message
        welcomeLabel = new JLabel(String.format(
                "<html><div style='text-align: center;'>"
                        + "<h1>Welcome, Mentor %s! 👨‍🏫</h1>"
                        + "<p style='color: #666; font-size: 16px;'>Share your knowledge, earn credits, make an impact!</p>"
                        + "</div></html>",
                currentUser.getFullName().split(" ")[0] // First name only
        ));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Create stat cards
        String[] statTitles = {
                "Upcoming Sessions", "Total Earnings", "Avg. Rating",
                "Learners Helped", "Hours Taught", "Credits Available"
        };

        String[] statValues = {"0", "0.00", "0.0", "0", "0", String.format("%.2f", currentUser.getCreditBalance())};

        for (int i = 0; i < statTitles.length; i++) {
            statsPanel.add(createStatCard(statTitles[i], statValues[i], i));
        }

        panel.add(statsPanel, BorderLayout.CENTER);

        // Quick actions panel
        JPanel actionsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Quick Actions"
        ));

        JButton scheduleBtn = new JButton("📅 Schedule Session");
        JButton viewRequestsBtn = new JButton("👥 View Requests");
        JButton redeemBtn = new JButton("🎁 Redeem Credits");

        styleDashboardButton(scheduleBtn, PRIMARY_COLOR);
        styleDashboardButton(viewRequestsBtn, new Color(52, 152, 219));
        styleDashboardButton(redeemBtn, SECONDARY_COLOR);

        scheduleBtn.addActionListener(e -> openCreateSessionDialog());
        viewRequestsBtn.addActionListener(e -> loadSessionRequests());
        redeemBtn.addActionListener(e -> openRewardsShop());

        actionsPanel.add(scheduleBtn);
        actionsPanel.add(viewRequestsBtn);
        actionsPanel.add(redeemBtn);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        // Load stats from database
        loadDashboardStats();

        return panel;
    }

    private JPanel createStatCard(String title, String value, int index) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Different icons for each card
        String[] icons = {"📅", "💰", "⭐", "👥", "⏱️", "🏦"};
        Color[] colors = {
                new Color(70, 130, 180), // Steel Blue
                new Color(46, 204, 113), // Green
                new Color(155, 89, 182), // Purple
                new Color(241, 196, 15), // Yellow
                new Color(230, 126, 34), // Orange
                new Color(52, 152, 219)  // Blue
        };

        JLabel iconLabel = new JLabel(icons[index]);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        iconLabel.setForeground(colors[index]);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(DARK_BLUE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(valueLabel);
        textPanel.add(titleLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSessionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("My Tutoring Sessions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshBtn = new JButton("🔄 Refresh");
        JButton newSessionBtn = new JButton("➕ New Session");
        JButton pastSessionsBtn = new JButton("📜 Past Sessions");

        styleSmallButton(refreshBtn, new Color(200, 200, 200));
        styleSmallButton(newSessionBtn, PRIMARY_COLOR);
        styleSmallButton(pastSessionsBtn, new Color(52, 152, 219));

        refreshBtn.addActionListener(e -> loadUpcomingSessions());
        newSessionBtn.addActionListener(e -> openCreateSessionDialog());
        pastSessionsBtn.addActionListener(e -> showPastSessions());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(newSessionBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(pastSessionsBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Table for sessions
        String[] columns = {"ID", "Learner", "Subject", "Date & Time", "Duration", "Status", "Credits", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only actions column is editable
            }
        };

        sessionsTable = new JTable(tableModel);
        sessionsTable.setRowHeight(40);
        sessionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sessionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        sessionsTable.getTableHeader().setBackground(DARK_BLUE);
        sessionsTable.getTableHeader().setForeground(Color.WHITE);

        // Add action buttons to table
        sessionsTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        sessionsTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox(), sessionsTable));

        JScrollPane scrollPane = new JScrollPane(sessionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        panel.add(scrollPane, BorderLayout.CENTER);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel infoLabel = new JLabel("💡 Tip: Click on action buttons to manage your sessions");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(infoLabel);

        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEarningsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Earnings & Credits");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);

        panel.add(titleLabel, BorderLayout.NORTH);

        // Placeholder for earnings content
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Total earnings card
        JPanel earningsCard = createEarningsCard();
        contentPanel.add(earningsCard);

        // Recent transactions
        JPanel transactionsPanel = createTransactionsPanel();
        contentPanel.add(transactionsPanel);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEarningsCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("💰 Total Earnings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(DARK_BLUE);

        JLabel amountLabel = new JLabel("R 0.00");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        amountLabel.setForeground(PRIMARY_COLOR);

        JLabel creditLabel = new JLabel(String.format("Equivalent to: %.2f Credits", currentUser.getCreditBalance()));
        creditLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        creditLabel.setForeground(new Color(100, 100, 100));

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        textPanel.setBackground(new Color(240, 248, 255));
        textPanel.add(titleLabel);
        textPanel.add(amountLabel);
        textPanel.add(creditLabel);

        panel.add(textPanel, BorderLayout.WEST);

        // Withdraw button
        JButton withdrawBtn = new JButton("Withdraw Credits");
        styleButton(withdrawBtn, SECONDARY_COLOR);
        withdrawBtn.setPreferredSize(new Dimension(150, 40));
        withdrawBtn.addActionListener(e -> openWithdrawDialog());

        panel.add(withdrawBtn, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Recent Transactions"
        ));

        String[] columns = {"Date", "Description", "Amount", "Type"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        // Add sample data
        String[][] sampleData = {
                {"2024-01-15", "Tutoring Session - Mathematics", "+15.00", "Earned"},
                {"2024-01-14", "Welcome Bonus", "+50.00", "Bonus"},
                {"2024-01-10", "Tutoring Session - Physics", "+15.00", "Earned"}
        };

        for (String[] row : sampleData) {
            model.addRow(row);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);

        panel.add(titleLabel, BorderLayout.NORTH);

        // Profile content
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Left: Profile info
        JPanel infoPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        infoPanel.add(createProfileField("Name:", currentUser.getFullName()));
        infoPanel.add(createProfileField("Email:", currentUser.getEmail()));
        infoPanel.add(createProfileField("Phone:", currentUser.getPhone() != null ? currentUser.getPhone() : "Not set"));
        infoPanel.add(createProfileField("Role:", formatRole(currentUser.getRole())));
        infoPanel.add(createProfileField("Subjects:", currentUser.getSubjects() != null ? currentUser.getSubjects() : "Not specified"));
        infoPanel.add(createProfileField("Member Since:", "2024-01-01"));

        // Right: Edit profile
        JPanel editPanel = new JPanel(new BorderLayout(10, 10));
        editPanel.setBackground(Color.WHITE);
        editPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel editTitle = new JLabel("Edit Profile");
        editTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        editTitle.setForeground(DARK_BLUE);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Update Phone:"));
        JTextField phoneField = new JTextField(currentUser.getPhone());
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Update Subjects:"));
        JTextField subjectsField = new JTextField(currentUser.getSubjects());
        formPanel.add(subjectsField);

        formPanel.add(new JLabel("Bio:"));
        JTextArea bioArea = new JTextArea(3, 20);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        JScrollPane bioScroll = new JScrollPane(bioArea);
        formPanel.add(bioScroll);

        JButton saveBtn = new JButton("Save Changes");
        styleButton(saveBtn, PRIMARY_COLOR);
        saveBtn.addActionListener(e -> updateProfile(phoneField.getText(), subjectsField.getText(), bioArea.getText()));

        editPanel.add(editTitle, BorderLayout.NORTH);
        editPanel.add(formPanel, BorderLayout.CENTER);
        editPanel.add(saveBtn, BorderLayout.SOUTH);

        contentPanel.add(infoPanel);
        contentPanel.add(editPanel);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProfileField(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComp.setForeground(DARK_BLUE);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComp.setForeground(new Color(100, 100, 100));

        panel.add(labelComp, BorderLayout.WEST);
        panel.add(valueComp, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BLUE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(Color.WHITE);

        // Update time every second
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss | EEEE, MMMM dd, yyyy");
            timeLabel.setText(sdf.format(new Date()));
        });
        timer.start();

        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(timeLabel, BorderLayout.EAST);

        return panel;
    }

    // Helper methods
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    private void styleSmallButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void styleDashboardButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    }

    // Database methods
    private void loadDashboardStats() {
        try (Connection conn = ConnectionBD.getConnection()) {
            // Load upcoming sessions count
            String sessionsQuery = "SELECT COUNT(*) FROM sessions WHERE mentor_id = ? AND status = 'scheduled'";
            PreparedStatement stmt = conn.prepareStatement(sessionsQuery);
            stmt.setInt(1, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();

            // Update UI here if needed

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUpcomingSessions() {
        if (tableModel == null) return;

        tableModel.setRowCount(0); // Clear existing rows

        try (Connection conn = ConnectionBD.getConnection()) {
            // Updated query with session_title and skill_name
            String query = "SELECT s.session_id, u.full_name, sk.skill_name, s.session_title, " +
                    "s.scheduled_time, s.duration_minutes, s.status, s.credit_cost " +
                    "FROM sessions s " +
                    "JOIN users u ON s.learner_id = u.user_id " +
                    "LEFT JOIN skills sk ON s.skill_id = sk.skill_id " +
                    "WHERE s.mentor_id = ? AND s.status IN ('scheduled', 'in_progress') " +
                    "ORDER BY s.scheduled_time";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            while (rs.next()) {
                int sessionId = rs.getInt("session_id");
                String learnerName = rs.getString("full_name");
                String skillName = rs.getString("skill_name");
                String sessionTitle = rs.getString("session_title");
                Timestamp scheduledTime = rs.getTimestamp("scheduled_time");
                int duration = rs.getInt("duration_minutes");
                String status = rs.getString("status");
                double credits = rs.getDouble("credit_cost");

                String[] row = {
                        String.valueOf(sessionId),
                        learnerName,
                        skillName != null ? skillName : "General",
                        sdf.format(scheduledTime),
                        duration + " min",
                        status,
                        String.format("%.2f", credits),
                        "Action"
                };

                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading sessions: " + e.getMessage());
        }
    }

    private void loadEarningsData() {
        // Implementation for earnings data
    }

    private void loadProfileData() {
        // Implementation for profile data
    }

    private void loadSessionRequests() {
        // Implementation for session requests
    }

    // Action methods
    private void openCreateSessionDialog() {
        JDialog dialog = new JDialog(this, "Create New Session", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Subject:"));
        JComboBox<String> subjectCombo = new JComboBox<>(new String[]{"Mathematics", "Physics", "English", "Programming"});
        formPanel.add(subjectCombo);

        formPanel.add(new JLabel("Date:"));
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Time:"));
        JTextField timeField = new JTextField("14:00");
        formPanel.add(timeField);

        formPanel.add(new JLabel("Duration (minutes):"));
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(60, 30, 180, 30));
        formPanel.add(durationSpinner);

        formPanel.add(new JLabel("Credits per session:"));
        JSpinner creditSpinner = new JSpinner(new SpinnerNumberModel(15.0, 5.0, 50.0, 5.0));
        formPanel.add(creditSpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createBtn = new JButton("Create Session");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(createBtn, PRIMARY_COLOR);
        styleButton(cancelBtn, ACCENT_COLOR);

        createBtn.addActionListener(e -> {
            // Save session to database
            JOptionPane.showMessageDialog(dialog, "Session created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            loadUpcomingSessions();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(createBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void openFindLearnersDialog() {
        JOptionPane.showMessageDialog(this,
                "Find Learners feature coming soon!\n\n" +
                        "Browse learners looking for mentors in your subjects.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void openRewardsShop() {
        JDialog dialog = new JDialog(this, "Rewards Shop", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("🎁 Redeem Your Credits");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(DARK_BLUE);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel rewardsPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        rewardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        String[][] rewards = {
                {"📱 10GB Mobile Data", "50 Credits"},
                {"📞 R50 Airtime", "30 Credits"},
                {"✏️ School Supplies Pack", "75 Credits"},
                {"🎓 Tutor Pro Badge", "100 Credits"},
                {"📚 Study Materials", "25 Credits"},
                {"☕ Coffee Voucher", "15 Credits"}
        };

        for (String[] reward : rewards) {
            JPanel rewardCard = new JPanel(new BorderLayout(10, 10));
            rewardCard.setBackground(new Color(240, 248, 255));
            rewardCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 220, 240), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));

            JLabel nameLabel = new JLabel(reward[0]);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JLabel costLabel = new JLabel(reward[1]);
            costLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            costLabel.setForeground(SECONDARY_COLOR);

            JButton redeemBtn = new JButton("Redeem");
            styleSmallButton(redeemBtn, PRIMARY_COLOR);
            redeemBtn.addActionListener(e -> {
                if (currentUser.getCreditBalance() >= Double.parseDouble(reward[1].split(" ")[0])) {
                    JOptionPane.showMessageDialog(dialog, "Reward redeemed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Insufficient credits!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            rewardCard.add(nameLabel, BorderLayout.NORTH);
            rewardCard.add(costLabel, BorderLayout.CENTER);
            rewardCard.add(redeemBtn, BorderLayout.SOUTH);

            rewardsPanel.add(rewardCard);
        }

        panel.add(rewardsPanel, BorderLayout.CENTER);

        JLabel balanceLabel = new JLabel(String.format("Your balance: %.2f Credits", currentUser.getCreditBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        balanceLabel.setForeground(PRIMARY_COLOR);
        panel.add(balanceLabel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void openWithdrawDialog() {
        JOptionPane.showMessageDialog(this,
                "Withdrawal feature coming soon!\n\n" +
                        "Convert credits to airtime, data, or bank transfer.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateProfile(String phone, String subjects, String bio) {
        try (Connection conn = ConnectionBD.getConnection()) {
            String query = "UPDATE users SET phone = ?, subjects = ?, bio = ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, phone);
            stmt.setString(2, subjects);
            stmt.setString(3, bio);
            stmt.setInt(4, currentUser.getUserId());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                currentUser.setPhone(phone);
                currentUser.setSubjects(subjects);
                currentUser.setBio(bio);
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage());
        }
    }

    private void showBadges() {
        JDialog dialog = new JDialog(this, "My Badges", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] badges = {"🏆 Expert Tutor", "⭐ 5-Star Rating", "👥 10+ Students",
                "⏱️ 50 Hours", "🚀 Fast Responder", "💡 Creative Teacher"};

        for (String badge : badges) {
            JLabel badgeLabel = new JLabel(badge, SwingConstants.CENTER);
            badgeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            badgeLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            panel.add(badgeLabel);
        }

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void openSettings() {
        JOptionPane.showMessageDialog(this,
                "Settings feature coming soon!\n\n" +
                        "Configure notifications, privacy, and account settings.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPastSessions() {
        // Implementation for past sessions
        JOptionPane.showMessageDialog(this,
                "Past sessions feature coming soon!",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginPanel().setVisible(true);
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

    // Button renderer and editor for table actions
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table; // Add this field

        public ButtonEditor(JCheckBox checkBox, JTable table) { // Add table parameter
            super(checkBox);
            this.table = table; // Store the table reference
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // Handle button click
                if (table != null) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int sessionId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                        String learnerName = (String) tableModel.getValueAt(row, 1);
                        showSessionActions(sessionId, learnerName);
                    }
                }
            }
            isPushed = false;
            return label;
        }
    }
    private void showSessionActions(int sessionId, String learnerName) {
        String[] options = {"Start Session", "Mark as Complete", "Reschedule", "Cancel", "View Details"};
        int choice = JOptionPane.showOptionDialog(this,
                String.format("Session #%d with %s\nWhat would you like to do?", sessionId, learnerName),
                "Session Actions",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0: // Start Session
                JOptionPane.showMessageDialog(this, "Starting session... Meeting link would open.", "Session Started", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 1: // Mark as Complete
                markSessionComplete(sessionId);
                break;
            case 2: // Reschedule
                openMentorRescheduleDialog(sessionId, learnerName);
                break;
            case 3: // Cancel
                cancelSession(sessionId);
                break;
            case 4: // View Details
                viewSessionDetails(sessionId);
                break;
        }
    }

    private void markSessionComplete(int sessionId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark this session as completed?\nThis will award you credits and the learner can leave feedback.",
                "Confirm Completion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Update database
            try (Connection conn = ConnectionBD.getConnection()) {
                String query = "UPDATE sessions SET status = 'completed' WHERE session_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, sessionId);
                stmt.executeUpdate();

                // Award credits
                String creditQuery = "INSERT INTO credit_transactions (user_id, amount, transaction_type, description, session_id) " +
                        "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement creditStmt = conn.prepareStatement(creditQuery);
                creditStmt.setInt(1, currentUser.getUserId());
                creditStmt.setDouble(2, 15.00); // Example credit amount
                creditStmt.setString(3, "earn");
                creditStmt.setString(4, "Completed tutoring session");
                creditStmt.setInt(5, sessionId);
                creditStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Session marked as completed! Credits awarded.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUpcomingSessions();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating session: " + e.getMessage());
            }
        }
    }

    private void openMentorRescheduleDialog(int sessionId, String learnerName) {
        // Similar to learner reschedule but from mentor perspective
        JOptionPane.showMessageDialog(this,
                "Reschedule feature for mentors coming soon!",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelSession(int sessionId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel this session?\nThe learner will be notified and may receive a credit refund.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Update database
            try (Connection conn = ConnectionBD.getConnection()) {
                String query = "UPDATE sessions SET status = 'cancelled' WHERE session_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, sessionId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Session cancelled. Learner has been notified.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                loadUpcomingSessions();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error cancelling session: " + e.getMessage());
            }
        }
    }

    private void viewSessionDetails(int sessionId) {
        try (Connection conn = ConnectionBD.getConnection()) {
            String query = "SELECT s.*, u.full_name as learner_name, sk.skill_name " +
                    "FROM sessions s " +
                    "JOIN users u ON s.learner_id = u.user_id " +
                    "LEFT JOIN skills sk ON s.skill_id = sk.skill_id " +
                    "WHERE s.session_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String details = String.format(
                        "Session Details #%d\n\n" +
                                "Learner: %s\n" +
                                "Subject: %s\n" +
                                "Scheduled: %s\n" +
                                "Duration: %d minutes\n" +
                                "Cost: %.2f credits\n" +
                                "Status: %s\n" +
                                "Description: %s",
                        sessionId,
                        rs.getString("learner_name"),
                        rs.getString("skill_name") != null ? rs.getString("skill_name") : "General",
                        sdf.format(rs.getTimestamp("scheduled_time")),
                        rs.getInt("duration_minutes"),
                        rs.getDouble("credit_cost"),
                        rs.getString("status"),
                        rs.getString("session_description") != null ? rs.getString("session_description") : "No description"
                );

                JOptionPane.showMessageDialog(this, details, "Session Details", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading session details: " + e.getMessage());
        }
    }
}