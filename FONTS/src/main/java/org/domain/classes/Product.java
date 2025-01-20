package org.domain.classes;

import org.domain.exceptions.ProductException;
import org.domain.types.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a product with a name, category, price, and amount.
 * Includes additional features such as validation, discount tracking, and utility methods.
 * @author Sergio Shmyhelskyy Yaskevych (sergio.shmyhelskyy@estudiantat.upc.edu)
 * @version 1.0
 */
public class Product {
    private final String name;
    private String category;
    private double price;
    private final double originalPrice; // Added to keep track of the original price
    private int amount;

    /**
     * Constructs a new Product with the specified attributes.
     *
     * @param name the name of the product
     * @param category the category of the product
     * @param price the price of the product
     * @param amount the amount of the product
     * @throws ProductException if any validation fails
     */
    public Product(String name, String category, double price, double originalPrice,int amount) throws ProductException {
        if (name == null || name.trim().isEmpty()) {
            throw new ProductException("Name cannot be null or empty");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new ProductException("Category cannot be null or empty");
        }
        if (price < 0) {
            throw new ProductException("Price cannot be negative");
        }
        if (amount < 0) {
            throw new ProductException("Amount cannot be negative");
        }

        this.name = name;
        this.category = category;
        this.originalPrice = roundToTwoDecimalPlaces(originalPrice);
        this.price = roundToTwoDecimalPlaces(price); // Store original price at initialization
        this.amount = amount;
    }

    // Getters and Setters with validation

    /**
     * Returns the name of the product.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the category of the product.
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the product.
     * @param category the new category
     * @throws IllegalArgumentException if the category is null or empty
     */
    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        this.category = category;
    }

    /**
     * Returns the price of the product.
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the original price of the product.
     * @return the original price
     */
    public double getOriginalPrice() {
        return originalPrice;
    }

    /**
     * Sets the price of the product.
     * @param price the new price
     * @throws IllegalArgumentException if the price is negative
     */
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = roundToTwoDecimalPlaces(price);
    }

    /**
     * Returns the amount of the product.
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the product.
     * @param amount the new amount
     * @throws IllegalArgumentException if the amount is negative
     */
    public void setAmount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }

    // Utility methods

    /**
     * Applies a discount to the product price.
     * @param discount represents the percentage discount to apply
     */
    public void applyDiscount(double discount) {
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        price = roundToTwoDecimalPlaces(originalPrice * (1 - discount / 100));
    }

    /**
     * Resets the product price to its original value.
     */
    public void applyOriginalPrice() {
        price = originalPrice;
    }

    /**
     * Updates the product amount by adding or removing a specified quantity.
     * @param change the quantity to add or remove
     * @return the new amount after the change
     * @throws ProductException if the new amount would be negative
     */
    public int updateAmount(int change) throws ProductException {
        int newAmount = this.amount + change;
        if (newAmount < 0) {
            throw new ProductException("Amount cannot be negative");
        }
        this.amount = newAmount;
        return this.amount;
    }

    /**
     * Rounds the given value to two decimal places.
     * @param value the value to round
     * @return the rounded value
     */
    private double roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return name.equals(product.name);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", originalPrice=" + originalPrice +
                ", amount=" + amount +
                '}';
    }
}