package org.presentation.views;

import org.domain.exceptions.ShelfException;
import org.domain.types.TupleType;
import org.presentation.controllers.PresentationController;
import org.presentation.utils.DesignUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The ManageDistributionsView class represents the GUI for managing distributions of a shelf.
 * It allows users to view, generate, and manage current and historical distributions.
 * @author Víctor Llorens Ramos (victor.llorens@estudiantat.upc.edu)
 * @version 1.0
 */
public class ManageDistributionsView extends JFrame {

        private final JTable currentDistributionTable;
        private final JTable historyTable;
        private final JButton viewDistributionButton;
        private final JButton makeCurrentButton;
        private final int idShelf;
        private final JTextField searchTextField;
        private final TableRowSorter<DefaultTableModel> sorter;
        private final JComboBox<String> filterCriteria;

        /**
         * Constructs a new ManageDistributionsView for the specified shelf.
         *
         * @param idShelf the ID of the shelf
         */
        public ManageDistributionsView(int idShelf) {
                this.idShelf = idShelf;
                ArrayList<TupleType> distributions = getDistributions(idShelf);

                setTitle("Manage Distributions");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setSize(800, 600);
                setLocationRelativeTo(null);
                setLayout(new BorderLayout());
                setResizable(false);

                TupleType shelf = null;
                try {
                        shelf = PresentationController.getInstance().getShelf(idShelf);
                } catch (ShelfException e) {
                        errorMessageView(e.getMessage());
                }

                // Title Panel
                JPanel titlePanel = new JPanel(new GridLayout(2, 1));
                JLabel mainTitle = new JLabel("MANAGE DISTRIBUTIONS", SwingConstants.CENTER);
                mainTitle.setFont(new Font("Arial", Font.BOLD, 20));
                JLabel subtitle1 = new JLabel("SHELF: " + idShelf, SwingConstants.CENTER);
                JLabel subtitle2 = new JLabel("PRODUCT LIST: " + shelf.get(3), SwingConstants.CENTER);
                JPanel subtitle = new JPanel(new GridLayout(3, 1));
                subtitle.add(subtitle1);
                subtitle.add(subtitle2);
                titlePanel.add(mainTitle);
                titlePanel.add(subtitle);
                add(titlePanel, BorderLayout.NORTH);

                // Center Panel
                JPanel centerPanel = new JPanel();
                centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

                // Current Distribution Table
                JLabel currentDistributionLabel = new JLabel("CURRENT DISTRIBUTION:");
                DesignUtils.configureLabel(currentDistributionLabel);

                DefaultTableModel currentDistributionModel = new DefaultTableModel(new Object[]{"name", "creationDate", "modifiedDate"}, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return column != 0 && column != 1 && column != 2;
                        }
                };

                if (!distributions.isEmpty()) currentDistributionModel.addRow(new Object[]{distributions.getLast().get(0), distributions.getLast().get(1), distributions.getLast().get(2)});
                currentDistributionTable = new JTable(currentDistributionModel);
                DesignUtils.configureTable(currentDistributionTable);
                currentDistributionTable.getTableHeader().setReorderingAllowed(false);
                JScrollPane currentDistributionScrollPane = new JScrollPane(currentDistributionTable);
                currentDistributionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                currentDistributionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                currentDistributionScrollPane.setPreferredSize(new Dimension(750, 50));
                DesignUtils.configureScrollPane(currentDistributionScrollPane);

                // History Table
                JLabel historyLabel = new JLabel("HISTORY:");
                DesignUtils.configureLabel(historyLabel);
                DefaultTableModel historyModel = new DefaultTableModel(new Object[]{"name", "creationDate", "modifiedDate"}, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return column != 0 && column != 1 && column != 2;
                        }
                };

                for (int i = distributions.size() - 2; i >= 0; i--) {
                        TupleType distribution = distributions.get(i);
                        historyModel.addRow(new Object[]{distribution.get(0), distribution.get(1), distribution.get(2)});
                }

                historyTable = new JTable(historyModel);
                DesignUtils.configureTable(historyTable);
                historyTable.getTableHeader().setReorderingAllowed(false);
                historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane historyScrollPane = new JScrollPane(historyTable);
                historyScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                historyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

                sorter = new TableRowSorter<>(historyModel);
                historyTable.setRowSorter(sorter);

                // Add Tables to Panel
                JPanel currentDistributionPanel = new JPanel(new BorderLayout());
                currentDistributionPanel.add(currentDistributionLabel, BorderLayout.NORTH);
                currentDistributionPanel.add(currentDistributionScrollPane, BorderLayout.CENTER);

                JPanel historyPanel = new JPanel();
                historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
                historyPanel.add(historyLabel);

                // Add a JTextField for the filter input
                JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel searchLabel = new JLabel("Search: ");
                DesignUtils.configureLabel(searchLabel);
                searchTextField = new JTextField(20);
                DesignUtils.configureTextField(searchTextField);
                searchTextField.getDocument().addDocumentListener(new MyDocumentListener());

                // Add a JComboBox for filter criteria
                JLabel filterByLabel = new JLabel("Filter by: ");
                DesignUtils.configureLabel(filterByLabel);
                filterCriteria = new JComboBox<>(new String[]{"name", "creationDate", "modifiedDate"});
                DesignUtils.configureComboBox(filterCriteria);
                filterPanel.add(searchLabel);
                filterPanel.add(searchTextField);
                filterPanel.add(filterByLabel);
                filterPanel.add(filterCriteria);
                historyPanel.add(filterPanel);
                historyPanel.add(historyScrollPane);

                centerPanel.add(currentDistributionPanel);
                centerPanel.add(Box.createVerticalStrut(10));
                centerPanel.add(historyPanel);
                add(centerPanel, BorderLayout.CENTER);

                // Button Panel
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                JButton generateNewDistributionButton = new JButton("Generate New Distribution");
                DesignUtils.configureButton(generateNewDistributionButton);
                generateNewDistributionButton.setPreferredSize(new Dimension(220, 30));
                viewDistributionButton = new JButton("View Distribution");
                DesignUtils.configureButton(viewDistributionButton);
                viewDistributionButton.setEnabled(false);
                makeCurrentButton = new JButton("Make Current");
                DesignUtils.configureButton(makeCurrentButton);
                makeCurrentButton.setEnabled(false);
                JButton backButton = new JButton("Back");
                DesignUtils.configureButton(backButton);

                buttonPanel.add(generateNewDistributionButton);
                buttonPanel.add(viewDistributionButton);
                buttonPanel.add(makeCurrentButton);
                buttonPanel.add(backButton);
                add(buttonPanel, BorderLayout.SOUTH);

                // Listeners
                historyTable.getSelectionModel().addListSelectionListener(new HistoryListener());
                currentDistributionTable.getSelectionModel().addListSelectionListener(new CurrentListener());
                backButton.addActionListener(new TurnBackListener());
                generateNewDistributionButton.addActionListener(new GenerateListener());
                makeCurrentButton.addActionListener(new MakeCurrentListener());
                viewDistributionButton.addActionListener(new ViewListener());
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
         * Applies the filter to the history table based on the search input and selected criteria.
         */
        private void applyFilter() {
                String text = searchTextField.getText();
                int columnIndex = filterCriteria.getSelectedIndex();
                if (text.trim().isEmpty()) {
                        sorter.setRowFilter(null);
                } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, columnIndex));
                }
        }

        /**
         * Retrieves the distributions for the specified shelf.
         *
         * @param idShelf the ID of the shelf
         * @return an ArrayList of TupleType representing the distributions
         */
        private ArrayList<TupleType> getDistributions(int idShelf) {
                PresentationController presentationController = PresentationController.getInstance();
                ArrayList<TupleType> distributions = new ArrayList<>();
                try {
                        distributions = presentationController.getDistributions(idShelf);
                        if (distributions.isEmpty()) {
                                errorMessageView("No distributions found for shelf " + idShelf);
                        }
                } catch (ShelfException e) {
                        errorMessageView(e.getMessage());
                }
                return distributions;
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
         * Action listener for navigating back to the previous view.
         */
        private class TurnBackListener implements ActionListener {
                @Override
                public void actionPerformed(ActionEvent e) {
                        PresentationController presentationController = PresentationController.getInstance();
                        hideView();
                        presentationController.shelfView();
                }
        }

        /**
         * Action listener for generating a new distribution.
         */
        private class GenerateListener implements ActionListener {
                @Override
                public void actionPerformed(ActionEvent e) {
                        PresentationController presentationController = PresentationController.getInstance();
                        hideView();
                        presentationController.generateDistributionView(idShelf);
                }
        }

        /**
         * Action listener for making a selected distribution the current one.
         */
        private class MakeCurrentListener implements ActionListener {
                @Override
                public void actionPerformed(ActionEvent e) {
                        String dist = (String) historyTable.getValueAt(historyTable.getSelectedRow(), 0);
                        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to make distribution " + dist + " the current one?", "Make Current", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                                PresentationController presentationController = PresentationController.getInstance();
                                try {
                                        presentationController.makeCurrentDistribution(idShelf, dist);
                                        hideView();
                                        presentationController.manageDistributionsView(idShelf);
                                } catch (ShelfException ex) {
                                        errorMessageView(ex.getMessage());
                                }
                        }
                }
        }

        /**
         * Action listener for viewing a selected distribution.
         */
        private class ViewListener implements ActionListener {
                @Override
                public void actionPerformed(ActionEvent e) {
                        PresentationController presentationController = PresentationController.getInstance();
                        if (historyTable.getSelectedRow() != -1) {
                                presentationController.showDistributionView(idShelf, (String) historyTable.getValueAt(historyTable.getSelectedRow(), 0));
                        } else if (currentDistributionTable.getSelectedRow() != -1) {
                                presentationController.showDistributionView(idShelf, (String) currentDistributionTable.getValueAt(currentDistributionTable.getSelectedRow(), 0));
                        }
                }
        }

        /**
         * List selection listener for the history table.
         */
        private class HistoryListener implements ListSelectionListener {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting() && !historyTable.getSelectionModel().isSelectionEmpty()) {
                                currentDistributionTable.clearSelection();
                                makeCurrentButton.setEnabled(true);
                                viewDistributionButton.setEnabled(true);
                        } else {
                                makeCurrentButton.setEnabled(false);
                                viewDistributionButton.setEnabled(false);
                        }
                }
        }

        /**
         * List selection listener for the current distribution table.
         */
        private class CurrentListener implements ListSelectionListener {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting() && !currentDistributionTable.getSelectionModel().isSelectionEmpty()) {
                                historyTable.clearSelection();
                                makeCurrentButton.setEnabled(false);
                                viewDistributionButton.setEnabled(true);
                        } else {
                                viewDistributionButton.setEnabled(false);
                        }
                }
        }

        /**
         * Document listener for filtering the history table based on search input.
         */
        private class MyDocumentListener implements DocumentListener {
                @Override
                public void insertUpdate(DocumentEvent e) {
                        applyFilter();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                        applyFilter();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                        applyFilter();
                }
        }
}