package org.persistence.controllers;

import org.domain.classes.*;
import org.domain.exceptions.DistributionException;
import org.domain.exceptions.ProductException;
import org.domain.exceptions.ProductListException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * UserManagerData
 * <p>
 * Class responsible for managing the user credentials data.
 * Provides methods to load, save, and verify user credentials.
 * Also manages user-specific product lists and distributions.
 */
public class UserManagerData {
    private static final String CONFIG_FILE_PATH = "src/main/resources/config/user_credentials.txt";
    private static final String USERS_DIRECTORY = "src/main/resources/users/";
    private static final Logger LOGGER = Logger.getLogger(UserManagerData.class.getName());
    private final Map<Integer, User> userCredentials;    // userID -> User
    private final ProductManagerData productManagerData;

    /**
     * Constructor
     *
     * @param productManagerData Reference to ProductManagerData for accessing the product catalog.
     */
    public UserManagerData(ProductManagerData productManagerData) {
        this.productManagerData = productManagerData;
        this.userCredentials = new HashMap<>();
        initConfigFile();
    }

    /**
     * Initializes the configuration file.
     * Creates the configuration file if it does not exist.
     */
    public void initConfigFile() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            try {
                if (configFile.getParentFile() != null) {
                    configFile.getParentFile().mkdirs();
                }
                configFile.createNewFile();
                System.out.println("Configuration file created at " + configFile.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error creating configuration file", e);
            }
        }
    }

    /**
     * Saves user credentials to the configuration file.
     */
    public void saveUserCredentials() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE_PATH))) {
            for (User user : userCredentials.values()) {
                writer.write(user.getUsername() + ":" + user.getEmail() + ":" + user.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving user credentials", e);
        }
    }

    /**
     * Saves user distributions to the user's directory.
     *
     * @param username      the username of the user
     * @param distributions the list of distributions to save
     */
    public void saveUserDistributions(String username, List<Distribution> distributions) {
        String userDirPath = USERS_DIRECTORY + "/" + username + "/distributions";
        File userDir = new File(userDirPath);

        try {
            // Delete all files in the directory
            if (userDir.exists() && userDir.isDirectory()) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(userDir.toPath())) {
                    for (Path entry : stream) {
                        Files.delete(entry);
                    }
                }
            }

            if (!userDir.exists() && !userDir.mkdirs()) {
                LOGGER.log(Level.SEVERE, "Failed to create directories for user distributions");
            }

            for (Distribution distribution : distributions) {
                String distributionPath = userDirPath + "/" + distribution.getName() + ".txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(distributionPath))) {
                    writer.write(distribution.toString());
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving user distributions", e);
        }
    }

    /**
     * Saves the user's shelves to the user's directory.
     *
     * @param username the username of the user
     * @param allShelves the set of shelves to save
     */
    public void saveUserShelves(String username, Set<Shelf> allShelves) {
        String userDirPath = USERS_DIRECTORY + "/" + username + "/shelves";
        File userDir = new File(userDirPath);

        try {
            // Delete all files in the directory
            if (userDir.exists() && userDir.isDirectory()) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(userDir.toPath())) {
                    for (Path entry : stream) {
                        Files.delete(entry);
                    }
                }
            }

            if (!userDir.exists() && !userDir.mkdirs()) {
                LOGGER.log(Level.SEVERE, "Failed to create directories for user shelves");
            }

            for (Shelf shelf : allShelves) {
                String shelfPath = userDirPath + "/" + shelf.getId() + ".txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(shelfPath))) {
                    writer.write("Id: " + shelf.getId());
                    writer.newLine();
                    writer.write("xsize: " + shelf.getXsize());
                    writer.newLine();
                    writer.write("ysize: " + shelf.getYsize());
                    writer.newLine();
                    writer.write("ProductList: " + shelf.getListName());
                    writer.newLine();
                    writer.write("Distributions: ");
                    writer.newLine();
                    for (String distributionName : shelf.getDistributionNames()) {
                        writer.write(distributionName);
                        writer.newLine();
                    }
                }
            }
        } catch (DistributionException e) {
            //Nothing to do
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving user shelves", e);
        }
    }

    /**
     * Saves user product lists to the user's directory.
     *
     * @param username     the username of the user
     * @param productLists the map of product list names to ProductList objects
     */
    public void saveUserProductLists(String username, Map<String, ProductList> productLists) {
        String userDirPath = USERS_DIRECTORY + "/" + username + "/productLists";
        File userDir = new File(userDirPath);

        try {
            if (!userDir.exists() && !userDir.mkdirs()) {
                LOGGER.log(Level.SEVERE, "Failed to create directories for user product lists");
            }

            for (Map.Entry<String, ProductList> entry : productLists.entrySet()) {
                String productListName = entry.getKey();
                ProductList productList = entry.getValue();
                String productListPath = userDirPath + "/" + productListName + ".txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(productListPath))) {
                    writer.write(productListName);
                    writer.newLine();
                    writer.write("Category: " + productList.getCategory());
                    writer.newLine();
                    writer.write("Last Modified Date: " + productList.getLastModified());
                    writer.newLine();
                    for (Product product : productList.getProducts()) {
                        writer.write(product.getName());
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving user product lists", e);
        }
    }

    /**
     * Loads user credentials from the configuration file.
     *
     * @return a UserSet containing the loaded user credentials
     */
    public UserSet loadUserCredentials() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        String username = parts[0].trim();
                        String email = parts[1].trim();
                        String hashedPassword = parts[2].trim();
                        int userID = username.hashCode();

                        File userDir = new File(USERS_DIRECTORY + username);
                        if (userDir.exists() && userDir.isDirectory()) {
                            User user = new User(userID, username, email, hashedPassword);
                            userCredentials.put(userID, user);
                        } else {
                            LOGGER.log(Level.WARNING, "User directory not found for user " + username);
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading user credentials", e);
            }
        }
        return new UserSet(userCredentials);
    }

    /**
     * Loads user product lists from the specified directory.
     *
     * @param username the username of the user
     * @return a map of product list names to ProductList objects
     */
    public Map<String, ProductList> loadUserProductLists(String username) {
        File productListsDir = new File(USERS_DIRECTORY + username + "/productLists");
        return loadUserProductLists(productListsDir);
    }

    /**
     * Loads user product lists from the specified directory.
     *
     * @param productListsDir the directory containing the product lists
     * @return a map of product list names to ProductList objects
     */
    private Map<String, ProductList> loadUserProductLists(File productListsDir) {
        Map<String, ProductList> productLists = new HashMap<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(productListsDir.toPath(), "*.txt")) {
            for (Path entry : stream) {
                List<String> lines = Files.readAllLines(entry);
                ProductList productList = getProductList(entry, lines, productManagerData);
                productLists.put(productList.getName(), productList);
            }
        } catch (IOException | ProductException | ProductListException e) {
            LOGGER.log(Level.SEVERE, "Error loading user product lists", e);
        }
        return productLists;
    }

    /**
     * Loads a product list from the specified file.
     *
     * @param path the path to the product list file
     * @param lines the list of lines in the file
     * @param productManagerData the ProductManagerData instance to get products from the catalog
     * @return a ProductList object
     * @throws IOException if there is an error reading the file
     * @throws ProductException if there is an error with the product data
     * @throws ProductListException if there is an error with the product list data
     */
    private static ProductList getProductList(Path path, List<String> lines, ProductManagerData productManagerData) throws IOException, ProductException, ProductListException {
        ProductList productList = getProductList(path, lines);

        for (int i = 3; i < lines.size(); i++) {
            String productName = lines.get(i).trim();
            if (!productName.isEmpty()) {
                Product product = productManagerData.getProduct(productName);
                if (product != null) {
                    productList.loadProduct(product);
                } else {
                    throw new ProductException("Product not found in catalog: " + productName);
                }
            }
        }
        return productList;
    }

    private static ProductList getProductList(Path path, List<String> lines) throws IOException {
        if (lines.isEmpty()) {
            throw new IOException("Product list file is empty: " + path);
        }

        String productListName = lines.get(0).trim();
        String category = lines.get(1).substring("Category:".length()).trim();
        String lastModifiedDateStr = lines.get(2).substring("Last Modified Date:".length()).trim();
        LocalDateTime lastModifiedDate = LocalDateTime.parse(lastModifiedDateStr);

        ProductList productList = new ProductList(productListName, category);
        productList.setLastModified(lastModifiedDate);
        return productList;
    }

    /**
     * Loads user distributions from the specified directory.
     *
     * @param username the username of the user
     * @param productCatalog the list of products to create the associations with the distributions
     * @return a map of distribution names to Distribution objects
     */
    public Map<String, Distribution> loadUserDistributions(String username, ArrayList<Product> productCatalog) {
        String userDirPath = USERS_DIRECTORY + "/" + username + "/distributions";
        File distributionsDir = new File(userDirPath);
        return loadUserDistributions(distributionsDir, productCatalog);
    }

    /**
     * Loads user distributions from the specified directory.
     *
     * @param distributionsDir the directory containing the distributions
     * @param productCatalog the list of products to create the associations with the distributions
     * @return a map of distribution names to Distribution objects
     */
    private Map<String, Distribution> loadUserDistributions(File distributionsDir, ArrayList<Product> productCatalog) {
        Map<String, Distribution> distributions = new HashMap<>();
        if (distributionsDir.exists() && distributionsDir.isDirectory()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(distributionsDir.toPath(), "*.txt")) {
                for (Path entry : stream) {
                    List<String> lines = Files.readAllLines(entry);
                    if (!lines.isEmpty()) {
                        Distribution distribution = parseDistribution(lines, productCatalog);
                        distributions.put(distribution.getName(), distribution);
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading user distributions", e);
            }
        }
        return distributions;
    }

    /**
     * Parses a distribution from the given list of lines.
     *
     * @param lines the list of lines representing the distribution data
     * @param productCatalog the list of products to create the associations with the distributions
     * @return the parsed Distribution object
     * @throws ProductException if there is an error with the product data (there should not be any)
     */
    private Distribution parseDistribution(List<String> lines, ArrayList<Product> productCatalog) {
        String name = lines.get(0).trim();
        LocalDateTime createdDate = LocalDateTime.parse(lines.get(1).substring("Created Date: ".length()).trim());
        LocalDateTime modifiedDate = LocalDateTime.parse(lines.get(2).substring("Last Modified Date: ".length()).trim());

        Distribution distribution = new Distribution(name);
        distribution.setCreatedDate(createdDate);
        distribution.setModifiedDate(modifiedDate);

        ArrayList<ArrayList<Product>> distMatrix = new ArrayList<>();
        for (int i = 3; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            ArrayList<Product> row = new ArrayList<>();
            String[] products = line.split("\t");
            for (String productName : products) {
                if (!productName.equals("null")) {
                    Product product = productCatalog.stream()
                            .filter(p -> p.getName().equals(productName))
                            .findFirst()
                            .orElse(null);
                    row.add(product);
                } else {
                    row.add(null);
                }
            }
            distMatrix.add(row);
        }
        distribution.setDistribution(distMatrix);
        return distribution;
    }

    /**
     * Gets the user credentials.
     *
     * @return the user credentials
     */
    public Map<Integer, User> getUserCredentials() {
        return userCredentials;
    }

    /**
     * Gets a distribution from the specified list of lines.
     *
     * @param lines the list of lines
     * @param distributionName the name of the distribution
     * @return the distribution
     * @throws ProductException if there is an error parsing the product data
     */
    private static Distribution getDistribution(List<String> lines, String distributionName) throws ProductException {
        Distribution distribution = new Distribution(distributionName);
        ArrayList<ArrayList<Product>> distMatrix = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("Last Modified Date:")) {
                String dateStr = line.substring("Last Modified Date:".length()).trim();
                LocalDateTime lastModified = LocalDateTime.parse(dateStr);
                distribution.setModifiedDate(lastModified);
            } else if (line.startsWith("Row")) {
                ArrayList<Product> row = new ArrayList<>();
                String[] products = line.substring(line.indexOf(":") + 1).split(",");
                for (String productStr : products) {
                    if (!productStr.trim().equals("{null}")) {
                        String[] productData = productStr.replace("{", "").replace("}", "").split("=");
                        String productName = productData[1].split(",")[0].trim();
                        int productAmount = Integer.parseInt(productData[2].trim());
                        Product product = new Product(productName, "", 0.0, 0.0,  productAmount); // Assuming category and price are not needed here
                        row.add(product);
                    } else {
                        row.add(null);
                    }
                }
                distMatrix.add(row);
            }
        }
        // distribution.setDistribution(distMatrix);
        return distribution;
    }

    /**
     * Loads user shelves from the specified directory.
     *
     * @param username the username of the user
     * @param distributionsList the map of distribution names to Distribution objects
     * @param productLists the map of product list names to ProductList objects
     * @return a map of shelf IDs to Shelf objects
     */
    public Map<Integer, Shelf> loadUserShelves(String username, Map<String, Distribution> distributionsList, Map<String, ProductList> productLists) {
        Map<Integer, Shelf> shelves = new HashMap<>();
        String userDirPath = USERS_DIRECTORY + "/" + username + "/shelves";
        File userDir = new File(userDirPath);

        if (userDir.exists() && userDir.isDirectory()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(userDir.toPath(), "*.txt")) {
                for (Path entry : stream) {
                    List<String> lines = Files.readAllLines(entry);
                    if (!lines.isEmpty()) {
                        Shelf shelf = parseShelf(lines, distributionsList, productLists);
                        shelves.put(shelf.getId(), shelf);
                    }
                }
            } catch (IOException | ProductListException e) {
                LOGGER.log(Level.SEVERE, "Error loading user shelves", e);
            }
        }
        return shelves;
    }

    /**
     * Parses a shelf from the given list of lines.
     *
     * @param lines the list of lines representing the shelf data
     * @param distributionsList the map of distribution names to Distribution objects
     * @param productLists the map of product list names to ProductList objects
     * @return the parsed Shelf object
     * @throws ProductListException if there is an error with the product list data
     */
    private Shelf parseShelf(List<String> lines, Map<String, Distribution> distributionsList, Map<String, ProductList> productLists) throws ProductListException {
        int id = Integer.parseInt(lines.get(0).substring("Id: ".length()).trim());
        int xsize = Integer.parseInt(lines.get(1).substring("xsize: ".length()).trim());
        int ysize = Integer.parseInt(lines.get(2).substring("ysize: ".length()).trim());
        String productListName = lines.get(3).substring("ProductList: ".length()).trim();

        ProductList productList = productLists.get(productListName);
        Shelf shelf = new Shelf(id, xsize, ysize, productList);

        for (int i = 5; i < lines.size(); i++) {
            String distributionName = lines.get(i).trim();
            if (!distributionName.isEmpty()) {
                Distribution distribution = distributionsList.get(distributionName);
                shelf.addDistribution(distribution);
            }
        }
        return shelf;
    }

    /**
     * Registers a new user.
     *
     * @param username the username
     * @param email the email
     * @param hashedPassword the hashed password
     */
    public void registerUser(String username, String email, String hashedPassword) {
        int userID = username.hashCode();
        userCredentials.put(userID, new User(userID, username, email, hashedPassword));
        createUserDirectories(username);
        saveUserCredentials();
    }

    /**
     * Deletes a user from the system.
     *
     * @param username the username of the user to delete
     */
    public void deleteUser(String username) {
        int userID = username.hashCode();
        userCredentials.remove(userID);
        deleteUserDirectories(username);
        saveUserCredentials();
    }

    /**
     * Creates directories for a new user.
     *
     * @param username the username
     */
    private void createUserDirectories(String username) {
        try {
            Path userDir = Paths.get(USERS_DIRECTORY, username);
            Files.createDirectories(userDir.resolve("distributions"));
            Files.createDirectories(userDir.resolve("productLists"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating user directories", e);
        }
    }

    /**
     * Deletes directories for a user.
     *
     * @param username the username
     */
    private void deleteUserDirectories(String username) {
        Path userDir = Paths.get(USERS_DIRECTORY, username);
        if (Files.exists(userDir)) {
            try (Stream<Path> paths = Files.walk(userDir)) {
                paths.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error deleting user directories", e);
            }
        }
    }
}