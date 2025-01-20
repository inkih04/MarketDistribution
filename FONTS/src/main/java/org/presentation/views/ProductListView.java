package org.presentation.views;

import org.domain.exceptions.ProductException;
import org.domain.exceptions.ProductListException;
import org.domain.types.TupleType;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The ProductListView class represents the view for displaying and managing a list of products.
 * It provides functionality to add products from the catalog to the list and update the product tables.
 * It extends the BasicView class and implements the showAddProductDialog and updateTables methods.
 * @see org.presentation.views.BasicView
 * @see org.presentation.views.AddProductView
 */
public class ProductListView extends BasicView {
    private final String listName;
    /**
     * Constructs a ProductListView and initializes the product list.
     *
     * @param products the initial list of products to display
     */
    public ProductListView(String listName, ArrayList<TupleType> products) {
        super("Product List - " + listName);
        this.allProducts = products;
        this.listName = listName;
        filterProducts();
    }

    /**
     * Sets the action listener for the back button.
     * When the back button is clicked, the current view is hidden and the ProductListMenuView is displayed.
     */
    @Override
    protected void setBackButtonListener() {
        backButton.addActionListener(_ -> {
            hideView();
            new ProductListMenuView().showView();
        });
    }

    /**
     * Displays a dialog for adding a new product to the list.
     * Opens the AddProductView dialog.
     */
    @Override
    protected void showAddProductDialog() {
        new AddProductView(this).setVisible(true);
    }

    /**
     * Updates the product table with the latest data from the list.
     * Fetches the product list from the presentation controller and repopulates the table model.
     */
    @Override
    protected void updateTables() {
        try {
            // Fetch the updated product list from the presentation controller
            ArrayList<TupleType> updatedProducts = presentationController.getList(listName);

            // Clear the current data in the table model
            tableModel.setRowCount(0);

            // Add all updated products to the table model
            for (TupleType product : updatedProducts) {
                tableModel.addRow(new Object[]{
                        product.get(0), // Name
                        product.get(1), // Category
                        product.get(2), // Price
                        product.get(3), // Original Price
                        product.get(4)  // Amount
                });
            }

            // Update the reference to the latest product list
            allProducts = updatedProducts;

        } catch (ProductListException | ProductException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    /**
     * Returns the name of the product list.
     *
     * @return the name of the product list
     */
    public String getListName() {
        return listName;
    }

    @Override
    protected void deleteSelectedProducts() {
        int[] selectedRows = productTable.getSelectedRows();
        if (selectedRows.length > 0) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected product(s)?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                Set<String> productsToDelete = new HashSet<>();

                // Iterate over the selected rows in reverse order to avoid index issues
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    // Get the product name from the selected row
                    String productName = (String) tableModel.getValueAt(selectedRows[i], 0);

                    // Remove the product from the list
                    productsToDelete.add(productName);
                    deleteProductFromTable(productName);
                }

                // Delete the selected products from the list
                try {
                    presentationController.deleteProductsFromList(listName, productsToDelete);
                } catch (ProductException | ProductListException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select at least one product to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
}