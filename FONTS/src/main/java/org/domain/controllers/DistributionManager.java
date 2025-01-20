package org.domain.controllers;

import org.domain.classes.Distribution;
import org.domain.exceptions.DistributionException;
import org.domain.types.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Manages all the distributions on the system and provides methods to add, remove, modify, and store distributions.
 * The distributions managed by the distribution manager are all the distributions from all the shelves in the system at that moment.
 * @author Sergio Shmyhelskyy Yaskevych (sergio.shmyhelskyy@estudiantat.upc.edu)
 * @version 1.0
 */
public class DistributionManager {
    private static final Scanner sc = new Scanner(System.in);

    private Map<String, Distribution> distributions;
    private TreeMap<LocalDateTime, String> distributionHistory;

    /**
     * Constructor for DistributionManager.
     * Initializes the distributions and distribution history.
     */
    public DistributionManager() {
        distributions = new HashMap<>();
        distributionHistory = new TreeMap<>();
    }

    /**
     * Sets the distributions map.
     *
     * @param distributionsList the map of distributions to set
     */
    public void setDistributions(Map<String, Distribution> distributionsList) {
        distributions = distributionsList;
    }

    /**
     * Helper class for singleton instance of DistributionManager.
     */
    public static class SingletonHelper {
        public static final DistributionManager INSTANCE = new DistributionManager();
    }

    /**
     * Gets the singleton instance of DistributionManager.
     *
     * @return the singleton instance of DistributionManager
     */
    public static DistributionManager getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * Adds a distribution to the manager.
     *
     * @param name the name of the distribution
     * @param distribution the distribution to add
     */
    public void addDistribution(String name, Distribution distribution) throws DistributionException {
        if (distributions.containsKey(name)) throw new DistributionException("Distribution already exists");
        distributions.put(name, distribution);
        distributionHistory.put(LocalDateTime.now(), "Added distribution: " + name);
    }

    /**
     * Removes a distribution from the manager.
     *
     * @param name the name of the distribution to remove
     * @return true if the distribution was removed, false if no distribution with the specified name was found
     */
    public boolean removeDistribution(String name) {
        if (!distributions.containsKey(name)) {
            // System.out.println("Distribution not found: " + name);
            return false;
        }
        distributions.remove(name);
        distributionHistory.put(LocalDateTime.now(), "Removed distribution: " + name);
        return true;
    }

    /**
     * Modifies a distribution in the manager.
     *
     * @param distName the name of the distribution to modify
     * @param productName1 the name of the first product to swap with the second product
     * @param productName2 the name of the second product to swap with the first product
     * @throws DistributionException if the distribution is not found
     * @return true if the distribution was modified, false if no distribution with the specified name was found
     */
    public boolean modifyDistribution(String distName, String productName1, String productName2) throws DistributionException {
        if (!distributions.containsKey(distName)) throw new DistributionException("Distribution does not exist");

        Distribution distribution = distributions.get(distName);

        Pair<Integer, Integer> positionProduct1 = distribution.getCoordProduct(productName1);
        Pair<Integer, Integer> positionProduct2 = distribution.getCoordProduct(productName2);

        distribution.modifyDistribution(positionProduct1.getFirst(), positionProduct1.getSecond(), positionProduct2.getFirst(), positionProduct2.getSecond());

        distributionHistory.put(LocalDateTime.now(), "Modified distribution: " + distName);
        return true;
    }

    /**
     * Swaps the position of two products in a specific distribution.
     *
     * @param distName the name of the distribution to modify
     * @param coord1 the coordinates of the first product to swap with the second product
     * @param coord2 the coordinates of the second product to swap with the first product
     * @throws DistributionException if the distribution is not found
     */
    public void swapProductsInDistribution(String distName, Pair<Integer,Integer> coord1, Pair<Integer,Integer> coord2) throws DistributionException {
        if (!distributions.containsKey(distName)) throw new DistributionException("Distribution does not exist");

        Distribution distribution = distributions.get(distName);
        distribution.modifyDistribution(coord1.getFirst(), coord1.getSecond(), coord2.getFirst(), coord2.getSecond());

        distributionHistory.put(LocalDateTime.now(), "Modified distribution: " + distName);
    }

    /**
     * Checks if a distribution exists.
     *
     * @param nameDistribution the name of the distribution to check
     * @return true if the distribution exists, false otherwise
     */
    public boolean exists(String nameDistribution) {
        return distributions.containsKey(nameDistribution);
    }

    /**
     * Gets a distribution by name.
     *
     * @param name the name of the distribution
     * @return the distribution with the specified name, or null if no distribution with the specified name was found
     */
    public Distribution getDistribution(String name) throws DistributionException {
        if (!distributions.containsKey(name)) throw new DistributionException("Distribution does not exist");
        return distributions.get(name);
    }

    /**
     * Gets all distributions in the system.
     *
     * @return a list of all distributions
     */
    public List<Distribution> getAllDistributions() {
        return new ArrayList<>(distributions.values());
    }

    /**
     * Removes all distributions on the system.
     */
    public void removeDistributions() {
        distributions.clear();
    }

    /**
     * Stores a distribution to a file.
     *
     * @param fileName the name of the file to store the distribution
     * @param path the path where the file will be stored
     * @param distName the name of the distribution to store
     * @throws IOException if an I/O error occurs
     * @throws DistributionException if the distribution is not found
     */
    public void storeDistributionToFile(String fileName, String path, String distName) throws IOException, DistributionException {
        // Check if the distribution exists
        Distribution dist = distributions.get(distName);
        if (dist == null) {
            throw new DistributionException("The distribution '" + distName + "' was not found.");
        }

        // Construct the file path
        File file = new File(path, fileName);

        // Ensure the parent directory exists
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IOException("Failed to create the directories for path: " + path);
        }

        // Write the distribution to the file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(dist.toString());
        }
    }


    /**
     * Gets the distribution history.
     *
     * @return the distribution history
    */
    public TreeMap<LocalDateTime, String> getDistributionHistory() {
        return distributionHistory;
    }
}