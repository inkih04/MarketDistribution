package org.domain.classes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a list of products with a specific category.
 * Ensures that products added to the list match the category of the list.
 * Provides methods to add products, apply discounts, and print the list of products.
 * Keeps track of the last modification date.
 * @author Sergio Shmyhelskyy Yaskevych (sergio.shmyhelskyy@estudiantat.upc.edu)
 * @version 1.0
 */
public class ProductList {

    private String name;
    private final String category; // Category of the product list
    private final Set<Product> products; // Map to avoid duplicates
    private LocalDateTime lastModified;

    /**
     * Constructs a new ProductList with the specified name and category.
     *
     * @param name the name of the product list
     * @param category the category of the products in the list
     */
    public ProductList(String name, String category) {
        this.name = name;
        this.category = category;
        this.products = new HashSet<>();
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Loads a product into the list.
     * This method is used to load products from the database.
     * @param product the product to add to the ProductList
     */
    public void loadProduct(Product product) {
        products.add(product);
    }

    /**
     * Adds a product to the list if it matches the category of the list.
     * Updates the last modification date if the product is added.
     *
     * @param product the product to add
     * @return true if the product was added, false otherwise
     */
    public boolean addProduct(Product product) {
        boolean added = products.add(product);
        if (added) {
            updateLastModified();
        }
        return added;
    }

    /**
     * Removes a product from the list.
     * Updates the last modification date if the product is removed.
     *
     * @param productName the name of the product to remove
     * @return true if the product was removed, false otherwise
     */
    public boolean removeProduct(String productName) {
        Product productToRemove = products.stream()
                .filter(product -> product.getName().equalsIgnoreCase(productName))
                .findFirst()
                .orElse(null);

        if (productToRemove != null) {
            updateLastModified();
            products.remove(productToRemove);
            return true;
        }
        return false;
    }

    /**
     * Applies a discount to all products in the list.
     * Updates the last modification date.
     *
     * @param discount the discount percentage to apply
     */
    public void applyDiscount(double discount) {
        products.forEach(product -> product.applyDiscount(discount));
        updateLastModified();
    }

    /**
     * Updates the last modification date to the current date and time.
     */
    private void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }

    //! Getters

    /**
     * Checks if the product list is empty.
     *
     * @return true if the product list is empty, false otherwise
     */
    public boolean isEmpty() { return products.isEmpty(); }

    /**
     * Gets the last modification date of the product list.
     *
     * @return the last modification date
     */
    public LocalDateTime getLastModified() {
        return lastModified;
    }

    /**
     * Sets the last modification date of the product list.
     *
     * @param lastModified the new last modification date
     */
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }


    /**
     * Gets the name of the product list.
     *
     * @return the name of the product list
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the product list.
     *
     * @param name the new name of the product list
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the category of the product list.
     *
     * @return the category of the product list
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets the products in the product list.
     *
     * @return the products in the product list
     */
    public Set<Product> getProducts() {
        return products;
    }

    /**
     * Gets a product from the product list by name.
     *
     * @param productName the name of the product to get
     * @return the product with the specified name, or null if the product is not found
     */
    public Product getProduct(String productName) {
        return products.stream()
                .filter(product -> product.getName().equalsIgnoreCase(productName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Converts the ProductList in a String to be displayed
     *
     * @return the String representation of the ProductList
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProductList{name='").append(name).append("', category='").append(category).append("', lastModified=").append(lastModified).append(",\n\tproducts=[\n");
        products.forEach(product -> sb.append("\t\t").append(product.toString()).append(",\n"));
        if (!products.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove the last comma and newline
        }
        sb.append("\n\t]}");
        return sb.toString();
    }

    /**
     * Gets the total quantity of products in the list. The quantity is the sum of all the amounts of the products in the ProductList.
     *
     * @return the total quantity of products in the list
     */
    public int getTotalQuantity() {
        int total = 0;
        for (Product p : products) {
            total += p.getAmount();
        }
        return total;
    }
}