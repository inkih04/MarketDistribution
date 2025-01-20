package org.domain.classes;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.domain.exceptions.ProductException;

/**
 * Unit tests for the Product class.
 * @see Product
 */
public class ProductTest {
    private static long totalStartTime;
    private long testStartTime;
    private boolean testPassed;
    private Product product;

    /**
     * Sets up resources before any tests are run.
     */
    @BeforeClass
    public static void setUpAll() {
        totalStartTime = System.currentTimeMillis();
        System.out.println("\n====================================");
        System.out.println("Executing ProductTest...");
        System.out.println("==================================== \n");
    }

    /**
     * Sets up resources before each test.
     */
    @Before
    public void setUp() throws ProductException {
        testStartTime = System.currentTimeMillis();
        System.out.println("Setting up the test environment...");
        product = new Product("Test Product", "Category", 10.0, 5, 100);
        testPassed = true;
    }

    /**
     * Cleans up resources after each test.
     */
    @After
    public void tearDown() {
        long testEndTime = System.currentTimeMillis();
        String result = testPassed ? "✔" : "✘";
        System.out.println("Finished test (" + result + ").");
        System.out.println("Time taken: " + (testEndTime - testStartTime) + " ms\n");
        System.out.println("Tearing down the test environment... \n");
        product = null;
    }

    /**
     * Cleans up resources after all tests are run.
     */
    @AfterClass
    public static void tearDownAll() {
        long totalEndTime = System.currentTimeMillis();
        System.out.println("Completed all ProductTest.");
        System.out.println("Total time taken: " + (totalEndTime - totalStartTime) + " ms");
    }

    // Name tests
    @Test
    public void testGetName() {
        System.out.println("Running testGetName...");
        try {
            assertEquals("Test Product", product.getName());
        } catch (AssertionError | Exception e) {
            testPassed = false;
            throw e;
        }
    }

    // Category tests
    @Test
    public void testGetCategory() {
        System.out.println("Running testGetCategory...");
        try {
            assertEquals("Category", product.getCategory());
        } catch (AssertionError | Exception e) {
            testPassed = false;
            throw e;
        }
    }

    @Test
    public void testSetCategory() {
        System.out.println("Running testSetCategory...");
        try {
            product.setCategory("New Category");
            assertEquals("New Category", product.getCategory());
        } catch (AssertionError | Exception e) {
            testPassed = false;
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullCategory() {
        System.out.println("Running testSetNullCategory...");
        product.setCategory(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetEmptyCategory() {
        System.out.println("Running testSetEmptyCategory...");
        product.setCategory("");
    }

    // Price tests
    @Test
    public void testGetPrice() {
        System.out.println("Running testGetPrice...");
        try {
            assertEquals(10.0, product.getPrice(), 0.0);
        } catch (AssertionError | Exception e) {
            testPassed = false;
            throw e;
        }
    }

    @Test
    public void testSetPrice() {
        System.out.println("Running testSetPrice...");
        try {
            product.setPrice(20.0);
            assertEquals(20.0, product.getPrice(), 0.0);
        } catch (AssertionError | Exception e) {
            testPassed = false;
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativePrice() {
        System.out.println("Running testSetNegativePrice...");
        product.setPrice(-5.0);
    }

    // Amount tests
    @Test
    public void testGetAmount() {
        System.out.println("Running testGetAmount...");
        try {
            assertEquals(5, product.getAmount());
        } catch (AssertionError | Exception e) {
            testPassed = false;
            throw e;
        }
    }

    @Test
    public void testSetAmount() {
        System.out.println("Running testSetAmount...");
        try {
            product.setAmount(10);
            assertEquals(10, product.getAmount());
        } catch (AssertionError | Exception e) {
            testPassed = false;
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativeAmount() {
        System.out.println("Running testSetNegativeAmount...");
        product.setAmount(-3);
    }

    // Discount tests
    @Test
    public void testApplyDiscount() {
        System.out.println("Running testApplyDiscount...");
        try {
            product.applyDiscount(10);
            assertEquals(9.0, product.getPrice(), 0.01);
        } catch (AssertionError | Exception e) {
            testPassed = false;
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApplyInvalidDiscount() {
        System.out.println("Running testApplyInvalidDiscount...");
        product.applyDiscount(110);
    }

    @Test
    public void testApplyOriginalPrice() {
        System.out.println("Running testApplyOriginalPrice...");
        try {
            product.applyDiscount(10);
            assertEquals(9.0, product.getPrice(), 0.01);
            product.applyOriginalPrice();
            assertEquals(10.0, product.getPrice(), 0.01);
        } catch (AssertionError | Exception e) {
            testPassed = false;
            throw e;
        }
    }

    // Amount update tests
    @Test
    public void testUpdateAmount() throws ProductException {
        System.out.println("Running testUpdateAmount...");
        try {
            product.updateAmount(5);
            assertEquals(10, product.getAmount());
        } catch (AssertionError | Exception e) {
            testPassed = false;
            throw e;
        }
    }

    @Test(expected = ProductException.class)
    public void testUpdateInvalidAmount() throws ProductException {
        System.out.println("Running testUpdateInvalidAmount...");
        product.updateAmount(-10);
    }

    // toString test
    @Test
    public void testToString() {
        System.out.println("Running testToString...");
        try {
            String expectedOutput = "Product{name='Test Product', category='Category', price=10.0, originalPrice=10.0, amount=5}";
            assertEquals(expectedOutput, product.toString());
        } catch (AssertionError | Exception e) {
            testPassed = false;
            throw e;
        }
    }
}