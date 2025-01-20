package org.domain.classes;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ShelfTest {

    private Shelf shelf;

    @Mock
    private ProductList mockProductList;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockProductList = mock(ProductList.class);
        when(mockProductList.getTotalQuantity()).thenReturn(10);
        shelf = new Shelf(1, 10, 10, mockProductList);
    }

    @Test
    public void testGetId() {
        assertEquals(1, shelf.getId());
    }

    @Test
    public void testGetMaxCapacity() {
        assertEquals(100, shelf.getMaxCapacity());
    }

    @Test
    public void testGetNumProducts() {
        assertEquals(10, shelf.getNumProducts());
    }

    @Test
    public void testGetProductList() {
        assertEquals(mockProductList, shelf.getProductList());
    }

    @Test
    public void testChangeProductList() {
        ProductList newProductList = mock(ProductList.class);
        shelf.changeProductList(newProductList);
        assertEquals(newProductList, shelf.getProductList());
    }
}