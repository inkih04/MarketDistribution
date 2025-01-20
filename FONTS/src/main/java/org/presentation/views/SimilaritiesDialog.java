package org.presentation.views;
// Types
import org.domain.types.TupleType;
// Design Utils
import org.presentation.utils.DesignUtils;
// Swing
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;

// Utils
import java.util.ArrayList;
import java.util.List;

/**
 * SimilaritiesDialog
 * <p>
 * This class represents a dialog that displays the similarities between products.
 * It shows a table where each cell represents the similarity score between two products.
 * The similarity score is a value between 0.0 and 1.0, where 1.0 means the products are identical.
 * </p>
 */
public class SimilaritiesDialog extends JDialog {
    /**
     * Constructs a new SimilaritiesDialog.
     * Initializes the components and sets up the layout.
     *
     * @param parent the parent frame of this dialog
     * @param allProducts the list of all products to display similarities for
     */
    public SimilaritiesDialog(JFrame parent, List<TupleType> allProducts) {
        super(parent, "Product Similarities", true);
        setLayout(new BorderLayout());

        // Get the list of product names
        List<String> productNames = new ArrayList<>();
        for (TupleType product : allProducts) {
            productNames.add(product.get(0).toString());
        }

        // Create the table model with an extra column for row headers
        DefaultTableModel similaritiesTableModel = new DefaultTableModel(productNames.size(), productNames.size() + 1) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Set the column headers
        Object[] columnIdentifiers = new Object[productNames.size() + 1];
        columnIdentifiers[0] = "";
        for (int i = 0; i < productNames.size(); i++) {
            columnIdentifiers[i + 1] = productNames.get(i);
        }
        similaritiesTableModel.setColumnIdentifiers(columnIdentifiers);

        // Fill the table with similarity values
        for (int i = 0; i < productNames.size(); i++) {
            similaritiesTableModel.setValueAt(productNames.get(i), i, 0); // Set row header
            TupleType product1 = allProducts.get(i);
            List<TupleType> similarities = (List<TupleType>) product1.get(5);   // Get the similarities list from each product
            for (int j = 0; j < productNames.size(); j++) {
                if (i == j) {
                    similaritiesTableModel.setValueAt("1.0", i, j + 1); // Similarity with itself
                } else {
                    boolean found = false;
                    for (TupleType similarity : similarities) {
                        if (similarity.get(0).toString().equals(productNames.get(j))) {
                            similaritiesTableModel.setValueAt(similarity.get(1).toString(), i, j + 1);
                            found = true;
                            break;
                        }
                    }
                    // If no similarity was found, set the value to 0.0
                    if (!found) {
                        similaritiesTableModel.setValueAt("0.0", i, j + 1);
                    }
                }
            }
        }

        JTable similaritiesTable = new JTable(similaritiesTableModel);
        DesignUtils.configureTable(similaritiesTable);

        // Adjust column widths to fit content and set minimum width
        int totalWidth = 0;
        for (int column = 0; column < similaritiesTable.getColumnCount(); column++) {
            TableColumn tableColumn = similaritiesTable.getColumnModel().getColumn(column);
            tableColumn.setMinWidth(70); // Set minimum width to 70 pixels
            int preferredWidth = tableColumn.getMinWidth();
            for (int row = 0; row < similaritiesTable.getRowCount(); row++) {
                int cellWidth = similaritiesTable.getCellRenderer(row, column)
                        .getTableCellRendererComponent(similaritiesTable, similaritiesTable.getValueAt(row, column), false, false, row, column)
                        .getPreferredSize().width;
                preferredWidth = Math.max(preferredWidth, cellWidth);
            }
            tableColumn.setPreferredWidth(preferredWidth);
            totalWidth += preferredWidth;
        }

        // Calculate total height
        int totalHeight = similaritiesTable.getRowHeight() * similaritiesTable.getRowCount();

        // Add the table to a scroll pane and the scroll pane to the dialog
        JScrollPane scrollPane = new JScrollPane(similaritiesTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        DesignUtils.configureButton(closeButton);
        closeButton.addActionListener(_ -> dispose());
        add(closeButton, BorderLayout.SOUTH);

        // Adjust the window size to fit all components
        setSize(totalWidth + 50, totalHeight + 100); // Add some padding
        setResizable(true); // Allow resizing the window
        setLocationRelativeTo(null); // Set the window to the center of the screen
    }
}