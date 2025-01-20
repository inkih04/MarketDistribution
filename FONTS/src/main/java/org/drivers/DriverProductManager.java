package org.drivers;

import org.domain.types.Pair;
import org.domain.classes.Product;
import org.domain.classes.ProductList;
import org.domain.controllers.ProductManager;

import org.domain.exceptions.ProductException;
import org.domain.exceptions.ProductListException;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Driver class to test the functionalities of ProductManager.
 *
 * @see ProductManager
 * @see ProductList
 * @see Product
 * @see ProductException
 * @see ProductListException
 */
public class DriverProductManager {
    /**
     * Represents the command to add a product to the catalog.
     */
    private static final String ADD_PRODUCT_TO_CATALOG = "1";
    /**
     * Represents the command to create a product list.
     */
    private static final String CREATE_PRODUCT_LIST = "2";
    /**
     * Represents the command to add a product (from the catalog) to a product list.
     */
    private static final String ADD_PRODUCT_TO_LIST = "3";
    /**
     * Represents the command to apply a discount to a product list.
     */
    private static final String REMOVE_PRODUCT_FROM_LIST = "4";
    /**
     * Represents the command to get a product list.
     */
    private static final String GET_PRODUCT_LIST = "5";
    /**
     * Represents the command to apply a discount to a product list.
     */
    private static final String APPLY_DISCOUNT_TO_LIST = "6";
    /**
     * Represents the command to increase the quantity of a product.
     */
    private static final String INCREASE_PRODUCT_QUANTITY = "7";
    /**
     * Represents the command to decrease the quantity of a product.
     */
    private static final String DECREASE_PRODUCT_QUANTITY = "8";
    /**
     * Represents the command to add a similarity between products.
     */
    private static final String ADD_SIMILARITY = "9";
    /**
     * Represents the command to remove a similarity between products.
     */
    private static final String REMOVE_SIMILARITY = "10";
    /**
     * Represents the command to show similarities between products.
     */
    private static final String SHOW_SIMILARITIES = "11";
    /**
     * Represents the command to show all products.
     */
    private static final String SHOW_ALL_PRODUCTS = "12";
    /**
     * Represents the command to show all product lists.
     */
    private static final String SHOW_ALL_PRODUCT_LISTS = "13";
    /**
     * Represents the command to show a specific product list.
     */
    private static final String SHOW_PRODUCT_LIST = "14";
    /**
     * Represents the command to remove a product from the catalog.
     */
    private static final String REMOVE_PRODUCT_FROM_CATALOG = "15";
    /**
     * Represents the command to remove a product list.
     */
    private static final String REMOVE_PRODUCT_LIST = "16";
    /**
     * Represents the command to apply a discount to a product.
     */
    private static final String APPLY_DISCOUNT_TO_PRODUCT = "17";
    /**
     * Represents the help command.
     */
    private static final String HELP = "help";
    /**
     * Represents the exit command.
     */
    private static final String EXIT = "exit";

    /**
     * Represents the help text.
     */
    private static final String HELPTEXT = "Available commands:\n" +
            "   " + ADD_PRODUCT_TO_CATALOG + " - Add a product to the catalog\n" +
            "   " + CREATE_PRODUCT_LIST + " - Create a product list\n" +
            "   " + ADD_PRODUCT_TO_LIST + " - Add a product to a product list\n" +
            "   " + REMOVE_PRODUCT_FROM_LIST + " - Remove a product from a product list\n" +
            "   " + GET_PRODUCT_LIST + " - Get a product list\n" +
            "   " + APPLY_DISCOUNT_TO_LIST + " - Apply a discount to a product list\n" +
            "   " + INCREASE_PRODUCT_QUANTITY + " - Increase the quantity of a product\n" +
            "   " + DECREASE_PRODUCT_QUANTITY + " - Decrease the quantity of a product\n" +
            "   " + ADD_SIMILARITY + " - Add a similarity between products\n" +
            "   " + REMOVE_SIMILARITY + " - Remove a similarity between products\n" +
            "   " + SHOW_SIMILARITIES + " - Show similarities between products\n" +
            "   " + SHOW_ALL_PRODUCTS + " - Show all products\n" +
            "   " + SHOW_ALL_PRODUCT_LISTS + " - Show all product lists\n" +
            "   " + SHOW_PRODUCT_LIST + " - Show a specific product list\n" +
            "   " + REMOVE_PRODUCT_FROM_CATALOG + " - Remove a product from the catalog\n" +
            "   " + REMOVE_PRODUCT_LIST + " - Remove a product list\n" +
            "   " + APPLY_DISCOUNT_TO_PRODUCT + " - Apply a discount to a product\n" +
            "   " + HELP + " - Show all available commands\n" +
            "   " + EXIT + " - Exit the program\n";

    private static final Logger LOGGER = Logger.getLogger(DriverProductManager.class.getName());
    private final ProductManager productManager;
    private Scanner in;

    /**
     * Constructor for DriverProductManager.
     */
    public DriverProductManager() {
        productManager = ProductManager.getInstance();
    }

    /**
     * Checks if the productManager is initialized.
     *
     * @throws IllegalStateException if the productManager is not initialized
     */
    private void checkProductManager() {
        if (productManager == null) {
            throw new IllegalStateException("ProductManager is not initialized. First, create the controller!");
        }
    }

    /**
     * Tests the creation of a product list.
     */
    public void testCreateProductList() {
        try {
            checkProductManager();
            String name = getInput("Enter the name of the product list to create: ");
            String category = getInput("Enter the category of the product list: ");

            // Create an empty product list
            if (productManager.createProductList(name, category)) {
                System.out.println("Product list created successfully!");
            } else {
                System.out.println("Failed to create product list.");
            }
        } catch (ProductListException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error creating product list", e);
        }
    }

    /**
     * Tests adding a product to a product list.
     */
    public void testAddProductToList() {
        try {
            checkProductManager();
            String listName = getInput("Enter the name of the product list: ");
            String productName = getInput("Enter the name of the product to add: ");
            productManager.addProductToList(listName, productName);
            System.out.println("Product added to list!");
        } catch (ProductListException | ProductException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error adding product to list", e);
        }
    }

    /**
     * Tests removing a product from a product list.
     */
    public void testRemoveProductFromList() {
        try {
            checkProductManager();
            String listName = getInput("Enter the name of the product list: ");
            String productName = getInput("Enter the name of the product to remove: ");
            productManager.removeProductFromList(listName, productName);
            System.out.println("Product removed from list!");
        } catch (ProductListException | ProductException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error removing product from list", e);
        }
    }

    /**
     * Tests adding a product to the catalog.
     */
    public void testAddProductToCatalog() {
        try {
            checkProductManager();
            String name = getInput("Enter the product name: ");
            String category = getInput("Enter the product category: ");
            double price = getDoubleInput("Enter the product price: ");
            int quantity = getIntInput("Enter the product quantity: ");
            ArrayList<Pair<String, Double>> similarities = new ArrayList<>();
            // For simplicity, we assume that the product has no similarities
            productManager.addProductToCatalog(name, category, price, quantity, similarities);
            System.out.println("Product added to catalog!");
        } catch (ProductException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error adding product to catalog", e);
        }
    }

    /**
     * Tests getting a product list.
     */
    public void testGetProductList() {
        try {
            checkProductManager();
            String name = getInput("Enter the name of the product list: ");
            ProductList list = productManager.getProductList(name);
            System.out.println("Product list obtained: " + list.getCategory());
        } catch (ProductListException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error getting product list", e);
        }
    }

    /**
     * Tests applying a discount to a product list.
     */
    public void testApplyDiscountToList() {
        try {
            checkProductManager();
            String name = getInput("Enter the name of the product list: ");
            double percentage = getDoubleInput("Enter the discount percentage: ");
            productManager.applyDiscountToList(name, percentage);
            System.out.println("Discount applied to product list!");
        } catch (ProductListException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error applying discount to product list", e);
        }
    }

    /**
     * Tests increasing the quantity of a product.
     */
    public void testIncreaseProductQuantity() {
        try {
            checkProductManager();
            String name = getInput("Enter the product name: ");
            int quantity = getIntInput("Enter the quantity to increase: ");
            int remainingAmount = productManager.increaseProductQuantity(name, quantity);
            System.out.println("Product quantity increased! Remaining amount: " + remainingAmount);
        } catch (ProductException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error increasing product quantity", e);
        }
    }

    /**
     * Tests decreasing the quantity of a product.
     */
    public void testDecreaseProductQuantity() {
        try {
            checkProductManager();
            String name = getInput("Enter the product name: ");
            int quantity = getIntInput("Enter the quantity to decrease: ");
            int remainingAmount = productManager.decreaseProductQuantity(name, quantity);
            System.out.println("Product quantity decreased! Remaining amount: " + remainingAmount);
        } catch (ProductException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error decreasing product quantity", e);
        }
    }

    /**
     * Tests adding a similarity between two products.
     */
    public void testAddSimilarity() {
        try {
            checkProductManager();
            String name1 = getInput("Enter the first product name: ");
            String name2 = getInput("Enter the second product name: ");
            double similarity = getDoubleInput("Enter the similarity value: ");
            productManager.setSimilarity(name1, name2, similarity);
            System.out.println("Similarity added between products!");
        } catch (ProductException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error adding similarity between products", e);
        }
    }

    /**
     * Tests removing a similarity between two products.
     */
    public void testRemoveSimilarity() {
        try {
            checkProductManager();
            String productName = getInput("Enter the first product name: ");
            productManager.removeSimilarity(productName);
            System.out.println("Similarity removed between products!");
        } catch (ProductException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error removing similarity between products", e);
        }
    }

    /**
     * Tests showing all similarities.
     */
    public void testShowSimilarities() {
        try {
            checkProductManager();
            productManager.showSimilarities();
        } catch (IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error showing similarities", e);
        }
    }

    /**
     * Tests showing all products.
     */
    public void testShowAllProducts() {
        try {
            checkProductManager();
            System.out.println("All products:");
            productManager.getAllProducts().forEach(System.out::println);
        } catch (IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error showing all products", e);
        }
    }

    /**
     * Tests showing all product lists.
     */
    public void testShowAllProductLists() {
        try {
            checkProductManager();
            System.out.println("All product lists:");
            productManager.getAllProductLists().forEach(System.out::println);
        } catch (IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error showing all product lists", e);
        }
    }

    /**
     * Tests showing a specific product list.
     */
    public void testShowProductList() {
        try {
            checkProductManager();
            String name = getInput("Enter the name of the product list: ");
            ProductList list = productManager.getProductList(name);
            System.out.println("Product list: " + list);
        } catch (ProductListException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error showing product list", e);
        }
    }

    /**
     * Tests removing a product from the catalog.
     */
    public void testRemoveProductFromCatalog() {
        try {
            checkProductManager();
            String name = getInput("Enter the product name to remove: ");
            productManager.removeProductFromCatalog(name);
            System.out.println("Product removed from catalog!");
        } catch (ProductException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error removing product from catalog", e);
        }
    }

    /**
     * Tests removing a product list.
     */
    public void testRemoveProductList() {
        try {
            checkProductManager();
            String name = getInput("Enter the name of the product list to remove: ");
            productManager.removeProductList(name);
            System.out.println("Product list removed!");
        } catch (ProductListException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error removing product list", e);
        }
    }

    /**
     * Tests applying a discount to a product.
     */
    public void testApplyDiscountToProduct() {
        try {
            checkProductManager();
            String name = getInput("Enter the product name: ");
            double percentage = getDoubleInput("Enter the discount percentage: ");
            productManager.applyDiscountToProduct(name, percentage);
            System.out.println("Discount applied to product!");
        } catch (ProductException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error applying discount to product", e);
        }
    }

    /**
     * Main method to run the DriverProductManager.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            DriverProductManager dpm = new DriverProductManager();
            dpm.in = scanner;
            System.out.println("Driver for ProductManager\n");
            showMethods();
            String input = scanner.nextLine();
            while (!input.equals(EXIT) && !input.equals("0")) {
                dpm.executeCommand(input);
                dpm.returnToMenu();
                showMethods();
                input = scanner.nextLine();
            }
        }
    }

    /**
     * Executes the command based on user input.
     *
     * @param input the user input
     */
    private void executeCommand(String input) {
        switch (input) {
            case ADD_PRODUCT_TO_CATALOG:
            case "addProductToCatalog":
                testAddProductToCatalog();
                break;
            case CREATE_PRODUCT_LIST:
            case "createProductList":
                testCreateProductList();
                break;
            case ADD_PRODUCT_TO_LIST:
            case "addProductToList":
                testAddProductToList();
                break;
            case REMOVE_PRODUCT_FROM_LIST:
            case "removeProductFromList":
                testRemoveProductFromList();
                break;
            case GET_PRODUCT_LIST:
            case "getProductList":
                testGetProductList();
                break;
            case APPLY_DISCOUNT_TO_LIST:
            case "applyDiscountToList":
                testApplyDiscountToList();
                break;
            case INCREASE_PRODUCT_QUANTITY:
            case "increaseProductQuantity":
                testIncreaseProductQuantity();
                break;
            case DECREASE_PRODUCT_QUANTITY:
            case "decreaseProductQuantity":
                testDecreaseProductQuantity();
                break;
            case ADD_SIMILARITY:
            case  "addSimilarity":
                testAddSimilarity();
                break;
            case REMOVE_SIMILARITY:
            case "removeSimilarity":
                testRemoveSimilarity();
                break;
            case SHOW_SIMILARITIES:
            case "showSimilarities":
                testShowSimilarities();
                break;
            case SHOW_ALL_PRODUCTS:
            case "showAllProducts":
                testShowAllProducts();
                break;
            case SHOW_ALL_PRODUCT_LISTS:
            case "showAllProductLists":
                testShowAllProductLists();
                break;
            case SHOW_PRODUCT_LIST:
            case "showProductList":
                testShowProductList();
                break;
            case REMOVE_PRODUCT_FROM_CATALOG:
            case "removeProductFromCatalog":
                testRemoveProductFromCatalog();
                break;
            case REMOVE_PRODUCT_LIST:
            case "removeProductList":
                testRemoveProductList();
                break;
            case APPLY_DISCOUNT_TO_PRODUCT:
            case "applyDiscountToProduct":
                testApplyDiscountToProduct();
                break;
            case HELP:
                showMethods();
                break;
            default:
                System.out.println("Invalid command: " + input);
                break;
        }
    }

    /**
     * Shows the available methods.
     */
    private static void showMethods() {
        clearConsole();
        System.out.println(HELPTEXT);
    }

    /**
     * Returns to the main menu.
     */
    private void returnToMenu() {
        System.out.println("Press ENTER to return to the main menu");
        in.nextLine();
    }

    // ! Helper methods

    /**
     * Clears the console.
     */
    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error clearing console", e);
        }
    }

    /**
     * Gets input from the user.
     *
     * @param prompt the prompt message
     * @return the user input
     */
    private String getInput(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = in.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please enter a valid value.");
            }
        } while (input.isEmpty());
        return input;
    }

    /**
     * Gets a double input from the user.
     *
     * @param prompt the prompt message
     * @return the user input as a double
     */
    private double getDoubleInput(String prompt) {
        String input;
        double value = 0;
        boolean valid = false;
        do {
            System.out.print(prompt);
            input = in.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please enter a valid value.");
            } else {
                try {
                    value = Double.parseDouble(input);
                    valid = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }
        } while (!valid);
        return value;
    }

    /**
     * Gets an integer input from the user.
     *
     * @param prompt the prompt message
     * @return the user input as an integer
     */
    private int getIntInput(String prompt) {
        String input;
        int value = 0;
        boolean valid = false;
        do {
            System.out.print(prompt);
            input = in.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please enter a valid value.");
            } else {
                try {
                    value = Integer.parseInt(input);
                    valid = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }
        } while (!valid);
        return value;
    }
}