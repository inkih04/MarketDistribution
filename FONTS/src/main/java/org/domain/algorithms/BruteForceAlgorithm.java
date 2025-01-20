package org.domain.algorithms;

import org.domain.exceptions.DistributionException;

import org.domain.classes.Product;
import org.domain.classes.ProductList;
import org.domain.types.Pair;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

/**
 * BruteForceAlgorithm is an extension of AbstractAlgorithm that arranges a
 * Productlist by evaluating all possible combinations to maximize their total similarity.
 * This approach ensures finding the optimal solution but may be computationally expensive
 * for larger inputs due to its exhaustive nature.
 * @author Max Estrade Pey {max.estrade@estudiantat.upc.edu}
 * @version 1.0
 */
public class BruteForceAlgorithm extends AbstractAlgorithm{
    private String name = "Brute Force";

    /**
     * Retrieves the name of the algorithm.
     *
     * <p>This method returns the name associated with this algorithm, which is
     * typically used to identify or display the algorithm's name in various contexts.</p>
     *
     * @return The name of the algorithm (e.g., "Brute Force").
     */
    public String getName() {
        return name;
    }


    /**
     * Constructs a new BruteForceAlgorithm with the specified similarity matrix.
     *
     * @param similarityMatrix A map representing the similarity matrix where the key is a product identifier
     *                         and the value is another map containing similarity scores with other products.
     */
    public BruteForceAlgorithm(Map<String, Map<String, Double>> similarityMatrix) {
        super(similarityMatrix);
    }

    /**
     * Arranges a given product list using a brute force approach.
     * The method generates all possible combinations of products,
     * evaluates each combination's total value, and selects the combination with the highest value.
     *
     * @param list        The product list to be arranged.
     * @param xsize       Represents the number of columns of the distribution
     * @param ysize       Represents the number of rows of the distribution
     * @param limit       Serves to limit the depth we want to search for the solution.If limit is less than 0, the code ignores the limit
     * @param coordinates Contains the coordinates of the products in the distribution.
     * @return A matrix that represent the arranged distribution of the list.
     * @throws DistributionException If the ProductList is empty.
     */
    @Override
    public ArrayList<ArrayList<Product>> orderProductList(ProductList list, int xsize, int ysize, int limit, HashMap<String, Pair<Integer, Integer>> coordinates) {
        List<Product> allP = new ArrayList<>(list.getProducts().stream().toList());
        if (allP.isEmpty()) throw new DistributionException("Empty list");
        int maxsize = xsize*ysize;
        List<List<Product>> combinations = generateCombinations(allP, maxsize, limit);
        List<Product> best = new ArrayList<>();
        double max = 0;

        for(List<Product> lp : combinations) {
            double sum = calculaSum(lp);
            if (sum > max) {
                max = sum;
                best = lp;
            }
        }

        return  adaptToShelf(best, xsize, ysize, coordinates);
    }

    /**
     * Generates all possible combinations of products of size `r`.
     * This method uses a helper function to recursively generate combinations.
     * @param products The list of products to combine.
     * @param r The desired size of each combination.
     * @param limit A integer used to limit the number of combinations that the code generates. If limit is < 0, the code ignores the limit.
     * @return A list of all possible combinations of products of size `r`.
     */
    private List<List<Product>> generateCombinations(List<Product> products, int r, int limit) {
        HashSet<Product> used = new HashSet<>();
        List<List<Product>> combinations = new ArrayList<>();
        combineHelper(products, r, 0, new ArrayList<>(), combinations, used, limit);
        return combinations;
    }

    /**
     * Helper method to recursively generate combinations of products.
     * This method builds combinations by iterating through the product list and ensuring
     * that no product is reused in a single combination.
     * @param products The list of available products.
     * @param r The desired size of each combination.
     * @param start The current starting index for combination generation.
     * @param currentCombination The current combination being built.
     * @param combinations The list of all generated combinations.
     * @param used A set to track products already included in the current combination.
     * @param limit Integer used to limit the number of combinations that the code generates. If limit is < 0, the code ignores the limit
     */
    private static void combineHelper(List<Product> products, int r, int start, List<Product> currentCombination, List<List<Product>> combinations, HashSet<Product> used, int limit) {
        if (currentCombination.size() == Math.min(r, products.size())) {
            combinations.add(new ArrayList<>(currentCombination));
            return;
        }

        if (limit > 0 && combinations.size() >= limit) {
            return;
        }

        for (int i = 0; i < products.size(); i++) {
            if (!used.contains(products.get(i))) {
                currentCombination.add(products.get(i));
                used.add(products.get(i));
                combineHelper(products, r, start + 1, currentCombination, combinations, used, limit);
                used.remove(products.get(i));
                currentCombination.removeLast();
            }

        }
    }

}