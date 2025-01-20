/** @file
 *  ProductManager class.
 *  Manages multiple product lists, providing methods to add, remove, and edit products.
 *  Keeps a log of modifications with details about the operations performed.
 *  Manages the creation and quantity of products.
 */

package org.domain.controllers;

import org.domain.classes.Product;
import org.domain.classes.ProductList;

import org.domain.types.Pair;

// ! Exception
import org.domain.exceptions.ProductException;
import org.domain.exceptions.ProductListException;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * ProductManager
 * Manages multiple product lists, providing methods to add, remove, and edit products.
 * Keeps a similarity map between all the products in the catalog and the product lists.
 * @author Sergio Shmyhelskyy Yaskevych (sergio.shmyhelskyy@estudiantat.upc.edu)
 * @version 1.0
 */
public class ProductManager {

    private Map<String, Product> productMap; // Map of unique products (key: product name)
    private Map<String, ProductList> productLists; // Map of product lists
    private Map<String, Map<String, Double>> similarityMap; // Map of similarities between products

    /**
     * Constructs a new ProductManager.
     */
    public ProductManager() {
        this.productMap = new HashMap<>();
        this.productLists = new HashMap<>();
        this.similarityMap = new HashMap<>();
    }

    /**
     * Gets the singleton instance of the ProductManager.
     */
    private static class SingletonHelper {
        private static final ProductManager INSTANCE = new ProductManager();
    }

    /**
     * Gets the singleton instance of the ProductManager.
     * @return the singleton instance
     */
    public static ProductManager getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * Gets the singleton instance of the ProductManager.
     * @param catalog the product catalog
     *                (key: product name, value: product object)
     * @param similarities the similarity map
     *                     (key: product name, value: map of similar products and their similarity scores)
     * @return the singleton instance
     */
    public static ProductManager getInstance(Map<String, Product> catalog, Map<String, Map<String, Double>> similarities) {
        ProductManager instance = getInstance();
        instance.productMap = catalog;
        instance.productLists = new HashMap<>();
        instance.similarityMap = similarities;
        return instance;
    }

    // ! Methods for managing products

    /**
     * Adds a unique product to the supermarket's product catalog.
     * @param productName the name of the product
     * @param category the category of the product
     * @param price the price of the product
     * @param amount the amount of the product
     * @param similarities a list of product names and similarity values
     *                     (key: product name, value: similarity score)
     * @throws ProductException if the product already exists in the catalog
     */
    public void addProductToCatalog(String productName, String category, double price, int amount, ArrayList<Pair<String, Double>> similarities) throws ProductException {
        if (productName.trim().isEmpty()) throw new ProductException("Invalid product name");
        if (category.trim().isEmpty()) throw new ProductException("Invalid product category");
        if (price <= 0) throw new ProductException("Invalid product price");
        if (amount <= 0) throw new ProductException("Invalid product amount");

        if (!productMap.containsKey(productName)) {
            Product product = new Product(productName, category, price, price,amount);
            productMap.put(productName, product);

            // Add similarities
            addSimilarities(similarities, product);
        }
        else throw new ProductException("Product already exists in the catalog: " + productName);
    }

    /**
     * Removes a product from the catalog.
     * @param productName the name of the product to remove
     * @throws ProductException if the product does not exist in the catalog
     */
    public void removeProductFromCatalog(String productName) throws ProductException {
        if (productMap.containsKey(productName)) {
            // Remove product from similarities
            removeSimilarity(productName);
            // Remove product from catalog
            productMap.remove(productName);
            // Remove product from all product lists
            productLists.forEach((_, productList) -> productList.removeProduct(productName));
        }
        else throw new ProductException("Product does not exist in the catalog: " + productName);
    }

    /**
     * Updates a product in the catalog.
     * @param productName the name of the product
     * @param category the category of the product
     * @param price the price of the product
     * @param amount the amount of the product
     * @param similarities a list of product names and similarity values
     *                     (key: product name, value: similarity score)
     * @throws ProductException if the product does not exist in the catalog, the category is invalid, the price is not positive, the name parameter is empty or the amount is not positive
     */
    public void updateProductFromCatalog(String productName, String category, double price, int amount, ArrayList<Pair<String, Double>> similarities) throws ProductException {
        if (productName.trim().isEmpty()) throw new ProductException("Invalid product name");
        if (category.trim().isEmpty()) throw new ProductException("Invalid product category");
        if (price <= 0) throw new ProductException("Invalid product price");
        if (amount <= 0) throw new ProductException("Invalid product amount");

        if (productMap.containsKey(productName)) {
            Product product = productMap.get(productName);
            product.setCategory(category);
            product.setPrice(price);
            product.setAmount(amount);

            // Add similarities
            addSimilarities(similarities, product);
        }
        else throw new ProductException("Product does not exist in the catalog: " + productName);
    }

    /**
     * Increases the quantity of a product in the catalog.
     * @param productName the name of the product
     * @param amount the amount to increase
     * @return the remaining amount of the product
     * @throws ProductException if the product does not exist or the amount is not positive
     */
    public int increaseProductQuantity(String productName, int amount) throws ProductException {
        if (amount <= 0) {
            throw new ProductException("Amount must be positive: " + amount);
        }
        Product product = productMap.get(productName);
        if (product == null) {
            throw new ProductException("Product does not exist in the catalog: " + productName);
        }
        return product.updateAmount(amount);
    }

    /**
     * Decreases the quantity of a product in the catalog.
     * @param productName the name of the product
     * @param amount the amount to decrease
     * @return the remaining amount of the product
     * @throws ProductException if the product does not exist, the amount is invalid, or the resulting amount is negative
     */
    public int decreaseProductQuantity(String productName, int amount) throws ProductException {
        if (amount <= 0) {
            throw new ProductException("Amount must be positive: " + amount);
        }
        Product product = productMap.get(productName);
        if (product == null) {
            throw new ProductException("Product does not exist in the catalog: " + productName);
        }
        int remainingAmount = product.updateAmount(-amount);

        // Remove product from catalog (and all lists) if quantity reaches zero
        if (remainingAmount == 0) removeProductFromCatalog(productName);

        // Return remaining amount
        return remainingAmount;
    }

    /**
     * Applies a discount to a product and logs the modification.
     * @param productName the name of the product
     * @param percentage the discount percentage to apply
     * @throws ProductException if the product does not exist in the catalog
     */
    public void applyDiscountToProduct(String productName, double percentage) throws ProductException {
        Product product = productMap.get(productName);
        if (product == null) {
            throw new ProductException("Product does not exist in the catalog: " + productName);
        }
        product.applyDiscount(percentage);
    }

    /**
     * Gets a product by name.
     * @param productName the name of the product
     * @return the product
     * @throws ProductException if the product does not exist in the catalog
     */
    public Product getProduct(String productName) throws ProductException {
        Product product = productMap.get(productName);
        if (product == null) {
            throw new ProductException("Product does not exist in the catalog: " + productName);
        }
        return product;
    }

    /**
     * Gets all products in the catalog.
     * @return a list of all products
     */
    public ArrayList<Product> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }

    // ! Methods for managing product lists

    /**
     * Creates a new product list in the supermarket.
     * @param listName the name of the product list
     * @param category the category of the product list
     * @return true if the list was created, false if it already exists
     * @throws ProductListException if the product list already exists
     */
    public boolean createProductList(String listName, String category) throws ProductListException {
        if (!productLists.containsKey(listName)) {
            ProductList newList = new ProductList(listName, category);
            productLists.put(listName, newList);
            return true;
        }
        throw new ProductListException("Product list already exists: " + listName);
    }
    /**
     * Removes a product list from the supermarket.
     * @param listName the name of the product list to remove
     * @throws ProductListException if the product list does not exist
     */
    public void removeProductList(String listName) throws ProductListException {
        productLists.remove(listName);
        throw new ProductListException("Product list does not exist: " + listName);
    }

    /**
     * Gets a product list by name.
     * @param listName the name of the product list
     * @return the product list
     * @throws ProductListException if the product list does not exist
     */
    public ProductList getProductList(String listName) throws ProductListException {
        ProductList productList = productLists.get(listName);
        if (productList == null) {
            throw new ProductListException("Product list does not exist: " + listName);
        }
        return productList;
    }

    /**
     * Gets all product lists.
     * @return a list of all product lists
     */
    public ArrayList<ProductList> getAllProductLists() {
        return new ArrayList<>(productLists.values());
    }

    /**
     * Gets all product lists.
     * @return a list (Map) of all product lists of the user
     */
    public Map<String, ProductList> getProductLists() {
        return productLists;
    }

    /**
     * Sets the product lists of a specific user.
     * @param productLists the list of product lists
     */
    public void setProductLists(Map<String, ProductList> productLists) {
        productLists.values().forEach(productList -> this.productLists.put(productList.getName(), productList));
    }

    /**
     * Removes all product lists charged in the system (current user).
     */
    public void removeProductLists() {
        productLists.clear();
    }

    /**
     * Adds an existing product from the catalog to a specific product list.
     * @param listName the name of the product list to which the product will be added
     * @param productName the name of the product to add
     * @throws ProductListException if the product list does not exist
     * @throws ProductException if the product does not exist
     */
    public void addProductToList(String listName, String productName) throws ProductListException, ProductException {
        boolean added = modifyProductInList(listName, productName, "add");
        if (!added) throw new ProductException("Product already exists in the list: " + productName);
    }

    /**
     * Removes a product from a specified product list.
     * This method removes a product from a specified product list. If the product
     * is successfully removed and the list becomes empty, the list is also removed.
     * @param listName The name of the product list from which the product will be removed.
     * @param productName The name of the product to remove from the list.
     * @throws ProductListException If the product list does not exist.
     * @throws ProductException If the product does not exist in the list.
     */
    public void removeProductFromList(String listName, String productName) throws ProductListException, ProductException {
        boolean removed = modifyProductInList(listName, productName, "remove");
        if (!removed) throw new ProductException("Product does not exist in the list: " + productName);
        // Remove list if empty
        ProductList productList = getProductList(listName);
        if (productList.isEmpty()) removeProductList(listName);
    }

    /**
     * Adds or removes a product from a specific list.
     * @param listName the name of the product list
     * @param productName the name of the product
     * @param action the action to perform (add or remove)
     * @return true if the action was successful, false otherwise
     * @throws ProductListException if the product list does not exist
     * @throws ProductException if the product does not exist
     */
    private boolean modifyProductInList(String listName, String productName, String action) throws ProductListException, ProductException {
        ProductList productList = productLists.get(listName);
        Product product = productMap.get(productName);
        if (productList == null) {
            throw new ProductListException("Product list does not exist: " + listName);
        }
        if (product == null) {
            throw new ProductException("Product does not exist: " + productName);
        }
        boolean result = false;
        if ("add".equals(action)) {
            result = productList.addProduct(product);
        } else if ("remove".equals(action)) {
            result = productList.removeProduct(productName);
        }
        // Return result of the operation : true if the product was added/removed, false otherwise
        return result;
    }

    /**
     * Applies a discount to a specific list and logs the modification.
     * @param listName the name of the product list
     * @param percentage the discount percentage to apply
     * @throws ProductListException if the product list does not exist
     */
    public void applyDiscountToList(String listName, double percentage) throws ProductListException {
        ProductList productList = productLists.get(listName);
        if (productList == null) {
            throw new ProductListException("Product list does not exist: " + listName);
        }
        productList.applyDiscount(percentage);
    }

    // ! Methods for similarity management

    /**
     * Shows the similarities between products.
     * @return a string representation of the similarities
     */
    public String showSimilarities() {
        StringBuilder sb = new StringBuilder();
        similarityMap.forEach((product1, similarities) ->
                similarities.forEach((product2, similarity) ->
                    sb.append(product1).append(" similarity ").append(similarity).append(" ").append(product2).append("\n")));
        return sb.toString();
    }

    /**
     * Adds a similarity between two products.
     * @param product1 the name of the first product
     * @param product2 the name of the second product
     * @param similarity the similarity value
     * @throws ProductException if either product does not exist
     */
    public void setSimilarity(String product1, String product2, double similarity) throws ProductException {
        // Check if products exist
        if (!productMap.containsKey(product1) || !productMap.containsKey(product2)) {
            throw new ProductException("One or both products do not exist: " + product1 + ", " + product2);
        }
        // Validate similarity value
        if (similarity < 0 || similarity > 1) {
            throw new ProductException("Similarity value must be between 0 and 1: " + similarity);
        }
        // Add the products to the similarity map if they do not exist
        similarityMap.putIfAbsent(product1, new HashMap<>());
        similarityMap.putIfAbsent(product2, new HashMap<>());

        // Add similarity to map
        similarityMap.get(product1).put(product2, similarity);
        similarityMap.get(product2).put(product1, similarity);
    }

    /**
     * Removes a similarity between the products with a specific product.
     * @param product1 the product name.
     * @throws ProductException if either product does not exist.
     */
    public void removeSimilarity(String product1) throws ProductException {
        if (productMap.containsKey(product1)) { //!Añadir a la rama
            similarityMap.remove(product1);
            for (String key : similarityMap.keySet()) {
                if (similarityMap.get(key).containsKey(product1)) {
                    similarityMap.get(key).remove(product1);
                }
            }
        } else {
            throw new ProductException("Product does not exist: " + product1);
        }
    }

    /**
     * Adds similarities between a product and a list of other products.
     * @param similarities a list of product names and similarity values
     * @param product the product to add similarities to
     * @throws ProductException if any product does not exist
     */
    private void addSimilarities(ArrayList<Pair<String, Double>> similarities, Product product) throws ProductException {
        for (Pair<String, Double> similarity : similarities) {
            String existingProductName = similarity.getFirst();
            double similarityScore = similarity.getSecond();
            Product existingProduct = productMap.get(existingProductName);
            if (existingProduct != null) {
                setSimilarity(product.getName(), existingProductName, similarityScore);
            } else {
                throw new ProductException("Product does not exist in the catalog: " + existingProductName);
            }
        }
    }

    /**
     * Gets the similarity between two products.
     * @param product1 the name of the first product
     * @param product2 the second product
     * @return the similarity value between the two products, or null if not found
     */
    public Double getSimilarity(String product1, String product2) {
        // Check if products exist
        if (similarityMap.containsKey(product1) && similarityMap.get(product1).containsKey(product2)) {
            return similarityMap.get(product1).get(product2);
        }
        return null;    // Similarity not found
    }

    /**
     * Retrieves the similarities for a given product.
     * @param productName the name of the product
     * @return a map of product names and their similarity scores
     * @throws ProductException if the product does not exist
     */
    public Map<String, Double> getSimilaritiesForProduct(String productName) throws ProductException {
        if (!productMap.containsKey(productName)) {
            throw new ProductException("Product does not exist: " + productName);
        }
        return similarityMap.getOrDefault(productName, new HashMap<>());
    }

    /**
     * Gets the similarity map.
     * @return the similarity map
     */
    public Map<String, Map<String, Double>> getSimilarityMap() { return similarityMap; }

    /**
     * Returns a string representation of the ProductManager.
     * @return a string representation of the ProductManager
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProductManager{\n");
        sb.append("  productMap={\n");
        productMap.forEach((name, product) -> sb.append("    ").append(name).append(": ").append(product).append(",\n"));
        if (!productMap.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove the last comma and newline
        }
        sb.append("\n  },\n");
        sb.append("  productLists={\n");
        productLists.forEach((name, list) -> sb.append("    ").append(name).append(": ").append(list).append(",\n"));
        if (!productLists.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove the last comma and newline
        }
        sb.append("\n  },\n");
        sb.append("}");
        return sb.toString();
    }
}
