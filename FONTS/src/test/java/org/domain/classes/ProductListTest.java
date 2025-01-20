package org.domain.classes;

import static org.junit.Assert.*;

import org.domain.exceptions.ProductException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

/**
 * Test class for the ProductList class.
 * Contains unit tests for adding, removing, and applying discounts to products in a ProductList.
 * @see ProductList
 * @see Product
 */
public class ProductListTest {
    private static final Logger logger = Logger.getLogger(ProductListTest.class.getName());
    private static long totalStartTime;
    private long testStartTime;
    private boolean testPassed;

    private ProductList productList;
    private Product product;

    @BeforeClass
    public static void setUpAll() {
        totalStartTime = System.currentTimeMillis();
        System.out.println("\n====================================");
        System.out.println("Executing ProductListTest...");
        System.out.println("==================================== \n");
    }

    @Before
    public void setUp() throws ProductException {
        testStartTime = System.currentTimeMillis();
        productList = new ProductList("testList", "TestCategory");
        product = new Product("TestProduct", "TestCategory", 10.0, 5, 100);
        testPassed = true;
    }

    @After
    public void tearDown() {
        long testEndTime = System.currentTimeMillis();
        String result = testPassed ? "✔" : "✘";
        System.out.println("Finished test (" + result + ").");
        System.out.println("Time taken: " + (testEndTime - testStartTime) + " ms\n");
    }

    @AfterClass
    public static void tearDownAll() {
        long totalEndTime = System.currentTimeMillis();
        System.out.println("Completed all ProductListTest.");
        System.out.println("Total time taken: " + (totalEndTime - totalStartTime) + " ms");
    }

    private Product[] generateRandomProducts(int count) throws ProductException {
        Product[] products = new Product[count];
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            String name = "Product " + (i + 1);
            String category = "TestCategory";
            double price = 10.0 + (random.nextDouble() * 90.0);
            double originalPrice = price;
            int amount = random.nextInt(100) + 1;
            products[i] = new Product(name, category, price, originalPrice, amount);
        }
        return products;
    }

    @Test
    public void testAddProduct() {
        logger.info("Running testAddProduct...");
        try {
            assertTrue(productList.addProduct(product));
            assertTrue(productList.getProducts().contains(product));
        } catch (AssertionError | Exception e) {
            testPassed = false;
            logger.log(Level.SEVERE, "Exception: " + e.getMessage(), e);
        } finally {
            logger.info("Result: " + productList.toString());
        }
    }

    @Test
    public void testAddMultipleProducts() {
        logger.info("Running testAddMultipleProducts...");
        try {
            Product[] products = generateRandomProducts(5);
            for (Product product : products) {
                assertTrue(productList.addProduct(product));
                assertTrue(productList.getProducts().contains(product));
            }
        } catch (AssertionError | Exception e) {
            testPassed = false;
            logger.log(Level.SEVERE, "Exception: " + e.getMessage(), e);
        } finally {
            logger.info("Result: " + productList.toString());
        }
    }

    @Test
    public void testAddProductWithDifferentCategory() {
        logger.info("Running testAddProductWithDifferentCategory...");
        try {
            Product differentCategoryProduct = new Product("Other Product", "DifferentCategory", 20.0, 3, 100);
            assertTrue(productList.addProduct(differentCategoryProduct));
            assertTrue(productList.getProducts().contains(differentCategoryProduct));
        } catch (AssertionError | Exception e) {
            testPassed = false;
            logger.log(Level.SEVERE, "Exception: " + e.getMessage(), e);
        } finally {
            logger.info("Result: " + productList.toString());
        }
    }

    @Test
    public void testRemoveProduct() {
        logger.info("Running testRemoveProduct...");
        try {
            productList.addProduct(product);
            assertTrue(productList.removeProduct(product.getName()));
            assertFalse(productList.getProducts().contains(product));
        } catch (AssertionError | Exception e) {
            testPassed = false;
            logger.log(Level.SEVERE, "Exception: " + e.getMessage(), e);
        } finally {
            logger.info("Result: " + productList.toString());
        }
    }

    @Test
    public void testRemoveNonExistentProduct() {
        logger.info("Running testRemoveNonExistentProduct...");
        try {
            assertFalse(productList.removeProduct("NonExistentProduct"));
        } catch (AssertionError | Exception e) {
            testPassed = false;
            logger.log(Level.SEVERE, "Exception: " + e.getMessage(), e);
        } finally {
            logger.info("Result: " + productList.toString());
        }
    }

    @Test
    public void testRemoveMultipleProducts() {
        logger.info("Running testRemoveMultipleProducts...");
        try {
            Product[] products = generateRandomProducts(5);
            for (Product product : products) {
                productList.addProduct(product);
            }
            for (Product product : products) {
                assertTrue(productList.removeProduct(product.getName()));
                assertFalse(productList.getProducts().contains(product));
            }
        } catch (AssertionError | Exception e) {
            testPassed = false;
            logger.log(Level.SEVERE, "Exception: " + e.getMessage(), e);
        } finally {
            logger.info("Result: " + productList.toString());
        }
    }

    @Test
    public void testApplyDiscount() {
        logger.info("Running testApplyDiscount...");
        try {
            productList.addProduct(product);
            double originalPrice = product.getPrice();
            double discount = 10;
            System.out.println("Applying discount: " + discount + "%");
            productList.applyDiscount(discount);
            double expectedPrice = originalPrice * 0.9;
            assertEquals(expectedPrice, product.getPrice(), 0.01);
        } catch (AssertionError | Exception e) {
            testPassed = false;
            logger.log(Level.SEVERE, "Exception: " + e.getMessage(), e);
        } finally {
            logger.info("Result: " + productList.toString());
        }
    }

    @Test
    public void testApplyDiscountToMultipleProducts() {
        logger.info("Running testApplyDiscountToMultipleProducts...");
        try {
            Product[] products = generateRandomProducts(5);
            for (Product product : products) {
                productList.addProduct(product);
            }
            double discount = 10;
            System.out.println("Applying discount: " + discount + "%");
            productList.applyDiscount(discount);
            for (Product product : products) {
                double expectedPrice = product.getOriginalPrice() * 0.9;
                assertEquals(expectedPrice, product.getPrice(), 0.01);
            }
        } catch (AssertionError | Exception e) {
            testPassed = false;
            logger.log(Level.SEVERE, "Exception: " + e.getMessage(), e);
        } finally {
            logger.info("Result: " + productList.toString());
        }
    }

    // TO FIX
    @Test
    public void testToString() {
        logger.info("Running testToString...");
        try {
            Product[] products = generateRandomProducts(4);
            for (Product product : products) {
                productList.addProduct(product);
            }

            String result = productList.toString();
            StringBuilder expectedOutput = new StringBuilder("ProductList{name='testList', category='TestCategory', lastModified=" + productList.getLastModified() + ",\n\tproducts=[\n");

            // Sort products by name for consistent comparison
            List<Product> sortedProducts = new ArrayList<>(productList.getProducts());
            sortedProducts.sort(Comparator.comparing(Product::getName));

            for (Product product : sortedProducts) {
                expectedOutput.append("\t\t").append(product.toString()).append(",\n");
            }
            if (!sortedProducts.isEmpty()) {
                expectedOutput.setLength(expectedOutput.length() - 2); // Remove the last comma and newline
            }
            expectedOutput.append("\n\t]}");

            assertEquals(expectedOutput.toString(), result);
        } catch (AssertionError | Exception e) {
            testPassed = false;
            logger.log(Level.SEVERE, "Exception: " + e.getMessage(), e);
        } finally {
            logger.info("Result: " + productList.toString());
        }
    }
}