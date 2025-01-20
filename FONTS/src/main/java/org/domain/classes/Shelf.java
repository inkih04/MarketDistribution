package org.domain.classes;

import org.domain.algorithms.AbstractAlgorithm;
import org.domain.exceptions.DistributionException;
import org.domain.exceptions.ShelfException;
import org.domain.types.TupleType;

import java.time.LocalDateTime;
import java.util.*;


/**
 * A class representing a shelf for storing products with a defined capacity.
 * A shelf has its vertical and horizontal dimensions, a unique identifier, and a list of products associated to it.
 * @author Víctor Díez Serrano (victor.diez@estudiantat.upc.edu)
 * @version 1.0
 */
public class Shelf {
    private final int id;
    private final int xsize;
    private final int ysize;

    private List<Distribution> distributionHistory;
    private ProductList productList;


    /**
     * Constructs a Shelf object with the specified parameters.
     *
     * <p>This constructor initializes a shelf with the provided ID, x and y size, product list,
     * and the algorithm to be used for the shelf's operations. It also initializes the distribution history
     * as an empty list.</p>
     *
     * @param id the unique identifier for the shelf.
     * @param xsize the horizontal size (capacity) of the shelf.
     * @param ysize the vertical size (capacity) of the shelf.
     * @param productList the list of products assigned to the shelf.
     */
    public Shelf(int id, int xsize, int ysize, ProductList productList) {
        this.xsize = xsize;
        this.ysize = ysize;
        this.id = id;
        this.productList = productList;
        this.distributionHistory = new ArrayList<>();
    }


    /**
     * Gets the unique identifier of the shelf.
     *
     * <p>This method returns the ID of the shelf, which uniquely identifies it within the system.</p>
     *
     * @return the unique ID of the shelf.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the horizontal size (x dimension) of the shelf.
     *
     * <p>This method returns the horizontal size of the shelf, which represents the number of columns
     * or items that can be stored in each row of the shelf.</p>
     *
     * @return the horizontal size (x dimension) of the shelf.
     */
    public int getXsize() {
        return xsize;
    }

    /**
     * Gets the vertical size (y dimension) of the shelf.
     *
     * <p>This method returns the vertical size of the shelf, which represents the number of rows
     * or items that can be stored in each column of the shelf.</p>
     *
     * @return the vertical size (y dimension) of the shelf.
     */
    public int getYsize() {
        return ysize;
    }

    /**
     * Gets the maximum capacity of the shelf.
     *
     * <p>This method calculates the maximum capacity of the shelf by multiplying its x and y dimensions
     * (size). The result represents the total number of items the shelf can hold.</p>
     *
     * @return the maximum capacity of the shelf, calculated as xsize * ysize.
     */
    public int getMaxCapacity() {
        return xsize * ysize;
    }


    /**
     * Gets the total number of products on the shelf.
     *
     * <p>This method returns the total quantity of products currently assigned to the shelf,
     * by retrieving the quantity from the associated product list.</p>
     *
     * @return the total quantity of products on the shelf.
     */
    public int getNumProducts() {
        return productList.getTotalQuantity();
    }

    /**
     * Retrieves the name of the product list associated with this object.
     *
     * <p>This method accesses the {@code productList} object and returns its name.
     * The name typically represents the identifier or description of the product list.</p>
     *
     * @return The name of the product list.
     */
    public String getListName() {
        return productList.getName();
    }


    /**
     * Gets the name of the last distribution applied to the shelf.
     *
     * <p>This method retrieves the name of the most recent distribution from the distribution history.
     * If no distributions have been applied, it throws a {@link ShelfException}.</p>
     *
     * @return the name of the last distribution.
     * @throws ShelfException if no distributions exist for the shelf.
     */
    public String getNameLastDistribution() throws ShelfException {
        if (distributionHistory.isEmpty()) throw new ShelfException("There are no distributions");
        return distributionHistory.getLast().getName();
    }

    /**
     * Retrieves the last distribution in the distribution history.
     *
     * <p>This method accesses the most recent {@link Distribution} object in the
     * {@code distributionHistory} list. It assumes that the list is non-empty,
     * so callers must ensure this precondition is met to avoid an {@link IndexOutOfBoundsException}.
     * If the list is empty, it is recommended to handle this scenario using appropriate checks
     * or exceptions prior to invoking this method.</p>
     *
     * @return the most recent {@link Distribution} in the {@code distributionHistory} list.
     */
    private Distribution getLast()  {
        if (distributionHistory.isEmpty()) return distributionHistory.get(distributionHistory.size() - 1);
        return null;
    }


    /**
     * Gets the last distribution applied to the shelf.
     *
     * <p>This method retrieves the most recent {@link Distribution} object from the distribution history.
     * If no distributions have been applied, it throws a {@link ShelfException}.</p>
     *
     * @return the last {@link Distribution} object applied to the shelf.
     * @throws ShelfException if there are no distributions for the shelf.
     */
    public Distribution getLastDistribution() throws ShelfException {
        if (distributionHistory.isEmpty()) throw new ShelfException("There are no distributions");
        return distributionHistory.getLast();
    }

    /**
     * Updates the distribution history to mark the specified distribution as the current one.
     *
     * <p>This method checks if the given distribution exists in the history. If it exists,
     * the distribution is removed from its current position and re-added to the end of the list,
     * effectively marking it as the "most recent" or "current" distribution.</p>
     *
     * @param dist the {@code Distribution} object to be marked as current.
     * @throws DistributionException if the specified distribution does not exist in the history.
     */
    public void makeCurrentDistribution(Distribution dist) throws DistributionException {
        int index = distributionHistory.indexOf(dist);
        if (index == -1) throw new DistributionException("Distribution does not exist");
        distributionHistory.remove(index);
        distributionHistory.addLast(dist);
    }

    /**
     * Adds a new distribution to the distribution history.
     *
     * <p>This method appends the specified {@link Distribution} object to the end of the
     * {@code distributionHistory} list, marking it as the most recent and current distribution.</p>
     *
     * @param dist the {@link Distribution} object to be added to the distribution history.
     */
    public void addDistribution(Distribution dist) {
        distributionHistory.addLast(dist);
    }


    /**
     * Gets the product list associated with the shelf.
     *
     * <p>This method returns the {@link ProductList} that is currently assigned to the shelf,
     * containing the products available on the shelf.</p>
     *
     * @return the product list of the shelf.
     */
    public ProductList getProductList() {
        return productList;
    }


    /**
     * Changes the product list assigned to the shelf.
     *
     * <p>This method updates the shelf's product list and resets the distribution history,
     * effectively starting a fresh distribution history for the new product list.</p>
     *
     * @param productList the new {@link ProductList} to be assigned to the shelf.
     */
    public void changeProductList(ProductList productList) {
        this.productList = productList;
        distributionHistory = new ArrayList<>();
    }


    /**
     * Returns a string representation of the shelf.
     *
     * <p>This method creates a string representation of the shelf, including its ID, size dimensions
     * (xSize and ySize), and the details of the associated product list.</p>
     *
     * @return a string representation of the shelf, including its ID, dimensions, and product list.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Shelf{id='").append(id).append("', xSize, ySize='").append(xsize).append(" ,").append(ysize).append("\n").append(productList.toString()).append("\n");
        return sb.toString();
    }


    /**
     * Returns a string representation of the shelf with its last distribution.
     *
     * <p>This method creates a string representation of the shelf, including its ID, size dimensions
     * (xSize and ySize), and details of the most recent distribution applied to the shelf. If no distributions
     * have been applied, it may throw a {@link ShelfException}.</p>
     *
     * @return a string representation of the shelf and its last distribution.
     */
    public String toStringLastDistribution() {
        if (!distributionHistory.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Shelf{id='").append(id).append("', xSize, ySize='").append(xsize).append(" ,").append(ysize).append("\n");
            sb.append(distributionHistory.getLast().toString());
            return sb.toString();
        }
        return "";
    }


    /**
     * Retrieves a set of distribution log entries, each represented as a {@link TupleType}.
     * The log entries contain the name, last modification date, and creation date
     * for each {@link Distribution} in the distribution history.
     *
     * @return a {@link Set} of {@link TupleType} objects, where each tuple represents
     *         the name, last modification date, and creation date of a distribution.
     */
    public ArrayList<TupleType> getDistributionLog() {
        ArrayList<TupleType> log = new ArrayList<>();
        for (int i = 0; i < distributionHistory.size(); i++) {
            Distribution distribution = distributionHistory.get(i);
            log.add(i, new TupleType(distribution.getName(), distribution.getModifiedDate(), distribution.getCreationDate().toString()));
        }
        return log;
    }

    /**
     * Retrieves the distribution of products for a given distribution name.
     * The distribution is returned as a nested list of strings, where each
     * string represents the name of a product. The structure mirrors the
     * original distribution matrix, with each row containing the names
     * of the products in that row.
     *
     * @param distName the name of the distribution to retrieve.
     * @return a nested list of strings representing the product names
     *         in the specified distribution.
     * @throws DistributionException if the distribution with the specified
     *         name is not found.
     */
    public ArrayList<ArrayList<String>> getDistribution(String distName) throws DistributionException {
        for (Distribution distribution : distributionHistory) {
            if (distribution.getName().equals(distName)) {
                return distribution.adaptForView();
            }
        }
        throw new DistributionException("Distribution "+ distName +" not found at shelf: " + id);
    }


    /**
     * Retrieves the names of all distributions in the system.
     *
     * <p>This method iterates through the distribution history and collects the names of
     * all distributions into a {@link Set}. If the distribution history is empty, it throws
     * a {@link DistributionException} with an appropriate error message.</p>
     *
     * @return A {@link Set} of strings containing the names of all distributions.
     * @throws DistributionException If there are no distributions in the system.
     */
    public Set<String> getDistributionNames() throws DistributionException {
        if (distributionHistory.isEmpty()) throw new DistributionException("There are no distributions");
        Set<String> distributionNames = new HashSet<>();
        for (Distribution distribution : distributionHistory) {
            distributionNames.add(distribution.getName());
        }
        return distributionNames;
    }
}
