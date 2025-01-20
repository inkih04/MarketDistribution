package org.presentation.views;

import org.domain.types.TupleType;
import org.presentation.controllers.PresentationController;
import org.presentation.utils.DesignUtils;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.util.ArrayList;

/**
 * This class represents the view for displaying the operation log.
 */
public class OperationLogView extends JFrame {

    private final JTable logTable;
    private final DefaultTableModel tableModel;
    private final JButton backButton;
    private final PresentationController presentationController;

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 600;

    /**
     * Initializes a new instance of the {@code OperationLogView} class.
     */
    public OperationLogView() {
        presentationController = PresentationController.getInstance();
        setTitle("Operation Log");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize components
        tableModel = new DefaultTableModel(new Object[]{"Date", "Operation"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        logTable = new JTable(tableModel);
        DesignUtils.configureTable(logTable);

        JScrollPane scrollPane = new JScrollPane(logTable);
        add(scrollPane, BorderLayout.CENTER);

        backButton = new JButton("Back");
        DesignUtils.configureButton(backButton);
        backButton.addActionListener(_ -> {
            this.hideView();
            new MenuView().showView();
        });

        add(backButton, BorderLayout.SOUTH);

        // Load operation log data
        loadOperationLog();

        // Adjust the window size to fit all components
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false); // Prevent resizing the window
        setLocationRelativeTo(null); // Center the window
    }

    /**
     * Loads the operation log data into the table.
     */
    public void loadOperationLog() {
        // Clear the table
        tableModel.setRowCount(0);

        ArrayList<TupleType> operationLog = presentationController.getOperationLog();
        for (int i = 0; i < operationLog.size(); i++) {
            TupleType logEntry = operationLog.get(i);
            tableModel.addRow(new Object[]{logEntry.get(0).toString(), logEntry.get(1).toString()});
        }
    }

    /**
     * Displays the operation log view.
     */
    public void showView() {
        setVisible(true);
    }

    /**
     * Hides the operation log view.
     */
    public void hideView() {
        setVisible(false);
    }
}