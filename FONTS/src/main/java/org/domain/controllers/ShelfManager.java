package org.domain.controllers;

import org.domain.algorithms.AbstractAlgorithm;
import org.domain.algorithms.BruteForceAlgorithm;
import org.domain.algorithms.HillClimbingAlgorithm;
import org.domain.classes.Distribution;
import org.domain.classes.ProductList;
import org.domain.classes.Shelf;
import org.domain.exceptions.DistributionException;
import org.domain.exceptions.ProductListException;
import org.domain.exceptions.ShelfException;
import org.domain.types.TupleType;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * Manages the shelves in the system.
 * Provides methods to create, retrieve, update, and delete shelves.
 * @author Víctor Llorens Ramos (victor.llorens@estudiantat.upc.edu)
 * @version 1.0
 */
public class ShelfManager {

    private final HashMap<Integer, Shelf> shelfMap; ///< Map of shelves by their IDs.

    /**
     * Constructs a new ShelfManager.
     */
    public ShelfManager() {
        shelfMap = new HashMap<>();
    }

    /**
     * Returns the singleton instance of ShelfManager.
     *
     * @return The singleton instance of ShelfManager.
     */
    public static ShelfManager getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * Helper class to hold the singleton instance of ShelfManager.
     */
    private static class SingletonHelper {
        private static final ShelfManager INSTANCE = new ShelfManager();
    }

    /**
     * Creates a new shelf with the specified details.
     *
     * @param id         The ID of the shelf.
     * @param xcapacity  The horizontal capacity of the shelf.
     * @param ycapacity  The vertical capacity of the shelf.
     * @param productList The product list associated with the shelf.
     * @throws ShelfException If the shelf ID is invalid or the shelf already exists.
     */
    public void createShelf(int id, int xcapacity, int ycapacity, ProductList productList) throws ShelfException {
        if(id < 0) throw new ShelfException("The id of the shelf must be a natural number.");
        if (shelfMap.containsKey(id)) throw new ShelfException("Shelf already exists");

        Shelf shelf = new Shelf(id, xcapacity, ycapacity, productList);
        shelfMap.put(id, shelf);
    }

    /**
     * Retrieves the details of a shelf.
     *
     * @param id The ID of the shelf.
     * @return A TupleType containing the shelf details.
     * @throws ShelfException If the shelf does not exist.
     */
    public Shelf getShelf(int id) throws ShelfException {
        if (!shelfMap.containsKey(id)) throw new ShelfException("The shelf does not exist");
        return shelfMap.get(id);
    }

    /**
     * Retrieves the details of all shelves.
     *
     * @return A set of TupleType containing the details of all shelves.
     */
    public Set<Shelf> getAllShelves() {
        Set<Shelf> set = new HashSet<>();
        set.addAll(shelfMap.values());
        return set;
    }

    /**
     * Sets the shelves in the system.
     *
     * @param shelfMap The map of shelves to set.
     */
    public void setShelves(Map<Integer, Shelf> shelfMap) {
        this.shelfMap.clear();
        this.shelfMap.putAll(shelfMap);
    }

    /**
     * Retrieves the distribution log of a shelf.
     *
     * @param id The ID of the shelf.
     * @return A set of TupleType containing the distribution log of the shelf.
     * @throws ShelfException If the shelf does not exist.
     */
    public ArrayList<TupleType> getDistributionsShelf(int id) throws ShelfException {
        if (!shelfMap.containsKey(id)) throw new ShelfException("The shelf does not exist");
        return shelfMap.get(id).getDistributionLog();
    }

    /**
     * Retrieves the distribution information for a specific distribution of a shelf.
     *
     * @param idSelf  The ID of the shelf.
     * @param nameDist The name of the distribution.
     * @return An ArrayList of ArrayLists containing the distribution information.
     * @throws ShelfException If the shelf does not exist.
     */
    public ArrayList<ArrayList<String>> getDistributionInfo(int idSelf, String nameDist) throws ShelfException {
        if (!shelfMap.containsKey(idSelf))  throw new ShelfException("Shelf does not exist");
        Shelf shelf = shelfMap.get(idSelf);
        return shelf.getDistribution(nameDist);
    }

    /**
     * Retrieves the name of the last distribution for a given shelf.
     *
     * @param idShelf The ID of the shelf.
     * @return The name of the last distribution.
     * @throws ShelfException If the shelf does not exist.
     */
    public String getNameLastDistributionShelf(int idShelf) throws ShelfException {
        if (!shelfMap.containsKey(idShelf)) throw new ShelfException("The shelf does not exist");
        Shelf shelf = shelfMap.get(idShelf);
        return shelf.getNameLastDistribution();
    }

    /**
     * Retrieves the names of all distributions for a given shelf.
     *
     * @param idShelf The ID of the shelf.
     * @return A set of distribution names.
     * @throws ShelfException If the shelf does not exist.
     * @throws DistributionException If there is an error retrieving the distributions.
     */
    public Set<String> getAllDistributionsNames(int idShelf) throws ShelfException, DistributionException {
        if (!shelfMap.containsKey(idShelf)) throw new ShelfException("Shelf does not exist");
        Shelf shelf = shelfMap.get(idShelf);
        return shelf.getDistributionNames();
    }

    /**
     * Retrieves the string representation of a shelf.
     *
     * @param idShelf The ID of the shelf.
     * @return The string representation of the shelf.
     * @throws ShelfException If the shelf does not exist.
     */
    public String showShelf(int idShelf) throws ShelfException {
        if (!shelfMap.containsKey(idShelf)) throw new ShelfException("The shelf does not exist");
        Shelf shelf = shelfMap.get(idShelf);
        return shelf.toString();
    }

    /**
     * Retrieves the string representation of all shelves.
     *
     * @return The string representation of all shelves.
     */
    public String showAllShelves() {
        StringBuilder sb = new StringBuilder();
        for (Shelf shelf : shelfMap.values()) {
            sb.append(shelf.toString());
        }
        return sb.toString();
    }

    /**
     * Retrieves the string representation of the last distribution of a shelf.
     *
     * @param idShelf The ID of the shelf.
     * @return The string representation of the last distribution of the shelf.
     * @throws ShelfException If the shelf does not exist.
     */
    public String showLastDistributionShelf(int idShelf) throws ShelfException {
        if (!shelfMap.containsKey(idShelf)) throw new ShelfException("The shelf does not exist");
        Shelf shelf = shelfMap.get(idShelf);
        return shelf.toStringLastDistribution();
    }

    /**
     * Changes the product list associated with a shelf.
     *
     * @param idShelf    The ID of the shelf.
     * @param productList The new product list.
     * @throws ShelfException If the shelf does not exist.
     * @throws ProductListException If the product list does not fit on the shelf.
     */
    public void changeProductListAtShelf(int idShelf, ProductList productList) throws ShelfException, ProductListException {
        if (!shelfMap.containsKey(idShelf)) throw new ShelfException("The shelf does not exist");

        Shelf shelf = shelfMap.get(idShelf);
        int capacity = shelf.getMaxCapacity();
        if (productList.getProducts().size() > capacity) throw new ProductListException("The list does not fit on the shelf");

        shelf.changeProductList(productList);
    }

    /**
     * Makes a distribution the current distribution for a shelf.
     *
     * @param idShelf     The ID of the shelf.
     * @param distribution The distribution to make current.
     * @throws ShelfException If the shelf does not exist.
     */
    public void makeCurrentDistributionShelf(int idShelf, Distribution distribution) throws ShelfException {
        if(!shelfMap.containsKey(idShelf))throw new ShelfException("The shelf does not exist");
        Shelf shelf = shelfMap.get(idShelf);
        shelf.makeCurrentDistribution(distribution);
    }

    /**
     * Checks if a product list can be removed from the system.
     *
     * @param productList The product list to check.
     * @throws ShelfException If the product list is associated with any shelf.
     */
    public void canBeRemoved(ProductList productList) throws ShelfException {
        for (Shelf shelf : shelfMap.values()) {
            if (shelf.getProductList() == productList) {
                int shelfId = shelf.getId();
                throw new ShelfException("There is a shelf id: " + shelfId + " with the associated list");
            }
        }
    }

    /**
     * Removes a shelf from the system.
     *
     * @param id The ID of the shelf to remove.
     * @throws ShelfException If the shelf does not exist.
     */
    public void removeShelf(int id) throws ShelfException {
        if (!shelfMap.containsKey(id)) throw new ShelfException("The shelf does not exist");
        shelfMap.remove(id);
    }

    /**
     * Deletes all shelves from the system.
     */
    public void deleteAllShelves() {
        shelfMap.clear();
    }

    /**
     * Distributes the products on the shelf using the specified algorithm.
     * The distribution is stored in the distribution history.
     *
     * @param idShelf          The ID of the shelf.
     * @param name             The name of the distribution.
     * @param algorithm        The algorithm to use (1 for Brute Force, 2 for Hill Climbing).
     * @param limit            The limit for the algorithm (number of combinations for Brute Force or maximum neighbors visited for Hill Climbing).
     * @param similarityMatrix The similarity matrix for the algorithm.
     * @return The Distribution object ordered by the algorithm.
     * @throws ShelfException If the shelf does not exist.
     * @throws DistributionException If the algorithm is invalid or the limit is zero.
     */
    public Distribution distributeShelf(int idShelf, String name, int algorithm, int limit, Map<String, Map<String, Double>> similarityMatrix) throws ShelfException {
        if (limit == 0) throw new DistributionException("Depth can not be  0"); //only if Depth < 0 then limit is not taken into account
        if (!shelfMap.containsKey(idShelf)) throw new ShelfException("The shelf does not exist");
        AbstractAlgorithm abstractAlgorithm = switch (algorithm) {
            case 1 -> new BruteForceAlgorithm(similarityMatrix);
            case 2 -> new HillClimbingAlgorithm(similarityMatrix);
            default -> throw new DistributionException("Invalid algorithm");
        };
        Shelf shelf = shelfMap.get(idShelf);
        ProductList productList = shelf.getProductList();
        Distribution dist = new Distribution(name);
        dist.orderList(productList, abstractAlgorithm, shelf.getXsize(), shelf.getYsize(), limit);
        // Add the distribution to the history
        shelf.addDistribution(dist);
        // Return the distribution object ordered by the algorithm
        return dist;
    }
}