package org.presentation.controllers;

import org.domain.controllers.ControllerDomain;
import org.domain.types.Pair;
import org.domain.types.TupleType;
import org.presentation.views.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Set;
import org.domain.exceptions.*;

/**
 * The {@code PresentationController} class represents the controller for the presentation layer.
 *
 * <p>This class is responsible for handling the interactions between the user interface
 * and the domain layer of the application. It provides methods to manage the views and
 * delegate operations to the domain controller.</p>
 */
public class PresentationController {
    private static  ControllerDomain controllerDomain;
    private LoginView loginView;
    private MenuView menuView;
    private ProductListMenuView productListMenuView;
    private ProductCatalogView productCatalogView;
    private OperationLogView operationLogView;

    /**
     * Initializes a new instance of the {@code PresentationController} class.
     *
     * This constructor creates a presentation controller and initializes the
     * {@code controllerDomain} by obtaining the singleton instance of
     * {@code ControllerDomain}.
     */
    public PresentationController() {
        controllerDomain = ControllerDomain.getInstance();
    }

    /**
     * Returns the singleton instance of the {@code PresentationController}.
     *
     * This method ensures that only one instance of {@code PresentationController}
     * exists, using the singleton design pattern with lazy initialization.
     *
     * @return the single instance of {@code PresentationController}
     */
    public static PresentationController getInstance() {return SingletonHelper.INSTANCE;}

    /**
     * Helper class to implement the singleton pattern for {@code PresentationController}.
     *
     * This class holds the single instance of {@code PresentationController}.
     * The instance is created lazily and is thread-safe due to the class
     * initialization phase, ensuring that it is only instantiated when accessed
     * for the first time.
     */
    private static class SingletonHelper {
        private static final PresentationController INSTANCE = new PresentationController();
    }

    /**
     * Starts the presentation layer of the application.
     *
     * This method initializes the login view and displays it to the user,
     * serving as the entry point for the presentation layer.
     */
    public void start() {
        if (loginView == null) loginView = new LoginView();
        loginView.showView();
    }

    /**
     * Retrieves all shelves in the system as a set of tuples.
     *
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
    public Set<TupleType> getAllShelves() throws ShelfException {
        return controllerDomain.getAllShelves();
    }

    /**
     * Handles the login process by hiding the login view and showing the menu view.
     * If the login is successful, the user is redirected to the menu view.
     * If the login fails, an error message is displayed.
     * @param userInput the username or email of the user
     * @param password the password of the user (it is hashed int his function before being sent to the domain controller)
     */
    public void handleLogin(String userInput, String password) {
        try {
            String hashedPassword = hashPassword(password);
            controllerDomain.loginUser(userInput, hashedPassword);

            if (menuView == null) menuView = new MenuView();
            loginView.hideView();
            menuView.showView();
        }
        catch (UnauthorizedAccessException e) {
            // Show a popup with the exception message
            loginView.showView();
            javax.swing.JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Login Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        };
    }

    /**
     * Handles the logout process by hiding the menu view and returning to the start view.
     */
    public void handleLogOut() {
        menuView.hideView();
        controllerDomain.logoutUser();
        loginView.showView();

        menuView = null;
        productListMenuView = null;
        productCatalogView  = null;
        operationLogView    = null;

    }

    /**
     * Handles the register process by calling the domain controller to register a new user.
     *
     * @param username the username of the new user
     * @param email the email of the new user
     * @param password the password of the new user
     */
    public void handleRegister(String username, String email, String password)  {
        try {
            String hashedPassword = hashPassword(password);
            controllerDomain.registerUser(username, email, hashedPassword);
        }
        catch (UnauthorizedAccessException | UserException e) {
            javax.swing.JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Registration Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Handles the exit process by closing the application.
     */
    public void handleExit() {
        System.exit(0);
    }

    /**
     * Handles the product list manager process by showing the product list manager view.
     */
    public void handleProductListMenu() {
        if(productListMenuView == null) productListMenuView = new ProductListMenuView();
        productListMenuView.showView();
    }

    /**
     * Handles the product catalog process by showing the product catalog view.
     */
    public void handleProductCatalog() {
        if(productCatalogView == null) productCatalogView = new ProductCatalogView();
        productCatalogView.showView();
    }

    /**
     * Handles the view operations process by showing the operation log view.
     */
    public void handleViewOperations() {
        if (operationLogView == null) operationLogView = new OperationLogView();
        operationLogView.loadOperationLog();    // Update the operation log view
        operationLogView.showView();
    }

    /**
     * Retrieves the operation log as a TupleType.
     *
     * @return a TupleType containing the operation log entries.
     */
    public ArrayList<TupleType> getOperationLog() {
        return controllerDomain.getOperationLog();
    }

    /**
     * Deletes the shelf with the specified ID from the system.
     *
     * @param id the unique identifier of the shelf to be deleted
     * @throws ShelfException if the shelf with the specified ID does not exist or cannot be deleted
     */
    public void deleteShelf(int id) throws ShelfException {
        controllerDomain.deleteShelf(id);
    }

    /**
     * Retrieves all distribution names associated with a specific shelf.
     *
     * @param idShelf the unique identifier of the shelf whose distribution names are to be retrieved
     * @return a set of {@code String} representing the names of the distributions assigned to the shelf
     * @throws ShelfException if the shelf with the specified ID does not exist or an error occurs while retrieving the distribution names
     */
    public Set<String> getAllDistributionsNames(int idShelf) throws ShelfException {
        return controllerDomain.getAllDistributionsNames(idShelf);
    }

    /**
     * Creates a new shelf in the system with the specified parameters.
     *
     * The shelf is created with the given ID, maximum capacity, product list, and algorithm.
     *
     * @param id the unique identifier of the shelf to be created
     * @param xCapacity the horizontal size of the shelf
     * @param yCapacity the vertical size of the shelf
     * @param productList the name of the product list to be assigned to the shelf
     * @throws ShelfException if there is an error creating the shelf, such as invalid parameters or an existing shelf with the same ID
     * @throws ProductListException if the specified product list does not exist or cannot be accessed
     */
    public void createShelf(int id, int xCapacity, int yCapacity,String productList) throws ShelfException, ProductListException {
        controllerDomain.createShelf(id, xCapacity, yCapacity,productList);
    }

    /**
     * Retrieves all product lists in the system as a set of tuples.
     *
     * Each tuple in the returned set contains the following information about a product list:
     * <ul>
     *   <li><b>name</b> (String): The name of the product list.</li>
     *   <li><b>category</b> (String): The category to which the product list belongs.</li>
     *   <li><b>lastModified</b> (LocalDateTime): The date and time when the product list was last modified.</li>
     *   <li><b>products</b> (Set&lt;org.domain.types.Product&gt;): A set of products included in the product list.</li>
     * </ul>
     *
     * @return a set of {@code TupleType}, where each tuple represents a product list with its details
     * @throws ProductListException if no product lists are found in the system or an error occurs while retrieving them
     */
    public Set<TupleType> getAllLists() throws ProductListException {
        return controllerDomain.getAllLists();
    }

    /**
     * Retrieves the details of a specific product list by its name.
     *
     * The returned tuple contains the following information about the product list:
     * <ul>
     *   <li><b>name</b> (String): The name of the product list.</li>
     *   <li><b>category</b> (String): The category to which the product list belongs.</li>
     *   <li><b>lastModified</b> (LocalDateTime): The date and time when the product list was last modified.</li>
     *   <li><b>products</b> (Set&lt;org.domain.types.Product&gt;): A set of products included in the product list.</li>
     * </ul>
     *
     * @param nameList the name of the product list to retrieve
     * @return a {@code TupleType} containing the details of the product list
     * @throws ProductListException if the product list with the specified name does not exist
     * @throws ProductException if there is an error while retrieving the products in the list
     */
    public ArrayList<TupleType> getList(String nameList) throws ProductListException, ProductException {
        return controllerDomain.getList(nameList);
    }


    /**
     * Applies a discount to all products in a specified list and delegates the operation to the domain controller.
     *
     * @param listName the name of the product list to which the discount will be applied
     * @param discount the discount percentage to be applied, which must be a number between 1 and 100
     * @throws ProductException if an error occurs during the discount application process, such as
     *                          an invalid list name or issues in the domain controller
     *
     * <p>This method applies a discount to all products in the specified list by delegating the
     * operation to the {@code controllerDomain}. The discount value must be a percentage in the
     * range of 1 to 100.</p>
     */
    public void applyDiscountToProduct(String listName, double discount) throws ProductException {
        controllerDomain.applyDiscountToProduct(listName, discount);
    }


    /**
     * Updates the product list assigned to a specific shelf.
     *
     * @param idShelf the unique identifier of the shelf whose product list is to be updated
     * @param productList the name of the new product list to assign to the shelf
     * @throws ProductListException if the specified product list does not exist or cannot be accessed
     * @throws ShelfException if the shelf with the specified ID does not exist or cannot be updated
     */
    public void updateShelfProductList(int idShelf, String productList) throws ProductListException, ShelfException {
        controllerDomain.changeProductListAtShelf(idShelf, productList);
    }

    /**
     * Retrieves the details of a specific shelf identified by its ID.
     *
     * The returned tuple contains the following information about the shelf:
     * <ul>
     *   <li><b>id</b> (int): The unique identifier of the shelf.</li>
     *   <li><b>maxCapacity</b> (int): The maximum capacity of the shelf.</li>
     *   <li><b>nameAlgorithm</b> (String): The name of the algorithm associated with the shelf.</li>
     *   <li><b>listName</b> (String): The name of the product list assigned to the shelf.</li>
     * </ul>
     *
     * @param idShelf the unique identifier of the shelf to retrieve
     * @return a {@code TupleType} containing the details of the shelf
     * @throws ShelfException if the shelf with the specified ID does not exist
     */
    public TupleType getShelf(int idShelf) throws ShelfException {
        return controllerDomain.getShelf(idShelf);
    }

    /**
     * Retrieves and returns a string representation of a product list by its name.
     *
     * @param listName the name of the product list to retrieve
     * @return a string representation of the product list
     * @throws ProductListException if the specified product list cannot be found or an error occurs while retrieving it
     */
    public String showProductList(String listName) throws ProductListException {
        return controllerDomain.showProductList(listName);
    }

    /**
     * Creates a new product list and adds it to the catalog by delegating the operation to the domain controller.
     *
     * @param listName the name of the new product list to be created.
     * @param category the category under which the product list will be classified.
     * @throws ProductListException if the product list cannot be created due to validation issues
     *         or any other error in the domain controller.
     */
    public void addProductList(String listName, String category) throws ProductListException {
        controllerDomain.createProductList(listName,category);
    }

    /**
     * Deletes a set of product lists from the system.
     *
     * This method iterates over the provided set of product list names and deletes each list
     * by calling the appropriate method in the controller.
     *
     * @param listsToDelete a set of {@code String} representing the names of the product lists
     *                      to be deleted
     * @throws ProductListException if there is an error removing any of the product lists,
     *                              such as the list not existing or being invalid
     * @throws ShelfException if there is an error related to the shelves associated with the
     *                        product lists, for example, if a shelf is dependent on one of the
     *                        lists being deleted
     */
    public void deleteProductLists(Set<String> listsToDelete) throws ProductListException, ShelfException {
        for (String nomList : listsToDelete) {
            controllerDomain.removeProductList(nomList);
        }
    }

    /**
     * Updates a product in the catalog by delegating the operation to the domain controller.
     *
     * @param name the name of the product to be updated.
     * @param category the category to which the product belongs.
     * @param price the new price of the product.
     * @param amount the new stock amount for the product.
     * @param similarities an {@link ArrayList} of {@link Pair} objects, where each pair represents
     *        the similarity between this product and another product, with the name and similarity score.
     * @throws ProductException if the product cannot be updated due to validation issues
     *         or any other error in the domain controller.
     */
    public void updateProduct(String name, String category, double price, int amount, ArrayList<Pair<String, Double>> similarities) throws ProductException {
        controllerDomain.updateProduct(name, category, price, amount, similarities);
    }

    /**
     * Deletes a set of products from the catalog.
     *
     * This method iterates over the provided set of product names and removes each product
     * from the catalog by calling the appropriate method in the controller.
     *
     * @throws ProductException if there is an error removing any of the products, such as the
     *                          product not existing or being invalid
     */
    public void deleteProduct(String productName) throws ProductException{
        controllerDomain.removeProductFromCatalog(productName);
    }


    /**
     * Removes products defined on a list (by name) from a specified list by delegating the operation to the domain controller.
     *
     * @param productNames the names of the product to remove from the list.
     * @param listName the name of the list from which the products will be removed.
     * @throws ProductException if the specified product does not exist or cannot be removed.
     * @throws ProductListException if the specified list does not exist or cannot be modified.
     */
    public void deleteProductsFromList(String listName, Set<String> productNames) throws ProductException, ProductListException {
        controllerDomain.removeProductsFromList(listName, productNames);
    }

    /**
     * Retrieves the product with the specified name from the catalog.
     *
     * This method delegates the operation to the domain controller to fetch the product
     * with the given name from the catalog. The returned {@code TupleType} contains the
     * details of the product, which may include information such as the product name,
     * category, price, queue, and the similarity with other products.
     *
     * @param productName the name of the product to retrieve from the catalog.
     * @return a {@code TupleType} representing the product with the specified name.
     * @throws ProductException if the product with the specified name does not exist in the catalog
     */
    public TupleType getProductByName(String productName) throws ProductException {
        return controllerDomain.getProductByName(productName);
    }

    /**
     * Retrieves the entire catalog of products as a {@code TupleType}.
     * <p>
     * This method delegates the operation to the domain controller to fetch all products
     * available in the catalog. The returned {@code TupleType} contains the details of
     * all products, which may include information such as product names, categories,
     * prices, and quantities.
     * </p>
     * <p>
     * The structure of the returned {@code TupleType} is expected to be a nested tuple
     * where each element represents a product with its associated attributes.
     * </p>
     * <p>
     * This method does not take any parameters and returns the complete product catalog
     * as a single {@code TupleType} object.
     * </p>
     *
     * @return a {@code TupleType} representing the entire product catalog.
     * @throws ProductException if there is an error retrieving the products from the catalog
     */
    public ArrayList<TupleType> getProductsFromCatalog() throws ProductException {
        return controllerDomain.getProductsFromCatalog();
    }

    /**
     * Adds a new product to the catalog by delegating the operation to the domain controller.
     *
     * @param productName the name of the product to be added.
     * @param category the category to which the product belongs.
     * @param price the price of the product.
     * @param amount the initial stock amount for the product.
     * @param similaritat an {@link ArrayList} of {@link Pair} objects, where each pair represents
     *        the similarity between this product and another product, with the name and similarity score.
     * @throws ProductException if the product cannot be added to the catalog due to validation issues
     *         or any other error in the domain controller.
     */
    public void addProductToCatalog(String productName, String category, double price, int amount, ArrayList<Pair<String, Double>> similaritat ) throws ProductException {
        controllerDomain.addProductToCatalog(productName, category, price, amount, similaritat);
    }

    /**
     * Delegates the retrieval of the distribution log for a specific shelf to the domain controller.
     *
     * @param id the unique identifier of the shelf whose distribution log is to be retrieved.
     * @return a {@link Set} of {@link TupleType} objects representing the distributions
     *         stored in the specified shelf.
     * @throws ShelfException if the domain controller indicates that the shelf with the given ID
     *         does not exist.
     */
    public ArrayList<TupleType> getDistributions(int id) throws ShelfException{
        return controllerDomain.getDistributionsShelf(id);

    }

    /**
     * Retrieves the distribution information for a given shelf and distribution name
     * by delegating the operation to the domain controller.
     *
     * @param idShelf the identifier of the shelf to retrieve the distribution from.
     * @param nameDistribution the name of the distribution to retrieve.
     * @return a nested list of strings representing the product names
     *         in the specified distribution.
     * @throws DistributionException if the distribution with the specified name
     *         is not found in the shelf.
     * @throws ShelfException if the shelf with the specified ID does not exist.
     */
    public ArrayList<ArrayList<String>> getDistribution(int idShelf, String nameDistribution) throws DistributionException, ShelfException {
        return controllerDomain.getDistributionInfo(idShelf, nameDistribution);
    }

    /**
     * Adds a product to a specified list by delegating the operation to the domain controller.
     *
     * @param productName the name of the product to add to the list.
     * @param listName the name of the list to which the product will be added.
     * @throws ProductException if the specified product does not exist or cannot be added.
     * @throws ProductListException if the specified list does not exist or cannot be modified.
     */
    public void addProductToList(String listName, String productName) throws ProductException, ProductListException {
        controllerDomain.addProductToList(listName, productName);
    }

    /**
     * Removes a product from a specified list by delegating the operation to the domain controller.
     *
     * @param productName the name of the product to remove from the list.
     * @param listName the name of the list from which the product will be removed.
     * @throws ProductException if the specified product does not exist or cannot be removed.
     * @throws ProductListException if the specified list does not exist or cannot be modified.
     */
    public void deleteProductFromList(String listName, String productName) throws ProductException, ProductListException {
        controllerDomain.removeProductFromList(listName, productName);
    }

    /**
     * Generates a new distribution for a specific shelf using the specified algorithm.
     *
     * @param idShelf   the identifier of the shelf where the distribution will be generated.
     * @param algorithm the algorithm used for generating the distribution (not currently used in the method,
     *                  but can be considered for future extensions).
     * @param limit     the maximum limit for the distribution.
     * @param nameDist  the name of the new distribution.
     * @throws DistributionException if there is an error creating the distribution.
     * @throws ShelfException        if there is an issue with the specified shelf (e.g., it does not exist or is invalid).
     */
    public void generateDistribution(int idShelf, String algorithm, int limit, String nameDist) throws DistributionException, ShelfException {
        if (algorithm.equals("Brute Force")) {
            controllerDomain.createNewDistribution(idShelf, nameDist, 1, limit);
        } else {
            controllerDomain.createNewDistribution(idShelf, nameDist, 2, limit);
        }
    }

    /**
     * Swaps two products in a distribution by delegating the operation to the domain controller.
     *
     * @param nameDist the name of the distribution where the products will be swapped.
     * @param coord1 the coordinates of the first product to swap.
     * @param coord2 the coordinates of the second product to swap.
     * @throws DistributionException if the distribution with the specified name does not exist
     *         or cannot be modified.
     */
    public void swapProductsInDistribution(String nameDist, Pair<Integer,Integer> coord1, Pair<Integer,Integer> coord2) throws DistributionException {
        controllerDomain.swapProductsInDistribution(nameDist, coord1, coord2);
    }

    /**
     * Hashes a password using SHA-256.
     *
     * @param password the password to hash
     * @return the hashed password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Displays the main menu view.
     *
     * <p>This method initializes and displays the main menu view, allowing the user to navigate
     * through different options available in the system.</p>
     */
    public void menuView() {
        menuView.showView();
    }

    /**
     * Displays the view for managing shelves.
     *
     * <p>This method initializes and displays a view component that allows the user to interact
     * with and manage the shelves in the system.</p>
     */
    public void shelfView() {
        ShelfView shelfView = new ShelfView();
        shelfView.showView();
    }

    /**
     * Displays the view for creating a new shelf.
     *
     * <p>This method initializes and displays a view component that allows the user to input
     * the necessary details to create a new shelf in the system.</p>
     */
    public void createShelfView() {
        CreateShelfView createShelfView = new CreateShelfView();
        createShelfView.showView();
    }

    /**
     * Displays the view for generating a new distribution for a specific shelf.
     *
     * <p>This method initializes and displays a view component that facilitates the creation
     * of a new distribution for the shelf identified by its unique ID.</p>
     *
     * @param idShelf the unique identifier of the shelf for which a new distribution is to be generated.
     */
    public void generateDistributionView(int idShelf) {
        GenerateDistributionView generateDistributionView = new GenerateDistributionView(idShelf);
        generateDistributionView.showView();
    }


    /**
     * Displays the management view for distributions associated with a specific shelf.
     *
     * <p>This method initializes and displays a view component that allows managing the
     * distributions related to the shelf identified by its unique ID.</p>
     *
     * @param idShelf the unique identifier of the shelf whose distributions are to be managed.
     */

    public void manageDistributionsView(int idShelf) {
        ManageDistributionsView manageDistributionsView = new ManageDistributionsView(idShelf);
        manageDistributionsView.showView();
    }

    /**
     * Displays the view for a specific distribution associated with a given shelf.
     *
     * <p>This method initializes a view component to present the details of the specified
     * distribution. The distribution is identified by its name, and it is associated with the
     * shelf identified by its unique ID.</p>
     *
     * @param idShelf   the unique identifier of the shelf.
     * @param distName  the name of the distribution to be displayed.
     */

    public void showDistributionView(int idShelf, String distName) {
        ShowDistributionView showDistributionView = new ShowDistributionView(idShelf, distName);
        showDistributionView.showView();
    }

    /**
     * Marks a specified distribution as the current distribution for a given shelf.
     *
     * <p>This method delegates the operation to the domain controller, invoking
     * {@code makeCurrentDistributionShelf} to perform the required checks and updates.
     * It ensures that the distribution identified by its name is marked as the current
     * distribution for the shelf identified by its unique ID.</p>
     *
     * @param idShelf   the unique identifier of the shelf.
     * @param distName  the name of the distribution to be set as current.
     * @throws ShelfException        if the shelf with the given ID does not exist.
     * @throws DistributionException if the distribution with the given name does not exist
     *                                or cannot be marked as current.
     */
    public void makeCurrentDistribution(int idShelf, String distName) throws DistributionException, ShelfException {
        controllerDomain.makeCurrentDistributionShelf(idShelf, distName);
    }
}
