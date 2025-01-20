package org.domain.classes;

/**
 * Represents a user in the system.
 * Manages user attributes.
 * @author Sergio Shmyhelskyy Yaskevych (sergio.shmyhelskyy@estudiantat.upc.edu)
 */
public class User {
    private int userID;
    private String username;
    private String password;
    private String email;

    /**
     * Constructs a new User.
     *
     * @param userID the user ID
     * @param username the username
     * @param password the password
     * @param email the email
     */
    public User(int userID, String username, String email, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string representation of the user.
     * @return a string representation of the user
     */
    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}