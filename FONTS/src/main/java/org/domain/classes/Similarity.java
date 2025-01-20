package org.domain.classes;

/**
 * The `Similarity` class represents a similarity between two products.
 * It contains the two products being compared and the similarity score between them.
 * @author Víctor Llorens Ramos (victor.llorens@estudiantat.upc.edu)
 * @version 1.0
 */
public class Similarity {

    private final Product product1;
    private final Product product2;
    private final double similarityScore;

    /**
     * Constructor for Similarity.
     * Initializes the similarity score and the two products being compared.
     *
     * @param similarityScore the similarity score between the two products
     * @param p1 the first product being compared
     * @param p2 the second product being compared
     */
    public Similarity(double similarityScore, Product p1, Product p2) {
        this.similarityScore = similarityScore;
        product1 = p1;
        product2 = p2;
    }

    /**
     * Gets the similarity score between the two products.
     *
     * @return the similarity score
     */
    public double getSimilarityScore() { return similarityScore; }

    /**
     * Gets the first product being compared.
     *
     * @return the first product
     */
    public Product getProduct1() { return product1; }

    /**
     * Gets the second product being compared.
     *
     * @return the second product
     */
    public Product getProduct2() { return product2; }

    /**
     * Gets the other product in the similarity.
     * This method returns the product that is not equal to the given product.
     *
     * @param product the product to compare
     * @return the other product in the similarity
     */
    public Product getOtherProduct(Product product) {
        if (product.equals(product1)) {
            return product2;
        } else if (product.equals(product2)) {
            return product1;
        } else {
            throw new IllegalArgumentException("Product not found in similarity");
        }
    }
}
