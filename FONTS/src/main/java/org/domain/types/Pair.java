package org.domain.types;

/**
 * The `Pair` class represents a pair of elements, which is an ordered collection of two elements.
 * This class provides methods to access, add, remove, and update elements within the pair.
 * It is designed to be flexible and can hold elements of any type.
 * @param <K> the type of the first element
 * @param <V> the type of the second element
 */
public class Pair<K, V> {
    private K first;
    private V second;

    /**
     * Constructs a new empty `Pair`.
     */
    public Pair() {}

    /**
     * Constructs a new `Pair` with the specified elements.
     *
     * @param first the first element of the pair
     * @param second the second element of the pair
     */
    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element of this pair.
     *
     * @return the first element of this pair
     */
    public K getFirst() {
        return first;
    }

    /**
     * Sets the first element of this pair.
     *
     * @param first the first element of this pair
     */
    public void setFirst(K first) {
        this.first = first;
    }

    /**
     * Returns the second element of this pair.
     *
     * @return the second element of this pair
     */
    public V getSecond() {
        return second;
    }

    /**
     * Sets the second element of this pair.
     *
     * @param second the second element of this pair
     */
    public void setSecond(V second) {
        this.second = second;
    }

    /**
     * Returns a string representation of this pair.
     *
     * @return a string representation of this pair
     */
    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}