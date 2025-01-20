package org.domain.classes;

import org.domain.algorithms.BruteForceAlgorithm;
import org.domain.algorithms.HillClimbingAlgorithm;
import org.domain.controllers.ShelfManager;
import org.domain.exceptions.ProductListException;
import org.domain.exceptions.ShelfException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ShelfIntegrationTest {

    private ShelfManager shelfManager;
    private Shelf shelf;
    private ProductList productList;
    private Map<String, Map<String, Double>> similarityMatrix;

    @Before
    public void setUp() {
        try {
            // Create a similarity matrix
            similarityMatrix = new HashMap<>();
            productList = new ProductList("Test Product List", "Test Category");
            for (int i = 0; i < 10; i++) {
                String name = "Product " + i;
                String category = "Category " + i;
                Product product = new Product(name, category, 1, 3, 100);
                productList.addProduct(product);
                similarityMatrix.put(name, new HashMap<>());
            }
            ArrayList<Product> products = new ArrayList<>(productList.getProducts());
            Random rand = new Random();
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (i != j) {
                        double similarity = rand.nextDouble();
                        // Fill the similarity matrix
                        similarityMatrix.get(products.get(i).getName()).put(products.get(j).getName(), similarity);
                        similarityMatrix.get(products.get(j).getName()).put(products.get(i).getName(), similarity);
                    }
                }
            }
            shelf = new Shelf(1, 10, 10, productList);
            shelfManager = ShelfManager.getInstance();
            shelfManager.createShelf(1, 10, 10, productList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void distributeShelf_ValidInputs_ReturnsCorrectDistribution() throws ShelfException {
        Distribution distribution = shelfManager.distributeShelf(1, "Test Distribution", 2, 10, similarityMatrix);
        assertEquals("Test Distribution", distribution.getName());
        Distribution distribution2 = shelfManager.distributeShelf(1, "Test Distribution 2", 1, 10, similarityMatrix);
        assertEquals("Test Distribution 2", distribution2.getName());
    }

    @Test
    public void getNameLastDistributionShelf_ValidShelf_ReturnsCorrectName() throws ShelfException {
        shelfManager.distributeShelf(1, "Test Distribution", 2, 10, similarityMatrix);
        assertEquals("Test Distribution", shelfManager.getNameLastDistributionShelf(1));
    }

    @Test
    public void getLastDistribution_ValidShelf_ReturnsCorrectDistribution() throws ShelfException {
        Distribution distribution = shelfManager.distributeShelf(1, "Test Distribution", 2, 10, similarityMatrix);
        assertEquals(distribution.getName(), shelfManager.getNameLastDistributionShelf(1));
    }

    @Test
    public void changeProductList_ValidShelf_ChangesProductList() throws ShelfException, ProductListException {
        ProductList newProductList = new ProductList("Test Product List 2", "Test Category");
        shelfManager.changeProductListAtShelf(1, newProductList);
        assertEquals(newProductList.getName(), shelfManager.getShelf(1).getProductList().getName());
        assertNotEquals(productList.getName(), shelfManager.getShelf(1).getProductList().getName());
    }
}