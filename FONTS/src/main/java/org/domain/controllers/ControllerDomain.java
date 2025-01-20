package org.domain.controllers;

import org.domain.classes.Product;
import org.domain.classes.ProductList;
import org.domain.classes.Shelf;
import org.domain.classes.Distribution;

import org.persistence.controllers.ControllerPersistence;

import org.domain.exceptions.ProductException;
import org.domain.exceptions.ProductListException;
import org.domain.exceptions.ShelfException;
import org.domain.exceptions.DistributionException;
import org.domain.exceptions.UnauthorizedAccessException;
import org.domain.exceptions.UserException;

import org.domain.types.TupleType;
import org.domain.types.Pair;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.List;

/**
 * ControllerDomain
 * Manages the main operations of the application, including handling products,
 * product lists, shelves, and data file processing.
 * Coordinates with ProductManager and DataParser classes to operate on the catalog
 * and product lists.
 * @version 1.0
 */
public class ControllerDomain {
    private final UserManager userManager;
    private final ProductManager productManager; ///< Manages the product catalog and lists.
    private final DistributionManager distributionManager;  ///< Manages the distribution of products in shelves.
    private final ShelfManager shelfManager;    ///< Manages the shelves in the system.

    private final InputManager inputManager;    ///< Manages the input data from files.
    private final ControllerPersistence controllerPersistence;    ///< Manages the persistence of data.

    private final TreeMap<LocalDateTime, String> operationLog; ///< Log of operations with date and details

    /**
     * Constructs a new ControllerDomain instance and initializes its components.
     * <p>
     * This constructor initializes various managers and components used by the ControllerDomain class,
     * including the shelf map, product manager, similarity manager, input manager, distribution manager,
     * persistence controller, user manager, and operation log. It also loads user credentials and configuration
     * files as part of the initialization process.
     */
    public ControllerDomain() {
        // Load data from persistence
        this.controllerPersistence = ControllerPersistence.getInstance();
        this.inputManager = InputManager.getInstance();

        this.userManager = UserManager.getInstance();
        userManager.setUserSet(controllerPersistence.loadUsersCredentials());
        this.productManager = ProductManager.getInstance(controllerPersistence.getProductCatalog(),
                                                         controllerPersistence.getProductSimilarities());
        this.distributionManager = DistributionManager.getInstance();

        this.shelfManager = ShelfManager.getInstance();
        this.operationLog = new TreeMap<>();

    }

    /**
     * Returns the single instance of the ControllerDomain class.
     * <p>
     * This method utilizes the Singleton design pattern to ensure that only one instance
     * of the ControllerDomain is created and provides a global point of access to it.
     *
     * @return the single instance of ControllerDomain
     */
    public static ControllerDomain getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * SingletonHelper class for ControllerDomain.
     * <p>
     * Holds the single instance of ControllerDomain.
     * Static nested class responsible for holding the single instance of ControllerDomain
     */
    private static class SingletonHelper {
        private static final ControllerDomain INSTANCE = new ControllerDomain();
    }

    //! User Methods
    //!---------------------------------------------------------------------------------------------------------

    /**
     * Logs in a user into the system.
     * Validates the user's credentials and loads the associated data, including product lists and distributions.
     *
     * @param userInput the username or identifier provided by the user.
     * @param password the corresponding password for the user.
     * @throws UnauthorizedAccessException if the provided credentials are invalid.
     */
    public void loginUser(String userInput, String password) throws UnauthorizedAccessException {
        userManager.loginUser(userInput, password);

        String username = userManager.getActiveUsername();
        Map<String, ProductList> productList = controllerPersistence.loadUserProductLists(username);
        Map<String, Distribution> distributionsList = controllerPersistence.loadUserDistributions(username, productManager.getAllProducts());
        Map<Integer, Shelf> shelfMap = controllerPersistence.loadUserShelves(username,distributionsList,productList);

        // Load the data into the system
        productManager.setProductLists(productList);
        distributionManager.setDistributions(distributionsList);
        shelfManager.setShelves(shelfMap);

        userManager.loadUserData(username, productList.keySet(), distributionsList.keySet());

        logOperation("User logged in: " + username);
    }

    /**
     * Logs out the currently active user from the system.
     * Saves the user's data before logging out, clears user-related data from the system memory,
     * and records the operation in the log.
     *
     *       <p>This method performs the following steps to log out a user:</p>
     *       <ol>
     *         <li>Retrieves the active username from the {@code UserManager}.</li>
     *         <li>Calls the {@code logoutUser} method of the {@code UserManager} to log out the user.</li>
     *         <li>If the logout is successful:
     *           <ol>
     *             <li>Saves the user's product lists to the persistence layer using the {@code ControllerPersistence}.</li>
     *             <li>Clears the product lists from the {@code ProductManager}.</li>
     *             <li>Clears the shelf map, removing all shelves from the system.</li>
     *             <li>Removes the user's data from the {@code UserManager}.</li>
     *             <li>Logs the logout operation in the operation log.</li>
     *           </ol>
     *         </li>
     *       </ol>
     *
     * @throws UserException if an issue occurs during the logout process.
     */
    public void logoutUser() throws UserException {
        String username = userManager.getActiveUsername();
        boolean logoutUser = userManager.logoutUser();
        if (logoutUser) {
            controllerPersistence.saveUserProductLists(username, productManager.getProductLists());
            controllerPersistence.saveUserDistributions(username, distributionManager.getAllDistributions());
            controllerPersistence.saveUserShelves(username, shelfManager.getAllShelves());
            controllerPersistence.saveProductCatalog();


            // Erase the data from the system
            productManager.removeProductLists();
            distributionManager.removeDistributions();
            shelfManager.deleteAllShelves();
            userManager.removeUserData(username);

            logOperation("User logged out: " + username);
        }
        else throw new UserException("There was a problem while logging out");
    }

    /**
     * Registers a new user in the system.
     *
     * <p>This method performs the following steps to register a new user:</p>
     * <ol>
     *   <li>Calls the {@code registerUser} method of the {@code UserManager} to register the user with the provided username, email, and password.</li>
     *   <li>Calls the {@code registerUser} method of the {@code ControllerPersistence} to persist the user data.</li>
     *   <li>Logs the registration operation in the operation log.</li>
     * </ol>
     *
     * <p>If the registration is successful, the user is added to the system and their credentials are stored in the persistence layer.</p>
     *
     * @param username the username of the new user
     * @param email the email address of the new user
     * @param password the password for the new user
     * @throws UnauthorizedAccessException if the user does not have permission to register
     * @throws UserException if there is an error during the registration process
     */
    public void registerUser(String username, String email, String password) throws UnauthorizedAccessException, UserException {
        userManager.registerUser(username, email, password);
        controllerPersistence.registerUser(username, email, password);
        logOperation("User registered: " + username);
    }

    //! Product and ProductList Methods
    //!---------------------------------------------------------------------------------------------------------

    /**
     * Adds a new product to the catalog with the specified details.
     * <p>
     * This method adds a product to the catalog by passing the provided product information to the product manager.
     * It trims the product name and processes the category, price, amount, and similar products as part of the addition.
     *
     * @param productName the name of the product to be added
     * @param category the category under which the product falls
     * @param price the price of the product
     * @param amount the quantity of the product
     * @param similarities a pair&lt;productName, similarity&gt;
     * @throws ProductException if any parameter is invalid
     */
    public void addProductToCatalog(String productName, String category, double price, int amount, ArrayList<Pair<String, Double>> similarities) throws ProductException{
        productManager.addProductToCatalog(productName.trim(), category, price, amount, similarities);
        logOperation("Product added to catalog: " + productName);
    }

    /**
     * Removes a product from the catalog by its name.
     * <p>
     * This method removes the specified product from the catalog by invoking the product manager's method
     * to handle the removal process.
     *
     * @param productName the name of the product to be removed from the catalog
     *
     * @throws ProductException if there is an error while removing the product from the catalog
     */
    public void removeProductFromCatalog(String productName) throws ProductException {
        productManager.removeProductFromCatalog(productName);
        logOperation("Product removed from catalog: " + productName);
    }

    /**
     * Updates a product in the catalog.
     * @param name the name of the product
     * @param category the category of the product
     * @param price the price of the product
     * @param amount the quantity of the product
     * @param similarities a list of pairs with the name of the product and the similarity value
     * @throws ProductException if the product name is invalid
     */
    public void updateProduct(String name, String category, double price, int amount, ArrayList<Pair<String, Double>> similarities) throws ProductException{
        productManager.updateProductFromCatalog(name, category, price, amount, similarities);
        logOperation("Product updated: " + name + " , by user: " + userManager.getActiveUsername());
    }

    /**
     * Creates a new product list with a specified category.
     * @param listName the name of the product list
     * @param category the category of products in the list
     * @throws ProductListException if the list name is invalid
     */
    public void createProductList(String listName, String category) throws ProductListException {
        productManager.createProductList(listName, category);
        logOperation("Product list created: " + listName + " with category: " + category + " , by user: " + userManager.getActiveUsername());
    }

    /**
     * Adds a product to a specified list.
     * <p>
     * This method adds a product to a product list identified by the list name. The product is added
     * by passing the product name and the list name to the product manager.
     *
     * @param productName the name of the product to be added to the list
     * @param listName the name of the list to which the product will be added
     *
     * @throws ProductException if there is an error while adding the product to the list
     * @throws ProductListException if there is an issue with the specified product list
     */
    public void addProductToList(String listName, String productName) throws ProductException, ProductListException {
        productManager.addProductToList(listName, productName);
        logOperation("Product " + productName + " added to list: " + listName + " , by user: " + userManager.getActiveUsername());
    }

    /**
     * Brief: Removes a product from a specific list.
     * @param productName the name of the product
     * @param listName the name of the product list
     * @throws ProductException if the product name is invalid
     * @throws ProductListException if the list name is invalid
     */
    public void removeProductFromList(String productName, String listName) throws ProductException, ProductListException {
        productManager.removeProductFromList(listName, productName);
        logOperation("Product removed from list: " + productName + " from list: " + listName + " , by user: " + userManager.getActiveUsername());
    }

    /**
     * Brief: Removes a set of products from a specific list.
     * @param listName the name of the product list
     * @param productNames the set of product names
     * @throws ProductException if the product name is invalid
     * @throws ProductListException if the list name is invalid
     */
    public void removeProductsFromList(String listName, Set<String> productNames) throws ProductException, ProductListException {
        for (String productName : productNames) {
            productManager.removeProductFromList(listName, productName);
            logOperation("Product " + productName + " removed from list: " + listName + " , by user: " + userManager.getActiveUsername());
        }
    }

    /**
     * Displays the similarities of products in the catalog.
     * <p>
     * This method retrieves and returns a string representation of the similarities between products,
     * as managed by the product manager.
     *
     * @return a string containing the product similarities
     */
    public String showSimilarity() {
        return productManager.showSimilarities();
    }

    /**
     * Adds a similarity relationship between two products.
     * <p>
     * This method establishes a similarity relationship between two products identified by their names
     * and assigns a similarity score. It retrieves the products and passes them along with the similarity score
     * to the product manager for processing.
     *
     * @param productName1 the name of the first product
     * @param productName2 the name of the second product
     * @param similarity the similarity score between the two products
     *
     * @throws ProductException if there is an error while adding the similarity between the products
     */
    public void setSimilarity(String productName1, String productName2, double similarity) throws ProductException {
        productManager.setSimilarity(productName1, productName2, similarity);
    }

    /**
     * Removes a product list from the catalog.
     * <p>
     * This method attempts to remove a product list identified by the provided list name. Before removing,
     * it checks if any shelf is associated with the list. If a shelf is found with the associated product list,
     * an exception is thrown to prevent the removal.
     *
     * @param listName the name of the product list to be removed
     *
     * @throws ProductListException if there is an error while removing the product list
     * @throws ShelfException if a shelf is associated with the product list and prevents its removal
     */
    public void removeProductList(String listName) throws ProductListException, ShelfException {
        ProductList productList = productManager.getProductList(listName);

        shelfManager.canBeRemoved(productList);

        productManager.removeProductList(listName);
        logOperation("Product list removed: " + listName + " , by user: " + userManager.getActiveUsername());
    }

    /**
     * Decreases the quantity of a specified product in the catalog.
     * <p>
     * This method decreases the available quantity of a product identified by its name. It delegates
     * the task to the product manager to handle the quantity reduction.
     *
     * @param productName the name of the product whose quantity is to be decreased
     * @param quantity the amount by which the product's quantity will be reduced
     *
     * @throws ProductException if there is an error while decreasing the product's quantity
     */
    public void decreaseProductQuantity(String productName, int quantity) throws ProductException {
        int remainAmount =  productManager.decreaseProductQuantity(productName, quantity);
        if (remainAmount == 0){
            logOperation("Product out of stock, removed from catalog: " + productName +
                        " ,by user: " + userManager.getActiveUsername());
        } else {
            logOperation("Product quantity decreased: " + productName + " by " + quantity +
                    " ,by user: " + userManager.getActiveUsername()
                    + " remaining amount: " + remainAmount);
        }
    }

    /**
     * Increases the quantity of a specified product in the catalog.
     * <p>
     * This method increases the available quantity of a product identified by its name. It delegates
     * the task to the product manager to handle the quantity increase.
     *
     * @param productName the name of the product whose quantity is to be increased
     * @param quantity the amount by which the product's quantity will be increased
     *
     * @throws ProductException if there is an error while increasing the product's quantity
     */
    public void increaseProductQuantity(String productName, int quantity) throws ProductException {
        int remainAmount = productManager.increaseProductQuantity(productName, quantity);
        logOperation("Product quantity increased: " + productName + " by " + quantity
                    + " , by user: " + userManager.getActiveUsername()
                    + " remaining amount: " + remainAmount);
    }

    /**
     * Retrieves and returns a string representation of a product list by its name.
     *
     * @param listName the name of the product list to retrieve
     * @return a string representation of the product list
     * @throws ProductListException if the specified list cannot be found or an error occurs while retrieving it
     */
    public String showProductList(String listName) throws ProductListException {
        return productManager.getProductList(listName).toString();
    }

    /**
     * Applies a discount to all products in a specified product list.
     * <p>
     * This method applies a discount, specified by the percentage, to all products within the given product list.
     * The operation is delegated to the product manager for processing.
     *
     * @param listName the name of the product list to which the discount will be applied
     * @param percentage the discount percentage to be applied to the products in the list
     *
     * @throws ProductListException if there is an error while applying the discount to the product list
     */
    public void applyDiscountToList(String listName, double percentage) throws ProductListException {
        productManager.applyDiscountToList(listName, percentage);
        logOperation("Discount applied to list: " + listName + " , by user: " + userManager.getActiveUsername());
    }

    /**
     * Applies a discount to a specified product and logs the operation.
     *
     * @param productName the name of the product to which the discount will be applied
     * @param discount    the discount amount to be applied (e.g., 0.1 for 10%)
     * @throws ProductException if the product cannot be found or if the discount operation fails
     *
     * <p>This method delegates the discount application to the {@code productManager}
     * and records the operation using the active username from {@code userManager}.
     */
    public void applyDiscountToProduct(String productName, double discount) throws ProductException {
        productManager.applyDiscountToProduct(productName, discount);
        logOperation("Discount ("+ discount+") applied to product: " + productName + " , by user: " + userManager.getActiveUsername());
    }

    //! Shelf and Distribution Methods
    //!---------------------------------------------------------------------------------------------------------

    /**
     * Creates a new shelf with the specified capacity, product list, and algorithm.
     * <p>
     * This method creates a shelf with a given ID, x and y capacity, and associates it with a product list
     * and an algorithm for shelf arrangement. The algorithm can either be a brute force or hill climbing method.
     * It also performs various validations to ensure the shelf's parameters are correct and that the product list
     * can fit on the shelf.
     *
     * @param id the unique identifier for the shelf
     * @param xcapacity the x-axis capacity (width) of the shelf
     * @param ycapacity the y-axis capacity (height) of the shelf
     * @param listName the name of the product list to be associated with the shelf
     *
     * @throws ShelfException if the shelf ID already exists, or if there is an invalid algorithm, or invalid shelf capacity
     * @throws ProductListException if the product list is empty, or if the product list cannot fit on the shelf due to its size
     */
    public void createShelf(int id, int xcapacity, int ycapacity, String listName) throws ShelfException, ProductListException {
        if(listName.trim().isEmpty()) throw new ProductListException("The name of the product list cannot be empty");
        ProductList productList = productManager.getProductList(listName);
        if (productList.getProducts().size() > xcapacity*ycapacity) throw new ProductListException("The list does not fit on the shelf because the quantity of products exceeds the maximum capacity.");

        shelfManager.createShelf(id, xcapacity, ycapacity, productList);

        logOperation("Shelf created: " + id + " with capacity: " + xcapacity*ycapacity + " and list: " + listName
                    + " by user: " + userManager.getActiveUsername());
    }


    /**
     * Changes the product list associated with a specified shelf.
     * <p>
     * This method replaces the current product list on a shelf with a new product list. It first checks if the shelf
     * exists and if the new product list can fit within the shelf's capacity. If the conditions are met, the product list
     * on the shelf is updated.
     *
     * @param idShelf the ID of the shelf whose product list is to be changed
     * @param listName the name of the new product list to be assigned to the shelf
     *
     * @throws ShelfException if the shelf with the specified ID does not exist
     * @throws ProductListException if the new product list exceeds the shelf's capacity
     */
    public void changeProductListAtShelf(int idShelf, String listName) throws ProductListException, ShelfException {
        ProductList productList = productManager.getProductList(listName);
        Shelf shelf = shelfManager.getShelf(idShelf);
        Set<String> distributions = shelf.getDistributionNames();
        for (String distName : distributions) {
            distributionManager.removeDistribution(distName);
        }
        shelfManager.changeProductListAtShelf(idShelf, productList);

        logOperation("Product list changed to " + listName + "at shelf" + idShelf);
    }

    /**
     * Creates a new distribution file for a specified shelf.
     * <p>
     * This method generates a distribution file based on the last distribution of the specified shelf. It saves the file
     * to the given path with the provided file name. If no path is provided, it defaults to the current directory.
     *
     * @param fileName the name of the file to store the distribution
     * @param path the directory path where the file will be saved (defaults to current directory if empty)
     * @param idShelf the ID of the shelf for which the distribution file will be created
     *
     * @throws ShelfException if the specified shelf does not exist
     * @throws DistributionException if there is an error while storing the distribution data
     * @throws IOException if an I/O error occurs while writing the file
     */
    public void createNewDistributionFile(String fileName, String path, int idShelf) throws ShelfException, DistributionException, IOException {
        if (path.trim().isEmpty()) path = "./";
        String nameDistribution = shelfManager.getNameLastDistributionShelf(idShelf);
        distributionManager.storeDistributionToFile(fileName, path, nameDistribution);
    }

    /**
     * Processes data from a file located at the specified file path.
     * <p>
     * This method reads and processes data from the file at the provided file path. It delegates the task to the
     * input manager to handle the file parsing. The file path is trimmed before being passed to the input manager.
     *
     * @param filePath the path to the file containing the data to be processed
     *
     * @throws IllegalArgumentException if the file path is invalid or if the file cannot be processed
     */
    public void getDataThroughFile(String filePath) throws IllegalArgumentException  {
        inputManager.parseFile(filePath.trim());
    }

    /**
     * Retrieves a string representation of all the shelves in the system.
     *
     * <p>This method iterates through all the shelves stored in the {@code shelfMap} and
     * appends their string representations to a single {@link StringBuilder}, which is
     * then converted to a string and returned.</p>
     *
     * @return A string containing the details of all shelves.
     */
    public String showAllShelves() {
        return shelfManager.showAllShelves();
    }

    /**
     * Retrieves a string representation of a specific shelf identified by its ID.
     *
     * <p>If the shelf with the specified ID does not exist, a {@link ShelfException} is thrown.
     * Otherwise, the string representation of the shelf is returned.</p>
     *
     * @param idShelf The ID of the shelf to retrieve.
     * @return A string containing the details of the specified shelf.
     * @throws ShelfException If no shelf with the specified ID exists.
     */
    public String showShelf(int idShelf) throws ShelfException {
        return shelfManager.showShelf(idShelf);
    }


    /**
     * Retrieves a string representation of all product lists in the system.
     *
     * <p>This method iterates through all the product lists managed by the {@code productManager}
     * and appends their string representations to a {@link StringBuilder}. The resulting string
     * contains the details of all product lists in the system.</p>
     *
     * @return A string containing the details of all product lists.
     */
    public String showAllLists() {
        StringBuilder sb = new StringBuilder();
        ArrayList<ProductList> productLists = productManager.getAllProductLists();
        for (ProductList productList : productLists) {
            sb.append(productList.toString());
        }
        return sb.toString();
    }

    //! Product and ProductList Type Methods
    //!---------------------------------------------------------------------------------------------------------

    /**
     * Helper method to create a TupleType for a product, including its similarities.
     * @param product the product to convert
     * @return a TupleType representing the product
     * @throws ProductException if there is an error retrieving similarities
     */
    private TupleType createProductTuple(Product product) throws ProductException {
        List<TupleType> similarities = new ArrayList<>();
        Map<String, Double> productSimilarities = productManager.getSimilaritiesForProduct(product.getName());
        for (Map.Entry<String, Double> entry : productSimilarities.entrySet()) {
            similarities.add(new TupleType(entry.getKey(), entry.getValue()));
        }
        return new TupleType(
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getOriginalPrice(),
                product.getAmount(),
                similarities
        );
    }

    /**
     * Retrieves the products in the catalog as an {@link ArrayList} of {@link TupleType} objects.
     *
     * <p>This method retrieves all products from the {@code productManager} and constructs a
     * {@link TupleType} for each product containing the name, category, price, amount, and similarities.
     * The resulting {@link ArrayList} of {@link TupleType} is returned.</p>
     *
     * @return An {@link ArrayList} of {@link TupleType} containing the products in the catalog.
     * @throws ProductException If there is an error while retrieving the products.
     */
    public ArrayList<TupleType> getProductsFromCatalog() throws ProductException {
        ArrayList<TupleType> catalogProducts = new ArrayList<>();
        // Iterate through all products in the catalog
        for (Product product : productManager.getAllProducts()) {
            // Create a TupleType object for each product and add it to the list
            catalogProducts.add(createProductTuple(product));
        }
        return catalogProducts;
    }

    /**
     * Retrieves the products in the catalog as a {@link Set} of {@link TupleType} objects.
     *
     * <p>This method retrieves all products from the {@code productManager} and constructs a
     * {@link TupleType} containing the name, category, and price of each product. The resulting
     * {@link TupleType} is returned.</p>
     *
     * @param productName The name of the product to retrieve.
     * @return A {@link Set} of {@link TupleType} objects containing the products in the catalog.
     * @throws ProductException If there is an error while retrieving the products.
     */
    public TupleType getProductByName(String productName) throws ProductException {
        Product product = productManager.getProduct(productName);
        return createProductTuple(product);
    }

    /**
     * Retrieves all product lists and their associated products as a set of {@link TupleType} objects.
     *
     * <p>This method first checks if there are any product lists in the system by querying the
     * {@code productManager}. If no product lists exist, it throws a {@link ProductListException}.
     * For each product list, it constructs a {@link TupleType} containing the list's name, category,
     * last modification date, and a nested {@link TupleType} representing the products in the list.
     * Each product is represented by its name, category, price, original price, and quantity.</p>
     *
     * @return A {@link Set} of {@link TupleType} objects, where each represents a product list
     *         along with its associated products.
     * @throws ProductListException If no product lists exist in the system.
     */
    public Set<TupleType> getAllLists() throws ProductListException {
        if (productManager.getAllProductLists().isEmpty()) throw new ProductListException("There are no lists in the system.");
        ArrayList<ProductList> lists = productManager.getAllProductLists();
        Set<TupleType> result = new HashSet<>();
        for (ProductList list : lists) {
            TupleType products = new TupleType();
            ArrayList<Product> productSet = new ArrayList<>(list.getProducts());
            for (Product p : productSet) {
                products.add(new TupleType(
                        p.getName(),
                        p.getCategory(),
                        p.getPrice(),
                        p.getOriginalPrice(),
                        p.getAmount()
                ));
            }
            result.add(new TupleType(list.getName(), list.getCategory(), list.getLastModified(), products));
        }
        return result;
    }

    /**
     * Retrieves a product list by name.
     * @param nameList the name of the product list
     * @return an ArrayList of TupleType where each TupleType represents a product
     * @throws ProductListException if the list name is invalid
     * @throws ProductException if there is an error while retrieving similarities in the products
     */
    public ArrayList<TupleType> getList(String nameList) throws ProductListException, ProductException {
        ProductList productList = productManager.getProductList(nameList);

        ArrayList<TupleType> products = new ArrayList<>();
        ArrayList<Product> productSet = new ArrayList<>(productList.getProducts());
        for (Product product : productSet) {
            products.add(createProductTuple(product));
        }
        return products;
    }

    /**
     * Retrieves the names of all distributions associated with a specific shelf.
     *
     * <p>This method checks if a shelf with the given ID exists in the {@code shelfMap}.
     * If the shelf exists, it retrieves the names of all distributions associated with that shelf.
     * If the shelf does not exist, a {@link ShelfException} is thrown. If no distributions are
     * found for the shelf, a {@link DistributionException} is thrown.</p>
     *
     * @param idShelf The ID of the shelf whose distribution names are to be retrieved.
     * @return A {@link Set} of strings containing the names of all distributions for the specified shelf.
     * @throws ShelfException If the shelf with the specified ID does not exist.
     * @throws DistributionException If there are no distributions associated with the shelf.
     */
    public Set<String> getAllDistributionsNames(int idShelf) throws ShelfException, DistributionException {
        return shelfManager.getAllDistributionsNames(idShelf);

    }

    /**
     * Retrieves the distribution information for a given shelf and distribution name.
     * This method checks if the specified shelf exists and then delegates the
     * retrieval of the distribution to the shelf.
     *
     * @param idSelf the identifier of the shelf to retrieve the distribution from.
     * @param nameDist the name of the distribution to retrieve.
     * @return a nested list of strings representing the product names
     *         in the specified distribution.
     * @throws DistributionException if the distribution with the specified name
     *         is not found in the shelf.
     * @throws ShelfException if the shelf with the specified ID does not exist.
     */
    public ArrayList<ArrayList<String>> getDistributionInfo(int idSelf, String nameDist) throws DistributionException, ShelfException {
        return shelfManager.getDistributionInfo(idSelf, nameDist);
    }

    /**
     * Retrieves the details of a specific shelf identified by its ID.
     * The returned tuple contains the following information about the shelf:
     * <ul>
     *   <li><b>id</b> (int): The unique identifier of the shelf.</li>
     *   <li><b>maxCapacity</b> (int): The maximum capacity of the shelf.</li>
     *   <li><b>nameAlgorithm</b> (String): The name of the algorithm associated with the shelf.</li>
     *   <li><b>listName</b> (String): The name of the product list assigned to the shelf.</li>
     * </ul>
     *
     * @param id the unique identifier of the shelf to retrieve
     * @return a {@code TupleType} containing the details of the shelf
     * @throws ShelfException if the shelf with the specified ID does not exist
     */
    public TupleType getShelf(int id) throws ShelfException{
        Shelf shelf = shelfManager.getShelf(id);
        int xsize = shelf.getXsize();
        int ysize = shelf.getYsize();
        String listName = shelf.getListName();
        return new TupleType(id, xsize, ysize, listName);
    }

    /**
     * Retrieves all shelves in the system as a set of tuples.
     * Each tuple in the returned set contains the following information about a shelf:
     * <ul>
     *   <li><b>id</b> (int): The unique identifier of the shelf.</li>
     *   <li><b>xsize</b> (int): The horizontal size of the shelf.</li>
     *   <li><b>ysize</b> (int): The vertical size of the shelf.</li>
     *   <li><b>nameAlgorithm</b> (String): The name of the algorithm associated with the shelf.</li>
     *   <li><b>listName</b> (String): The name of the product list assigned to the shelf.</li>
     * </ul>
     *
     * @return a set of {@code TupleType}, where each tuple represents a shelf with its details.
     * @throws ShelfException if there are no shelves in the system.
     */
    public Set<TupleType> getAllShelves() throws ShelfException{
        HashSet<Shelf> shelves = (HashSet<Shelf>) shelfManager.getAllShelves();
        if (shelves.isEmpty()) throw new ShelfException("There are no shelves in the system.");
        Set<TupleType> result = new HashSet<>();
        for (Shelf shelf : shelves) {
            result.add(new TupleType(shelf.getId(), shelf.getXsize(), shelf.getYsize(), shelf.getListName()));
        }
        return result;
    }


    /**
     * Retrieves the distribution log for a specific shelf identified by its ID.
     *
     * @param id the unique identifier of the shelf whose distribution log is to be retrieved.
     * @return a {@link Set} of {@link TupleType} objects representing the distributions
     *         stored in the specified shelf.
     * @throws ShelfException if the shelf with the given ID does not exist in the {@code shelfMap}.
     */
    public ArrayList<TupleType> getDistributionsShelf(int id) throws ShelfException {
        return shelfManager.getDistributionsShelf(id);
    }

    /**
     * Retrieves all products in the catalog.
     * @return an ArrayList of all products
     */
    public ArrayList<Product> getAllProductsCatalog() {
        return productManager.getAllProducts();
    }

    //! Shelf Manager methods
    //!------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Deletes a shelf from the shelf map by its ID.
     * This method removes the shelf with the specified ID from the internal
     * mapping of shelves. If the ID does not exist in the map, the method
     * does nothing.
     *
     * @param id the unique identifier of the shelf to be removed
     * @throws ShelfException if the shelf with the given ID does not exist
     */
    public void deleteShelf(int id) throws ShelfException {
        Shelf shelf = shelfManager.getShelf(id);
        Set<String> distributions = shelf.getDistributionNames();
        shelfManager.removeShelf(id);
        for (String dist : distributions) {
            distributionManager.removeDistribution(dist);
        }
        logOperation("Shelf " +id+ " has been deleted.");
    }


    /**
     * Creates a new distribution for a specified shelf.
     * <p>
     * This method creates a distribution for the given shelf based on the provided distribution name and depth limit.
     * It validates that the shelf exists and that the depth limit is greater than zero. The distribution is then applied,
     * and the new distribution is added to the distribution manager.
     *
     * @param idShelf the ID of the shelf for which the distribution will be created
     * @param nameDistribution the name of the new distribution
     * @param limit the depth limit for the distribution (must be greater than 0)
     *
     * @throws ShelfException if the specified shelf does not exist
     * @throws DistributionException if the depth limit is invalid (i.e., zero or negative)
     */
    public void createNewDistribution(int idShelf, String nameDistribution, int algorithm, int limit) throws ShelfException, DistributionException {
        Map<String, Map<String, Double>> similarityMatrix = productManager.getSimilarityMap();
        if (distributionManager.exists(nameDistribution)) throw new DistributionException("The distribution with name '" + nameDistribution + "' already exists");
        Distribution dist =  shelfManager.distributeShelf(idShelf, nameDistribution, algorithm, limit, similarityMatrix);
        distributionManager.addDistribution(nameDistribution, dist);

        logOperation("A new distribution " +nameDistribution+" has been created at " + idShelf + "with limit " + limit);
    }

    /**
     * Marks a specified distribution as the current distribution for a given shelf.
     *
     * <p>This method checks if the shelf with the provided ID exists in the system. If the shelf
     * exists, it retrieves the corresponding {@code Shelf} object and a {@code Distribution} object
     * identified by its name. The method then updates the shelf to mark the specified distribution
     * as the current one using the {@code makeCurrentDistribution} method of the {@code Shelf} class.</p>
     *
     * @param idShelf          the unique identifier of the shelf.
     * @param nameDistribution the name of the distribution to be set as current.
     * @throws ShelfException         if the shelf with the given ID does not exist in the system.
     * @throws DistributionException  if the distribution with the given name does not exist or cannot be marked as current.
     */
    public void makeCurrentDistributionShelf(int idShelf, String nameDistribution) throws ShelfException, DistributionException {
        Distribution dist = distributionManager.getDistribution(nameDistribution);
        shelfManager.makeCurrentDistributionShelf(idShelf, dist);
    }


    /**
     * Displays the details of the last distribution for a specified shelf.
     * <p>
     * This method retrieves and returns the details of the most recent distribution applied to the shelf
     * identified by the given shelf ID. If the shelf does not exist, an exception is thrown.
     *
     * @param idShelf the ID of the shelf whose last distribution details are to be retrieved
     *
     * @return a string representation of the last distribution for the specified shelf
     *
     * @throws ShelfException if the specified shelf does not exist
     */
    public String showLastDistributionShelf(int idShelf) throws ShelfException {
        return shelfManager.showLastDistributionShelf(idShelf);
    }
    
    /**
     * Modifies a distribution by updating the relationship between two specified products.
     * <p>
     * This method modifies the specified distribution by adjusting the relationship between two products
     * based on their names. The modification is handled by the distribution manager.
     *
     * @param nameDistribution the name of the distribution to be modified
     * @param nameProduct1 the name of the first product involved in the modification
     * @param nameProduct2 the name of the second product involved in the modification
     *
     * @throws DistributionException if there is an error while modifying the distribution
     */
    public void modifyDistribution(String nameDistribution, String nameProduct1, String nameProduct2) throws DistributionException{
        distributionManager.modifyDistribution(nameDistribution, nameProduct1, nameProduct2);
        logOperation("Distribution modified: " + nameDistribution + " by user: " + userManager.getActiveUsername());
    }

    /**
     * Swaps the positions of two products in a specified distribution.
     * <p>
     * This method swaps the positions of two products in the specified distribution based on their coordinates.
     * The operation is delegated to the distribution manager for processing.
     *
     * @param nameDistribution the name of the distribution in which the products will be swapped
     * @param coord1 the coordinates of the first product
     * @param coord2 the coordinates of the second product
     *
     * @throws DistributionException if there is an error while swapping the products in the distribution
     */
    public void swapProductsInDistribution(String nameDistribution, Pair<Integer,Integer> coord1, Pair<Integer,Integer> coord2) throws DistributionException {
        distributionManager.swapProductsInDistribution(nameDistribution, coord1, coord2);
        logOperation("Products swapped in distribution: " + nameDistribution + " by user: " + userManager.getActiveUsername());
    }

    //! Log Operation methods
    //! ------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Logs an operation in the operation log.
     * @param operation a description of the operation performed
     */
    private void logOperation(String operation) {
        operationLog.put(LocalDateTime.now(), operation);
    }


    /**
     * Retrieves the operation log as a string.
     * <p>
     * This method iterates through the operation log entries and constructs a string
     * representation of each entry, including the date and description of the operation.
     * The resulting string contains all the logged operations in the system.
     *
     * @return A string containing the operation log entries.
     */
    public String showOperationLog() {
        StringBuilder sb = new StringBuilder();
        operationLog.forEach((date, operation) -> {
            sb.append(date.toString()).append(" - ").append(operation).append("\n");
        });
        return sb.toString();
    }

    /**
     * Retrieves the operation log as a ArrayList.
     *
     * @return a ArrayList<TupleType> containing the operation log entries.
     */
    public ArrayList<TupleType> getOperationLog() {
        ArrayList<TupleType> logEntries = new ArrayList<>();
        operationLog.forEach((date, operation) -> {
            logEntries.add(new TupleType(date.toString(), operation));
        });
        return logEntries;
    }
}
