package Models;

public class User {
    private int userId;
    private String email;
    private String fullName;
    private String role;
    private double creditBalance;
    private String phone;
    private String subjects;
    private String bio;

    // Constructors
    public User() {
        this.role = ""; // Initialize to empty string instead of null
    }

    public User(int userId, String fullName, String email, String role, double creditBalance) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role != null ? role : ""; // Handle null role
        this.creditBalance = creditBalance;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() {
        return role != null ? role : ""; // Ensure never returns null
    }
    public void setRole(String role) {
        this.role = role != null ? role : ""; // Handle null assignment
    }

    public double getCreditBalance() { return creditBalance; }
    public void setCreditBalance(double creditBalance) { this.creditBalance = creditBalance; }

    public String getPhone() { return phone != null ? phone : ""; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSubjects() { return subjects != null ? subjects : ""; }
    public void setSubjects(String subjects) { this.subjects = subjects; }

    public String getBio() { return bio != null ? bio : ""; }
    public void setBio(String bio) { this.bio = bio; }

    // Helper methods with null checks
    public boolean isMentor() {
        String userRole = getRole();
        return "mentor".equals(userRole) || "both".equals(userRole);
    }

    public boolean isLearner() {
        String userRole = getRole();
        return "learner".equals(userRole) || "both".equals(userRole);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", creditBalance=" + creditBalance +
                ", phone='" + phone + '\'' +
                ", subjects='" + subjects + '\'' +
                '}';
    }
}