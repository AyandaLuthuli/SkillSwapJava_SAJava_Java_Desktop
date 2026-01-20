package View;

import Models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CombinedDashboard extends JFrame {
    private User currentUser;
    private JLabel welcomeLabel;
    private JLabel creditLabel;
    private JTable learnerSessionsTable;
    private JTable mentorSessionsTable;
    private DefaultTableModel learnerTableModel;
    private DefaultTableModel mentorTableModel;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Colors
    private final Color PRIMARY_COLOR = new Color(0, 119, 73); // SA Green
    private final Color SECONDARY_COLOR = new Color(255, 184, 28); // SA Gold
    private final Color ACCENT_COLOR = new Color(220, 36, 31); // SA Red
    private final Color DARK_BLUE = new Color(25, 52, 65);
    private final Color LEARNER_BLUE = new Color(52, 152, 219);
    private final Color MENTOR_GREEN = new Color(46, 204, 113);
    private final Color COMBINED_PURPLE = new Color(155, 89, 182);

    public CombinedDashboard(User user) {
        this.currentUser = user;
        setTitle("SkillSwap SA - Combined Dashboard");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadLearnerSessions();
        loadMentorSessions();
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
        JPanel learnerPanel = createLearnerPanel();
        JPanel mentorPanel = createMentorPanel();
        JPanel combinedPanel = createCombinedPanel();
        JPanel profilePanel = createProfilePanel();
        JPanel rewardsPanel = createRewardsPanel();

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(learnerPanel, "LEARNER");
        contentPanel.add(mentorPanel, "MENTOR");
        contentPanel.add(combinedPanel, "COMBINED");
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
        panel.setBackground(COMBINED_PURPLE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setPreferredSize(new Dimension(getWidth(), 60));

        // Left side: Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(COMBINED_PURPLE);

        JLabel logoLabel = new JLabel("🔄");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        logoLabel.setForeground(SECONDARY_COLOR);

        JLabel titleLabel = new JLabel("SkillSwap Dual Role");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        leftPanel.add(logoLabel);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(titleLabel);

        // Right side: User info and credits
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(COMBINED_PURPLE);

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
        JLabel sidebarHeader = new JLabel("Dual Role Tools");
        sidebarHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sidebarHeader.setForeground(COMBINED_PURPLE);
        sidebarHeader.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebarHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(sidebarHeader);
        panel.add(Box.createVerticalStrut(10));

        // Navigation buttons
        String[] menuItems = {
                "📊 Dashboard",
                "📚 As Learner",
                "👨‍🏫 As Mentor",
                "🔄 Combined View",
                "🎁 Earn & Redeem",
                "👤 Profile",
                "⚙️ Settings",
                "🏆 Achievements"
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
                case "📚 As Learner":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "LEARNER");
                    loadLearnerSessions();
                    break;
                case "👨‍🏫 As Mentor":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "MENTOR");
                    loadMentorSessions();
                    break;
                case "🔄 Combined View":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "COMBINED");
                    break;
                case "🎁 Earn & Redeem":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "REWARDS");
                    break;
                case "👤 Profile":
                    cardLayout.show(((JPanel) mainPanel.getComponent(2)), "PROFILE");
                    break;
                case "⚙️ Settings":
                    openSettings();
                    break;
                case "🏆 Achievements":
                    showAchievements();
                    break;
            }
        });

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(230, 240, 255));
                button.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, COMBINED_PURPLE));
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
                        + "<h1>Welcome, %s! 🔄</h1>"
                        + "<p style='color: #666; font-size: 16px;'>You're both a Learner and a Mentor - Get the best of both worlds!</p>"
                        + "</div></html>",
                currentUser.getFullName().split(" ")[0] // First name only
        ));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Role switcher
        JPanel roleSwitcher = new JPanel(new GridLayout(1, 2, 20, 20));
        roleSwitcher.setBackground(Color.WHITE);
        roleSwitcher.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Learner card
        JPanel learnerCard = createRoleSwitchCard("📚 Switch to Learner", "Browse mentors, learn new skills", LEARNER_BLUE,
                e -> cardLayout.show(((JPanel) mainPanel.getComponent(2)), "LEARNER"));

        // Mentor card
        JPanel mentorCard = createRoleSwitchCard("👨‍🏫 Switch to Mentor", "Manage sessions, earn credits", MENTOR_GREEN,
                e -> cardLayout.show(((JPanel) mainPanel.getComponent(2)), "MENTOR"));

        roleSwitcher.add(learnerCard);
        roleSwitcher.add(mentorCard);

        panel.add(roleSwitcher, BorderLayout.CENTER);

        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Create stat cards
        String[] statTitles = {
                "Learning Sessions", "Teaching Sessions", "Net Credits",
                "Skills Learning", "Skills Teaching", "Balance"
        };

        String[] statValues = {"0", "0", "0.00",
                currentUser.getSubjects() != null ? String.valueOf(currentUser.getSubjects().split(",").length) : "0",
                currentUser.getSubjects() != null ? String.valueOf(currentUser.getSubjects().split(",").length) : "0",
                String.format("%.2f", currentUser.getCreditBalance())};

        for (int i = 0; i < statTitles.length; i++) {
            statsPanel.add(createCombinedStatCard(statTitles[i], statValues[i], i));
        }

        panel.add(statsPanel, BorderLayout.SOUTH);

        // Load stats from database
        loadDashboardStats();

        return panel;
    }

    private JPanel createRoleSwitchCard(String title, String description, Color color, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(30, 20, 30, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton switchBtn = new JButton("Switch Now");
        switchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        switchBtn.setBackground(color);
        switchBtn.setForeground(Color.WHITE);
        switchBtn.setFocusPainted(false);
        switchBtn.setBorderPainted(false);
        switchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchBtn.setOpaque(true);
        switchBtn.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        switchBtn.addActionListener(action);

        // Add hover effect
        switchBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                switchBtn.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                switchBtn.setBackground(color);
            }
        });

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(switchBtn, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCombinedStatCard(String title, String value, int index) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Different icons for each card
        String[] icons = {"📚", "👨‍🏫", "💰", "🎯", "📖", "🏦"};
        Color[] colors = {
                LEARNER_BLUE,
                MENTOR_GREEN,
                SECONDARY_COLOR,
                COMBINED_PURPLE,
                PRIMARY_COLOR,
                DARK_BLUE
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

    private JPanel createLearnerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("📚 Learner Activities");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(LEARNER_BLUE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshBtn = new JButton("🔄 Refresh");
        JButton findMentorBtn = new JButton("👨‍🏫 Find Mentor");
        JButton bookSessionBtn = new JButton("📅 Book Session");

        styleSmallButton(refreshBtn, new Color(200, 200, 200));
        styleSmallButton(findMentorBtn, LEARNER_BLUE);
        styleSmallButton(bookSessionBtn, PRIMARY_COLOR);

        refreshBtn.addActionListener(e -> loadLearnerSessions());
        findMentorBtn.addActionListener(e -> openFindMentorsDialog());
        bookSessionBtn.addActionListener(e -> openBookSessionDialog());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(findMentorBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(bookSessionBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Table for learner sessions
        String[] columns = {"ID", "Mentor", "Subject", "Date & Time", "Duration", "Status", "Cost", "Actions"};
        learnerTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only actions column is editable
            }
        };

        learnerSessionsTable = new JTable(learnerTableModel);
        learnerSessionsTable.setRowHeight(40);
        learnerSessionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        learnerSessionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        learnerSessionsTable.getTableHeader().setBackground(LEARNER_BLUE);
        learnerSessionsTable.getTableHeader().setForeground(Color.WHITE);

        // Add action buttons to table
        learnerSessionsTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        learnerSessionsTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox(), learnerSessionsTable, "LEARNER"));

        JScrollPane scrollPane = new JScrollPane(learnerSessionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        panel.add(scrollPane, BorderLayout.CENTER);

        // Quick stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        statsPanel.setBackground(new Color(240, 248, 255));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        String[][] learnerStats = {
                {"📚 Learning", "2 Subjects"},
                {"⏱️ This Week", "3 hours"},
                {"💰 Spent", "45 credits"},
                {"⭐ Avg Rating", "4.8"}
        };

        for (String[] stat : learnerStats) {
            JPanel statPanel = new JPanel(new BorderLayout(5, 5));
            statPanel.setBackground(new Color(240, 248, 255));

            JLabel nameLabel = new JLabel(stat[0]);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

            JLabel valueLabel = new JLabel(stat[1]);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            valueLabel.setForeground(LEARNER_BLUE);

            statPanel.add(nameLabel, BorderLayout.NORTH);
            statPanel.add(valueLabel, BorderLayout.CENTER);
            statsPanel.add(statPanel);
        }

        panel.add(statsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMentorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("👨‍🏫 Mentor Activities");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(MENTOR_GREEN);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshBtn = new JButton("🔄 Refresh");
        JButton createSessionBtn = new JButton("➕ Create Session");
        JButton findLearnersBtn = new JButton("👥 Find Learners");

        styleSmallButton(refreshBtn, new Color(200, 200, 200));
        styleSmallButton(createSessionBtn, MENTOR_GREEN);
        styleSmallButton(findLearnersBtn, PRIMARY_COLOR);

        refreshBtn.addActionListener(e -> loadMentorSessions());
        createSessionBtn.addActionListener(e -> openCreateSessionDialog());
        findLearnersBtn.addActionListener(e -> openFindLearnersDialog());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(createSessionBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(findLearnersBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Table for mentor sessions
        String[] columns = {"ID", "Learner", "Subject", "Date & Time", "Duration", "Status", "Earnings", "Actions"};
        mentorTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only actions column is editable
            }
        };

        mentorSessionsTable = new JTable(mentorTableModel);
        mentorSessionsTable.setRowHeight(40);
        mentorSessionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mentorSessionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        mentorSessionsTable.getTableHeader().setBackground(MENTOR_GREEN);
        mentorSessionsTable.getTableHeader().setForeground(Color.WHITE);

        // Add action buttons to table
        mentorSessionsTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        mentorSessionsTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox(), mentorSessionsTable, "MENTOR"));

        JScrollPane scrollPane = new JScrollPane(mentorSessionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        panel.add(scrollPane, BorderLayout.CENTER);

        // Quick stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        statsPanel.setBackground(new Color(240, 255, 240));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        String[][] mentorStats = {
                {"👥 Students", "5 Active"},
                {"💰 Earned", "120 credits"},
                {"⭐ Rating", "4.9/5"},
                {"⏱️ Hours", "8 this week"}
        };

        for (String[] stat : mentorStats) {
            JPanel statPanel = new JPanel(new BorderLayout(5, 5));
            statPanel.setBackground(new Color(240, 255, 240));

            JLabel nameLabel = new JLabel(stat[0]);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

            JLabel valueLabel = new JLabel(stat[1]);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            valueLabel.setForeground(MENTOR_GREEN);

            statPanel.add(nameLabel, BorderLayout.NORTH);
            statPanel.add(valueLabel, BorderLayout.CENTER);
            statsPanel.add(statPanel);
        }

        panel.add(statsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCombinedPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("🔄 Combined Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COMBINED_PURPLE);
        panel.add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Today's Schedule Tab
        JPanel schedulePanel = createSchedulePanel();
        tabbedPane.addTab("📅 Today's Schedule", schedulePanel);

        // Balance Overview Tab
        JPanel balancePanel = createBalancePanel();
        tabbedPane.addTab("💰 Balance Overview", balancePanel);

        // Quick Actions Tab
        JPanel actionsPanel = createQuickActionsPanel();
        tabbedPane.addTab("⚡ Quick Actions", actionsPanel);

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Create schedule table
        String[] columns = {"Time", "Activity", "Role", "With", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable scheduleTable = new JTable(model);

        // Add sample schedule data
        String[][] scheduleData = {
                {"09:00 - 10:00", "Mathematics Tutoring", "Mentor", "Thabo Mbeki", "Upcoming"},
                {"14:00 - 15:30", "Physics Learning", "Learner", "Sarah Johnson", "Upcoming"},
                {"16:00 - 17:00", "English Practice", "Learner", "Lerato Smith", "Completed"},
                {"18:00 - 19:00", "Programming Session", "Mentor", "John Doe", "Scheduled"}
        };

        for (String[] row : scheduleData) {
            model.addRow(row);
        }

        scheduleTable.setRowHeight(35);
        scheduleTable.getTableHeader().setBackground(COMBINED_PURPLE);
        scheduleTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBalancePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Total Balance
        JPanel totalPanel = new JPanel(new BorderLayout(10, 10));
        totalPanel.setBackground(new Color(240, 248, 255));
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LEARNER_BLUE, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel totalLabel = new JLabel("Total Balance");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel amountLabel = new JLabel(String.format("%.2f Credits", currentUser.getCreditBalance()));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        amountLabel.setForeground(LEARNER_BLUE);

        totalPanel.add(totalLabel, BorderLayout.NORTH);
        totalPanel.add(amountLabel, BorderLayout.CENTER);

        // Earnings this month
        JPanel earningsPanel = new JPanel(new BorderLayout(10, 10));
        earningsPanel.setBackground(new Color(240, 255, 240));
        earningsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MENTOR_GREEN, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel earningsLabel = new JLabel("Earned (This Month)");
        earningsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel earningsAmount = new JLabel("85.50 Credits");
        earningsAmount.setFont(new Font("Segoe UI", Font.BOLD, 24));
        earningsAmount.setForeground(MENTOR_GREEN);

        earningsPanel.add(earningsLabel, BorderLayout.NORTH);
        earningsPanel.add(earningsAmount, BorderLayout.CENTER);

        // Spent this month
        JPanel spentPanel = new JPanel(new BorderLayout(10, 10));
        spentPanel.setBackground(new Color(255, 248, 240));
        spentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel spentLabel = new JLabel("Spent (This Month)");
        spentLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel spentAmount = new JLabel("45.00 Credits");
        spentAmount.setFont(new Font("Segoe UI", Font.BOLD, 24));
        spentAmount.setForeground(SECONDARY_COLOR);

        spentPanel.add(spentLabel, BorderLayout.NORTH);
        spentPanel.add(spentAmount, BorderLayout.CENTER);

        panel.add(totalPanel);
        panel.add(earningsPanel);
        panel.add(spentPanel);

        return panel;
    }

    record QuickAction(String name, String tooltip, Color color) {}

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        QuickAction[] actions = {
                new QuickAction("Book Learning", "Schedule a session as learner", LEARNER_BLUE),
                new QuickAction("Create Teaching", "Offer a session as mentor", MENTOR_GREEN),
                new QuickAction("Redeem Credits", "Exchange for rewards", SECONDARY_COLOR),
                new QuickAction("View Reports", "See your performance", COMBINED_PURPLE),
                new QuickAction("Find People", "Connect with others", PRIMARY_COLOR),
                new QuickAction("View Badges", "Your achievements", DARK_BLUE),
                new QuickAction("Settings", "Account preferences", new Color(100, 100, 100)),
                new QuickAction("Resources", "Learning materials", new Color(155, 89, 182))
        };

        for (QuickAction action : actions) {
            JButton actionBtn = new JButton(action.name());
            actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            actionBtn.setBackground(action.color());
            actionBtn.setForeground(Color.WHITE);
            actionBtn.setFocusPainted(false);
            actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            actionBtn.setToolTipText(action.tooltip());
            panel.add(actionBtn);
        }

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("👤 Dual Role Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COMBINED_PURPLE);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Left: Profile info with dual role badges
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel badgesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        badgesPanel.setBackground(Color.WHITE);

        JLabel learnerBadge = createBadge("📚 Active Learner", LEARNER_BLUE);
        JLabel mentorBadge = createBadge("👨‍🏫 Active Mentor", MENTOR_GREEN);
        JLabel dualBadge = createBadge("🔄 Dual Role Pro", COMBINED_PURPLE);

        badgesPanel.add(learnerBadge);
        badgesPanel.add(mentorBadge);
        badgesPanel.add(dualBadge);

        JPanel profileInfo = new JPanel(new GridLayout(6, 1, 10, 10));
        profileInfo.setBackground(Color.WHITE);
        profileInfo.add(createProfileField("Name:", currentUser.getFullName()));
        profileInfo.add(createProfileField("Email:", currentUser.getEmail()));
        profileInfo.add(createProfileField("Phone:", currentUser.getPhone() != null ? currentUser.getPhone() : "Not set"));
        profileInfo.add(createProfileField("Role:", "Learner & Mentor 🔄"));
        profileInfo.add(createProfileField("Subjects:", currentUser.getSubjects() != null ? currentUser.getSubjects() : "Not specified"));
        profileInfo.add(createProfileField("Member Since:", "2024-01-01"));

        infoPanel.add(badgesPanel, BorderLayout.NORTH);
        infoPanel.add(profileInfo, BorderLayout.CENTER);

        // Right: Edit profile
        JPanel editPanel = new JPanel(new BorderLayout(10, 10));
        editPanel.setBackground(Color.WHITE);
        editPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel editTitle = new JLabel("Update Dual Profile");
        editTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        editTitle.setForeground(COMBINED_PURPLE);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Update Phone:"));
        JTextField phoneField = new JTextField(currentUser.getPhone());
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Learning Subjects:"));
        JTextField learnSubjectsField = new JTextField(currentUser.getSubjects());
        formPanel.add(learnSubjectsField);

        formPanel.add(new JLabel("Teaching Subjects:"));
        JTextField teachSubjectsField = new JTextField(currentUser.getSubjects());
        formPanel.add(teachSubjectsField);

        formPanel.add(new JLabel("Bio:"));
        JTextArea bioArea = new JTextArea(3, 20);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        JScrollPane bioScroll = new JScrollPane(bioArea);
        formPanel.add(bioScroll);

        formPanel.add(new JLabel("Availability:"));
        JComboBox<String> availabilityCombo = new JComboBox<>(new String[]{"Both Roles", "Mostly Learner", "Mostly Mentor", "Flexible"});
        formPanel.add(availabilityCombo);

        JButton saveBtn = new JButton("Save Changes");
        styleButton(saveBtn, COMBINED_PURPLE);
        saveBtn.addActionListener(e -> updateProfile(phoneField.getText(), learnSubjectsField.getText(), teachSubjectsField.getText(), bioArea.getText()));

        editPanel.add(editTitle, BorderLayout.NORTH);
        editPanel.add(formPanel, BorderLayout.CENTER);
        editPanel.add(saveBtn, BorderLayout.SOUTH);

        contentPanel.add(infoPanel);
        contentPanel.add(editPanel);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createBadge(String text, Color color) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setBackground(color);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return badge;
    }

    private JPanel createRewardsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("🎁 Earn & Redeem - Dual Benefits");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COMBINED_PURPLE);
        panel.add(titleLabel, BorderLayout.NORTH);

        JTabbedPane rewardsTabs = new JTabbedPane();
        rewardsTabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // How to Earn Tab
        JPanel earnPanel = createEarnPanel();
        rewardsTabs.addTab("💰 How to Earn", earnPanel);

        // Redeem Tab
        JPanel redeemPanel = createRedeemPanel();
        rewardsTabs.addTab("🎁 Redeem Now", redeemPanel);

        // Special Benefits Tab
        JPanel benefitsPanel = createBenefitsPanel();
        rewardsTabs.addTab("🌟 Dual Benefits", benefitsPanel);

        panel.add(rewardsTabs, BorderLayout.CENTER);

        // Balance display
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        balancePanel.setBackground(new Color(240, 240, 240));
        balancePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel balanceLabel = new JLabel(String.format("Available Balance: %.2f Credits", currentUser.getCreditBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        balanceLabel.setForeground(PRIMARY_COLOR);
        balancePanel.add(balanceLabel);

        panel.add(balancePanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEarnPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // As Learner
        JPanel learnerEarn = new JPanel(new BorderLayout(10, 10));
        learnerEarn.setBackground(new Color(240, 248, 255));
        learnerEarn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LEARNER_BLUE, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel learnerTitle = new JLabel("📚 As Learner:");
        learnerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        learnerTitle.setForeground(LEARNER_BLUE);

        JPanel learnerMethods = new JPanel(new GridLayout(4, 1, 5, 5));
        learnerMethods.setBackground(new Color(240, 248, 255));

        String[] learnerWays = {
                "✅ Attend sessions: 5 credits/hour",
                "✅ Complete feedback: 2 credits/session",
                "✅ Daily streak: 1 credit/day (max 7/week)",
                "✅ Refer friends: 10 credits/referral"
        };

        for (String way : learnerWays) {
            JLabel wayLabel = new JLabel(way);
            wayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            learnerMethods.add(wayLabel);
        }

        learnerEarn.add(learnerTitle, BorderLayout.NORTH);
        learnerEarn.add(learnerMethods, BorderLayout.CENTER);

        // As Mentor
        JPanel mentorEarn = new JPanel(new BorderLayout(10, 10));
        mentorEarn.setBackground(new Color(240, 255, 240));
        mentorEarn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MENTOR_GREEN, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel mentorTitle = new JLabel("👨‍🏫 As Mentor:");
        mentorTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mentorTitle.setForeground(MENTOR_GREEN);

        JPanel mentorMethods = new JPanel(new GridLayout(4, 1, 5, 5));
        mentorMethods.setBackground(new Color(240, 255, 240));

        String[] mentorWays = {
                "✅ Conduct sessions: 15 credits/hour",
                "✅ High ratings: 5 credits/5-star review",
                "✅ Session completion: 3 credits/completed",
                "✅ Help new learners: 20 credits/5 learners"
        };

        for (String way : mentorWays) {
            JLabel wayLabel = new JLabel(way);
            wayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            mentorMethods.add(wayLabel);
        }

        mentorEarn.add(mentorTitle, BorderLayout.NORTH);
        mentorEarn.add(mentorMethods, BorderLayout.CENTER);

        panel.add(learnerEarn);
        panel.add(mentorEarn);

        return panel;
    }

    private JPanel createRedeemPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[][] rewards = {
                {"📱 10GB Data", "50 Credits", "All Networks"},
                {"📞 R50 Airtime", "30 Credits", "Any Network"},
                {"✏️ Supplies Pack", "75 Credits", "Stationery Set"},
                {"🎓 Course Access", "100 Credits", "Online Course"},
                {"📚 E-Book Bundle", "40 Credits", "5 E-books"},
                {"☕ Coffee Voucher", "15 Credits", "Local Cafe"}
        };

        for (String[] reward : rewards) {
            JPanel rewardCard = new JPanel(new BorderLayout(10, 10));
            rewardCard.setBackground(new Color(255, 248, 240));
            rewardCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));

            JLabel nameLabel = new JLabel(reward[0]);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

            JLabel costLabel = new JLabel(reward[1]);
            costLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            costLabel.setForeground(SECONDARY_COLOR);

            JLabel detailsLabel = new JLabel(reward[2]);
            detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            detailsLabel.setForeground(new Color(100, 100, 100));

            JButton redeemBtn = new JButton("Redeem");
            styleSmallButton(redeemBtn, PRIMARY_COLOR);
            redeemBtn.addActionListener(e -> {
                double cost = Double.parseDouble(reward[1].split(" ")[0]);
                if (currentUser.getCreditBalance() >= cost) {
                    JOptionPane.showMessageDialog(this,
                            String.format("Successfully redeemed: %s\nCost: %.0f credits", reward[0], cost),
                            "Redeemed!",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            String.format("Need %.0f more credits to redeem %s",
                                    cost - currentUser.getCreditBalance(), reward[0]),
                            "Insufficient Credits",
                            JOptionPane.WARNING_MESSAGE);
                }
            });

            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            infoPanel.setBackground(new Color(255, 248, 240));
            infoPanel.add(nameLabel);
            infoPanel.add(costLabel);
            infoPanel.add(detailsLabel);

            rewardCard.add(infoPanel, BorderLayout.CENTER);
            rewardCard.add(redeemBtn, BorderLayout.SOUTH);

            panel.add(rewardCard);
        }

        return panel;
    }

    private JPanel createBenefitsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(248, 240, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("🌟 Exclusive Dual Role Benefits");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COMBINED_PURPLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel benefitsList = new JPanel(new GridLayout(6, 1, 10, 10));
        benefitsList.setBackground(new Color(248, 240, 255));

        String[] benefits = {
                "🎯 Priority matching with top mentors/learners",
                "💰 20% more credits when switching roles same day",
                "🏆 Exclusive dual role achievement badges",
                "📊 Advanced analytics for both roles",
                "👥 Access to exclusive community forums",
                "⚡ Faster credit processing and rewards"
        };

        for (String benefit : benefits) {
            JLabel benefitLabel = new JLabel(benefit);
            benefitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            benefitLabel.setIcon(new ImageIcon("✓")); // You can add actual icons
            benefitsList.add(benefitLabel);
        }

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(benefitsList, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COMBINED_PURPLE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JLabel statusLabel = new JLabel("Dual Role Active - Ready for both learning and teaching!");
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

    // Database methods
    private void loadDashboardStats() {
        // Implementation would load stats from database
    }

    private void loadLearnerSessions() {
        if (learnerTableModel == null) return;

        learnerTableModel.setRowCount(0);

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
                String[] row = {
                        String.valueOf(rs.getInt("session_id")),
                        rs.getString("full_name"),
                        rs.getString("skill_name") != null ? rs.getString("skill_name") : "General",
                        sdf.format(rs.getTimestamp("scheduled_time")),
                        rs.getInt("duration_minutes") + " min",
                        rs.getString("status"),
                        String.format("%.2f", rs.getDouble("credit_cost")),
                        "Actions"
                };
                learnerTableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMentorSessions() {
        if (mentorTableModel == null) return;

        mentorTableModel.setRowCount(0);

        try (Connection conn = ConnectionBD.getConnection()) {
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
                String[] row = {
                        String.valueOf(rs.getInt("session_id")),
                        rs.getString("full_name"),
                        rs.getString("skill_name") != null ? rs.getString("skill_name") : "General",
                        sdf.format(rs.getTimestamp("scheduled_time")),
                        rs.getInt("duration_minutes") + " min",
                        rs.getString("status"),
                        String.format("%.2f", rs.getDouble("credit_cost")),
                        "Actions"
                };
                mentorTableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Action methods
    private void openBookSessionDialog() {
        // Similar to LearnerDashboard's method
        JOptionPane.showMessageDialog(this,
                "Book a learning session (as learner)",
                "Book Session",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void openCreateSessionDialog() {
        // Similar to MentorDashboard's method
        JOptionPane.showMessageDialog(this,
                "Create a teaching session (as mentor)",
                "Create Session",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void openFindMentorsDialog() {
        JOptionPane.showMessageDialog(this,
                "Find mentors to learn from",
                "Find Mentors",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void openFindLearnersDialog() {
        JOptionPane.showMessageDialog(this,
                "Find learners to teach",
                "Find Learners",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateProfile(String phone, String learnSubjects, String teachSubjects, String bio) {
        try (Connection conn = ConnectionBD.getConnection()) {
            String query = "UPDATE users SET phone = ?, subjects = ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, phone);
            // Combine both learning and teaching subjects
            String combinedSubjects = learnSubjects + " | Teaching: " + teachSubjects;
            stmt.setString(2, combinedSubjects);
            stmt.setInt(3, currentUser.getUserId());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                currentUser.setPhone(phone);
                currentUser.setSubjects(combinedSubjects);
                JOptionPane.showMessageDialog(this,
                        "Dual profile updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage());
        }
    }

    private void openSettings() {
        JOptionPane.showMessageDialog(this,
                "Dual role settings feature coming soon!",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAchievements() {
        JDialog dialog = new JDialog(this, "Dual Role Achievements", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] achievements = {
                "🔄 Role Master", "📚 Learning Pro", "👨‍🏫 Teaching Star",
                "💰 Balance Expert", "⏱️ Time Manager", "👥 Community Leader",
                "🌟 Dual Role Elite", "🎯 Goal Achiever", "🚀 Fast Switcher"
        };

        for (String achievement : achievements) {
            JLabel badge = new JLabel(achievement, SwingConstants.CENTER);
            badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            badge.setForeground(Color.WHITE);
            badge.setBackground(COMBINED_PURPLE);
            badge.setOpaque(true);
            badge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COMBINED_PURPLE.darker(), 2),
                    BorderFactory.createEmptyBorder(15, 10, 15, 10)
            ));
            panel.add(badge);
        }

        dialog.add(panel);
        dialog.setVisible(true);
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
        private JTable table;
        private String roleType;

        public ButtonEditor(JCheckBox checkBox, JTable table, String roleType) {
            super(checkBox);
            this.table = table;
            this.roleType = roleType;
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
                int row = table.getSelectedRow();
                if (row >= 0) {
                    if ("LEARNER".equals(roleType)) {
                        // Learner session actions
                        int sessionId = Integer.parseInt(learnerTableModel.getValueAt(row, 0).toString());
                        String mentorName = (String) learnerTableModel.getValueAt(row, 1);
                        showLearnerSessionActions(sessionId, mentorName);
                    } else if ("MENTOR".equals(roleType)) {
                        // Mentor session actions
                        int sessionId = Integer.parseInt(mentorTableModel.getValueAt(row, 0).toString());
                        String learnerName = (String) mentorTableModel.getValueAt(row, 1);
                        showMentorSessionActions(sessionId, learnerName);
                    }
                }
            }
            isPushed = false;
            return label;
        }
    }

    private void showLearnerSessionActions(int sessionId, String mentorName) {
        String[] options = {"Join Session", "Request Reschedule", "Cancel", "View Details"};
        int choice = JOptionPane.showOptionDialog(this,
                String.format("Learner Session #%d with %s", sessionId, mentorName),
                "Learner Actions",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        // Handle choice
        JOptionPane.showMessageDialog(this,
                String.format("Action '%s' for session #%d", options[choice], sessionId),
                "Action Selected",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMentorSessionActions(int sessionId, String learnerName) {
        String[] options = {"Start Session", "Mark Complete", "Reschedule", "Cancel", "Details"};
        int choice = JOptionPane.showOptionDialog(this,
                String.format("Mentor Session #%d with %s", sessionId, learnerName),
                "Mentor Actions",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        // Handle choice
        JOptionPane.showMessageDialog(this,
                String.format("Action '%s' for session #%d", options[choice], sessionId),
                "Action Selected",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Test the dashboard with a sample user
        User testUser = new User(3, "Lerato Smith", "both@skillswap.co.za", "both", 95.75);
        testUser.setPhone("+27 31 555 1212");
        testUser.setSubjects("English,Business Studies");

        SwingUtilities.invokeLater(() -> {
            new CombinedDashboard(testUser).setVisible(true);
        });
    }
}