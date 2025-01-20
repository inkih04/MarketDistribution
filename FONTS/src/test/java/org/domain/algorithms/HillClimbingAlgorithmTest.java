package org.domain.algorithms;

import org.domain.classes.Product;
import org.domain.classes.ProductList;
import org.domain.exceptions.DistributionException;
import org.domain.exceptions.ProductException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HillClimbingAlgorithmTest {

    private HillClimbingAlgorithm algorithm;

    @Mock
    private ProductList mockProductList;
    private List<Product> mockProducts;
    private Map<String, Map<String, Double>> similarityMatrix;

    @Before
    public void setUp() throws ProductException {
        MockitoAnnotations.openMocks(this);
        similarityMatrix = new HashMap<>();
        algorithm = new HillClimbingAlgorithm(similarityMatrix);
        mockProducts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String s = String.valueOf(i);
            Product p = new Product(s, s, 0, 10, 100);
            mockProducts.add(p);
        }

        // Fill the similarityMatrix with random values between 0 and 1
        Random rand = new Random();
        for (int i = 0; i < mockProducts.size(); i++) {
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
        assertThrows(DistributionException.class, () -> algorithm.orderProductList(mockProductList, 10, 10, -1, new HashMap<>()));
    }

    @Test
    public void orderProductList_LimitZero_ReturnsEmptyList() {
        when(mockProductList.getProducts()).thenReturn(new HashSet<>(mockProducts));
        ArrayList<ArrayList<Product>> result = algorithm.orderProductList(mockProductList, 10, 10, 0, new HashMap<>());
        assertTrue(result.isEmpty());
    }

    @Test
    public void orderProductList_LimitPositive_ReturnsLimitedCombinations() {
        when(mockProductList.getProducts()).thenReturn(new HashSet<>(mockProducts));
        ArrayList<ArrayList<Product>> result = algorithm.orderProductList(mockProductList, 10, 10, 5, new HashMap<>());
        assertEquals(5, result.size());
    }

    @Test
    public void orderProductList_LimitNegative_IgnoresLimit() {
        when(mockProductList.getProducts()).thenReturn(new HashSet<>(mockProducts));
        ArrayList<ArrayList<Product>> result = algorithm.orderProductList(mockProductList, 10, 10, -1, new HashMap<>());
        assertTrue(result.size() > 5);
    }

    @Test
    public void orderProductList_ValidInputs_ReturnsCorrectDistribution() {
        when(mockProductList.getProducts()).thenReturn(new HashSet<>(mockProducts));
        ArrayList<ArrayList<Product>> result = algorithm.orderProductList(mockProductList, 3, 3, 10, new HashMap<>());
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(3, result.get(0).size());
        assertEquals(3, result.get(1).size());
        assertEquals(3, result.get(2).size());
    }

    @Test
    public void orderProductList_InvalidXSize_ThrowsDistributionException() {
        when(mockProductList.getProducts()).thenReturn(new HashSet<>(mockProducts));
        assertThrows(DistributionException.class, () -> algorithm.orderProductList(mockProductList, -1, 10, 10, new HashMap<>()));
    }

    @Test
    public void orderProductList_InvalidYSize_ThrowsDistributionException() {
        when(mockProductList.getProducts()).thenReturn(new HashSet<>(mockProducts));
        assertThrows(DistributionException.class, () -> algorithm.orderProductList(mockProductList, 10, -1, 10, new HashMap<>()));
    }
}