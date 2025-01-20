package org.domain.algorithms;

import org.domain.classes.Product;
import org.domain.classes.ProductList;
import org.domain.exceptions.DistributionException;
import org.domain.types.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AbstractAlgorithm serves as the base class for implementing various product arrangement algorithms.
 * It provides shared utility methods and an abstract method for defining the main ordering logic.
 * Subclasses should implement the orderProductList method to define specific algorithms.
 * @author Max Estrade Pey {max.estrade@estudiantat.upc.edu}
 * @version 1.0
 */

public abstract class AbstractAlgorithm {
    /**
     * A matrix that stores similarity scores between products.
     * The outer map's key is the product name, and the inner map's key is the name of another product.
     * The value is the similarity score between the two products.
     */
    protected Map<String, Map<String, Double>> similarityMatrix;

    /**
     * Constructs an `AbstractAlgorithm` with the specified similarity matrix.
     *
     * @param similarityMatrix A matrix that stores similarity scores between products.
     *                         The outer map's key is the product name, and the inner map's key is the name of another product.
     *                         The value is the similarity score between the two products.
     */
    public AbstractAlgorithm(Map<String, Map<String, Double>> similarityMatrix) {
        this.similarityMatrix = similarityMatrix;
    }

    /**
     * Arranges a given product list into an optimized distribution.
     * Subclasses must override this method to provide specific implementations.
     *
     * @param list        The product list to be arranged.
     * @param xsize       Represents the number of columns of the distribution
     * @param ysize       Represents the number of rows of the distribution
     * @param limit       Serves to limit the depth we want to search for the solution. If limit is less than 0, the code ignores the limit
     * @param coordinates Contains the coordinates of the products in the distribution.
     * @return A matrix that represent the arranged distribution of the list.
     * @throws DistributionException If the ProductList is empty.
     */

    public abstract ArrayList<ArrayList<Product>> orderProductList(ProductList list, int xsize, int ysize, int limit, HashMap<String, Pair<Integer, Integer>> coordinates);

    /**
     * Retrieves the name of the algorithm.
     *
     * <p>This method returns the name associated with this algorithm, which is
     * typically used to identify or display the algorithm's name in various contexts.</p>
     *
     * @return The name of the algorithm (e.g., "Brute Force").
     */
    public abstract String getName();

    /**
     * Adapts a linear list of products into a shelf-like 2D arrangement.
     * The method divides the products into three shelves. Shelves alternate between
     * left-to-right and right-to-left filling orders for aesthetic distribution.
     * Remaining products beyond evenly distributed shelves are added to the last shelf.
     *
     * @param list        The list of products to distribute.
     * @param xsize       Represents the number of columns of the distribution
     * @param ysize       Represents the number of rows of the distribution
     * @param coordinates Contains the coordinates of the products in the distribution
     * @return A matrix that represent the arranged distribution of the list.
     */

    public ArrayList<ArrayList<Product>> adaptToShelf(List<Product> list, int xsize, int ysize, HashMap<String, Pair<Integer, Integer>> coordinates){
        int count = 0;
        ArrayList<ArrayList<Product>> distribution = new ArrayList<>();
        for (int i = 0; i<ysize; i++) {
            ArrayList<Product> aux = new ArrayList<>();
            // In our model, the last product in a row is compared to the one it has under itself,
            // to represent that we arrange in reverse order the odd rows
            if (i%2 == 0) {
                for (int j = 0; j < xsize; j++) {
                    if (count < list.size()) {
                        aux.add(j, list.get(count));
                        coordinates.put(list.get(count).getName(), new Pair<>(i, j));
                    }
                    else aux.add(j, null);
                    ++count;
                }
            }
            else {
                for (int j = 0; j < xsize; j++) {
                    if (count < list.size()) {
                        aux.addFirst(list.get(count));
                        coordinates.put(list.get(count).getName(), new Pair<>(i, j));
                    }
                    else aux.addFirst(null);
                    ++count;
                }
            }
            distribution.add(i, aux);

        }

        //We add the rest of the remaining products to the last row
        for (int i = count; i < xsize*ysize; ++i) {
            if (count < list.size()) {
                distribution.get(ysize-1).addLast(list.get(i));
                coordinates.put(list.get(i).getName(), new Pair<>(ysize-1, i));
            }
            else distribution.get(ysize-1).addLast(null);

        }

        return distribution;
    }


    /**
     * Calculates the sum of similarities between consecutive products in a combination.
     * This method iterates through the list and computes the total similarity between
     * adjacent products. Similarity is defined by the getSimilarity method of
     * the Product class.
     * @param combination The list of products to evaluate.
     * @return The total similarity score of the product combination.
     */

    public double calculaSum(List<Product> combination) {
        double sum = 0;
        for (int i = 0; i < combination.size() - 1; i++) {
            Product p1 = combination.get(i);
            Product p2 = combination. get(i+1);
            sum = sum + getSimilarityScore(p1, p2);
        }
        Product p1 = combination.getFirst();
        Product p2 = combination.getLast();
        sum = sum + getSimilarityScore(p1, p2);
        return sum;
    }

    /**
     * Finds the most similar product to a given product from a list of candidates.
     * This method iterates through the list of candidates and selects the product
     * with the highest similarity score to the given product.
     * @param product The product to compare.
     * @param candidates The list of products to compare against.
     * @return The most similar product to the given product.
     */
    protected Product mostSimilarP(Product product, List<Product> candidates) {
        double maxSimilarity = -1;
        Product mostSimilar = null;
        for (Product candidate : candidates) {
            double similarity = similarityMatrix.getOrDefault(product.getName(), new HashMap<>())
                    .getOrDefault(candidate.getName(), 0.0);
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilar = candidate;
            }
        }
        return mostSimilar;
    }

    /**
     * Retrieves the similarity score between two products.
     * This method retrieves the similarity score between two products from the similarity matrix.
     * If the similarity score is not found, the method returns 0.
     * @param p1 The first product to compare.
     * @param p2 The second product to compare.
     * @return The similarity score between the two products.
     */
    private double getSimilarityScore(Product p1, Product p2) {
        return similarityMatrix.getOrDefault(p1.getName(), new HashMap<>())
                .getOrDefault(p2.getName(), 0.0);
    }
}
