package org.domain.algorithms;

import org.domain.classes.Product;
import org.domain.classes.ProductList;
import org.domain.exceptions.DistributionException;
import org.domain.types.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BruteForceAlgorithmTest {

    private BruteForceAlgorithm algorithm;

    @Mock
    private ProductList mockProductList;
    private List<Product> mockProducts;
    private Map<String, Map<String, Double>> similarityMatrix;
    private HashMap<String, Pair<Integer, Integer>> coordinates;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        similarityMatrix = new HashMap<>();
        algorithm = new BruteForceAlgorithm(similarityMatrix);
        mockProducts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product mockProduct = mock(Product.class);
            mockProducts.add(mockProduct);
        }

        // Fill the similarityMatrix with random values between 0 and 1
        Random rand = new Random();
        for (int i = 0; i < mockProducts.size(); i++) {
            when(mockProducts.get(i).getName()).thenReturn("Product " + i);
            for (int j = 0; j < mockProducts.size(); j++) {
                if (i != j) {
                    double similarity = rand.nextDouble();
                    similarityMatrix.computeIfAbsent(mockProducts.get(i).getName(), k -> new HashMap<>())
                                    .put(mockProducts.get(j).getName(), similarity);
                    similarityMatrix.computeIfAbsent(mockProducts.get(j).getName(), k -> new HashMap<>())
                                    .put(mockProducts.get(i).getName(), similarity);
                }
            }
        }
    }

    @Test
    public void orderProductList_EmptyProductList_ThrowsDistributionException() {
        when(mockProductList.getProducts()).thenReturn(new HashSet<>());
        assertThrows(DistributionException.class, () -> algorithm.orderProductList(mockProductList, 10, 10, -1, coordinates));
    }

    @Test
    public void orderProductList_LimitZero_ReturnsEmptyList() {
        when(mockProductList.getProducts()).thenReturn(new HashSet<>(mockProducts));
        ArrayList<ArrayList<Product>> result = algorithm.orderProductList(mockProductList, 10, 10, 0, coordinates);
        assertTrue(result.isEmpty());
    }

    @Test
    public void orderProductList_LimitPositive_ReturnsLimitedCombinations() {
        when(mockProductList.getProducts()).thenReturn(new HashSet<>(mockProducts));
        ArrayList<ArrayList<Product>> result = algorithm.orderProductList(mockProductList, 10, 10, 5, coordinates);
        assertEquals(5, result.size());
    }

    @Test
    public void orderProductList_LimitNegative_IgnoresLimit() {
        when(mockProductList.getProducts()).thenReturn(new HashSet<>(mockProducts));
        ArrayList<ArrayList<Product>> result = algorithm.orderProductList(mockProductList, 10, 10, -1, coordinates);
        assertTrue(result.size() > 5);
    }
}