package org.domain.types;

/**
 * The `TupleType` class represents a tuple, which is an ordered collection of elements.
 * This class provides methods to access, add, remove, and update elements within the tuple.
 * It is designed to be flexible and can hold elements of any type.
 */
public class TupleType {

    private Object[] elements;

    /**
     * Constructs a new `TupleType` with the specified elements.
     *
     * @param elements the elements to be included in the tuple
     */
    public TupleType(Object... elements) {
        this.elements = elements;
    }

    /**
     * Returns the element at the specified position in this tuple.
     *
     * @param index the index of the element to return
     * @return the element at the specified position in this tuple
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size())
     */
    public Object get(int index) {
        if (index < 0 || index >= elements.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return elements[index];
    }

    /**
     * Returns the number of elements in this tuple.
     *
     * @return the number of elements in this tuple
     */
    public int size() {
        return elements.length;
    }

    /**
     * Returns a string representation of this tuple.
     * The string representation consists of a list of the tuple's elements in the order they are returned by its iterator,
     * enclosed in curly braces ("{}"). Adjacent elements are separated by the characters ", " (comma and space).
     *
     * @return a string representation of this tuple
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TupleType{");
        for (int i = 0; i < elements.length; i++) {
            sb.append(elements[i]);
            if (i < elements.length - 1) {
                sb.append(", ");
            }
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * Adds a new element to the end of this tuple.
     *
     * @param element the element to be added
     */
    public void add(Object element) {
        Object[] newElements = new Object[elements.length + 1];
        System.arraycopy(elements, 0, newElements, 0, elements.length);
        newElements[elements.length] = element;
        elements = newElements;
    }

    /**
     * Removes the element at the specified position in this tuple.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     *
     * @param index the index of the element to be removed
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size())
     */
    public void remove(int index) {
        if (index < 0 || index >= elements.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        Object[] newElements = new Object[elements.length - 1];
        System.arraycopy(elements, 0, newElements, 0, index);
        System.arraycopy(elements, index + 1, newElements, index, elements.length - index - 1);
        elements = newElements;
    }

    /**
     * Updates the element at the specified position in this tuple with the specified new value.
     *
     * @param index the index of the element to be updated
     * @param newValue the new value to be stored at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size())
     */
    public void updateValue(int index, Object newValue) {
        if (index < 0 || index >= elements.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        elements[index] = newValue;
    }
}