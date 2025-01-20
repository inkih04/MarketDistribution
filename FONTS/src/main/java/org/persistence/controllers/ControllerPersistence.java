package org.persistence.controllers;

import org.domain.classes.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Set;

/**
 * ControllerPersistence
 * <p>
 * This class is responsible for managing the persistence of user and product data within the system.
 * It provides methods to load, save, and register users, as well as to manage the product catalog and similarities.
 * The class follows the Singleton design pattern to ensure that only one instance of the controller exists.
 * </p>
 */
public class ControllerPersistence {
    private UserManagerData userManagerData;
    private ProductManagerData productManagerData;

    /**
     * Constructor
     * <p>
     * Initializes the ControllerPersistence instance by creating instances of UserManagerData and ProductManagerData.
     * It also loads the user credentials and product catalog from their respective configuration files.
     * </p>
     */
    public ControllerPersistence() {
        productManagerData = new ProductManagerData();
        userManagerData = new UserManagerData(productManagerData);

        // Load product catalog and user credentials
        loadProductCatalog();
        loadUsersCredentials();
    }

    /**
     * Returns the instance of the ControllerPersistence class.
     * <p>
     * This method ensures that only one instance of the ControllerPersistence class exists.
     * If the instance is null, it creates a new instance; otherwise, it returns the existing instance.
     * </p>
     *
     * @return the instance of the ControllerPersistence class
     */
    public static ControllerPersistence getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * SingletonHelper
     * <p>
     * This static inner class is used to hold the singleton instance of the ControllerPersistence class.
     * The instance is created when the class is loaded, ensuring thread-safe lazy initialization.
     * </p>
     */
    private static class SingletonHelper {
        private static final ControllerPersistence INSTANCE = new ControllerPersistence();
    }

    /**
     * Initializes the configuration files for user and product data.
     * <p>
     * This method calls the initConfigFile methods of UserManagerData and ProductManagerData
     * to ensure that the necessary configuration files are created and initialized.
     * </p>
     */
    public void initConfigFiles() {
        userManagerData.initConfigFile();
        productManagerData.initConfigFile();
    }

    /**
     * Loads users into the system from the configuration file.
     * <p>
     * This method calls the loadUserCredentials method of UserManagerData to load user credentials
     * from the configuration file and returns the loaded UserSet.
     * </p>
     *
     * @return the UserSet containing the loaded user credentials
     */
    public UserSet loadUsersCredentials() {
        return userManagerData.loadUserCredentials();
    }

    /**
     * Loads the product catalog from the file.
     * <p>
     * This method calls the loadProductCatalog method of ProductManagerData to load the product catalog
     * from the configuration file.
     * </p>
     */
    public void loadProductCatalog() {
        productManagerData.loadProductCatalog();
    }

    /**
     * Retrieves the product catalog.
     * <p>
     * This method returns the product catalog map from ProductManagerData.
     * </p>
     *
     * @return the product catalog map
     */
    public Map<String, Product> getProductCatalog() {
        return productManagerData.getProductCatalog();
    }

    /**
     * Retrieves the product similarities.
     * <p>
     * This method returns the product similarities map from ProductManagerData.
     * </p>
     *
     * @return the product similarities map
     */
    public Map<String, Map<String, Double>> getProductSimilarities() {
        return productManagerData.getProductSimilarities();
    }

    /**
     * Saves the product catalog.
     * <p>
     * This method calls the saveProductCatalog method of ProductManagerData to save the product catalog
     * and similarities to the configuration file.
     * </p>
     */
    public void saveProductCatalog() {
        productManagerData.saveProductCatalog();
    }

    /**
     * Retrieves the user credentials.
     * <p>
     * This method returns the user credentials map from UserManagerData.
     * </p>
     *
     * @return the user credentials map
     */
    public Map<Integer, User> getUserCredentials() {
        return userManagerData.getUserCredentials();
    }

    /**
     * Saves users from the system to the configuration file.
     * <p>
     * This method calls the saveUserCredentials method of UserManagerData to save the user credentials
     * to the configuration file.
     * </p>
     */
    public void saveUsers() {
        userManagerData.saveUserCredentials();
    }

    /**
     * Saves the user distributions.
     * <p>
     * This method calls the saveUserDistributions method of UserManagerData to save the distributions
     * of a specific user to the configuration file.
     * </p>
     *
     * @param username the username of the user
     * @param distributions the list of distributions to be saved
     */
    public void saveUserDistributions(String username, List<Distribution> distributions) {
        userManagerData.saveUserDistributions(username, distributions);
    }

    /**
     * Saves the user shelves.
     * <p>
     * This method calls the saveUserShelves method of UserManagerData to save the shelves
     * of a specific user to the configuration file.
     * </p>
     *
     * @param username the username of the user
     * @param allShelves the set of shelves to be saved
     */
    public void saveUserShelves(String username, Set<Shelf> allShelves) {
        userManagerData.saveUserShelves(username, allShelves);
    }

    /**
     * Saves the user product lists.
     * <p>
     * This method calls the saveUserProductLists method of UserManagerData to save the product lists
     * of a specific user to the configuration file.
     * </p>
     *
     * @param username the username of the user
     * @param productLists the map of product lists to be saved
     */
    public void saveUserProductLists(String username, Map<String, ProductList> productLists) {
        userManagerData.saveUserProductLists(username, productLists);
    }

    /**
     * Registers a new user.
     * <p>
     * This method calls the registerUser method of UserManagerData to register a new user
     * with the specified username, email, and password.
     * </p>
     *
     * @param username the username of the new user
     * @param email the email of the new user
     * @param password the password of the new user
     */
    public void registerUser(String username, String email, String password) {
        userManagerData.registerUser(username, email, password);
    }

    /**
     * Deletes a user.
     * <p>
     * This method calls the deleteUser method of UserManagerData to delete a user
     * with the specified username from the system.
     * </p>
     *
     * @param username the username of the user to be deleted
     */
    public void deleteUser(String username) {
        userManagerData.deleteUser(username);
    }

    /**
     * Loads the user product lists.
     * <p>
     * This method calls the loadUserProductLists method of UserManagerData to load the product lists
     * of a specific user from the configuration file.
     * </p>
     *
     * @param username the username of the user
     * @return the map of product lists
     */
    public Map<String, ProductList> loadUserProductLists(String username) {
        return userManagerData.loadUserProductLists(username);
    }

    /**
     * Loads the user distributions.
     * <p>
     * This method calls the loadUserDistributions method of UserManagerData to load the distributions
     * of a specific user from the configuration file.
     * </p>
     *
     * @param username the username of the user
     * @param productCatalog the list of products to create the associations with the distributions
     * @return the map of distributions
     */
    public Map<String, Distribution> loadUserDistributions(String username, ArrayList<Product> productCatalog) {
        return userManagerData.loadUserDistributions(username, productCatalog);
    }

    /**
     * Loads the user shelves.
     * <p>
     * This method calls the loadUserShelves method of UserManagerData to load the shelves
     * of a specific user from the configuration file.
     * </p>
     *
     * @param username the username of the user
     * @param distributionsList the map of distributions to be associated with the shelves
     * @param productLists the map of product lists to create the associations with the shelves
     * @return the map of shelves of the user
     */
    public Map<Integer, Shelf> loadUserShelves(String username, Map<String, Distribution> distributionsList, Map<String, ProductList> productLists) {
        return userManagerData.loadUserShelves(username, distributionsList, productLists);
    }
}