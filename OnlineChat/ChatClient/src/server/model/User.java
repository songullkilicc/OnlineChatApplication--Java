package server.model;

public class User {
    private String username;
    private String password;
    private String displayName;

    public User(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String toCsv() {
        return username + "," + password + "," + displayName;
    }

    public static User fromCsv(String line) {
        String[] parts = line.split(",", 3);
        return new User(parts[0], parts[1], parts[2]);
    }

    @Override
    public String toString() {
        return "User{username='" + username + "', displayName='" + displayName + "'}";
    }
}