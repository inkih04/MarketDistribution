package org.domain.controllers;

import org.domain.classes.AdminUser;
import org.domain.classes.UserSet;
import org.domain.classes.User;
import org.domain.classes.CurrentUser;

import org.domain.exceptions.NoActiveUserException;
import org.domain.exceptions.UnauthorizedAccessException;
import org.domain.exceptions.UserException;

import java.util.Set;

/**
 * This class manages user operations such as registration, login, and data management.
 * @author Sergio Shmyhelskyy Yaskevych (sergio.shmyhelskyy@estudiantat.upc.edu)
 * @version 1.0
 */
public class UserManager {

    private UserSet userSet;
    private CurrentUser activeUser;

    /**
     * Private constructor to initialize the user set and active user.
     */
    private UserManager() {
        userSet = new UserSet();
        activeUser = null;
    }

    /**
     * Private constructor to initialize the user set with a given set of users.
     *
     * @param userSet the initial set of users
     */
    private UserManager(UserSet userSet) {
        this.userSet = userSet;
        activeUser = null;
    }

    /**
     * Singleton holder class for lazy initialization.
     */
    private static class SingletonHelper {
        public static final UserManager INSTANCE = new UserManager();
    }

    /**
     * Gets the singleton instance of UserManager.
     *
     * @return the singleton instance
     */
    public static UserManager getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * Loads user data for the active user.
     *
     * @param username the username of the active user
     * @param productLists the product lists of the active user
     * @param distributions the distributions of the active user
     */
    public void loadUserData(String username, Set<String> productLists, Set<String> distributions) {
        if (activeUser != null && activeUser.getUsername().equals(username)) {
            activeUser.setProductLists(productLists);
            activeUser.setDistributions(distributions);
        }
    }

    /**
     * Sets the user set for the user manager.
     *
     * @param userSet the new user set to be assigned
     */
    public void setUserSet(UserSet userSet) {
        this.userSet = userSet;
    }

    /**
     * Removes user data for the active user.
     *
     * @param username the username of the active user
     */
    public void removeUserData(String username) {
        if (activeUser != null && activeUser.getUsername().equals(username)) {
            activeUser.setProductLists(null);
            activeUser.setDistributions(null);
        }
    }

    /**
     * Gets the current active user.
     *
     * @return the current active user
     */
    public CurrentUser getCurrentUser() {
        return activeUser;
    }

    /**
     * Gets the username of the active user.
     *
     * @return the username of the active user
     */
    public String getActiveUsername() {
        return activeUser.getUsername();
    }

    /**
     * Gets a user by their ID.
     *
     * @param userID the ID of the user to get
     * @return the user with the specified ID, or null if not found
     */
    public User getUser(int userID) {
        return userSet.getUser(userID);
    }

    /**
     * Gets all distributions from the active user.
     *
     * @return a set of all distributions
     * @throws NoActiveUserException if there is no active user
     */
    public Set<String> getDistributions() throws NoActiveUserException {
        if (activeUser == null) throw new NoActiveUserException();
        return activeUser.getUserDistributions();
    }

    /**
     * Gets all distributions from all users.
     *
     * @return a set of all distributions
     */
    public Set<String> getAllDistributions() {
        return userSet.getDistributions();
    }

    /**
     * Gets all product lists from the active user.
     *
     * @return a set of all product lists
     * @throws NoActiveUserException if there is no active user
     */
    public Set<String> getProductLists() throws NoActiveUserException {
        if (activeUser == null) throw new NoActiveUserException();
        return activeUser.getUserProductLists();
    }

    /**
     * Gets all product lists from all users.
     *
     * @return a set of all product lists
     */
    public Set<String> getAllProductLists() {
        return userSet.getProductLists();
    }

    /**
     * Checks if a user exists by their ID.
     *
     * @param userID the ID to check
     * @return true if the user exists, false otherwise
     */
    public boolean userExists(int userID) {
        return userSet.userExists(userID);
    }

    /**
     * Checks if the active user is an admin.
     *
     * @return true if the active user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return activeUser instanceof AdminUser;
    }

    /**
     * Returns the product lists of the active user.
     *
     * @return a set of product lists
     * @throws NoActiveUserException if there is no active user
     */
    public Set<String> getUserProductLists() throws NoActiveUserException {
        if (activeUser == null) throw new NoActiveUserException();
        return activeUser.getUserProductLists();
    }

    /**
     * Registers a new user.
     *
     * @param username the username
     * @param email the email
     * @param password the password
     * @throws UnauthorizedAccessException if the username is empty
     * @throws UserException if the username or email is already in use
     */
    public void registerUser(String username, String email, String password) throws UnauthorizedAccessException, UserException {
        if (username.isEmpty()) throw new UnauthorizedAccessException("The username must not be empty");
        if (userSet.usernameExists(username)) throw new UserException("Username already in use");
        if (userSet.emailExists(email)) throw new UserException("Email already in use");
        int userID = (username + email).hashCode();
        User newUser = new User(userID, username, email, password);
        userSet.addUser(newUser);
    }

    /**
     * Registers a new admin user.
     *
     * @param username the username
     * @param password the password
     * @param email the email
     * @throws UnauthorizedAccessException if the username is empty
     * @throws UserException if the username or email is already in use
     */
    public void registerAdmin(String username, String password, String email) throws UnauthorizedAccessException, UserException {
        int userID = username.hashCode();
        if (username.isEmpty()) throw new UnauthorizedAccessException("The username must not be empty");
        AdminUser adminUser = new AdminUser(userID, username, password, email);
        userSet.addUser(adminUser);
    }

    /**
     * Logs in with an existing user.
     *
     * @param userInput the username or email
     * @param password the password
     * @throws UnauthorizedAccessException if the username/email or password is incorrect
     */
    public void loginUser(String userInput, String password) throws UnauthorizedAccessException {
        User user = userSet.getUser(userInput.hashCode());
        if (user == null) {
            user = userSet.getUserByEmail(userInput);
        }
        if (user != null && user.getPassword().equals(password)) {
            activeUser = new CurrentUser(user.getUserID(), user.getUsername(), user.getEmail(), user.getPassword());
        } else {
            throw new UnauthorizedAccessException("Wrong username/email or password");
        }
    }

    /**
     * Logs out the active user.
     *
     * @return true if the logout was successful, false otherwise
     */
    public boolean logoutUser() {
        if (activeUser != null) {
            activeUser = null;
            return true;
        } else return false;
    }

    /**
     * Deletes a user by username.
     *
     * @param username the username of the user to delete
     * @return true if the user was deleted, false otherwise
     */
    public boolean deleteUser(String username) {
        int userID = username.hashCode();
        if (!userSet.userExists(userID)) {
            return false;
        }
        userSet.removeUser(userID);
        if (activeUser != null && activeUser.getUsername().equals(username)) {
            activeUser = null;
        }
        return true;
    }

    /**
     * Adds a product list to the active user.
     *
     * @param productListName the product list to add
     * @throws NoActiveUserException if there is no active user
     */
    public void addProductList(String productListName) throws NoActiveUserException {
        if (activeUser == null) throw new NoActiveUserException();
        activeUser.addProductList(productListName);
    }

    /**
     * Deletes a product list from the active user by name.
     *
     * @param productListName the name of the product list to delete
     * @throws NoActiveUserException if there is no active user
     */
    public void deleteProductList(String productListName) throws NoActiveUserException {
        if (activeUser == null) throw new NoActiveUserException();
        activeUser.removeProductList(productListName);
    }

    /**
     * Adds a distribution to the active user.
     *
     * @param distributionName the distribution to add
     * @throws NoActiveUserException if there is no active user
     */
    public void addDistribution(String distributionName) throws NoActiveUserException {
        if (activeUser == null) throw new NoActiveUserException();
        activeUser.addDistribution(distributionName);
    }

    /**
     * Deletes a distribution from the active user by name.
     *
     * @param distributionName the name of the distribution to delete
     * @throws NoActiveUserException if there is no active user
     */
    public void deleteDistribution(String distributionName) throws NoActiveUserException {
        if (activeUser == null) throw new NoActiveUserException();
        activeUser.removeDistribution(distributionName);
    }

    /**
     * Changes the password of the active user.
     *
     * @param newPassword the new password
     * @throws NoActiveUserException if there is no active user
     */
    public void changePassword(String newPassword) throws NoActiveUserException {
        if (activeUser == null) throw new NoActiveUserException();
        activeUser.setPassword(newPassword);
    }

    /**
     * Changes the username of the active user.
     *
     * @param newUsername the new username
     * @throws NoActiveUserException if there is no active user
     */
    public void changeUsername(String newUsername) throws NoActiveUserException {
        if (activeUser == null) throw new NoActiveUserException();
        activeUser.setUsername(newUsername);
    }

    /**
     * Deletes the current active user account.
     *
     * @throws NoActiveUserException if there is no active user
     */
    public void deleteCurrentAccount() throws NoActiveUserException {
        if (activeUser == null) throw new NoActiveUserException();
        userSet.removeUser(activeUser.getUserID());
        activeUser = null;
    }
}