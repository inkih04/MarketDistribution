package org.drivers;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import org.domain.exceptions.*;

import org.domain.controllers.ControllerDomain;
import org.domain.classes.Product;
import org.domain.types.Pair;


/**
 * DriverControllerDomain class.
 * Handles the main user interface through a text-based menu system.
 * Provides interaction with the ControllerDomain to manage products, shelves, and data parsing.
 * @version 1.0
 */


public class DriverControllerDomain {
    private final ControllerDomain controllerDomain; ///< Controller to manage domain-level operations.
    private final Scanner scanner; ///< Scanner for user input.

    /**
     * Constructs a DriverControllerDomain instance with a specified ControllerDomain.
     * <p>
     * This constructor initializes the DriverControllerDomain with a given ControllerDomain object
     * and sets up a scanner for user input.
     *
     * @param controllerDomain the ControllerDomain object to be associated with this DriverControllerDomain
     */
    public DriverControllerDomain(ControllerDomain controllerDomain) {
        this.controllerDomain = controllerDomain;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main method to run the DriverControllerDomain class.
     * @param args command-line arguments passed to the application (not used)
     */
    public static void main(String[] args) {
        ControllerDomain controllerDomain = ControllerDomain.getInstance();
        DriverControllerDomain driver = new DriverControllerDomain(controllerDomain);
        driver.showLoginRegistrationMenu();
    }

    /**
     * Displays the login and registration menu.
     * <p>
     * This method presents a menu to the user with options to log in, register, or exit.
     * It processes the user's input and navigates to the corresponding action based on the selected option.
     * The loop continues until the user selects option 0 to exit.
     * </p>
     */
    public void showLoginRegistrationMenu() {
        int option = -1;
        while (option != 0) {
            System.out.println("\nMenu:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        login();
                        break;
                    case 2:
                        register();
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid option");
                }
            } else {
                System.out.println("Error: Please enter a valid number");
                scanner.next();
            }
        }
    }

    /**
     * Hashes a password using SHA-256.
     * This function is only here to test the controllerDomain, The logic is at the controller presentation
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
     * Handles the login process for the user.
     * <p>
     * Prompts the user to input their username (or email) and password,
     * hashes the password for security, and attempts to log in using the
     * provided credentials. If successful, the user is redirected to the main menu.
     * If login fails due to unauthorized access, an error message is displayed.
     * </p>
     */
    public void login() {
        System.out.print("Enter your username or mail: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        try {
            controllerDomain.loginUser(username, hashPassword(password));
            showMenu();
        }
        catch (UnauthorizedAccessException e) {System.out.println(e.getMessage());}
    }


    /**
     * Handles the user registration process.
     * <p>
     * Prompts the user to input their username, email, password, and confirmation password.
     * Validates that the password and confirmation password match before attempting to register
     * the user. If registration fails due to invalid input or unauthorized access,
     * an appropriate error message is displayed.
     * </p>
     *
     * @throws UserException if the provided passwords do not match or there is an issue with user input.
     */
    public void register() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        System.out.print("Enter your confirm password: ");
        String confirmPassword = scanner.nextLine();

        try {
            if(!Objects.equals(password, confirmPassword)) throw new UserException("Passwords do not match");
            controllerDomain.registerUser(username, email, hashPassword(password));
        }
        catch (UnauthorizedAccessException | UserException e) {System.out.println(e.getMessage());}
    }

    /**
     * Displays a menu of options and processes user input.
     * <p>
     * This method presents a menu to the user with several options related to files, products, and shelves.
     * It processes the user's input and navigates to the corresponding menu based on the selected option.
     * The loop continues until the user selects option 0 to exit.
     */
    public void showMenu() {
        int option = -1;

        while (option != 0) {
            System.out.println("\nMenu:");
            System.out.println("1. Options about the files");
            System.out.println("2. Options about the products");
            System.out.println("3. Options about the shelves");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
                scanner.nextLine();  // Consumir el salto de línea

                // Procesar la opción ingresada
                switch (option) {
                    case 1:
                        showParserMenu();
                        break;
                    case 2:
                        showProductMenu();
                        break;
                    case 3:
                        showShelfMenu();
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        controllerDomain.logoutUser();
                        break;
                    default:
                        System.out.println("Invalid option");
                }
            } else {
                System.out.println("Error: Please enter a valid number");
                scanner.next(); // Consumir entrada inválida
            }
        }
    }


    /**
     * Displays the shelf-related menu and processes user input for shelf-related actions.
     * <p>
     * This method presents a menu to the user with various options related to shelves, such as creating a shelf,
     * changing sorting algorithms, showing shelf details, modifying distributions, and more. It processes the user's
     * input and performs the corresponding action based on the selected option. The loop continues until the user selects
     * option 0 to exit the menu.
     */
    public void showShelfMenu() {
        System.out.println("\nShelves Menu:");
        System.out.println("1. Create a shelf");
        System.out.println("2. Show all shelves");
        System.out.println("3. Show a shelf");
        System.out.println("4. Show shelf distribution");
        System.out.println("5. Change the list associated with a shelf");
        System.out.println("6. Create a new shelf distribution");
        System.out.println("7. Modify distribution");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");

        // Validar si la entrada es un entero
        if (scanner.hasNextInt()) {
            int  option = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea

            // Procesar la opción ingresada
            switch (option) {
                case 0:
                    System.out.println("Exiting...");
                    break;
                case 1:
                    createShelf();
                    break;
                case 2:
                    showAllShelfs();
                    break;
                case 3:
                    showShelf();
                    break;
                case 4:
                    showDistributionShelf();
                    break;
                case 5:
                    changeProductListAtShelf();
                    break;
                case 6:
                    createNewDistribution();
                    break;
                case 7:
                    modifyDistribution();
                    break;

                default:
                    System.out.println("Invalid option");
                    break;
            }
        } else {
            // Mensaje de error y consumir entrada no válida
            System.out.println("Error: Please enter a valid number");
            scanner.next(); // Consumir entrada inválida
        }
    }




    /**
     * Displays the file-related menu and processes user input for file operations.
     * <p>
     * This method presents a menu to the user with options to read data from an external file, create a file with the distribution,
     * or exit the menu. It processes the user's input and performs the corresponding action based on the selected option.
     * The loop continues until the user selects option 0 to exit the menu.
     */
    public void showParserMenu() {
        System.out.println("\nFiles Menu:");
        System.out.println("1. Read data from an external file");
        System.out.println("2. Create a file with the distribution");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");

        if (scanner.hasNextInt()) {
            int option = scanner.nextInt();
            scanner.nextLine(); // Consumir el salt de línia

            switch (option) {
                case 0:
                    System.out.println("Exiting..");
                    break;
                case 1:
                    getDataThroughFile();
                    break;
                case 2:
                    crateNewDistributionFile();
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
        else {
            // Mensaje de error y consumir entrada no válida
            System.out.println("Error: Please enter a valid number");
            scanner.next(); // Consumir entrada inválida
        }
    }


    /**
     * Displays the product-related menu and processes user input for product operations.
     * <p>
     * This method presents a menu to the user with various options related to product management, such as adding/removing products,
     * changing quantities, managing product lists, applying discounts, and more. It processes the user's input and performs the
     * corresponding action based on the selected option. The loop continues until the user selects option 0 to exit the menu.
     */
    public void showProductMenu() {
        System.out.println("\nProducts Menu:");
        System.out.println("1. Add product to the system");
        System.out.println("2. Remove product from the system");
        System.out.println("3. Decrease product quantity in the system");
        System.out.println("4. Increase product quantity in the system");
        System.out.println("5. Add product to the list");
        System.out.println("6. Remove product from the list");
        System.out.println("7. Show all products in the catalog");
        System.out.println("8. Create a new list");
        System.out.println("9. Delete a list");
        System.out.println("10. Show information about all product lists");
        System.out.println("11. Show information about a product list");
        System.out.println("12. Show history of changes in product lists");
        System.out.println("13. Apply discount to the product list");
        System.out.println("14. Modify similarities between products");
        System.out.println("15. Show similarities");

        System.out.println("0. Exit");
        System.out.print("Select an option: ");

        if (scanner.hasNextInt()) {
            int option = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea

            // Procesar la opción ingresada
            switch (option) {
                case 0:
                    System.out.println("Exiting...");
                    break;
                case 1:
                    addProductToCatalog();
                    break;
                case 2:
                    removeProductFromCatalog();
                    break;
                case 3:
                    decreaseProductQuantity();
                    break;
                case 4:
                    increaseProductQuantity();
                    break;
                case 5:
                    addProductToList();
                    break;
                case 6:
                    removeProductFromList();
                    break;
                case 7:
                    showAllProductsCatalog();
                    break;
                case 8:
                    createNewProductList();
                    break;
                case 9:
                    removeList();
                    break;
                case 10:
                    showAllLists();
                    break;
                case 11:
                    showListContent();
                    break;
                case 12:
                    showOperationLog();
                    break;
                case 13:
                    applyDiscountToList();
                    break;
                case 14:
                    changeSimilarity();
                    break;

                case 15:
                    showSimilarity();
                    break;

                default:
                    System.out.println("Invalid option");
                    break;
            }
        } else {
            // Mensaje de error y consumir entrada no válida
            System.out.println("Error: Please enter a valid number");
            scanner.next(); // Consumir entrada inválida
        }
    }

    /**
     * Modifies the distribution by updating the relationship between two specified products.
     * <p>
     * This method prompts the user to enter the names of a distribution and two products. It then calls the
     * {@code modifyDistribution} method of the {@code controllerDomain} to modify the distribution. If the modification is
     * successful, a confirmation message is displayed. If an error occurs, the exception message is displayed.
     */
    public void modifyDistribution() {
        try {
            System.out.print("Enter the name of the distribution: ");
            String distributionName = scanner.nextLine();
            System.out.print("Enter the name of product1: ");
            String product1Name = scanner.nextLine();
            System.out.print("Enter the name of product2: ");
            String product2Name = scanner.nextLine();

            controllerDomain.modifyDistribution(distributionName, product1Name, product2Name);

            System.out.println("Successfully modified distribution " + distributionName);

        }
        catch (DistributionException e) {System.out.println(e.getMessage());}
    }


    /**
     * Facilitates the creation of a new distribution by interacting with the user through the console.
     *
     * This method prompts the user to input the ID of a shelf and the name of the distribution.
     * It then invokes the domain controller to create the distribution. If an error occurs,
     * it catches and displays the corresponding exception message.
     *
     * @throws IllegalArgumentException if the provided shelf ID is not an integer.
     */
    public void createNewDistribution() {
        try {
            System.out.print("Enter the shelf ID");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The shelf ID must be of type int");
            int id = scanner.nextInt();

            System.out.print("Enter the name of the distribution");
            String nom = scanner.nextLine();

            System.out.println("Enter the type of algorithm you want to distribute the shelf with: ");
            System.out.println("1- Algorithm Brute Force ");
            System.out.println("2- Algorithm Hill Climbing ");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The algorithm must be of type int");
            int algorithm = scanner.nextInt();

            System.out.print("Enter the limit to witch you want to search ");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The limit must be of type int");
            int limit = scanner.nextInt();

            controllerDomain.createNewDistribution(id, nom, algorithm, limit);

            System.out.println("Distribution created");
        }
        catch (ShelfException e) { System.out.println(e.getMessage()); }

    }


    /**
     * Removes a product list from the system.
     * <p>
     * This method prompts the user to enter the name of the product list they wish to remove. It then calls the
     * {@code removeProductList} method of the {@code controllerDomain} to remove the list. If successful, a confirmation message
     * is displayed. If an error occurs, such as the list being associated with a shelf or other issues, the exception message is displayed.
     */
    public void removeList() {
        try {
            System.out.print("Enter the name of the list you want to remove from the system: ");
            String listName = scanner.nextLine();
            controllerDomain.removeProductList(listName);

            System.out.println("List removed");
        }
        catch (ShelfException | ProductListException e) { System.out.println(e.getMessage()); }
    }


    /**
     * Changes the product list associated with a specific shelf.
     * <p>
     * This method prompts the user to enter the shelf ID and the name of the product list they want to associate with the shelf.
     * It then calls the {@code changeProductListAtShelf} method of the {@code controllerDomain} to update the association.
     * If successful, a confirmation message is displayed. If an error occurs, such as an invalid shelf ID, invalid list name,
     * or other exceptions, the respective error message is shown.
     */
    public void changeProductListAtShelf() {
        try {
            System.out.print("Enter the shelf ID: ");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The shelf ID must be of type int");
            int id = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            System.out.print("Enter the name of the list you want to associate with the shelf: ");
            String listName = scanner.nextLine();


            controllerDomain.changeProductListAtShelf(id, listName);

            System.out.println("List " + listName + " associated with shelf");
        }
        catch (ShelfException | ProductListException | IllegalArgumentException e) { System.out.println(e.getMessage()); }
    }


    /**
     * Displays the history of changes made to product lists.
     * <p>
     * This method retrieves the history of changes in the product lists by calling the {@code showOperationLog} method of the
     * {@code controllerDomain} and then displays the retrieved information to the user.
     * Displays the history of changes made to product lists.
     * <p>
     * This method retrieves the history of changes in the product lists by calling the {@code showListChanges} method of the
     * {@code controllerDomain} and then displays the retrieved information to the user.
     */
    public void showOperationLog () {
        String info = controllerDomain.showOperationLog();
        System.out.println( "Showing info \n"+info);

    }


    /**
     * Increases the quantity of a specified product in the system.
     * <p>
     * This method prompts the user to enter the product name and the quantity to be added. It then calls the
     * {@code increaseProductQuantity} method of the {@code controllerDomain} to update the product's quantity. If successful,
     * a confirmation message is displayed. If an error occurs, such as an invalid product name or quantity, the respective
     * exception message is shown.
     */
    public void increaseProductQuantity() {
        try {
            System.out.print("Enter the product name: ");
            String productName = scanner.nextLine();

            System.out.print("Enter the quantity of the product to increase: ");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The product quantity must be of type int");

            int amount = scanner.nextInt();
            scanner.nextLine(); //Consumir salto de linea

            controllerDomain.increaseProductQuantity(productName, amount);

            System.out.println("Product " + productName + " increased");
        }
        catch (ProductException| IllegalArgumentException e) {System.out.println(e.getMessage());}
    }

    /**
     * Decreases the quantity of a specified product in the system.
     * <p>
     * This method prompts the user to enter the product name and the quantity to be reduced. It then calls the
     * {@code decreaseProductQuantity} method of the {@code controllerDomain} to update the product's quantity. If successful,
     * a confirmation message is displayed. If an error occurs, such as an invalid product name or quantity, the respective
     * exception message is shown.
     */
    public void decreaseProductQuantity() {
        try {
            System.out.print("Enter the product name: ");
            String productName = scanner.nextLine();

            System.out.print("Enter the quantity of the product to decrease: ");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The product quantity must be of type int");

            int amount = scanner.nextInt();
            scanner.nextLine(); //Consumir salto de linea

            controllerDomain.decreaseProductQuantity(productName, amount);

            System.out.println("Product " + productName + " decreased");
        }
        catch (ProductException | IllegalArgumentException e) {System.out.println(e.getMessage());}
    }


    /**
     * Applies a discount to a specified product list.
     * <p>
     * This method prompts the user to enter the name of the product list and the discount percentage to be applied.
     * It then calls the {@code applyDiscountToList} method of the {@code controllerDomain} to apply the discount.
     * If successful, a confirmation message is displayed. If an error occurs, such as an invalid list name or discount value,
     * the respective exception message is shown.
     */
    public void applyDiscountToList() {
        try {
            System.out.print("Enter the name of the list to which you want to apply a discount: ");
            String listName = scanner.nextLine();

            System.out.print("Enter the discount you want to apply to the list");
            if (!scanner.hasNextDouble()) throw new IllegalArgumentException("The percentage must be of type double");

            double percentatge = scanner.nextDouble();
            scanner.nextLine(); //Consumir salto de linea


            controllerDomain.applyDiscountToList(listName, percentatge);

            System.out.println("List " + listName + " applied to discount");
        }
        catch (ProductListException | IllegalArgumentException e) {System.out.println(e.getMessage());}
    }


    /**
     * Creates a new product list with a specified name and category.
     * <p>
     * This method prompts the user to enter the name and category for the new product list. It then calls the
     * {@code createProductList} method of the {@code controllerDomain} to create the list. If successful, a confirmation message
     * is displayed. If an error occurs, such as an invalid list name or category, the respective exception message is shown.
     */
    public void createNewProductList() {
        try {
            System.out.print("Enter the name of the list you want to create: ");
            String listName = scanner.nextLine();

            System.out.print("Enter the category of the list you want to create: ");
            String category = scanner.nextLine();


            controllerDomain.createProductList(listName, category);

            System.out.println("List " + listName + " created");
        }
        catch (ProductListException  e ) {System.out.println(e.getMessage());}
    }

    /**
     * Displays all products in the catalog.
     * <p>
     * This method retrieves the list of all products from the catalog by calling the {@code getAllProductsCatalog} method
     * of the {@code controllerDomain}. It then iterates through the list of products and prints the details of each product.
     * A placeholder message is shown before listing the products.
     */
    public void showAllProductsCatalog() {
        ArrayList<Product> productArray = controllerDomain.getAllProductsCatalog();
        System.out.println("Showing all products form the catalog "); //todo hay que cambiar esta funcion por una que traiga el string directamente
        for (Product product : productArray) {
            System.out.println(product.toString());
        }
    }

    /**
     * Displays the similarities between products.
     * <p>
     * This method retrieves the product similarities by calling the {@code showSimilarity} method of the {@code controllerDomain}.
     * It then prints the retrieved information, showing the similarities between products in the system.
     */
    public void showSimilarity() {
        System.out.println("Showing similarities: ");
        String info  = controllerDomain.showSimilarity();
        System.out.println(info);
    }


    /**
     * Changes the similarity value between two products.
     * <p>
     * This method prompts the user for the names of two products and the new similarity value between them.
     * It then removes the existing similarity between the two products and adds the updated similarity.
     * If the user provides an invalid similarity value (non-double), an {@code IllegalArgumentException} is thrown.
     * <p>
     * The method calls {@code controllerDomain.removeSimilarity} and {@code controllerDomain.addSimilarity} to update the similarity between the products.
     *
     * @throws IllegalArgumentException if the similarity value provided is not a valid double.
     */
    public void changeSimilarity() {
        try {
            System.out.print("Enter the name of product 1: ");
            String productName1 = scanner.nextLine();

            System.out.print("Enter the name of product 2: ");
            String productName2 = scanner.nextLine();

            System.out.print("Enter the similarity between " + productName1 + " and " + productName2 + " (The similarity must be of type double)");
            if (!scanner.hasNextDouble()) throw new IllegalArgumentException("The similarity must be of type double");

            double similarity = scanner.nextDouble();
            scanner.nextLine();

            controllerDomain.setSimilarity(productName1, productName2, similarity);

            System.out.println("The similarity has changed");
        }
        catch (ProductException e) {System.out.println(e.getMessage());}

    }




    /**
     * Adds a new product to the product catalog.
     * <p>
     * This method prompts the user to input details for a new product, including its name, category, price,
     * and available quantity. The user is also prompted to input similarity values between the new product
     * and other products already in the catalog. The similarity values are stored as pairs of product names
     * and similarity values (of type {@code double}).
     * <p>
     * The method then calls {@code controllerDomain.addProductToCatalog} to add the product to the catalog with the specified
     * details and similarities.
     *
     * @throws IllegalArgumentException if any input value is not of the expected type, such as a non-numeric price or quantity.
     */
    public void addProductToCatalog() {
        try {
            System.out.print("Enter the product name: ");
            String productName = scanner.nextLine();

            System.out.print("Enter the product category: ");
            String category = scanner.nextLine();

            System.out.print("Enter the product price: ");
            if (!scanner.hasNextDouble()) throw new IllegalArgumentException("The product price must be of type double");

            double price = scanner.nextDouble();
            scanner.nextLine(); //Consumir salto de linea

            System.out.print("Enter the available product quantity: ");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The product quantity must be of type integer");
            int amount = scanner.nextInt();


            ArrayList<Product> productArray = controllerDomain.getAllProductsCatalog();
            ArrayList<Pair <String, Double>> similituds = new ArrayList<>();
            if (productArray.size() > 1) System.out.print("Enter the similarity of the product with the other products in the catalog: ");
            for (Product product : productArray) {
                if (!product.getName().equals(productName)) {
                    System.out.print("Enter the similarity between " + productName + " and " + product.getName() + " (The similarity must be of type double)");
                    if (!scanner.hasNextDouble()) throw new IllegalArgumentException("The similarity must be of type double");
                    double similarity = scanner.nextDouble();
                    scanner.nextLine();

                    similituds.add(new Pair<>(product.getName(), similarity));

                }
            }
            controllerDomain.addProductToCatalog(productName, category, price, amount, similituds);
            System.out.println("Product " + productName + " added to the catalog");

        } catch (ProductException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds a product to a specified product list.
     * <p>
     * This method prompts the user to input the name of the product and the name of the list
     * to which the product should be added. The method then calls
     * {@code controllerDomain.addProductToList} to add the product to the specified list.
     * <p>
     * If successful, a confirmation message is displayed. If any error occurs (e.g., invalid product or list),
     * an appropriate error message is shown.
     */
    public void addProductToList() {
        try {
            System.out.print("Enter the product name: ");
            String productName = scanner.nextLine();

            System.out.print("Enter the list name: ");
            String listName = scanner.nextLine();


            controllerDomain.addProductToList(listName, productName);
            System.out.println("Product " + productName + " added to a list");

        } catch (ProductException | ProductListException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Removes a product from a specified product list.
     * <p>
     * This method prompts the user to input the name of the product and the name of the list
     * from which the product should be removed. The method then calls
     * {@code controllerDomain.removeProductFromList} to remove the product from the specified list.
     * <p>
     * If successful, a confirmation message is displayed. If any error occurs (e.g., invalid product or list),
     * an appropriate error message is shown.
     */
    public void removeProductFromList() {
        try {
            System.out.print("Enter the product name: ");
            String productName = scanner.nextLine();

            System.out.print("Enter the list name: ");

            String listName = scanner.nextLine();

            controllerDomain.removeProductFromList(productName, listName);
            System.out.println("Product " + productName + " removed from a list");

        }
        catch (ProductException | ProductListException e) {System.out.println(e.getMessage());}
    }


    /**
     * Removes a product from the catalog.
     * <p>
     * This method prompts the user to input the name of the product that should be removed
     * from the catalog. It then calls {@code controllerDomain.removeProductFromCatalog} to
     * remove the product from the system's catalog.
     * <p>
     * If successful, a confirmation message is displayed. If the product is not found or
     * there is an issue with the product, an error message is shown.
     */
    public void removeProductFromCatalog() {
        try {
            System.out.print("Enter the product name:");
            String productName = scanner.nextLine();

            controllerDomain.removeProductFromCatalog(productName);
            System.out.println("Product " + productName + " removed from the catalog");

        }
        catch (ProductException e) {System.out.println(e.getMessage());}
    }



    /**
     * Displays all product lists in the system.
     * <p>
     * This method retrieves and displays all the product lists available in the system
     * by calling {@code controllerDomain.showAllLists}. It prints the result to the console.
     * </p>
     * <p>
     * The method provides a summary of all the lists managed by the system.
     * </p>
     */
    public void showAllLists() {
        System.out.print("Showing all lists: ");
        System.out.print(controllerDomain.showAllLists());
    }


    /**
     * Displays the content of a specific product list.
     * <p>
     * This method prompts the user to enter the name of a product list, retrieves the list
     * from the system, and then displays its content. If the list is not found or any issue
     * arises, an appropriate error message is shown.
     * </p>
     */
    public void showListContent() {
        try {
            System.out.print("Enter the list name: ");
            String listName = scanner.nextLine();
            System.out.println(controllerDomain.showProductList(listName));
        }
        catch (ProductListException e) {System.out.println(e.getMessage());}
    }


    /**
     * Creates a new shelf with the specified attributes and associates it with a product list and sorting algorithm.
     * <p>
     * This method prompts the user to enter the necessary information such as the shelf ID, size, associated product list,
     * and algorithm type, and then calls the controller to create the shelf. If any input is invalid or missing,
     * an error message is displayed.
     * </p>
     */
    public void createShelf() {
        try {
            System.out.print("Enter the shelf ID: ");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The shelf ID must be of type integer");

            int id = scanner.nextInt();
            scanner.nextLine(); // consumir salto de linea

            System.out.print("Enter the horizontal shelf size");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The shelf size must be of type integer");
            int xsize = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter the vertical shelf size");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The shelf size must be of type integer");
            int ysize = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter the name of the list you want to associate with the shelf: ");
            String listName = scanner.nextLine();

            scanner.nextLine(); //consumir salto de linea

            controllerDomain.createShelf(id, xsize, ysize, listName.trim());
            System.out.println("Shelf " + id + " created");
        }
        catch (ShelfException | ProductListException | IllegalArgumentException e) {System.out.println(e.getMessage());}
    }

    /**
     * Displays all the shelves in the system.
     * <p>
     * This method retrieves and prints out a list of all the shelves available in the system.
     * If there are no shelves or the system encounters an issue, an appropriate message will be displayed.
     * </p>
     */
    public void showAllShelfs() {
        System.out.println("Showing all shelfs: ");
        System.out.println(controllerDomain.showAllShelves());
    }

    /**
     * Displays the details of a shelf based on its ID.
     * <p>
     * This method prompts the user for a shelf ID, validates the input, and then retrieves and displays the details
     * of the shelf with that ID from the system.
     * If the provided ID is invalid or the shelf cannot be found, an appropriate error message will be shown.
     * </p>
     */
    public void showShelf() {
        try {
            System.out.print("Enter the shelf ID: ");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The shelf ID must be of type integer");

            int id = scanner.nextInt();
            scanner.nextLine(); //consumir salto de linea
            System.out.println("Showing shelf: ");

            System.out.println(controllerDomain.showShelf(id));
        }
        catch (ShelfException | IllegalArgumentException e ) {System.out.println(e.getMessage());}
    }

    /**
     * Displays the distribution details of a shelf based on its ID.
     * <p>
     * This method prompts the user to input a shelf ID, validates the input, and retrieves the distribution
     * information of the specified shelf from the system. The details are then displayed to the user.
     * If the provided ID is invalid or the shelf cannot be found, an appropriate error message will be shown.
     * </p>
     */
    public void showDistributionShelf() {
        try {
            System.out.println("Enter the shelf ID:");
            if (!scanner.hasNextInt()) throw new IllegalArgumentException("The shelf ID must be of type integer");

            int id = scanner.nextInt();
            scanner.nextLine(); //consumir salto de linea

            System.out.println("Showing distribution ");
            System.out.println(controllerDomain.showLastDistributionShelf(id));
        }
        catch (ShelfException | IllegalArgumentException e) {System.out.println(e.getMessage());}
    }


    /**
     * Loads data from a file specified by the user.
     * <p>
     * This method prompts the user to input the file path where the data is located. It then attempts to load the data
     * from the file through the controller domain. If successful, a confirmation message is displayed. If there is an issue
     * with the file path or data format, an appropriate error message is shown.
     * </p>
     */
    public void getDataThroughFile() {
        try {
            System.out.print("Enter the path to the file that contains the data: ");

            String path = scanner.nextLine();

            controllerDomain.getDataThroughFile(path);

            System.out.println("File's data has been loaded ");
        }
        catch (IllegalArgumentException e) {System.out.println(e.getMessage());}
    }


    /**
     * Creates a new distribution file based on the specified shelf and file information.
     * <p>
     * This method prompts the user for the name and path of the new file, as well as the shelf ID. It then attempts to
     * create a new distribution file using the provided information. If the file creation is successful, a confirmation
     * message is displayed. In case of errors such as invalid shelf ID, file path issues, or other exceptions, an appropriate
     * error message is shown.
     * </p>
     */
    public void crateNewDistributionFile() {
        try {
            System.out.print("Enter the name of the file you want to create: ");
            String file = scanner.nextLine();

            System.out.print("Enter the path where you want to save it (If left empty, it will be saved to the default location): ");
            String path = scanner.nextLine();

            System.out.print("Enter the shelf ID: ");

            int id = scanner.nextInt();
            scanner.nextLine();

            controllerDomain.createNewDistributionFile(file, path, id);

            System.out.println("Distribution file has been created: ");
        }
        catch (ShelfException | IllegalArgumentException | DistributionException | IOException e) {System.out.println(e.getMessage());}
    }

}


