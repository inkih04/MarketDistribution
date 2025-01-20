package org.domain.classes;

import org.domain.controllers.UserManager;
import org.domain.exceptions.NoActiveUserException;
import org.domain.exceptions.UnauthorizedAccessException;

import java.util.Set;

/**
 * Represents an admin user in the system.
 * Admin users have elevated privileges and can manage product lists and distributions for other users.
 * @author Sergio Shmyhelskyy Yaskevych (sergio.shmyhelskyy@estudiantat.upc.edu)
 * @version 1.0
 */
public class AdminUser extends CurrentUser {

    /**
     * Constructs an AdminUser with the specified details.
     *
     * @param userID   The unique identifier for the user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param email    The email address of the user.
     */
    public AdminUser(int userID, String username, String password, String email) {
        super(userID, username, password, email);
    }

    /**
     * Lists all product lists managed by the user manager.
     *
     * @param userManager The user manager instance.
     * @return A set of all product list names.
     */
    public Set<String> listAllProductLists(UserManager userManager) {
        return userManager.getAllProductLists();
    }

    /**
     * Lists all distributions managed by the user manager.
     *
     * @param userManager The user manager instance.
     * @return A set of all distribution names.
     */
    public Set<String> listAllDistributions(UserManager userManager) {
        return userManager.getAllDistributions();
    }

    /**
     * Adds a product list to a specified user.
     *
     * @param userManager     The user manager instance.
     * @param targetUsername  The username of the target user.
     * @param productListName The name of the product list to add.
     * @throws NoActiveUserException       If there is no active user.
     * @throws UnauthorizedAccessException If the target user is not found.
     */
    public void addProductListToUser(UserManager userManager, String targetUsername, String productListName) throws NoActiveUserException, UnauthorizedAccessException {
        CurrentUser targetUser = (CurrentUser) userManager.getUser(targetUsername.hashCode());
        if (targetUser != null) {
            targetUser.addProductList(productListName);
        } else {
            throw new UnauthorizedAccessException("User not found: " + targetUsername);
        }
    }

    /**
     * Removes a product list from a specified user.
     *
     * @param userManager     The user manager instance.
     * @param targetUsername  The username of the target user.
     * @param productListName The name of the product list to remove.
     * @throws NoActiveUserException       If there is no active user.
     * @throws UnauthorizedAccessException If the target user is not found.
     */
    public void removeProductListFromUser(UserManager userManager, String targetUsername, String productListName) throws NoActiveUserException, UnauthorizedAccessException {
        CurrentUser targetUser = (CurrentUser) userManager.getUser(targetUsername.hashCode());
        if (targetUser != null) {
            targetUser.removeProductList(productListName);
        } else {
            throw new UnauthorizedAccessException("User not found: " + targetUsername);
        }
    }

    /**
     * Adds a distribution to a specified user.
     *
     * @param userManager     The user manager instance.
     * @param targetUsername  The username of the target user.
     * @param distributionName The name of the distribution to add.
     * @throws NoActiveUserException       If there is no active user.
     * @throws UnauthorizedAccessException If the target user is not found.
     */
    public void addDistributionToUser(UserManager userManager, String targetUsername, String distributionName) throws NoActiveUserException, UnauthorizedAccessException {
        CurrentUser targetUser = (CurrentUser) userManager.getUser(targetUsername.hashCode());
        if (targetUser != null) {
            targetUser.addDistribution(distributionName);
        } else {
            throw new UnauthorizedAccessException("User not found: " + targetUsername);
        }
    }

    /**
     * Removes a distribution from a specified user.
     *
     * @param userManager     The user manager instance.
     * @param targetUsername  The username of the target user.
     * @param distributionName The name of the distribution to remove.
     * @throws NoActiveUserException       If there is no active user.
     * @throws UnauthorizedAccessException If the target user is not found.
     */
    public void removeDistributionFromUser(UserManager userManager, String targetUsername, String distributionName) throws NoActiveUserException, UnauthorizedAccessException {
        CurrentUser targetUser = (CurrentUser) userManager.getUser(targetUsername.hashCode());
        if (targetUser != null) {
            targetUser.removeDistribution(distributionName);
        } else {
            throw new UnauthorizedAccessException("User not found: " + targetUsername);
        }
    }
}