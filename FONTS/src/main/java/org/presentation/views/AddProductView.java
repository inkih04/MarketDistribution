package org.presentation.views;

import org.domain.exceptions.ProductException;
import org.domain.exceptions.ProductListException;
import org.domain.types.TupleType;
import org.presentation.controllers.PresentationController;
import org.presentation.utils.DesignUtils;

import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * The AddProductView class represents a dialog for adding products to a list.
 * It displays a table of products from the catalog and allows the user to select
 * products to add to the list.
 */
public class AddProductView extends JDialog {

    private final JTable catalogTable;
    private final DefaultTableModel tableModel;
    private final JButton addButton;
    private final JButton cancelButton;
    private final PresentationController presentationController;
    private final ProductListView productListView;

    /**
     * Constructs an AddProductView dialog.
     *
     * @param productListView the parent view that displays the product list
     */
    public AddProductView(ProductListView productListView) {
        super(productListView, "Add Product to List", true);
        this.productListView = productListView;
        this.presentationController = PresentationController.getInstance();
        setSize(800, 600);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(productListView);

        tableModel = new DefaultTableModel(new Object[]{"Name", "Category", "Price", "OriginalPrice", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        catalogTable = new JTable(tableModel);
        DesignUtils.configureTable(catalogTable);

        JScrollPane scrollPane = new JScrollPane(catalogTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add and cancel buttons
        addButton = new JButton("Add");
        DesignUtils.configureButton(addButton);
        addButton.addActionListener(_ -> {
            try {
                addSelectedProducts();
            } catch (ProductException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton = new JButton("Cancel");
        DesignUtils.configureButton(cancelButton);
        cancelButton.addActionListener(_ -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load the products from the catalog
        loadCatalogProducts();
    }

    /**
     * Loads the products from the catalog and populates the table.
     */
    private void loadCatalogProducts() {
        ArrayList<TupleType> catalogProducts = null;
        try {
            catalogProducts = presentationController.getProductsFromCatalog();
        } catch (ProductException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Populate the table with the catalog products if available
        for (int i = 0; i < catalogProducts.size(); i++) {
            TupleType product = catalogProducts.get(i);
            tableModel.addRow(new Object[]{
                                product.get(0),     // Name
                                product.get(1),     // Category
                                product.get(2),     // Price
                                product.get(3),     // Original Price
                                product.get(4)}     // Amount
            );
        }
    }

    /**
     * Adds the selected products from the table to the product list.
     */
    private void addSelectedProducts() throws ProductException {
        int[] selectedRows = catalogTable.getSelectedRows();
        if (selectedRows.length > 0) {
            List<TupleType> selectedProducts = new ArrayList<>();
            List<String> selectedProductNames = new ArrayList<>();
            for (int row : selectedRows) {
                TupleType product = presentationController.getProductByName((String) catalogTable.getValueAt(row, 0));
                selectedProducts.add(product);  // Add the product to the list
                selectedProductNames.add((String) product.get(0));  // Add the product name to the list
            }
            List<TupleType> successfullyAddedProducts = new ArrayList<>();

            // Add the selected products to the product list
            for (int i = 0; i < selectedProducts.size(); i++) {
                // Attempt to add the product to the list
                try {
                    presentationController.addProductToList(productListView.getListName(), selectedProductNames.get(i));
                    successfullyAddedProducts.add(selectedProducts.get(i));
                } catch (ProductException | ProductListException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            // Add the successfully added products to the product list view
            productListView.addProductsToTable(successfullyAddedProducts);
            dispose();  // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "Please select at least one product to add.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
}