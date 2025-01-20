package org.presentation.views;

import org.domain.exceptions.ShelfException;
import org.domain.types.Pair;
import org.presentation.controllers.PresentationController;
import org.presentation.utils.DesignUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * The ShowDistributionView class represents the GUI for displaying a distribution.
 * It allows users to view and interact with one of the distributions of a shelf.
 * Users can drag and drop cells to swap their values.
 * @author Víctor Llorens Ramos (victor.llorens@estudiantat.upc.edu)
 * @version 1.0
 */
public class ShowDistributionView extends JFrame {

    private final JTable table;
    private final String nameDistribution;

    /**
     * Constructs a ShowDistributionView instance with a specified shelf ID and distribution name.
     * @param idShelf The ID of the shelf containing the distribution.
     * @param nameDistribution The name of the distribution to display.
     */
    public ShowDistributionView(int idShelf, String nameDistribution) {
        ArrayList<ArrayList<String>> distribution = getDistribution(idShelf, nameDistribution);
        this.nameDistribution = nameDistribution;

        setTitle("Show Distribution");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a table model with a variable number of rows and columns
        DefaultTableModel tableModel = new DefaultTableModel(0, distribution.get(0).size());
        table = new JTable(tableModel);
        DesignUtils.configureTable(table);

        for (ArrayList<String> row : distribution) {
            tableModel.addRow(row.toArray());
        }

        // Remove the table header
        table.setTableHeader(null);

        // Set selection mode to single cell selection
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(true);

        // Enable drag and drop
        table.setDragEnabled(true);
        table.setTransferHandler(new TableTransferHandler());

        // Adjust column widths
        adjustColumnWidths(table);

        // Set auto resize mode to off
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        DesignUtils.configureScrollPane(scrollPane);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Calculate preferred size for the scroll pane
        Dimension tableSize = table.getPreferredSize();
        Dimension preferredSize = new Dimension(
                Math.min(tableSize.width, 800),
                Math.min(tableSize.height, 600)
        );
        scrollPane.setPreferredSize(preferredSize);
        setMinimumSize(new Dimension(400, 300));

        // Add the scroll pane to the frame
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton closeButton = new JButton("Close");
        DesignUtils.configureButton(closeButton);
        closeButton.addActionListener(new CloseListener());
        buttonPanel.add(closeButton, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Pack the frame to fit the table size
        setLocationRelativeTo(null);
        pack();
    }

    /**
     * Adjusts the column widths of a table to fit the content.
     * @param table The table to adjust.
     */
    private void adjustColumnWidths(JTable table) {
        for (int col = 0; col < table.getColumnCount(); col++) {
            int maxWidth = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, col);
                Component comp = table.prepareRenderer(renderer, row, col);
                maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
            }
            table.getColumnModel().getColumn(col).setPreferredWidth(maxWidth + 10);
        }
    }

    /**
     * Retrieves the distribution of a shelf with a specified ID and name of a distribution.
     * @param idShelf The ID of the shelf containing the distribution.
     * @param nameDistribution The name of the distribution to retrieve.
     * @return The distribution as a list of lists of strings.
     */
    private ArrayList<ArrayList<String>> getDistribution(int idShelf, String nameDistribution) {
        PresentationController presentationController = PresentationController.getInstance();
        ArrayList<ArrayList<String>> distribution = null;
        try {
            distribution = presentationController.getDistribution(idShelf, nameDistribution);
        } catch (ShelfException e) {
            errorMessageView(e.getMessage());
        }
        return distribution;
    }

    /**
     * Swaps the values of two cells in the table.
     * @param cell1 The coordinates of the first cell.
     * @param cell2 The coordinates of the second cell.
     */
    private void swapCells(Point cell1, Point cell2) {
        String value1 = (String) table.getValueAt(cell1.x, cell1.y);
        String value2 = (String) table.getValueAt(cell2.x, cell2.y);
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to swap these products?\n" + value1 + "\n" + value2, "Swap Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            PresentationController presentationController = PresentationController.getInstance();
            presentationController.swapProductsInDistribution(nameDistribution, new Pair<>(cell1.x, cell1.y), new Pair<>(cell2.x, cell2.y));
            table.setValueAt(value2, cell1.x, cell1.y);
            table.setValueAt(value1, cell2.x, cell2.y);
        }
    }

    /**
     * Displays the view.
     */
    public void showView() {
        setVisible(true);
    }

    /**
     * Hides the view.
     */
    public void hideView() {
        setVisible(false);
        dispose();
    }

    /**
     * Displays an error message dialog with a specified message.
     * @param message The message to display.
     */
    private void errorMessageView(String message) {
        JDialog dialog = new JDialog();
        DesignUtils.configureDialog(dialog);
        JLabel errorMessage = new JLabel(message);
        DesignUtils.configureErrorMessage(errorMessage);
        JOptionPane.showMessageDialog(dialog, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Closes the view when the close button is clicked.
     */
    private class CloseListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            hideView();
        }
    }

    /**
     * Handles drag and drop operations on the table.
     */
    private class TableTransferHandler extends TransferHandler {
        private int sourceRow;
        private int sourceCol;

        @Override
        protected Transferable createTransferable(JComponent c) {
            sourceRow = table.getSelectedRow();
            sourceCol = table.getSelectedColumn();
            return new StringSelection((String) table.getValueAt(sourceRow, sourceCol));
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            try {
                String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                JTable.DropLocation dropLocation = (JTable.DropLocation) support.getDropLocation();
                int targetRow = dropLocation.getRow();
                int targetCol = dropLocation.getColumn();

                swapCells(new Point(sourceRow, sourceCol), new Point(targetRow, targetCol));
                return true;
            } catch (Exception e) {
                errorMessageView("An error occurred while importing data.");
            }
            return false;
        }
    }
}