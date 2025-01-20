package org.presentation.views;

import org.domain.exceptions.ProductException;
import org.domain.exceptions.ProductListException;
import org.domain.exceptions.ShelfException;
import org.presentation.controllers.PresentationController;
import org.domain.types.TupleType;
import org.presentation.utils.DesignUtils;

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

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * ProductListMenuView
 * <p>
 * This class represents the view for managing product lists. It provides functionalities to display, add, delete, and filter product lists.
 * It also allows adding and deleting products within a selected product list.
 * </p>
 */
public class ProductListMenuView extends JFrame {
    private final JTable productListTable;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JComboBox<String> filterComboBox;
    private final JButton showListButton;
    private final JButton addListButton;
    private final JButton deleteListButton;
    private final JButton backButton;

    private Set<TupleType> allProductLists;

    private final PresentationController presentationController;

    /**
     * Constructs a new ProductListMenuView.
     * Initializes the components and sets up the layout and event listeners.
     */
    public ProductListMenuView() {
        presentationController = PresentationController.getInstance();
        allProductLists = new HashSet<>();
        try {
            allProductLists = presentationController.getAllLists();
        } catch (ProductListException e) {
            JOptionPane.showMessageDialog(ProductListMenuView.this, e.getMessage(), "Error, there's no list", JOptionPane.ERROR_MESSAGE);
        }

        setTitle("Product List Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        searchField = new JTextField(20);
        filterComboBox = new JComboBox<>(new String[]{
                "Alphabetical Ascending", "Alphabetical Descending",
                "Date Ascending", "Date Descending"
        });
        filterComboBox.setSelectedItem("Alphabetical Ascending");

        showListButton = new JButton("Show List");
        addListButton = new JButton("Add List");
        deleteListButton = new JButton("Delete List(s)");
        backButton = new JButton("Back");

        DesignUtils.configureTextField(searchField);
        DesignUtils.configureComboBox(filterComboBox);
        DesignUtils.configureButton(showListButton);
        DesignUtils.configureButton(addListButton);
        DesignUtils.configureButton(deleteListButton);
        DesignUtils.configureButton(backButton);

        tableModel = new DefaultTableModel(new Object[]{"List Name", "Category", "Date Last Modified"}, 0);
        productListTable = new JTable(tableModel);
        productListTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        DesignUtils.configureTable(productListTable);

        JPanel filterPanel = new JPanel();
        JLabel searchLabel = new JLabel("Search:");
        JLabel filterLabel = new JLabel("Filter by:");
        DesignUtils.configureLabel(searchLabel);
        DesignUtils.configureLabel(filterLabel);
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);
        filterPanel.add(showListButton);
        filterPanel.add(addListButton);
        filterPanel.add(deleteListButton);

        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(productListTable), BorderLayout.CENTER);

        updateProductLists(allProductLists);

        showListButton.addActionListener(new ShowListButtonListener());
        addListButton.addActionListener(new AddListButtonListener());
        deleteListButton.addActionListener(new DeleteListButtonListener());
        backButton.addActionListener(_ -> {
            this.hideView();
            presentationController.menuView();
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterProductLists(); }
            public void removeUpdate(DocumentEvent e) { filterProductLists(); }
            public void changedUpdate(DocumentEvent e) { filterProductLists(); }
        });

        filterComboBox.addActionListener(_ -> filterProductLists());

        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 0) {
                    String newName = (String) tableModel.getValueAt(row, column);
                    TupleType productList = (TupleType) allProductLists.toArray()[row];
                    productList.add(newName);
                    tableModel.setValueAt(productList.get(2), row, 1);
                    // TODO: Notify the presentation controller about the list name change
                    // PresentationController.updateProductListName(productList, newName);
                }
            }
        });

        // Add the back button to the bottom of the frame
        add(backButton, BorderLayout.SOUTH);

        // Adjust the window size to fit all components
        pack();
        setLocationRelativeTo(null);

        // Filter the product lists based on the search text and selected filter criteria
        filterProductLists();
    }

    /**
     * Displays the product list menu view.
     */
    public void showView() {
        setVisible(true);
    }

    /**
     * Hides the product list menu view.
     */
    public void hideView() {
        setVisible(false);
    }

    /**
     * Listener for the "Show List" button.
     * Displays the selected product list in a new view.
     */
    private class ShowListButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = productListTable.getSelectedRow();
            // Check if a list is selected
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(ProductListMenuView.this, "Please select a list to show.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Check if multiple lists are selected
            if (productListTable.getSelectedRowCount() > 1) {
                JOptionPane.showMessageDialog(ProductListMenuView.this, "Please select only one list to show.", "Multiple Selections", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String listName = (String) tableModel.getValueAt(selectedRow, 0);

            // Fetch the list from the presentation controller and display it in a new view
            try {
                ArrayList<TupleType> list = presentationController.getList(listName);
                ProductListView productListView = new ProductListView(listName, list);
                ProductListMenuView.this.hideView();
                productListView.showView();
            } catch (ProductException | ProductListException ex) {
                JOptionPane.showMessageDialog(ProductListMenuView.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Listener for the "Add List" button.
     * Displays a dialog to add a new product list.
     */
    private class AddListButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel panel = new JPanel(new GridLayout(2, 2));
            JTextField listNameField = new JTextField(20);
            JTextField categoryField = new JTextField(20);
            panel.add(new JLabel("List Name:"));
            panel.add(listNameField);
            panel.add(new JLabel("Category:"));
            panel.add(categoryField);

            int result = JOptionPane.showConfirmDialog(ProductListMenuView.this, panel, "Add List", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String listName = listNameField.getText().trim();
                String category = categoryField.getText().trim();
                if (!listName.isEmpty() && !category.isEmpty()) {
                    try {
                        presentationController.addProductList(listName, category);
                        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        TupleType emptyProductsList = new TupleType();
                        TupleType newList = new TupleType(listName, category, date, emptyProductsList);
                        allProductLists.add(newList);
                        updateProductLists(allProductLists);
                        filterProductLists();   // Reapply filter after adding a new list
                    }
                    catch (ProductListException ex) {
                        JOptionPane.showMessageDialog(ProductListMenuView.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(ProductListMenuView.this, "Invalid list name or category.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Listener for the "Delete List(s)" button.
     * Deletes the selected product lists after confirmation.
     */
    private class DeleteListButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] selectedRows = productListTable.getSelectedRows();
            if (selectedRows.length > 0) {
                int confirmation = JOptionPane.showConfirmDialog(ProductListMenuView.this, "Are you sure you want to delete the selected list(s)?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    List<TupleType> listsToDelete = new ArrayList<>();
                    Set<String> listNamesToDelete = new HashSet<>();
                    for (int row : selectedRows) {
                        String listName = (String) tableModel.getValueAt(row, 0);
                        for (TupleType list : allProductLists) {
                            if (list.get(0).equals(listName)) {
                                listsToDelete.add(list);
                                listNamesToDelete.add(listName);
                                break;
                            }
                        }
                    }
                    // TODO: Notice the presentation controller about the deletion of the list
                    try {
                        presentationController.deleteProductLists(listNamesToDelete);
                        allProductLists.removeIf(list -> listsToDelete.contains(list.get(0)));
                        updateProductLists(allProductLists);
                        filterProductLists();   // Reapply filter after deleting lists
                    } catch (ProductListException | ShelfException ex) {
                        JOptionPane.showMessageDialog(ProductListMenuView.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Remove the lists from the allProductLists variable
                    for (TupleType listToDelete : listsToDelete) {
                        allProductLists.removeIf(list -> list.get(0).equals(listToDelete.get(0)));
                    }
                }
            } else {
                JOptionPane.showMessageDialog(ProductListMenuView.this, "Please select at least one list to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Updates the product lists displayed in the table.
     *
     * @param productLists the set of product lists to display
     */
    private void updateProductLists(Set<TupleType> productLists) {
        allProductLists = new HashSet<>(productLists);

        // Clear the table model
        tableModel.setRowCount(0);

        // Add the lists to the table model
        for (TupleType productList : allProductLists) {
            tableModel.addRow(new Object[]{productList.get(0), productList.get(1), productList.get(2)});
        }
    }

    /**
     * Filters the product lists based on the search text and selected filter criteria.
     * Updates the product list table to display only the filtered product lists.
     */
    private void filterProductLists() {
        String searchText = searchField.getText().trim().toLowerCase();
        String filter = (String) filterComboBox.getSelectedItem();

        List<TupleType> filteredProductLists = new ArrayList<>();
        for (TupleType productList : allProductLists) {
            if (searchText.isEmpty() || productList.get(0).toString().toLowerCase().contains(searchText)) {
                filteredProductLists.add(productList);
            }
        }

        if (filter != null) {
            switch (filter) {
                case "Alphabetical Ascending":
                    filteredProductLists.sort(Comparator.comparing(p -> p.get(0).toString().toLowerCase()));
                    break;
                case "Alphabetical Descending":
                    filteredProductLists.sort(Comparator.comparing((TupleType p) -> p.get(0).toString().toLowerCase()).reversed());
                    break;
                case "Date Ascending":
                    filteredProductLists.sort(Comparator.comparing(p -> p.get(2).toString()));
                    break;
                case "Date Descending":
                    filteredProductLists.sort(Comparator.comparing(p -> ((TupleType) p).get(2).toString()).reversed());
                    break;
                default:
                    break;
            }
        }

        // Update the table with the filtered product lists without modifying allProductLists
        tableModel.setRowCount(0);
        for (TupleType productList : filteredProductLists) {
            tableModel.addRow(new Object[]{productList.get(0), productList.get(1), productList.get(2)});
        }
    }
}