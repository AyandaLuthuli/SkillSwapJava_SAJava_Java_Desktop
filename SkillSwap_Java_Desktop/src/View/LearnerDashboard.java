package View;

import Models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LearnerDashboard extends JFrame {
    private User currentUser;
    private JLabel welcomeLabel;
    private JLabel creditLabel;
    private JTable sessionsTable;
    private JTable availableMentorsTable;
    private DefaultTableModel sessionsTableModel;
    private DefaultTableModel mentorsTableModel;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Colors
    private final Color PRIMARY_COLOR = new Color(0, 119, 73); // SA Green
    private final Color SECONDARY_COLOR = new Color(255, 184, 28); // SA Gold
    private final Color ACCENT_COLOR = new Color(220, 36, 31); // SA Red
    private final Color DARK_BLUE = new Color(25, 52, 65);
    private final Color LEARNER_BLUE = new Color(52, 152, 219);

    public LearnerDashboard(User user) {
        this.currentUser = user;
        setTitle("SkillSwap SA - Learner Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadUpcomingSessions();
        loadAvailableMentors();
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
        JPanel mentorsPanel = createMentorsPanel();
        JPanel profilePanel = createProfilePanel();
        JPanel rewardsPanel = createRewardsPanel();

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(sessionsPanel, "SESSIONS");
        contentPanel.add(mentorsPanel, "MENTORS");
        contentPanel.add(profilePanel, "PROFILE");
        contentPanel.add(rewardsPanel, "REWARDS");

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
        panel.setBackground(LEARNER_BLUE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setPreferredSize(new Dimension(getWidth(), 60));

        // Left side: Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(LEARNER_BLUE);

        JLabel logoLabel = new JLabel("📚");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        logoLabel.setForeground(SECONDARY_COLOR);

        JLabel titleLabel = new JLabel("SkillSwap Learner");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        leftPanel.add(logoLabel);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(titleLabel);

        // Right side: User info and credits
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(LEARNER_BLUE);

        JPanel creditPanel = new JPanel();
        creditPanel.setBackground(PRIMARY_COLOR);
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
        JLabel sidebarHeader = new JLabel("Learner Tools");
        sidebarHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sidebarHeader.setForeground(LEARNER_BLUE);
        sidebarHeader.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebarHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(sidebarHeader);
        panel.add(Box.createVerticalStrut(10));

        // Navigation buttons
        String[] menuItems = {
                "📊 Dashboard",
                "📅 My Sessions",
                "👨‍🏫 Find Mentors",
                "🎁 Earn Rewards",
                "👤 Profile",
                "📝 Request Session",
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
                case "👨‍🏫 Find Mentors":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "MENTORS");
                    loadAvailableMentors();
                    break;
                case "🎁 Earn Rewards":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "REWARDS");
                    break;
                case "👤 Profile":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "PROFILE");
                    loadProfileData();
                    break;
                case "📝 Request Session":
                    openRequestSessionDialog();
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
                button.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, LEARNER_BLUE));
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
                        + "<h1>Welcome, %s! 📚</h1>"
                        + "<p style='color: #666; font-size: 16px;'>Learn new skills, earn credits, grow together!</p>"
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
                "Upcoming Sessions", "Credits Earned", "Avg. Session Rating",
                "Skills Learning", "Hours Learned", "Credits Available"
        };

        String[] statValues = {"0", "0.00", "0.0",
                currentUser.getSubjects() != null ? String.valueOf(currentUser.getSubjects().split(",").length) : "0",
                "0",
                String.format("%.2f", currentUser.getCreditBalance())};

        for (int i = 0; i < statTitles.length; i++) {
            statsPanel.add(createLearnerStatCard(statTitles[i], statValues[i], i));
        }

        panel.add(statsPanel, BorderLayout.CENTER);

        // Quick actions panel
        JPanel actionsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Quick Actions"
        ));

        JButton findMentorBtn = new JButton("👨‍🏫 Find Mentor");
        JButton bookSessionBtn = new JButton("📅 Book Session");
        JButton earnCreditsBtn = new JButton("💰 Earn Credits");

        styleDashboardButton(findMentorBtn, LEARNER_BLUE);
        styleDashboardButton(bookSessionBtn, PRIMARY_COLOR);
        styleDashboardButton(earnCreditsBtn, SECONDARY_COLOR);

        findMentorBtn.addActionListener(e -> {
            cardLayout.show(((JPanel) mainPanel.getComponent(2)), "MENTORS");
            loadAvailableMentors();
        });
        bookSessionBtn.addActionListener(e -> openRequestSessionDialog());
        earnCreditsBtn.addActionListener(e -> {
            cardLayout.show(((JPanel) mainPanel.getComponent(2)), "REWARDS");
        });

        actionsPanel.add(findMentorBtn);
        actionsPanel.add(bookSessionBtn);
        actionsPanel.add(earnCreditsBtn);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        // Load stats from database
        loadDashboardStats();

        return panel;
    }

    private JPanel createLearnerStatCard(String title, String value, int index) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Different icons for each card
        String[] icons = {"📅", "💰", "⭐", "🎯", "⏱️", "🏦"};
        Color[] colors = {
                LEARNER_BLUE,
                new Color(46, 204, 113), // Green
                new Color(155, 89, 182), // Purple
                new Color(241, 196, 15), // Yellow
                new Color(230, 126, 34), // Orange
                PRIMARY_COLOR
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

        JLabel titleLabel = new JLabel("My Learning Sessions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshBtn = new JButton("🔄 Refresh");
        JButton newSessionBtn = new JButton("📝 Request Session");
        JButton pastSessionsBtn = new JButton("📜 Past Sessions");

        styleSmallButton(refreshBtn, new Color(200, 200, 200));
        styleSmallButton(newSessionBtn, LEARNER_BLUE);
        styleSmallButton(pastSessionsBtn, new Color(52, 152, 219));

        refreshBtn.addActionListener(e -> loadUpcomingSessions());
        newSessionBtn.addActionListener(e -> openRequestSessionDialog());
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
        String[] columns = {"ID", "Mentor", "Subject", "Date & Time", "Duration", "Status", "Cost", "Actions"};
        sessionsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only actions column is editable
            }
        };

        sessionsTable = new JTable(sessionsTableModel);
        sessionsTable.setRowHeight(40);
        sessionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sessionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        sessionsTable.getTableHeader().setBackground(LEARNER_BLUE);
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

        JLabel infoLabel = new JLabel("💡 Tip: Attend sessions to earn credits! 5 credits per hour of learning.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(infoLabel);

        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMentorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Available Mentors");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshBtn = new JButton("🔄 Refresh");
        JButton filterBtn = new JButton("🔍 Filter by Subject");
        JButton sortBtn = new JButton("📊 Sort by Rating");

        styleSmallButton(refreshBtn, new Color(200, 200, 200));
        styleSmallButton(filterBtn, LEARNER_BLUE);
        styleSmallButton(sortBtn, PRIMARY_COLOR);

        refreshBtn.addActionListener(e -> loadAvailableMentors());
        filterBtn.addActionListener(e -> filterMentorsBySubject());
        sortBtn.addActionListener(e -> sortMentorsByRating());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(filterBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(sortBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Table for mentors
        String[] columns = {"Name", "Subjects", "Rating", "Rate (Credits/hr)", "Sessions Done", "Book Session"};
        mentorsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only "Book Session" column is editable
            }
        };

        availableMentorsTable = new JTable(mentorsTableModel);
        availableMentorsTable.setRowHeight(50);
        availableMentorsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        availableMentorsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        availableMentorsTable.getTableHeader().setBackground(LEARNER_BLUE);
        availableMentorsTable.getTableHeader().setForeground(Color.WHITE);

        // Add action buttons to table
        availableMentorsTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        availableMentorsTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), availableMentorsTable));

        JScrollPane scrollPane = new JScrollPane(availableMentorsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        panel.add(scrollPane, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 248, 255));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        styleSmallButton(searchBtn, LEARNER_BLUE);

        searchBtn.addActionListener(e -> searchMentors(searchField.getText()));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchBtn);

        panel.add(searchPanel, BorderLayout.SOUTH);

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
        infoPanel.add(createProfileField("Learning Subjects:", currentUser.getSubjects() != null ? currentUser.getSubjects() : "Not specified"));
        infoPanel.add(createProfileField("Member Since:", "2024-01-01"));

        // Right: Edit profile and learning goals
        JPanel editPanel = new JPanel(new BorderLayout(10, 10));
        editPanel.setBackground(Color.WHITE);
        editPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel editTitle = new JLabel("Update Learning Profile");
        editTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        editTitle.setForeground(DARK_BLUE);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Update Phone:"));
        JTextField phoneField = new JTextField(currentUser.getPhone());
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Learning Subjects:"));
        JTextField subjectsField = new JTextField(currentUser.getSubjects());
        formPanel.add(subjectsField);

        formPanel.add(new JLabel("Learning Goals:"));
        JTextArea goalsArea = new JTextArea(2, 20);
        goalsArea.setLineWrap(true);
        goalsArea.setWrapStyleWord(true);
        JScrollPane goalsScroll = new JScrollPane(goalsArea);
        formPanel.add(goalsScroll);

        formPanel.add(new JLabel("Preferred Learning Style:"));
        JComboBox<String> styleCombo = new JComboBox<>(new String[]{"Visual", "Auditory", "Kinesthetic", "Reading/Writing", "Mixed"});
        formPanel.add(styleCombo);

        JButton saveBtn = new JButton("Save Changes");
        styleButton(saveBtn, LEARNER_BLUE);
        saveBtn.addActionListener(e -> updateProfile(phoneField.getText(), subjectsField.getText(), goalsArea.getText()));

        editPanel.add(editTitle, BorderLayout.NORTH);
        editPanel.add(formPanel, BorderLayout.CENTER);
        editPanel.add(saveBtn, BorderLayout.SOUTH);

        contentPanel.add(infoPanel);
        contentPanel.add(editPanel);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRewardsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Earn & Redeem Credits");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // How to earn credits
        JPanel earnPanel = new JPanel(new BorderLayout(10, 10));
        earnPanel.setBackground(new Color(240, 248, 255));
        earnPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel earnTitle = new JLabel("💡 How to Earn Credits");
        earnTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        earnTitle.setForeground(LEARNER_BLUE);

        JPanel earnMethods = new JPanel(new GridLayout(4, 1, 10, 10));
        earnMethods.setBackground(new Color(240, 248, 255));

        String[] methods = {
                "✅ Attend tutoring sessions: 5 credits/hour",
                "✅ Complete session feedback: 2 credits/session",
                "✅ Refer a friend: 10 credits/referral",
                "✅ Daily login streak: 1 credit/day (max 7/week)"
        };

        for (String method : methods) {
            JLabel methodLabel = new JLabel(method);
            methodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            earnMethods.add(methodLabel);
        }

        earnPanel.add(earnTitle, BorderLayout.NORTH);
        earnPanel.add(earnMethods, BorderLayout.CENTER);

        // Redeem credits
        JPanel redeemPanel = new JPanel(new BorderLayout(10, 10));
        redeemPanel.setBackground(new Color(255, 248, 240));
        redeemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 200, 200), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel redeemTitle = new JLabel("🎁 Redeem Your Credits");
        redeemTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        redeemTitle.setForeground(ACCENT_COLOR);

        JPanel rewardsGrid = new JPanel(new GridLayout(2, 3, 15, 15));
        rewardsGrid.setBackground(new Color(255, 248, 240));

        String[][] rewards = {
                {"📱 10GB Data", "50 Credits", "Redeem"},
                {"📞 R50 Airtime", "30 Credits", "Redeem"},
                {"✏️ Supplies Pack", "75 Credits", "Redeem"},
                {"📚 E-Book", "25 Credits", "Redeem"},
                {"🎓 Course Access", "100 Credits", "Redeem"},
                {"☕ Coffee", "15 Credits", "Redeem"}
        };

        for (String[] reward : rewards) {
            JPanel rewardCard = new JPanel(new BorderLayout(5, 5));
            rewardCard.setBackground(Color.WHITE);
            rewardCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            JLabel nameLabel = new JLabel(reward[0]);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JLabel costLabel = new JLabel(reward[1]);
            costLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            costLabel.setForeground(SECONDARY_COLOR);

            JButton redeemBtn = new JButton(reward[2]);
            styleSmallButton(redeemBtn, PRIMARY_COLOR);
            redeemBtn.addActionListener(e -> {
                if (currentUser.getCreditBalance() >= Double.parseDouble(reward[1].split(" ")[0])) {
                    JOptionPane.showMessageDialog(this, "Reward redeemed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient credits! Attend more sessions to earn credits.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            rewardCard.add(nameLabel, BorderLayout.NORTH);
            rewardCard.add(costLabel, BorderLayout.CENTER);
            rewardCard.add(redeemBtn, BorderLayout.SOUTH);

            rewardsGrid.add(rewardCard);
        }

        redeemPanel.add(redeemTitle, BorderLayout.NORTH);
        redeemPanel.add(rewardsGrid, BorderLayout.CENTER);

        contentPanel.add(earnPanel);
        contentPanel.add(redeemPanel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Balance label
        JLabel balanceLabel = new JLabel(String.format("Your balance: %.2f Credits", currentUser.getCreditBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        balanceLabel.setForeground(PRIMARY_COLOR);
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(balanceLabel, BorderLayout.SOUTH);

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
        panel.setBackground(LEARNER_BLUE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JLabel statusLabel = new JLabel("Ready to learn!");
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
            String sessionsQuery = "SELECT COUNT(*) FROM sessions WHERE learner_id = ? AND status = 'scheduled'";
            PreparedStatement stmt = conn.prepareStatement(sessionsQuery);
            stmt.setInt(1, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                // Could update a label here if needed
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUpcomingSessions() {
        if (sessionsTableModel == null) return;

        sessionsTableModel.setRowCount(0); // Clear existing rows

        try (Connection conn = ConnectionBD.getConnection()) {
            String query = "SELECT s.session_id, u.full_name, sk.skill_name, s.session_title, " +
                    "s.scheduled_time, s.duration_minutes, s.status, s.credit_cost " +
                    "FROM sessions s " +
                    "JOIN users u ON s.mentor_id = u.user_id " +
                    "LEFT JOIN skills sk ON s.skill_id = sk.skill_id " +
                    "WHERE s.learner_id = ? AND s.status IN ('scheduled', 'in_progress') " +
                    "ORDER BY s.scheduled_time";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            while (rs.next()) {
                int sessionId = rs.getInt("session_id");
                String mentorName = rs.getString("full_name");
                String skillName = rs.getString("skill_name");
                String sessionTitle = rs.getString("session_title");
                Timestamp scheduledTime = rs.getTimestamp("scheduled_time");
                int duration = rs.getInt("duration_minutes");
                String status = rs.getString("status");
                double credits = rs.getDouble("credit_cost");

                String[] row = {
                        String.valueOf(sessionId),
                        mentorName,
                        skillName != null ? skillName : "General",
                        sdf.format(scheduledTime),
                        duration + " min",
                        status,
                        String.format("%.2f", credits),
                        "Action"
                };

                sessionsTableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading sessions: " + e.getMessage());
        }
    }

    private void loadAvailableMentors() {
        if (mentorsTableModel == null) return;

        mentorsTableModel.setRowCount(0); // Clear existing rows

        try (Connection conn = ConnectionBD.getConnection()) {
            // Get all mentors (users with role 'mentor' or 'both')
            String query = "SELECT user_id, full_name, subjects, credit_balance FROM users " +
                    "WHERE role IN ('mentor', 'both') AND user_id != ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String mentorName = rs.getString("full_name");
                String subjects = rs.getString("subjects");
                double credits = rs.getDouble("credit_balance");

                // Calculate average rating (placeholder - would come from ratings table)
                double avgRating = 4.5 + (Math.random() * 0.5); // Random between 4.5-5.0
                double ratePerHour = 15.0 + (Math.random() * 10); // Random between 15-25
                int sessionsDone = 10 + (int)(Math.random() * 40); // Random between 10-50

                String[] row = {
                        mentorName,
                        subjects != null ? subjects : "Various",
                        String.format("%.1f", avgRating),
                        String.format("%.0f credits/hr", ratePerHour),
                        String.valueOf(sessionsDone),
                        "Book Now"
                };

                mentorsTableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading mentors: " + e.getMessage());
        }
    }

    private void loadProfileData() {
        // Implementation for profile data
    }

    // Action methods
    private void openRequestSessionDialog() {
        JDialog dialog = new JDialog(this, "Request Tutoring Session", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Subject
        formPanel.add(new JLabel("Subject:"));
        JComboBox<String> subjectCombo = new JComboBox<>(new String[]{
                "Mathematics", "Physics", "English", "Programming",
                "Business Studies", "Life Sciences", "Accounting"
        });
        formPanel.add(subjectCombo);

        // Preferred mentor
        formPanel.add(new JLabel("Preferred Mentor (Optional):"));
        JComboBox<String> mentorCombo = new JComboBox<>(new String[]{"Any", "Sarah Johnson", "Lerato Smith"});
        formPanel.add(mentorCombo);

        // Date
        formPanel.add(new JLabel("Preferred Date:"));
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        formPanel.add(dateField);

        // Time
        formPanel.add(new JLabel("Preferred Time:"));
        JComboBox<String> timeCombo = new JComboBox<>(new String[]{
                "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        });
        formPanel.add(timeCombo);

        // Duration
        formPanel.add(new JLabel("Duration (minutes):"));
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(60, 30, 180, 30));
        formPanel.add(durationSpinner);

        // Additional notes
        formPanel.add(new JLabel("Additional Notes:"));
        JTextArea notesArea = new JTextArea(2, 20);
        notesArea.setLineWrap(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        formPanel.add(notesScroll);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton requestBtn = new JButton("Send Request");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(requestBtn, LEARNER_BLUE);
        styleButton(cancelBtn, ACCENT_COLOR);

        requestBtn.addActionListener(e -> {
            double estimatedCost = (int)durationSpinner.getValue() / 60.0 * 15.0;

            if (currentUser.getCreditBalance() >= estimatedCost) {
                JOptionPane.showMessageDialog(dialog,
                        "Session request sent successfully!\n" +
                                "Estimated cost: " + String.format("%.2f", estimatedCost) + " credits\n" +
                                "Mentors will respond to your request soon.",
                        "Request Sent",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadUpcomingSessions();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Insufficient credits!\n" +
                                "You need " + String.format("%.2f", estimatedCost) + " credits but only have " +
                                String.format("%.2f", currentUser.getCreditBalance()) + ".\n" +
                                "Attend more sessions to earn credits.",
                        "Insufficient Credits",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(requestBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void filterMentorsBySubject() {
        String subject = JOptionPane.showInputDialog(this,
                "Enter subject to filter mentors:",
                "Filter by Subject",
                JOptionPane.QUESTION_MESSAGE);

        if (subject != null && !subject.trim().isEmpty()) {
            // Filter logic would go here
            JOptionPane.showMessageDialog(this,
                    "Filtering mentors by subject: " + subject,
                    "Filter Applied",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void sortMentorsByRating() {
        // Sort logic would go here
        JOptionPane.showMessageDialog(this,
                "Sorting mentors by rating...",
                "Sort Applied",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchMentors(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadAvailableMentors();
            return;
        }

        // Search logic would go here
        JOptionPane.showMessageDialog(this,
                "Searching for mentors with: " + searchTerm,
                "Search Results",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateProfile(String phone, String subjects, String goals) {
        try (Connection conn = ConnectionBD.getConnection()) {
            String query = "UPDATE users SET phone = ?, subjects = ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, phone);
            stmt.setString(2, subjects);
            stmt.setInt(3, currentUser.getUserId());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                currentUser.setPhone(phone);
                currentUser.setSubjects(subjects);
                JOptionPane.showMessageDialog(this,
                        "Profile updated successfully!\n" +
                                "Learning goals saved: " + (goals.isEmpty() ? "Not specified" : goals),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage());
        }
    }

    private void showBadges() {
        JDialog dialog = new JDialog(this, "My Learning Badges", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] badges = {"📚 Curious Learner", "⭐ Session Star", "⏱️ Time Master",
                "🏆 Skill Achiever", "🤝 Community Helper", "🚀 Fast Learner"};

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
                        "Configure notifications, learning preferences, and privacy settings.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPastSessions() {
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
    private void showSessionActions(int sessionId, String mentorName) {
        JDialog dialog = new JDialog(this, "Session Actions", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel infoLabel = new JLabel(String.format(
                "<html><div style='text-align: center;'>"
                        + "<h3>Session #%d</h3>"
                        + "<p>With: <b>%s</b></p>"
                        + "</div></html>",
                sessionId, mentorName
        ));
        panel.add(infoLabel);

        JButton joinBtn = new JButton("📞 Join Session");
        JButton rescheduleBtn = new JButton("🔄 Request Reschedule");
        JButton cancelBtn = new JButton("❌ Cancel Session");

        styleSmallButton(joinBtn, LEARNER_BLUE);
        styleSmallButton(rescheduleBtn, SECONDARY_COLOR);
        styleSmallButton(cancelBtn, ACCENT_COLOR);

        joinBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Meeting link would open here in a real application.", "Join Session", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        rescheduleBtn.addActionListener(e -> {
            openRescheduleDialog(sessionId, mentorName);
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to cancel this session?\nNote: Cancellation fees may apply.",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(dialog, "Session cancellation requested.", "Cancellation Requested", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadUpcomingSessions();
            }
        });

        panel.add(joinBtn);
        panel.add(rescheduleBtn);
        panel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void openBookMentorDialog(String mentorName, String rate) {
        JDialog dialog = new JDialog(this, "Book Session with " + mentorName, true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Mentor:"));
        JTextField mentorField = new JTextField(mentorName);
        mentorField.setEditable(false);
        formPanel.add(mentorField);

        formPanel.add(new JLabel("Rate:"));
        JTextField rateField = new JTextField(rate);
        rateField.setEditable(false);
        formPanel.add(rateField);

        formPanel.add(new JLabel("Subject:"));
        JComboBox<String> subjectCombo = new JComboBox<>(new String[]{
                "Mathematics", "Physics", "English", "Programming",
                "Business Studies", "Life Sciences", "Accounting"
        });
        formPanel.add(subjectCombo);

        formPanel.add(new JLabel("Date:"));
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Time:"));
        JComboBox<String> timeCombo = new JComboBox<>(new String[]{
                "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        });
        formPanel.add(timeCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookBtn = new JButton("Book Session");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(bookBtn, LEARNER_BLUE);
        styleButton(cancelBtn, ACCENT_COLOR);

        bookBtn.addActionListener(e -> {
            // Calculate cost based on duration (default 1 hour)
            double cost = 15.0; // Default rate
            try {
                String rateStr = rate.replace(" credits/hr", "").trim();
                cost = Double.parseDouble(rateStr);
            } catch (NumberFormatException ex) {
                // Use default rate
            }

            if (currentUser.getCreditBalance() >= cost) {
                JOptionPane.showMessageDialog(dialog,
                        String.format("Session booked with %s!\nCost: %.2f credits\nDate: %s at %s",
                                mentorName, cost, dateField.getText(), timeCombo.getSelectedItem()),
                        "Booking Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadUpcomingSessions();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        String.format("Insufficient credits!\nRequired: %.2f\nAvailable: %.2f\n\nAttend more sessions to earn credits.",
                                cost, currentUser.getCreditBalance()),
                        "Insufficient Credits",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(bookBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void openRescheduleDialog(int sessionId, String mentorName) {
        JDialog dialog = new JDialog(this, "Request Reschedule", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel infoLabel = new JLabel(String.format(
                "<html><div style='text-align: center;'>"
                        + "<h3>Reschedule Session #%d</h3>"
                        + "<p>With: <b>%s</b></p>"
                        + "</div></html>",
                sessionId, mentorName
        ));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.add(new JLabel("New Date:"));
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        formPanel.add(dateField);

        formPanel.add(new JLabel("New Time:"));
        JComboBox<String> timeCombo = new JComboBox<>(new String[]{
                "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        });
        formPanel.add(timeCombo);

        formPanel.add(new JLabel("Reason:"));
        JTextField reasonField = new JTextField("Schedule conflict");
        formPanel.add(reasonField);

        JButton sendBtn = new JButton("Send Request");
        styleButton(sendBtn, LEARNER_BLUE);
        sendBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog,
                    "Reschedule request sent to mentor. They will respond soon.",
                    "Request Sent",
                    JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(sendBtn, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
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
                // Handle button click - different for sessions vs mentors
                if (table != null) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        if (table == sessionsTable) {
                            // Session action
                            int sessionId = Integer.parseInt(sessionsTableModel.getValueAt(row, 0).toString());
                            String mentorName = (String) sessionsTableModel.getValueAt(row, 1);
                            showSessionActions(sessionId, mentorName);
                        } else if (table == availableMentorsTable) {
                            // Book mentor action
                            String mentorName = (String) mentorsTableModel.getValueAt(row, 0);
                            String rate = (String) mentorsTableModel.getValueAt(row, 3);
                            openBookMentorDialog(mentorName, rate);
                        }
                    }
                }
            }
            isPushed = false;
            return label;
        }
    }

//    public static void main(String[] args) {
//        // Test the dashboard with a sample learner user
//        User testUser = new User(1, "Thabo Mbeki", "learner@skillswap.co.za", "learner", 75.50);
//        testUser.setPhone("+27 11 123 4567");
//        testUser.setSubjects("Mathematics,English");
//
//        SwingUtilities.invokeLater(() -> {
//            new LearnerDashboard(testUser).setVisible(true);
//        });
//    }
}