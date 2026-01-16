package View;

import Models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CombinedDashboard extends JFrame {
    private User currentUser;

    public CombinedDashboard(User user) {
        this.currentUser = user;
        setTitle("SkillSwap SA - Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 52, 65));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Welcome, " + user.getFullName() + "!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel role = new JLabel("You can both Learn and Teach!");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        role.setForeground(new Color(255, 215, 0));

        header.add(title, BorderLayout.WEST);
        header.add(role, BorderLayout.EAST);

        // Content
        JPanel content = new JPanel(new GridLayout(2, 2, 20, 20));
        content.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        content.setBackground(Color.WHITE);

        // Create role cards
        content.add(createRoleCard("🎓 Enter as Learner", "Learn new skills from mentors", Color.decode("#3498db"),
                e -> JOptionPane.showMessageDialog(this, "Learner dashboard coming soon!")));

        content.add(createRoleCard("👨‍🏫 Enter as Mentor", "Teach and earn credits", Color.decode("#2ecc71"),
                e -> {
                    this.dispose();
                    new MentorDashboard(user).setVisible(true);
                }));

        content.add(createRoleCard("📊 View Dashboard", "See all your activities", Color.decode("#9b59b6"),
                e -> JOptionPane.showMessageDialog(this, "Combined dashboard coming soon!")));

        content.add(createRoleCard("⚙️ Settings", "Manage your account", Color.decode("#34495e"),
                e -> JOptionPane.showMessageDialog(this, "Settings coming soon!")));

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(content, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createRoleCard(String title, String description, Color color, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(color);

        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(100, 100, 100));

        JButton enterBtn = new JButton("Enter");
        enterBtn.setBackground(color);
        enterBtn.setForeground(Color.WHITE);
        enterBtn.setFocusPainted(false);
        enterBtn.addActionListener(action);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(enterBtn, BorderLayout.SOUTH);

        return card;
    }
}