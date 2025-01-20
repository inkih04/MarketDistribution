package org.domain.classes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * This class represents a set of users and provides methods to manage them.
 * @author Sergio Shmyhelskyy Yaskevych (sergio.shmyhelskyy@estudiantat.upc.edu)
 * @version 1.0
 */
public class UserSet {

    private Map<Integer, User> users;
    private Map<String, User> usersByUsername;
    private Map<String, User> usersByEmail;

    /**
     * Default constructor initializing the user maps.
     */
    public UserSet() {
        users = new HashMap<>();
        usersByUsername = new HashMap<>();
        usersByEmail = new HashMap<>();
    }

    /**
     * Constructor initializing the user maps with a given set of users.
     *
     * @param users the initial set of users
     */
    public UserSet(Map<Integer, User> users) {
        this.users = users;
        usersByUsername = new HashMap<>();
        usersByEmail = new HashMap<>();
        for (User user : users.values()) {
            usersByUsername.put(user.getUsername(), user);
            usersByEmail.put(user.getEmail(), user);
        }
    }

    /**
     * Adds a user to the set.
     *
     * @param user the user to add
     */
    public void addUser(User user) {
        users.put(user.getUserID(), user);
        usersByUsername.put(user.getUsername(), user);
        usersByEmail.put(user.getEmail(), user);
    }

    /**
     * Removes a user from the set by user ID.
     *
     * @param userID the ID of the user to remove
     */
    public void removeUser(int userID) {
        User user = users.remove(userID);
        if (user != null) {
            usersByUsername.remove(user.getUsername());
            usersByEmail.remove(user.getEmail());
        }
    }

    /**
     * Gets a user by their ID.
     *
     * @param userID the ID of the user to get
     * @return the user with the specified ID, or null if not found
     */
    public User getUser(int userID) {
        return users.get(userID);
    }

    /**
     * Gets a user by their email.
     *
     * @param email the email of the user to get
     * @return the user with the specified email, or null if not found
     */
    public User getUserByEmail(String email) {
        return usersByEmail.get(email);
    }

    /**
     * Gets a user by their username.
     *
     * @param username the username of the user to get
     * @return the user with the specified username, or null if not found
     */
    public User getUserByUsername(String username) {
        return usersByUsername.get(username);
    }

    /**
     * Checks if a user exists by their ID.
     *
     * @param userID the ID to check
     * @return true if the user exists, false otherwise
     */
    public boolean userExists(int userID) {
        return users.containsKey(userID);
    }

    /**
     * Checks if a username exists.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return usersByUsername.containsKey(username);
    }

    /**
     * Checks if an email exists.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return usersByEmail.containsKey(email);
    }

    /**
     * Gets the number of users in the set.
     *
     * @return the number of users
     */
    public int getNumberOfUsers() {
        return users.size();
    }

    /**
     * Gets a list of all user IDs.
     *
     * @return a list of user IDs
     */
    public List<Integer> getUserIDs() {
        return new ArrayList<>(users.keySet());
    }

    /**
     * Gets all product lists from all users.
     *
     * @return a set of all product lists
     */
    public Set<String> getProductLists() {
        Set<String> productLists = new HashSet<>();
        for (User user : users.values()) {
            if (user instanceof CurrentUser) {
                productLists.addAll(((CurrentUser) user).getUserProductLists());
            }
        }
        return productLists;
    }

    /**
     * Gets all distributions from all users.
     *
     * @return a set of all distributions
     */
    public Set<String> getDistributions() {
        Set<String> distributions = new HashSet<>();
        for (User user : users.values()) {
            if (user instanceof CurrentUser) {
                distributions.addAll(((CurrentUser) user).getUserDistributions());
            }
        }
        return distributions;
    }

    /**
     * Checks if the user set is empty.
     *
     * @return true if the user set is empty, false otherwise
     */
    public boolean isUserSetEmpty() {
        return users.isEmpty();
    }

    /**
     * Returns a string representation of the UserSet.
     *
     * @return a string representation of the UserSet
     */
    @Override
    public String toString() {
        return "UserSet{" +
                "users=" + users +
                '}';
    }
}