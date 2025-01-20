package org.domain.classes;

import org.domain.algorithms.AbstractAlgorithm;
import org.domain.algorithms.BruteForceAlgorithm;
import org.domain.exceptions.DistributionException;
import org.domain.types.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Distribution class represents a distribution system for arranging products.
 * It maintains the distribution layout, handles modifications, and calculates metrics
 * for evaluating the arrangement.
 * This class provides methods to organize products using algorithms, modify the distribution,
 * and manage the associated metadata such as name and timestamps.
 * @author Max Estrade Pey {max.estrade@estudiantat.upc.edu}
 * @version 1.0
 */
public class Distribution {

    private String name;

    /**
     * A matrix representing the product distribution.
     **/
    private ArrayList<ArrayList<Product>> distribution;

    private LocalDateTime modifiedDate;

    private LocalDateTime createdDate;

    /**
     *  Map that saves the coordenate of the products in the distribution
     */
    public HashMap<String, Pair<Integer, Integer>> coordinates;

    /**
     * Constructs a Distribution instance with a specified name.
     * @param name The name of the distribution.
     */
    public Distribution(String name){
        this.name = name;
        this.createdDate = LocalDateTime.now();
        coordinates = new HashMap<>();
    };

    /**
     * Retrieves the name of the distribution.
     * @return The name of the distribution.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the current distribution layout.
     * @return A matrix representing the product distribution.
     */
    public ArrayList<ArrayList<Product>> getDistribution() {
        return distribution;
    }

    /**
     * Retrieves the last modified timestamp of the distribution.
     * @return The last modified timestamp.
     */
    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }


    /**
     * Sets the modified date of the distribution.
     *
     * @param modifiedDate The new modified date to set.
     */
    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * Sets the creation date of the distribution.
     *
     * @param createdDate The new creation date to set.
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Sets the distribution layout.
     * @param distMatrix A matrix representing the product distribution.
     */
    public void setDistribution(ArrayList<ArrayList<Product>> distMatrix) {
        this.distribution = distMatrix;
        coordinates = new HashMap<>();
        for (int i = 0; i < distribution.size(); i++) {
            for (int j = 0; j < distribution.get(i).size(); j++) {
                if (distribution.get(i).get(j) != null) {
                    coordinates.put(distribution.get(i).get(j).getName(), new Pair<>(i, j));
                }
                else coordinates.put("null", new Pair<>(i, j));
            }
        }
    }

    /**
     * Retrieves the last modified timestamp of the distribution.
     * @return The last modified timestamp.
     */
    public LocalDateTime getCreationDate() {
        return createdDate;
    }

    /**
     * Adapts the distribution to be displayed in the view.
     * @return A list of lists of strings that are the names of the products on the distribution.
     */
    public ArrayList<ArrayList<String>> adaptForView() {
        ArrayList<ArrayList<String>> solution = new ArrayList<>();
        for (int i = 0; i < distribution.size(); i++) {
            ArrayList<String> aux = new ArrayList<>();
            for (int j = 0; j < distribution.get(i).size(); j++) {
                Product product = distribution.get(i).get(j);
                if (product != null) aux.add(product.getName());
                else aux.add("null");
            }
            solution.add(aux);
        }
        return solution;
    }


    /**
     * Arranges the product list using the specified algorithm and capacity.
     *
     * @param list  The product list to be arranged.
     * @param algo  The algorithm used for arranging products.
     * @param xsize Represents the number of columns of the distribution
     * @param ysize Represents the number of rows of the distribution
     * @param limit Serves to limit the depth we want to search for the solution.If limit is less than 0, the code ignores the limit
     */
    public void orderList(ProductList list, AbstractAlgorithm algo, int xsize, int ysize, int limit){
        setDistribution(algo.orderProductList(list, xsize, ysize, limit, coordinates));
        setModifiedDate(LocalDateTime.now());
    }


    /**
     * Swaps two products in the distribution and updates the modified time.
     * @param i The row index of the first product.
     * @param j The column index of the first product.
     * @param ii The row index of the second product.
     * @param jj The column index of the second product.
     */
    public void modifyDistribution(int i, int j, int ii, int jj) {
        Product temp = distribution.get(i).get(j);
        if (temp == null) coordinates.put("null", new Pair<>(ii, jj));
        else coordinates.put(temp.getName(), new Pair<>(ii, jj));

        Product temp2 = distribution.get(ii).get(jj);
        if (temp2 == null) coordinates.put("null", new Pair<>(i, j));
        else coordinates.put(temp2.getName(), new Pair<>(i, j));

        distribution.get(i).set(j, distribution.get(ii).get(jj));
        distribution.get(ii).set(jj, temp);
        modifiedDate = LocalDateTime.now();
    }

    /**
     * Returns a string representation of the distribution, including metadata and layout.
     * @return A string describing the distribution.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        sb.append("Created Date: ").append(createdDate).append("\n");
        sb.append("Last Modified Date: ").append(modifiedDate).append("\n");
        for (ArrayList<Product> row : distribution) {
            for (Product product : row) {
                if (product != null) {
                    sb.append(product.getName()).append("\t");
                } else {
                    sb.append("null").append("\t");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Retrieves the coordinates of a product in the distribution.
     * @param name The name of the product to locate.
     * @return A pair of integers representing the row and column indices of the product.
     * @throws DistributionException If the product is not found in the distribution.
     */
    public Pair<Integer, Integer> getCoordProduct(String name){
        if(coordinates.containsKey(name)) return coordinates.get(name);
        throw new DistributionException("product: " + name + " not found at the distribution");
    }
}