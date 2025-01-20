package org.persistence.controllers;

import org.domain.classes.Product;
import org.domain.exceptions.ProductException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ProductManagerData
 * Brief: Class responsible for managing the product catalog data.
 * Provides methods to load, save, and initialize the product catalog.
 */
public class ProductManagerData {
    private static final String CATALOG_FILE_PATH = "src/main/resources/catalog/products_catalog.txt";
    private final Map<String, Product> productCatalog; // Map of product name to Product
    private final Map<String, Map<String, Double>> similaritiesMap; // Map of product name to Map of product name to similarity score

    // Constructor
    public ProductManagerData() {
        productCatalog = new HashMap<>();
        similaritiesMap = new HashMap<>();
        initConfigFile();
    }

    /**
     * Initializes the configuration file.
     */
    public void initConfigFile() {
        File catalogFile = new File(CATALOG_FILE_PATH);
        if (!catalogFile.exists()) {
            try {
                catalogFile.getParentFile().mkdirs();
                System.out.println("Catalog directory created at " + catalogFile.getParentFile().getAbsolutePath());
                catalogFile.createNewFile();
                System.out.println("Catalog file created at " + catalogFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads the product catalog from the file.
     */
    public void loadProductCatalog() {
        File catalogFile = new File(CATALOG_FILE_PATH);
        if (catalogFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(catalogFile))) {
                String line;
                boolean isProductSection = true;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        isProductSection = false;
                        continue; // Skip empty lines
                    }
                    if (line.startsWith("#")) {
                        continue; // Skip comments
                    }
                    String[] parts = line.split(",");
                    if (isProductSection && parts.length == 5) {                    // Product section
                        String productName = parts[0].trim();
                        String category = parts[1].trim();
                        double price = Double.parseDouble(parts[2].trim());
                        double originalPrice = Double.parseDouble(parts[3].trim());
                        int amount = Integer.parseInt(parts[4].trim());
                        Product product = new Product(productName, category, price, originalPrice,amount);
                        productCatalog.put(productName, product);
                    } else if (!isProductSection && parts.length == 3) {            // Similarity section
                        String product1Name = parts[0].trim();
                        String product2Name = parts[1].trim();
                        double similarityScore = Double.parseDouble(parts[2].trim());
                        similaritiesMap.computeIfAbsent(product1Name, k -> new HashMap<>()).put(product2Name, similarityScore);
                        similaritiesMap.computeIfAbsent(product2Name, k -> new HashMap<>()).put(product1Name, similarityScore);
                    }
                }
            } catch (IOException | ProductException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the product catalog and similarities to the file.
     */
    public void saveProductCatalog() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CATALOG_FILE_PATH))) {
            writer.write("# ProductName,Category,Price,OriginalPrice,Amount\n");
            for (Product product : productCatalog.values()) {
                writer.write(product.getName() + "," + product.getCategory() + "," + product.getPrice() + "," + product.getOriginalPrice()+ "," +product.getAmount());
                writer.newLine();
            }
            writer.write("\n# Product1,Product2,SimilarityScore\n");
            for (Map.Entry<String, Map<String, Double>> entry : similaritiesMap.entrySet()) {
                String product1 = entry.getKey();
                for (Map.Entry<String, Double> similarityEntry : entry.getValue().entrySet()) {
                    String product2 = similarityEntry.getKey();
                    double similarityScore = similarityEntry.getValue();
                    writer.write(product1 + "," + product2 + "," + similarityScore);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the product catalog.
     *
     * @return the product catalog
     */
    public Map<String, Product> getProductCatalog() { return productCatalog; }

    /**
     * Gets the product similarities.
     *
     * @return the product similarities map
     */
    public Map<String, Map<String, Double>> getProductSimilarities() { return similaritiesMap; }


    /**
     * Retrieves a product from the catalog by its name.
     *
     * @param productName the name of the product to retrieve
     * @return the product with the specified name, or null if not found
     */
    public Product getProduct(String productName) { return productCatalog.get(productName); }
}