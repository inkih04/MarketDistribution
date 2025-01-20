package org.presentation.views;
// Exceptions and types
import org.domain.exceptions.ProductException;
import org.domain.types.Pair;
import org.domain.types.TupleType;
// Design Utils
import org.presentation.utils.DesignUtils;
// Swing
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
// AwT
import java.awt.BorderLayout;
import java.awt.GridLayout;
// Utils
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The ProductCatalogView class represents the view for displaying and managing the product catalog.
 * It provides functionality to add new products to the catalog and update the product tables.
 * It extends the BasicView class and implements the showAddProductDialog and updateTables methods.
 * @see org.presentation.views.BasicView
 * @see org.presentation.views.AddProductView
 * @see org.presentation.views.ProductListView
 * @author Sergio Shmyhelskyy Yaskevych (sergio.shmyhelskyy@estudiantat.upc.edu)
 */
public class ProductCatalogView extends BasicView {

    /**
     * Constructs a ProductCatalogView and initializes the product catalog.
     */
    public ProductCatalogView() {
        super("Product Catalog");
        updateTables();
    }

    /**
     * Displays a dialog for adding a new product to the catalog.
     * Collects product details from the user and adds the product to the catalog.
     */
    @Override
    public void showAddProductDialog() {
        JDialog addProductDialog = new JDialog(this, "Add Product", true);
        addProductDialog.setSize(600, 700);
        addProductDialog.setLayout(new BorderLayout());
        addProductDialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel categoryLabel = new JLabel("Category:");
        JTextField categoryField = new JTextField();
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();

        // Configure the labels and text fields with the design settings
        DesignUtils.configureLabel(nameLabel);
        DesignUtils.configureTextField(nameField);
        DesignUtils.configureLabel(categoryLabel);
        DesignUtils.configureTextField(categoryField);
        DesignUtils.configureLabel(priceLabel);
        DesignUtils.configureTextField(priceField);
        DesignUtils.configureLabel(amountLabel);
        DesignUtils.configureTextField(amountField);

        // Add the labels and text fields to the input panel
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryField);
        inputPanel.add(priceLabel);
        inputPanel.add(priceField);
        inputPanel.add(amountLabel);
        inputPanel.add(amountField);

        JPanel similarityPanel = new JPanel(new GridLayout(allProducts.size(), 2, 10, 10));
        Map<TupleType, JTextField> similarityFields = new HashMap<>();
        for (int i = 0; i < allProducts.size(); i++) {
            TupleType product = allProducts.get(i);
            JLabel similarityLabel = new JLabel("Similarity with " + product.get(0) + ":");
            JTextField similarityField = new JTextField();
            DesignUtils.configureLabel(similarityLabel);
            DesignUtils.configureTextField(similarityField);
            similarityFields.put(product, similarityField);
            similarityPanel.add(similarityLabel);
            similarityPanel.add(similarityField);
        }

        JScrollPane scrollPane = new JScrollPane(similarityPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        // Configure the buttons with the design settings
        DesignUtils.configureButton(addButton);
        DesignUtils.configureButton(cancelButton);

        addButton.addActionListener(_ -> {
            try {
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                double price = Double.parseDouble(priceField.getText());
                int amount = Integer.parseInt(amountField.getText());

                if (name.isEmpty() || category.isEmpty() || price <= 0 || amount <= 0) {
                    throw new IllegalArgumentException("Invalid input values.");
                }

                ArrayList<Pair<String, Double>> similarities = getSimilarities(similarityFields.entrySet());

                // Notify the controller to add the product to the catalog
                presentationController.addProductToCatalog(name, category, price, amount, similarities);
                addProductDialog.dispose();
                updateTables();

            } catch (ProductException ex) {
                JOptionPane.showMessageDialog(addProductDialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addProductDialog, "Invalid input. Please correct it.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(_ -> addProductDialog.dispose());    // Close the dialog

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Add the components to the dialog
        addProductDialog.add(inputPanel, BorderLayout.NORTH);   // Add the input panel to the top
        addProductDialog.add(scrollPane, BorderLayout.CENTER);  // Add the similarity panel to the center
        addProductDialog.add(buttonPanel, BorderLayout.SOUTH);  // Add the button panel to the bottom
        addProductDialog.setVisible(true);
    }

    private  ArrayList<Pair<String, Double>> getSimilarities(Set<Map.Entry<TupleType, JTextField>> entryPairs) {
        ArrayList<Pair<String, Double>> similarities = new ArrayList<>();
        for (Map.Entry<TupleType, JTextField> entry : entryPairs) {
            double similarity = Double.parseDouble(entry.getValue().getText().trim());
            if (similarity < 0 || similarity > 1) {
                throw new IllegalArgumentException("Similarity values must be between 0 and 1.");
            }
            similarities.add(new Pair<>((String) entry.getKey().get(0), similarity));
        }
        return similarities;
    }

    @Override
    protected void deleteSelectedProducts() {
        int[] selectedRows = productTable.getSelectedRows();
        if (selectedRows.length > 0) {
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the selected product(s)?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirmation == JOptionPane.YES_OPTION) {
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int rowIndex = selectedRows[i];
                    if (rowIndex >= 0 && rowIndex < allProducts.size()) {
                        String name = (String) tableModel.getValueAt(rowIndex, 0);
                        String category = (String) tableModel.getValueAt(rowIndex, 1);
                        double price = (double) tableModel.getValueAt(rowIndex, 2);
                        double originalPrice = (double) tableModel.getValueAt(rowIndex, 3);
                        int amount = (int) tableModel.getValueAt(rowIndex, 4);

                        TupleType product = new TupleType(name, category, price, originalPrice, amount);
                        try {
                            presentationController.deleteProduct((String) product.get(0));
                            allProducts.remove(rowIndex);
                            similarityMap.remove(product);

                            for (Map<TupleType, Double> similarities : similarityMap.values()) {
                                similarities.remove(product);
                            }
                            tableModel.removeRow(rowIndex);
                        } catch (ProductException e) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    e.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "Invalid index: " + rowIndex,
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select at least one product to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    @Override
    protected void updateTables() {
        ArrayList<TupleType> updatedProducts = null;
        try {
            updatedProducts = presentationController.getProductsFromCatalog();
        } catch (ProductException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (updatedProducts == null) {
            JOptionPane.showMessageDialog(this, "No products found in the catalog.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        allProducts = updatedProducts;

        // Clear the table
        tableModel.setRowCount(0);

        // Add the filtered products to the table
        for (TupleType product : allProducts) {
            tableModel.addRow(new Object[]{
                product.get(0), // Name
                product.get(1), // Category
                product.get(2), // Price
                product.get(3), // Original Price
                product.get(4)  // Amount
            });
        }

        filterProducts(); // Apply the current filter to the updated products
    }

}