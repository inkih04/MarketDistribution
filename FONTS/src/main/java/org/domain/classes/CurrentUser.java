package org.domain.classes;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the currently active user in the system.
 * Manages user attributes, product lists, and distributions.
 * @author Sergio Shmyhelskyy Yaskevych (sergio.shmyhelskyy@estudiantat.upc.edu)
 */
public class CurrentUser extends User {
    private Set<String> distributions;
    private Set<String> productLists;

    /**
     * Constructs a new CurrentUser.
     *
     * @param userID the user ID
     * @param username the username
     * @param password the password
     * @param email the email
     */
    public CurrentUser(int userID, String username, String password, String email) {
        super(userID, username, password, email);
        this.distributions = new HashSet<>();
        this.productLists = new HashSet<>();
    }

    /**
     * Adds a product list name of the user.
     *
     * @param productListName the product list name to add
     */
    public void addProductList(String productListName) { productLists.add(productListName); }

    /**
     * Removes a product list from the user by name.
     *
     * @param productListName the name of the product list to remove
     */
    public void removeProductList(String productListName) { productLists.remove(productListName); }

    /**
     * Gets all product lists of the user.
     *
     * @return a list of all product lists
     */
    public Set<String> getUserProductLists() { return productLists; }

    /**
     * Adds a distribution to the current user.
     *
     * @param distributionName the distribution to add
     */
    public void addDistribution(String distributionName) { distributions.add(distributionName); }

    /**
     * Removes a distribution from the current user by name.
     *
     * @param distributionName the name of the distribution to remove
     */
    public void removeDistribution(String distributionName) { distributions.remove(distributionName); }

    /**
     * Gets all distributions of the current user.
     *
     * @return a list of all distributions
     */
    public Set<String> getUserDistributions() { return distributions; }

    /**
     * Sets the distributions of the current user.
     *
     * @param distributions the distributions to set
     */
    public void setDistributions(Set<String> distributions) { this.distributions = distributions; }

    /**
     * Sets the product lists of the current user.
     *
     * @param productLists the product lists to set
     */
    public void setProductLists(Set<String> productLists) { this.productLists = productLists; }

    /**
     * Returns a string representation of the current user.
     * @return a string representation of the current user
     */
    @Override
    public String toString() {
        return "CurrentUser{" +
                "userID=" + getUserID() +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}