package org.presentation.views;

import org.domain.exceptions.ProductListException;
import org.domain.exceptions.ShelfException;
import org.domain.types.TupleType;
import org.presentation.controllers.PresentationController;
import org.presentation.utils.DesignUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The ShelfView class represents the GUI for managing shelves.
 * It allows users to create, delete, and manage distributions of shelves, along with the ProductLists associated to it and its AbstractAlgorithm.
 * @author Víctor Llorens Ramos (victor.llorens@estudiantat.upc.edu)
 * @version 1.0
 */
public class ShelfView extends JFrame {

    private final PresentationController presentationController = PresentationController.getInstance();
    private final ArrayList<TupleType> shelves;

    private final JTable table;
    private final JButton deleteButton;
    private final JButton manageDistributionButton;
    private final JTextField searchBar;
    private final JComboBox<String> filterComboBox;
    private final TableRowSorter<DefaultTableModel> rowSorter;

    /**
     * Constructs a new ShelfView.
     */
    public ShelfView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);
        setTitle("Manage Shelves");

        JPanel northPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("MANAGE SHELVES", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Search bar, filter and buttons
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding

        JLabel searchLabel = new JLabel("Search:");
        JLabel filterLabel = new JLabel("Filter by:");
        DesignUtils.configureLabel(searchLabel);
        DesignUtils.configureLabel(filterLabel);
        searchBar = new JTextField();
        DesignUtils.configureTextField(searchBar);
        filterComboBox = new JComboBox<>(new String[]{"id", "xsize", "ysize", "productList"});
        DesignUtils.configureComboBox(filterComboBox);

// Set the preferred size of the search bar to match the height of the filter combo box
        Dimension comboBoxSize = filterComboBox.getPreferredSize();
        searchBar.setPreferredSize(new Dimension(searchBar.getPreferredSize().width, comboBoxSize.height));

// Add components to the search panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(searchLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        searchPanel.add(searchBar, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        searchPanel.add(filterLabel, gbc);

        gbc.gridx = 3;
        searchPanel.add(filterComboBox, gbc);

        JPanel filterPanel = new JPanel(new FlowLayout());
        JButton createButton = new JButton("Create");
        manageDistributionButton = new JButton("Manage Distributions");
        deleteButton = new JButton("Delete");
        DesignUtils.configureButton(createButton);
        createButton.setPreferredSize(new Dimension(150, 30));
        DesignUtils.configureButton(manageDistributionButton);
        manageDistributionButton.setPreferredSize(new Dimension(200, 30));
        DesignUtils.configureButton(deleteButton);
        deleteButton.setPreferredSize(new Dimension(150, 30));
        deleteButton.setEnabled(false);
        manageDistributionButton.setEnabled(false);

        filterPanel.add(createButton);
        filterPanel.add(manageDistributionButton);
        filterPanel.add(deleteButton);

        gbc.gridx = 4;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        searchPanel.add(filterPanel, gbc);

        northPanel.add(titleLabel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // Table Styling
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"id", "xsize", "ysize", "productList"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 1 && column != 2;
            }
        };

        table = new JTable(tableModel);
        DesignUtils.configureTable(table); // Apply the table styling
        // Set the selection mode to single selection
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        searchBar.getDocument().addDocumentListener(new MyDocumentListener());
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        shelves = getShelves();
        if (shelves != null) {
            for (TupleType shelf : shelves) {
                tableModel.addRow(new Object[]{shelf.get(0), shelf.get(1), shelf.get(2), shelf.get(3)});
            }
            table.getColumnModel().getColumn(3).setCellEditor(new ComboBoxCellEditor(new JComboBox<>(getProductListNames())));
        }

        JScrollPane scrollPane = new JScrollPane(table);
        DesignUtils.configureScrollPane(scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton turnBackButton = new JButton("Back");
        DesignUtils.configureButton(turnBackButton);

        bottomPanel.add(turnBackButton, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        createButton.addActionListener(new CreateListener());
        deleteButton.addActionListener(new DeleteListener());
        manageDistributionButton.addActionListener(new ManageDistributionsListener());
        turnBackButton.addActionListener(new TurnBackListener());

        table.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = table.getSelectedRow() != -1;
            deleteButton.setEnabled(rowSelected);
            manageDistributionButton.setEnabled(rowSelected);
        });
    }

    /**
     * Displays the view.
     */
    public void showView() {
        setVisible(true);
    }

    /**
     * Hides the view and disposes of its resources.
     */
    public void hideView() {
        setVisible(false);
        dispose();
    }

    /**
     * Retrieves the names of all product lists.
     *
     * @return an array of product list names
     */
    private String[] getProductListNames() {
        ArrayList<TupleType> lists = null;
        try {
            lists = new ArrayList<>(presentationController.getAllLists());
        } catch (Exception e) {
            errorMessageView(e.getMessage());
        }

        if (lists != null) {
            String[] listNames = new String[lists.size()];
            for (int i = 0; i < lists.size(); i++) {
                TupleType list = lists.get(i);
                listNames[i] = (String) list.get(0);
            }
            return listNames;
        }
        return new String[]{};
    }

    /**
     * Retrieves all shelves.
     *
     * @return an ArrayList of TupleType representing the shelves
     */
    private ArrayList<TupleType> getShelves() {
        ArrayList<TupleType> result = null;
        try {
            result = new ArrayList<>(presentationController.getAllShelves());
        } catch (ShelfException e) {
            errorMessageView(e.getMessage());
        }
        return result;
    }

    /**
     * Displays an error message dialog with the given message.
     *
     * @param message the error message to display
     */
    private void errorMessageView(String message) {
        JDialog dialog = new JDialog();
        DesignUtils.configureDialog(dialog);
        JLabel errorMessage = new JLabel(message);
        DesignUtils.configureErrorMessage(errorMessage);
        JOptionPane.showMessageDialog(dialog, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Document listener for filtering the table based on search input.
     */
    private class MyDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            filterTable();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            filterTable();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            filterTable();
        }

        /**
         * Filters the table based on the search input.
         */
        private void filterTable() {
            String text = searchBar.getText();
            int columnIndex = filterComboBox.getSelectedIndex();
            if (text.trim().isEmpty()) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, columnIndex));
            }
        }
    }

    /**
     * Action listener for creating a new shelf.
     */
    private class CreateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            PresentationController presentationController = PresentationController.getInstance();
            hideView();
            presentationController.createShelfView();
        }
    }

    /**
     * Action listener for deleting a selected shelf.
     */
    private class DeleteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) table.getValueAt(selectedRow, 0);
                JDialog dialog = new JDialog();
                DesignUtils.configureDialog(dialog);
                int response = JOptionPane.showConfirmDialog(
                        dialog,
                        "Are you sure you want to delete this shelf?",
                        "Delete confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        presentationController.deleteShelf(id);
                    } catch (ShelfException ex) {
                        throw new RuntimeException(ex);
                    }
                    ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
                }
            }
        }
    }

    /**
     * Action listener for managing distributions of a selected shelf.
     */
    private class ManageDistributionsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) table.getValueAt(selectedRow, 0);
                PresentationController presentationController = PresentationController.getInstance();
                hideView();
                presentationController.manageDistributionsView(id);
            }
        }
    }

    /**
     * Action listener for navigating back to the previous view.
     */
    private class TurnBackListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            PresentationController presentationController = PresentationController.getInstance();
            hideView();
            presentationController.menuView();
        }
    }

    /**
     * Custom cell editor for JComboBox in the table.
     */
    private class ComboBoxCellEditor extends DefaultCellEditor {
        public ComboBoxCellEditor(JComboBox<String> comboBox) {
            super(comboBox);
            comboBox.addActionListener(e -> {
                int row = table.getEditingRow();
                int column = table.getEditingColumn();
                if (column == 3) {
                    String selectedValue = (String) comboBox.getSelectedItem();
                    try {
                        for (TupleType shelf : shelves) {
                            if (shelf.get(0).equals(table.getValueAt(row, 0)) && (shelf.get(3) != selectedValue)) {
                                int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to change the product list? (This will delete all the previous distributions assigned to this shelf)", "Confirmation", JOptionPane.YES_NO_OPTION);
                                if (response == JOptionPane.YES_OPTION) presentationController.updateShelfProductList((int) table.getValueAt(row, 0), String.valueOf(selectedValue));
                                break;
                            }
                        }
                    } catch (ShelfException | ProductListException ex) {
                        errorMessageView(ex.getMessage());
                    }
                }
            });

            DesignUtils.configureComboBox(comboBox);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            JComboBox<String> comboBox = (JComboBox<String>) super.getTableCellEditorComponent(table, value, isSelected, row, column);
            comboBox.setSelectedItem(value);
            return comboBox;
        }
    }
}