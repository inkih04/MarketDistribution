package org.domain.algorithms;

import org.domain.classes.Product;
import org.domain.classes.ProductList;
import org.domain.exceptions.DistributionException;
import org.domain.types.Pair;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * HillClimbingAlgorithm is an extension of AbstractAlgorithm designed to optimize
 * the arrangement of a list of products using Hill Climbing.
 * @author Max Estrade Pey {max.estrade@estudiantat.upc.edu}
 * @version 1.0
 */
public class HillClimbingAlgorithm extends AbstractAlgorithm{
    private String name = "Hill Climbing";

    /**
     * Constructs a HillClimbingAlgorithm with a similarity matrix.
     * @param similarityMatrix The similarity matrix used to get the similarities between products.
     */
    public HillClimbingAlgorithm(Map<String, Map<String, Double>> similarityMatrix) {
        super(similarityMatrix);
    }

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
     * @param list        Productlist which we want to arrange
     * @param xsize       Represents the number of columns of the distribution
     * @param ysize       Represents the number of rows of the distribution
     * @param limit       Serves to limit the depth we want to search for the solution.If limit is less than 0, the code ignores the limit
     * @param coordinates Contains the coordinates of the products in the distribution.
     * @return A matrix that represent the arranged distribution of the list.
     * @throws DistributionException If the ProductList is empty.
     */
    @Override
    public ArrayList<ArrayList<Product>> orderProductList(ProductList list, int xsize, int ysize, int limit, HashMap<String, Pair<Integer, Integer>> coordinates) {
        Random rand = new Random();
        List<Product> allP = new ArrayList<>(list.getProducts());
        if (allP.isEmpty()) throw new DistributionException("Empty list");
        Product randP = allP.get(rand.nextInt(allP.size() - 1));     // Get a random product
        int maxsize = xsize*ysize;
        List<Product> smart = iniSolution(maxsize, randP, allP);
        smart = hillClimbing(smart, limit);

        return adaptToShelf(smart, xsize, ysize, coordinates);
    }

    /**
     * Performs the Hill Climbing optimization algorithm to improve a list of products.
     * The method iteratively explores neighboring solutions to maximize a calculated sum.
     * @param current The initial list of products to arrange.
     * @param limit The number of time the code searches for neighbors of the currents solution. If limit is < 0, the code ignores the limit
     * @return The optimized list of products with the highest calculated sum found.
     */
    private List<Product> hillClimbing(List<Product> current, int limit){
        boolean isBetter = true;
        int count = 0;
        while(isBetter){
            if(limit < 0 || (limit > 0 && count < limit)) {
                List<Product> neighbor = best_neighbor(current);
                if (calculaSum(neighbor) > calculaSum(current)) {
                    current = new ArrayList<>(neighbor);
                } else isBetter = false;
            }
            else break;
            ++count;
        }
        return current;
    }

    /**
     * Initializes a solution based on the similarity of products.
     * This method selects a starting product and builds an initial list of products by
     * repeatedly adding the most similar unused product.
     * @param size  The number of products to include in the solution.
     * @param first The starting product.
     * @param allP  The list of all available products.
     * @return An initial solution list based on product similarity.
     */
    private List<Product> iniSolution(int size, Product first, List<Product> allP) {
        ArrayList<Product> smart = new ArrayList<>();
        smart.add(first);
        List<Product> candidates = new ArrayList<>(allP);
        candidates.remove(first);
        for (int i = 1; i < Math.min(size, allP.size()); i++) {
            Product mostSimilar = mostSimilarP(smart.get(i - 1), candidates);
            while (!candidates.contains(mostSimilar) && smart.contains(mostSimilar) ) {
                mostSimilar = mostSimilarP(smart.get(i - 1), candidates);
            }
            candidates.remove(mostSimilar);
            smart.add(mostSimilar);
        }
        return smart;
    }

    /**
     * Finds the best neighboring solution for a given list by swapping product positions.
     * This method evaluates all possible neighbors of the current solution by swapping pairs
     * of products, and selects the one with the highest calculated metric.
     * @param list The current list of products.
     * @return The neighboring solution with the highest calculated sum.
     */
    private List<Product> best_neighbor(List<Product> list){
        List<Product> best = list;
        double maxval = calculaSum(best);
        for (int i = 0; i < list.size()-1; i++) {
            for (int j = i + 1; j < list.size(); j++){
                List<Product> neighbor = new ArrayList<>(list);
                Collections.swap(neighbor, i, j);

                if (maxval < calculaSum(neighbor)){
                    best =  neighbor;
                    maxval = calculaSum(neighbor);
                }
            }
        }
        return best;
    }

}