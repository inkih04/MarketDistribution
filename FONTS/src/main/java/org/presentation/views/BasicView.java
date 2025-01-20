// BasicView.java
package org.presentation.views;
// Exceptions
import org.domain.exceptions.ProductException;
import org.domain.exceptions.ProductListException;
import org.domain.types.TupleType;
// Presentation layer
import org.presentation.controllers.PresentationController;
// Design Utils
import org.presentation.utils.DesignUtils;
// Swing
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
// Swing events
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
// AWT
import java.awt.BorderLayout;
// Util
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The `BasicView` class serves as an abstract base class for various views in the presentation layer.
 * It provides common functionalities such as displaying a table of products, filtering products,
 * and handling user interactions like adding, deleting, and applying discounts to products.
 *
 * <p>This class extends `JFrame` and includes components like `JTable`, `JTextField`, `JComboBox`,
 * and `JButton` to create a user interface for managing products. It also defines abstract methods
 * that must be implemented by subclasses to provide specific functionalities.</p>
 *
 * @version 1.0
 * @see javax.swing.JFrame
 */
public abstract class BasicView extends JFrame {
    protected final JTable productTable;
    protected final DefaultTableModel tableModel;
    protected final JTextField searchField;
    protected final JComboBox<String> filterComboBox;
    protected final JButton deleteButton;
    protected final JButton discountButton;
    protected final JButton showSimilaritiesButton;
    protected final JButton backButton;
    protected ArrayList<TupleType> allProducts;
    protected final Map<TupleType, Map<TupleType, Double>> similarityMap = new HashMap<>();
    protected final PresentationController presentationController;

    /**
     * Constructs a new `BasicView` with the specified title.
     * Initializes the user interface components and sets up event listeners.
     *
     * @param title the title of the view
     */
    public BasicView(String title) {
        presentationController = PresentationController.getInstance();
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        searchField = new JTextField(20);
        filterComboBox = new JComboBox<>(new String[]{
                "Alphabetical Ascending", "Alphabetical Descending",
                "Category", "Price Ascending", "Price Descending"
        });
        filterComboBox.setSelectedIndex(0);
        JButton addButton = new JButton("Add Product");
        deleteButton = new JButton("Delete Product");
        backButton = new JButton("Back");
        discountButton = new JButton("Apply Discount");
        showSimilaritiesButton = new JButton("Show Similarities");

        DesignUtils.configureTextField(searchField);
        DesignUtils.configureComboBox(filterComboBox);
        DesignUtils.configureButton(addButton);
        DesignUtils.configureButton(deleteButton);
        DesignUtils.configureButton(backButton);
        DesignUtils.configureButton(discountButton);
        DesignUtils.configureButton(showSimilaritiesButton);

        String[] columnNames = {"Name", "Category", "Price", "OriginalPrice", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        DesignUtils.configureTable(productTable);

        JPanel filterPanel = new JPanel();
        JLabel searchLabel = new JLabel("Search:");
        JLabel filterLabel = new JLabel("Filter by:");
        DesignUtils.configureLabel(searchLabel);
        DesignUtils.configureLabel(filterLabel);

        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);
        filterPanel.add(addButton);
        filterPanel.add(deleteButton);
        filterPanel.add(discountButton);
        filterPanel.add(showSimilaritiesButton);

        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(productTable), BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        addButton.addActionListener(_ -> showAddProductDialog());
        deleteButton.addActionListener(_ -> deleteSelectedProducts());
        discountButton.addActionListener(_ -> applyDiscountToSelectedProducts());
        showSimilaritiesButton.addActionListener(_ -> showSimilaritiesDialog());
        setBackButtonListener();

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterProducts(); }
            public void removeUpdate(DocumentEvent e) { filterProducts(); }
            public void changedUpdate(DocumentEvent e) { filterProducts(); }
        });

        filterComboBox.addActionListener(_ -> filterProducts());

        productTable.getSelectionModel().addListSelectionListener(_ -> {
            boolean isSelected = productTable.getSelectedRowCount() > 0;
            deleteButton.setEnabled(isSelected);
            discountButton.setEnabled(isSelected);
        });

        tableModel.addTableModelListener(new ProductTableModelListener());

        deleteButton.setEnabled(false);
        discountButton.setEnabled(false);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Displays a dialog for adding a new product.
     * This method must be implemented by subclasses to provide specific functionality.
     */
    protected abstract void showAddProductDialog();

    protected abstract void deleteSelectedProducts();

    // Listener for table model changes to update the product when a row is edited.
    private class ProductTableModelListener implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            if (e.getType() == TableModelEvent.UPDATE && e.getFirstRow() == e.getLastRow()) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 1 || column == 2 || column == 4) { // Only update for category, price, and amount
                    String productName = (String) tableModel.getValueAt(row, 0); // Get product name
                    updateProduct(productName, row);
                }
            }
        }

        private void updateProduct(String productName, int row) {
            TupleType product = allProducts.stream()
                    .filter(p -> p.get(0).equals(productName))
                    .findFirst()
                    .orElse(null);

            if (product != null) {
                String category = (String) tableModel.getValueAt(row, 1);
                double price = Double.parseDouble(tableModel.getValueAt(row, 2).toString());
                int amount = Integer.parseInt(tableModel.getValueAt(row, 4).toString());

                try {
                    presentationController.updateProduct(productName, category, price, amount, new ArrayList<>());
                } catch (ProductException ex) {
                    JOptionPane.showMessageDialog(BasicView.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Applies a discount to the selected products.
     * Prompts the user to enter a discount percentage and updates the prices of the selected products.
     */
    protected void applyDiscountToSelectedProducts() {
        int[] selectedRows = productTable.getSelectedRows();
        if (selectedRows.length > 0) {
            String discountStr = JOptionPane.showInputDialog(this, "Enter discount percentage:", "Apply Discount", JOptionPane.PLAIN_MESSAGE);
            try {
                double discount = Double.parseDouble(discountStr);
                if (discount <= 0 || discount > 100) {
                    throw new IllegalArgumentException("Invalid discount percentage.");
                }
                for (int row : selectedRows) {
                    int modelRow = productTable.convertRowIndexToModel(row);
                    String productName = (String) tableModel.getValueAt(modelRow, 0);
                    double price = (double) tableModel.getValueAt(modelRow, 2);
                    tableModel.setValueAt(price * (1 - discount / 100), modelRow, 2);
                    presentationController.applyDiscountToProduct(productName, discount);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid discount percentage. Please correct it.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select at least one product to apply discount.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Displays a dialog showing the similarities between products.
     */
    protected void showSimilaritiesDialog() {
        List<TupleType> productList = new ArrayList<>();
        // Add all products to the list
        for (int i = 0; i < allProducts.size(); i++) {
            productList.add(allProducts.get(i));
        }
        new SimilaritiesDialog(this, productList).setVisible(true);
    }

    /**
     * Filters the products based on the search text and selected filter criteria.
     * Updates the table to display only the filtered products.
     */
    protected void filterProducts() {
        String searchText = searchField.getText().trim().toLowerCase();
        String filter = (String) filterComboBox.getSelectedItem();

        Set<TupleType> filteredProducts = new HashSet<>();
        for (int i = 0; i < allProducts.size(); i++) {
            TupleType product = allProducts.get(i);
            if (searchText.isEmpty() || product.get(0).toString().toLowerCase().contains(searchText) || product.get(1).toString().toLowerCase().contains(searchText)) {
                filteredProducts.add(product);
            }
        }

        if (filter != null) {
            switch (filter) {
                case "Alphabetical Ascending":
                    filteredProducts = filteredProducts.stream()
                            .sorted(Comparator.comparing(p -> p.get(0).toString().toLowerCase()))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    break;
                case "Alphabetical Descending":
                    filteredProducts = filteredProducts.stream()
                            .sorted(Comparator.comparing((TupleType p) -> p.get(0).toString().toLowerCase()).reversed())
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    break;
                case "Category":
                    filteredProducts = filteredProducts.stream()
                            .sorted(Comparator.comparing((TupleType p) -> p.get(1).toString().toLowerCase())
                                    .thenComparing(p -> p.get(0).toString().toLowerCase())) // Sort by category, then by name
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    break;
                case "Price Ascending":
                    filteredProducts = filteredProducts.stream()
                            .sorted(Comparator.comparing(p -> Double.valueOf(p.get(2).toString())))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    break;
                case "Price Descending":
                    filteredProducts = filteredProducts.stream()
                            .sorted(Comparator.comparing(p -> Double.valueOf(((TupleType) p).get(2).toString())).reversed())
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    break;
                default:
                    break;
            }
        }

        tableModel.setRowCount(0);

        for (TupleType product : filteredProducts) {
            tableModel.addRow(new Object[]{
                    product.get(0), // Name
                    product.get(1), // Category
                    product.get(2), // Price
                    product.get(3), // Original Price
                    product.get(4)  // Amount
            });
        }
    }

    /**
     * Adds the selected products to the table.
     * Fetches product details by name and adds them to the table model.
     *
     * @param products the list of product (each product as TupleType) to add
     */
    protected void addProductsToTable(List<TupleType> products) {
        for (TupleType product : products) {
            allProducts.add(product);
            tableModel.addRow(new Object[]{
                    product.get(0), // Name
                    product.get(1), // Category
                    product.get(2), // Price
                    product.get(3), // Original Price
                    product.get(4)  // Amount
            });
            similarityMap.put(product, new HashMap<>());
        }
    }

    /**
     * Deletes a product from the table by its name.
     * Removes the product from the table model and updates the similarity map.
     *
     * @param productName the name of the product to delete
     */
    protected void deleteProductFromTable(String productName) {
        for (int i = 0; i < allProducts.size(); i++) {
            TupleType product = allProducts.get(i);
            if (product.get(0).equals(productName)) {
                allProducts.remove(i);
                tableModel.removeRow(i);
                similarityMap.remove(product);
                for (Map<TupleType, Double> similarities : similarityMap.values()) {
                    similarities.remove(product);
                }
                break;
            }
        }
    }
    /**
     * Sets the back button listener to hide the current view and show the previous view.
     */
    protected void setBackButtonListener() {
        backButton.addActionListener(_ -> {
            hideView();
            presentationController.menuView();
        });
    }
    protected abstract void updateTables();

    /**
     * Displays the view.
     */
    public void showView() {
        updateTables();
        this.setVisible(true);
    }

    /**
     * Hides the view.
     */
    public void hideView() {
        this.setVisible(false);
    }
}