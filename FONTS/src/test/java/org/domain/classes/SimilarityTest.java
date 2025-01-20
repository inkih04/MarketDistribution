package org.domain.classes;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SimilarityTest {

    private Similarity similarity;

    @Mock
    private Product mockProduct1;

    @Mock
    private Product mockProduct2;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockProduct1 = mock(Product.class);
        mockProduct2 = mock(Product.class);
        similarity = new Similarity(5.0, mockProduct1, mockProduct2);
    }

    @Test
    public void testGetScore() {
        assertEquals(5.0, similarity.getSimilarityScore(), 0.01);
    }

    @Test
    public void testGetProduct1() {
        assertEquals(mockProduct1, similarity.getProduct1());
    }

    @Test
    public void testGetProduct2() {
        assertEquals(mockProduct2, similarity.getProduct2());
    }

    @Test
    public void testGetOtherProduct() {
        assertEquals(mockProduct2, similarity.getOtherProduct(mockProduct1));
        assertEquals(mockProduct1, similarity.getOtherProduct(mockProduct2));
    }
}