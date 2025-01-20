package org.domain.controllers;

import java.io.BufferedReader;
import java.io.FileReader;

import org.domain.classes.Product;

import org.domain.types.Pair;

import org.domain.exceptions.*;
import java.util.ArrayList;
import java.io.IOException;


/**
 * CommandParser class.
 * <p>Parses and executes commands from a text file to manage product lists.</p>
 * @author Sergio Shmyhelskyy Yaskevych
 * @version 1.0
 */
public class InputManager {
    private ControllerDomain controllerDomain;

    /**
     * Constructs a new CommandParser with the specified ProductManager.
     *
     */
    public InputManager() {
        this.controllerDomain = ControllerDomain.getInstance();
    }


    /**
     * Retrieves the singleton instance of the InputManager.
     *
     * <p>This method returns the singleton instance of the InputManager class,
     * ensuring that only one instance of the class exists throughout the application.
     * It uses the Singleton design pattern to provide a global point of access to the instance.</p>
     *
     * @return The singleton instance of InputManager.
     */
    public static InputManager getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final InputManager INSTANCE = new InputManager();
    }



    /**
     * Parses a file with commands and executes them.
     *
     * @param filePath the path to the file containing commands
     */
    public void parseFile(String filePath)  {
        controllerDomain = ControllerDomain.getInstance();
        if (!filePath.endsWith(".txt")) throw new IllegalArgumentException("File is not a text file");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                executeCommand(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }


    /**
     * Executes a command line.
     *
     * @param commandLine the command line to execute
     */
    public void executeCommand(String commandLine) {
        // Ignore empty lines or lines with # as comments
        if (commandLine.isEmpty() || commandLine.startsWith("#")) {
            return;
        }

        String[] parts = commandLine.split("\\s+");
        String command = parts[0].toUpperCase();

        try {
            switch (command) {
                case "CREATE_LIST":
                    handleCreateList(parts);
                    break;
                case "DELETE_LIST":
                    handleDeleteList(parts);
                    break;
                case "ADD_PRODUCT":
                    handleAddProductToList(parts);
                    break;
                case "REMOVE_PRODUCT":
                    handleRemoveProductFromList(parts);
                    break;
                case "SHOW_SIMILARITIES":
                    handleShowSimilarities();
                    break;
                case "CHANGE_SIMILARITIES":
                    handleChangeSimilarities(parts);
                    break;
                case "DECREASE_PRODUCT_QUANTITY":
                    handleDecreaseProductQuantity(parts);
                    break;
                case "INCREASE_PRODUCT_QUANTITY":
                    handleIncreaseProductQuantity(parts);
                    break;
                case "ADD_PRODUCT_TO_CATALOG":
                    handleAddProductToCatalog(parts);
                    break;
                case "REMOVE_PRODUCT_CATALOG":
                    handleRemoveProductFromCatalog(parts);
                    break;
                case "SHOW_MODIFICATION_LOG":
                    handleShowModificationLog(parts);
                    break;
                case  "APPLY_DISCOUNT_TO_LIST":
                    handleApplyDiscountToList(parts);
                    break;
                case "CREATE_SHELF":
                    handleCreateShelf(parts);
                    break;
                case "CHANGE_PRODUCT_LIST_AT_SHELF":
                    handleChangeProductListAtShelf(parts);
                    break;
                case "CREATE_NEW_DISTRIBUTION_FILE":
                    handleCreateNewDistributionFile(parts);
                    break;
                case "SHOW_PRODUCTS":
                    handleShowProducts();
                    break;
                case "SHOW_ALL_LISTS":
                    handleShowAllProductList();
                    break;
                case "SHOW_DISTRIBUTION":
                    handleShowDistibution(parts);
                    break;
                case "CREATE_DISTRIBUTION":
                    handleCreateNewDistribution(parts);
                    break;
                case "MODIFY_DISTRIBUTION":
                    handleModifyDistribution(parts);
                    break;
                case "SHOW_SHELVES":
                    handleShowShelves(parts);
                    break;

                default:
                    System.out.println("Error: Unknown command '" + command + "'.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error in command '" + command + "': " + e.getMessage());
        }
    }

    /**
     * Handles the process of displaying the last distribution of a specific shelf.
     * This method validates the input, retrieves the last distribution of the specified shelf
     * using the domain controller, and prints the result.
     * If an error occurs, it catches and displays the corresponding exception message.
     * @param parts an array of strings representing the input command and its arguments.
     *              It should have exactly 2 elements: the command name and the shelf ID.
     * @throws InputManagerException if the input does not have the correct number of parameters
     *         or if the shelf ID cannot be parsed.
     * @throws ShelfException if there is an issue retrieving the distribution for the specified shelf.
     */
    private void handleShowDistibution(String[] parts) {
        try {
            if (parts.length != 2) throw new InputManagerException("SHOW_DISTRIBUTION requires exactly 1 parameter idShelf");

            int idShelf = parseInteger(parts[1], "idShelf");

            String distribution = controllerDomain.showLastDistributionShelf(idShelf);

            System.out.println("Last distribution of shelf:" + idShelf+ "\n" + distribution);
        }
        catch (InputManagerException | ShelfException e) {System.out.println(e.getMessage());}
    }

    /**
     * Handles the modification of a distribution by updating the associated products.
     *
     * <p>This method processes the input parameters, which should include the distribution name
     * and two product names. It validates the input, ensuring that exactly four arguments are
     * provided. If valid, it delegates the modification process to the controller layer.</p>
     *
     * @param parts an array of strings containing the distribution name and two product names.
     * @throws IllegalArgumentException if the number of input arguments is incorrect.
     * @throws DistributionException if an error occurs while modifying the distribution.
     */

    private void handleModifyDistribution(String[] parts) {
        try {
            if (parts.length != 4) throw new IllegalArgumentException("Modify distribution requires exactly 4 nameDistribution, nameProduct1, nameProduct2)");
            String nameDistribution = parts[1];
            String nameProduct1 = parts[2];
            String nameProduct2 = parts[3];

            controllerDomain.modifyDistribution(nameDistribution, nameProduct1, nameProduct2);
            System.out.println("The distribution has changed");

        }
        catch (IllegalArgumentException | DistributionException e) {System.out.println(e.getMessage());}
    }


    /**
     * Handles the creation of a new distribution for a specific shelf.
     *
     * This method validates the input, parses the shelf ID and distribution name,
     * and invokes the domain controller to create the distribution.
     * It prints a success message if the operation is successful or displays an
     * exception message if an error occurs.
     *
     * @param parts an array of strings representing the input command and its arguments.
     *              It should have exactly 3 elements: the command name, the shelf ID,
     *              and the name of the distribution.
     */
    private void handleCreateNewDistribution(String[] parts) {
        try {
            if (parts.length > 5 || parts.length < 3) throw new InputManagerException("CREATE_NEW_DISTRIBUTION requires exactly 3 parameters idShelf, nomDistribution, algorithm and (optional) limit");
            int idShelf = parseInteger(parts[1], "idShelf");
            String nomDistribution = parts[2];
            int algorithm = parseInteger(parts[3], "algorithm");
            int limit;
           if (parts.length == 5)  limit = parseInteger(parts[4], "limit");
           else limit = 100;

           controllerDomain.createNewDistribution(idShelf, nomDistribution, algorithm, limit);

            System.out.println("A new distribution of " + idShelf + " has been created with name" + nomDistribution +".");
        }
        catch (InputManagerException | ShelfException | DistributionException e) {System.out.println(e.getMessage());}
    }


    /**
     * Handles the display of all products in the catalog.
     *
     * This method retrieves the list of all products from the catalog using the domain controller
     * and prints their details to the console.
     */
    private void handleShowProducts() {
        ArrayList<Product> product =  controllerDomain.getAllProductsCatalog(); //todo hay que cambiar esta funcion por una que traiga el string directamente
        System.out.println("Showing all products");
        for (Product p : product) {
            System.out.println(p.toString());
        }
    }

    /**
     * Handles the display of all product lists.
     *
     * This method retrieves all product lists from the domain controller and prints their details
     * to the console. If an error occurs while retrieving the lists, the exception message is displayed.
     *
     * @throws ProductListException if there is an issue retrieving the product lists.
     */
    private void handleShowAllProductList() {
        System.out.println("Showing all productLists");
        System.out.println(controllerDomain.showAllLists());
    }

    /**
     * Handles the creation of a new product list.
     *
     * <p>This method processes the input parameters to create a new product list. It ensures that
     * exactly two arguments are provided: the list name and the list category. If valid, the method
     * delegates the creation of the product list to the controller layer.</p>
     *
     * @param parts an array of strings containing the list name and list category.
     * @throws InputManagerException if the number of input arguments is incorrect.
     * @throws ProductListException if an error occurs while creating the product list.
     */
    private void handleCreateList(String[] parts)  {
        try {
            if (parts.length != 3) {
                throw new InputManagerException("CREATE_LIST requires 2 parameters: listName and listCategory");
            }
            String listName = parts[1];
            String listCategory = parts[2];
            controllerDomain.createProductList(listName, listCategory);
            System.out.println("List created: " + listName);
        }
        catch (InputManagerException | ProductListException e) {System.out.println(e.getMessage());}
    }

    /**
     * Handles the deletion of an existing product list.
     *
     * <p>This method processes the input parameters to delete a product list by its name. It validates
     * that exactly one argument (the list name) is provided. If valid, the method delegates the deletion
     * of the product list to the controller layer.</p>
     *
     * @param parts an array of strings containing the list name to be deleted.
     * @throws InputManagerException if the number of input arguments is incorrect.
     * @throws ProductListException if an error occurs while removing the product list.
     * @throws ShelfException if there is an issue related to the shelf during the deletion process.
     */
    private void handleDeleteList(String[] parts) {
        try {
            if (parts.length != 2) {
                throw new InputManagerException("DELETE_LIST command requires exactly 1 parameter: listId.");
            }
            String listName = parts[1];
            /// ////////////////////////////////////////////////////////////////////////////////////////////////////////
            controllerDomain.removeProductList(listName);
            /// ///////////////////////////////////////////////////////////////////////////////////////////////////////
            System.out.println("List deleted: " + listName);
        }
        catch (ProductListException | ShelfException |InputManagerException e) {System.out.println(e.getMessage());}
    }

    /**
     * Handles the addition of a product to a specified product list.
     *
     * <p>This method processes the input parameters to add a product to a product list. It ensures
     * that exactly two arguments are provided: the product name and the list name. If valid, the
     * method delegates the product addition to the controller layer.</p>
     *
     * @param parts an array of strings containing the product name and the list name.
     */
    public void handleAddProductToList(String[] parts) {
        try {
            if (parts.length != 3)
                throw new InputManagerException("ADD_PRODUCT requires 2 parameters: nameProduct and nameList");
            String listName = parts[2];
            String productName = parts[1];

            controllerDomain.addProductToList(listName, productName);
            System.out.println("Product added: " + productName + " to list: " + listName);
        }
        catch (ProductException | InputManagerException | ProductListException e) {System.out.println(e.getMessage());}
    }

    /**
     * Displays similarities related to products or distributions.
     *
     * <p>This method retrieves similarity information from the controller layer and displays it to
     * the user. The retrieved information is printed in the console to provide insight into the
     * similarities being tracked or calculated within the system.</p>
     */
    public void handleShowSimilarities() {
        String info = controllerDomain.showSimilarity();
        System.out.println("Showing similarities: \n"+info);
    }

    /**
     * Handles the modification of similarities between two products.
     *
     * <p>This method processes the input parameters to change the similarity value between two
     * specified products. It validates that exactly three arguments are provided: the two product
     * names and the new similarity value. The method first removes the existing similarity and
     * then adds the new similarity between the products.</p>
     *
     * @param parts an array of strings containing the two product names and the new similarity value.
     */
    public void handleChangeSimilarities(String[] parts) {
        try {
            if (parts.length != 4) throw new InputManagerException("REMOVE_SIMILARITIES requires 3 parameters: nomProduct1, nomProduct2, and newSimilarity");

            String productName2 = parts[2];
            String productName1 = parts[1];
            double newSimilarity = parseDouble(parts[3], "newSimilarity");

            controllerDomain.setSimilarity(productName1, productName2, newSimilarity);

            System.out.println("Similarity changed: " + productName1 + " to " + productName2);

        }
        catch (ProductException | InputManagerException e) {System.out.println(e.getMessage());}
    }

    /**
     * Handles the removal of a product from a specified product list.
     *
     * <p>This method processes the input parameters to remove a product from a product list. It
     * ensures that exactly two arguments are provided: the product name and the list name. If valid,
     * the method delegates the product removal to the controller layer.</p>
     *
     * @param parts an array of strings containing the product name and the list name.
     */
    public void handleRemoveProductFromList(String[] parts) {
        try {
            if (parts.length != 3) throw new InputManagerException("REMOVE_PRODUCT requires 2 parameters: nameProduct and nameList");
            String listName = parts[2];
            String productName = parts[1];

            controllerDomain.removeProductFromList(productName, listName);
            System.out.println("Product removed: " + productName + " from list: " + listName);
        }
        catch (ProductException | ProductListException | InputManagerException e) {System.out.println(e.getMessage());}
    }

    /**
     * Handles the decrease in quantity of a specified product.
     *
     * <p>This method processes the input parameters to decrease the quantity of a product. It ensures
     * that exactly two arguments are provided: the product name and the quantity to be decreased.
     * If valid, the method delegates the quantity decrease to the controller layer.</p>
     *
     * @param parts an array of strings containing the product name and the quantity to decrease.
     */
    public void handleDecreaseProductQuantity(String[] parts) {
        try {
            if (parts.length != 3) throw new InputManagerException("DECREASE_PRODUCT_QUANTITY requires 2 parameters: nameProduct and quantity");

            String nameProduct = parts[1];
            int quantity = parseInteger(parts[2], "quantity");

            controllerDomain.decreaseProductQuantity(nameProduct, quantity);
            System.out.println("Product decreased: " + nameProduct + " with quantity: " + quantity);
        }
        catch (ProductException | InputManagerException e) {System.out.println(e.getMessage());}
    }

    /**
     * Handles the increase in quantity of a specified product.
     *
     * <p>This method processes the input parameters to increase the quantity of a product. It ensures
     * that exactly two arguments are provided: the product name and the quantity to be increased.
     * If valid, the method delegates the quantity increase to the controller layer.</p>
     *
     * @param parts an array of strings containing the product name and the quantity to increase.
     */
    public void handleIncreaseProductQuantity(String[] parts) {
        try {
            if (parts.length != 3) throw new InputManagerException("INCREASE_PRODUCT_QUANTITY requires 2 parameters: nameProduct and quantity");

            String nameProduct = parts[1];
            int quantity = parseInteger(parts[2], "quantity");

            controllerDomain.increaseProductQuantity(nameProduct, quantity);
            System.out.println("Product increased: " + nameProduct + " with quantity: " + quantity);
        }
        catch (ProductException | InputManagerException e) {System.out.println(e.getMessage());}
    }

    /**
     * Handles the addition of a product to the catalog, including its details and optional similarities to other products.
     *
     * <p>This method processes the input parameters to add a product to the catalog. It expects five mandatory
     * parameters: product name, category, price, amount, and an optional list of similar products with their similarity values.
     * The method validates the input, parses the data, and delegates the addition of the product to the catalog to the controller layer.</p>
     *
     * @param parts an array of strings containing the product details and optional similarities.
     */
    public void handleAddProductToCatalog(String[] parts) {
        try {
            // Divide la entrada en la parte del producto y las similitudes
            String[] commandAndSimilitudes = String.join(" ", parts).split("\\|");
            String[] commandParts = commandAndSimilitudes[0].trim().split("\\s+");

            // Validar la cantidad mínima de parámetros
            if (commandParts.length < 5) {
                throw new InputManagerException("ADD_PRODUCT_TO_CATALOG requires 5 parameters: productName, category, price, and amount");
            }

            // Parsear los parámetros básicos
            String productName = commandParts[1];
            String category = commandParts[2];
            double price = parseDouble(commandParts[3], "price");
            int amount = parseInteger(commandParts[4], "amount");

            // Crear el ArrayList de similitudes
            ArrayList<Pair<String, Double>> similitudes = new ArrayList<>();

            // Si existen similitudes, parsearlas
            if (commandAndSimilitudes.length > 1) {
                String similitudesPart = commandAndSimilitudes[1].trim();
                String[] similitudesEntries = similitudesPart.split("\\}");

                for (String entry : similitudesEntries) {
                    entry = entry.replace("{", "").trim();
                    if (!entry.isEmpty()) {
                        String[] simParts = entry.split(",");
                        if (simParts.length != 2) {
                            throw new InputManagerException("ADD_PRODUCT_TO_CATALOG requires 5 parameters: productName, category, price, and amount");
                        }
                        String similarProductName = simParts[0].trim();
                        double similarityValue = parseDouble(simParts[1].trim(), "similarity");
                        similitudes.add(new Pair<>(similarProductName, similarityValue));
                    }
                }
            }
            controllerDomain.addProductToCatalog(productName, category, price, amount, similitudes);
            System.out.println("Product added: " + productName + " with category: " + category);
        } catch (InputManagerException |ProductException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles the removal of a product from the catalog.
     *
     * <p>This method processes the input parameters to remove a product from the catalog. It expects
     * exactly one argument: the product name. If valid, the method delegates the removal of the product
     * from the catalog to the controller layer.</p>
     *
     * @param parts an array of strings containing the product name to be removed.
     */
    public void handleRemoveProductFromCatalog(String[] parts) {
        try {
            if (parts.length != 2) throw new InputManagerException("ADD_PRODUCT_TO_CATALOG requires 1 parameter: productName");
            String productName = parts[1];
            controllerDomain.removeProductFromCatalog(productName);
            System.out.println("Product removed: " + productName);
        }
        catch (InputManagerException |ProductException e) {System.out.println(e.getMessage());}
    }

    /**
     * Displays the modification log showing changes made to the product list.
     *
     * <p>This method retrieves the modification log from the controller layer and displays it
     * to the user, providing information on the recent changes made to the product list.</p>
     * @param parts an array of strings representing the input command and its arguments.
     */
    public void handleShowModificationLog(String[] parts) {
        String info = controllerDomain.showOperationLog();
        System.out.println("Showing modifation log: \n"+info);
    }


    /**
     * Applies a discount to all products in a specified product list.
     *
     * <p>This method processes the input parameters to apply a discount to a product list. It expects
     * exactly two arguments: the list name and the discount percentage. If valid, the method delegates
     * the discount application to the controller layer.</p>
     * @param parts an array of strings containing the list name and the discount percentage.
     */
    public void handleApplyDiscountToList(String[] parts) {
        try {
            if (parts.length != 3) throw new InputManagerException("APPLY_DISCOUNT_TO_LIST requires 2 parameters: nameList and percentage");
            String listName = parts[1];
            double percentage = parseDouble(parts[2], "percentage");

            controllerDomain.applyDiscountToList(listName, percentage);
            System.out.println("Discount applied: " + listName + " with percentage: " + percentage);
        }
        catch (ProductListException | InputManagerException e) {System.out.println(e.getMessage());}
    }

    /**
     * Creates a new shelf with specified capacity and algorithm, and assigns a product list to it.
     *
     * <p>This method processes the input parameters to create a new shelf. It expects exactly four arguments:
     * the shelf ID, x and y capacities, the product list name, and the algorithm to be used for the shelf.
     * If valid, the method delegates the creation of the shelf to the controller layer.</p>
     *
     * @param parts an array of strings containing the shelf ID, x and y capacities, list name, and algorithm.
     */
    public void handleCreateShelf(String[] parts) {
        try {
            if (parts.length != 5) throw new InputManagerException("CREATE_SHELF requires 4 parameters: id, xCapacity, yCapacity and listName");
            int id = parseInteger(parts[1], "id");
            int xCapacity = parseInteger(parts[2], "xCapacity");
            int yCapacity = parseInteger(parts[3], "yCapacity");
            String listName = parts[4];

            controllerDomain.createShelf(id, xCapacity, yCapacity,listName);
            System.out.println("Shelf created: " + id + " with xCapacity: " + xCapacity + " and yCapacity: " + yCapacity);
        }
        catch (ShelfException | InputManagerException | ProductListException e) {System.out.println(e.getMessage());}

    }

    /**
     * Displays all the shelves in the system.
     *
     * <p>This method retrieves and prints the details of all the shelves available in the system by
     * calling the appropriate method from the controller layer. It provides a list of all shelves.</p>
     * @param parts an array of strings representing the input command and its arguments.
     */
    public void handleShowShelves(String[] parts) {
        System.out.println("Showing Shelves");
        System.out.println(controllerDomain.showAllShelves());
    }

    /**
     * Changes the product list assigned to a specified shelf.
     *
     * <p>This method processes the input parameters to change the product list assigned to a shelf.
     * It expects exactly two arguments: the shelf ID and the new product list name. If valid,
     * the method delegates the update to the controller layer.</p>
     *
     * @param parts an array of strings containing the shelf ID and the new product list name.
     */
    public void handleChangeProductListAtShelf(String[] parts) {
        try {
            if (parts.length != 3) throw new InputManagerException("CHANGE_PRODUCT_LIST_AT_SHELF requires 2 parameters: shelfId and listName");

            int id = parseInteger(parts[1], "id");
            String listName = parts[2];

            controllerDomain.changeProductListAtShelf(id, listName);

            System.out.println("Shelf changed: " + id + " with listName: " + listName);
        }
        catch (ProductListException | ShelfException | InputManagerException e) {System.out.println(e.getMessage());}
    }

    /**
     * Creates a new distribution file for a specified shelf.
     *
     * <p>This method processes the input parameters to create a new distribution file. It expects
     * exactly three arguments: the new distribution file name, the file path, and the shelf ID.
     * If valid, the method delegates the creation of the distribution file to the controller layer.</p>
     *
     * @param parts an array of strings containing the new distribution file name, path, and shelf ID.
     */
    public void handleCreateNewDistributionFile(String[] parts) {
        try {
            if (parts.length != 4) throw new InputManagerException("CREATE_NEW_DISTRIBUTION_FILE requires 3 parameters: newName, path, and shelfId");

            String newName = parts[1];
            String path = parts[2];
            int id = parseInteger(parts[3], "id");

            controllerDomain.createNewDistributionFile(newName, path, id);

            System.out.println("Distribution file created: " + newName + " with path: " + path);
        }
        catch (InputManagerException | ShelfException | DistributionException |IOException e) {System.out.println(e.getMessage());}
    }

    /**
     * Parses a string value into an integer.
     *
     * <p>This method attempts to convert the provided string value into an integer. If the conversion fails,
     * it throws an {@link IllegalArgumentException} with a message indicating that the specified parameter
     * must be an integer.</p>
     *
     * @param value the string value to be parsed into an integer.
     * @param paramName the name of the parameter, used for the error message in case of failure.
     * @return the parsed integer value.
     */
    private int parseInteger(String value, String paramName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(paramName + " must be an integer.");
        }
    }

    /**
     * Parses a string value into a double.
     *
     * <p>This method attempts to convert the provided string value into a double. If the conversion fails,
     * it throws an {@link IllegalArgumentException} with a message indicating that the specified parameter
     * must be a decimal number.</p>
     *
     * @param value the string value to be parsed into a double.
     * @param paramName the name of the parameter, used for the error message in case of failure.
     * @return the parsed double value.
     */
    private double parseDouble(String value, String paramName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(paramName + " must be a decimal number.");
        }
    }
}