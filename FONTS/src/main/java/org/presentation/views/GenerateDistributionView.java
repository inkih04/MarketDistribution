package org.presentation.views;

import org.domain.exceptions.DistributionException;
import org.domain.exceptions.ShelfException;
import org.domain.types.TupleType;
import org.presentation.controllers.PresentationController;
import org.presentation.utils.DesignUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The GenerateDistributionView class represents the GUI for generating a new distribution on a Shelf.
 * It allows users to input the distribution details to generate a new distribution for a given Shelf.
 * @author Víctor Llorens Ramos (victor.llorens@estudiantat.upc.edu)
 * @version 1.0
 */
public class GenerateDistributionView extends JFrame {

    private final TupleType shelf;
    private final JComboBox<String> algorithmComboBox;
    private final JSpinner combinationsSpinner;
    private final JSpinner neighboursSpinner;
    private final JTextField distributionNameField;

    /**
     * Constructs a new GenerateDistributionView for the specified shelf.
     *
     * @param idShelf the ID of the shelf
     */
    public GenerateDistributionView(int idShelf) {
        shelf = getShelf(idShelf);
        neighboursSpinner = new JSpinner(new SpinnerNumberModel(0, -1, Integer.MAX_VALUE, 1));
        combinationsSpinner = new JSpinner(new SpinnerNumberModel(0, -1, Integer.MAX_VALUE, 1));
        algorithmComboBox = new JComboBox<>(new String[]{"Brute Force", "Hill Climbing"});
        distributionNameField = new JTextField(20);

        // Create the frame
        setTitle("Generate Distribution");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(570, 390);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("GENERATE DISTRIBUTION", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Shelf Label
        JLabel shelfLabel1 = new JLabel("Shelf id:");
        DesignUtils.configureLabel(shelfLabel1);
        shelfLabel1.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(shelfLabel1, gbc);
        JLabel shelfLabel2 = new JLabel(String.valueOf(shelf.get(0)));
        DesignUtils.configureLabel(shelfLabel2);
        shelfLabel2.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(shelfLabel2, gbc);

        // Sorting Algorithm Label and ComboBox
        JLabel sortingLabel = new JLabel("Sorting Algorithm:");
        DesignUtils.configureLabel(sortingLabel);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(sortingLabel, gbc);

        algorithmComboBox.addActionListener(new AlgorithmListener());
        DesignUtils.configureComboBox(algorithmComboBox);
        if (shelf.get(2).equals("Hill Climbing")) algorithmComboBox.setSelectedIndex(1);
        else algorithmComboBox.setSelectedIndex(0);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(algorithmComboBox, gbc);

        // Depth Label and Spinner
        JLabel combinationsLabel = new JLabel("Num. combinations (Brute Force):");
        DesignUtils.configureLabel(combinationsLabel);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(combinationsLabel, gbc);

        DesignUtils.configureSpinner(combinationsSpinner);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(combinationsSpinner, gbc);

        // Max Neighbours Label and Spinner
        JLabel neighboursLabel = new JLabel("Maximum neighbours (Hill Climbing):");
        DesignUtils.configureLabel(neighboursLabel);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(neighboursLabel, gbc);

        DesignUtils.configureSpinner(neighboursSpinner);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(neighboursSpinner, gbc);

        // Information Label
        JLabel infoLabel = new JLabel("If the fields are left at -1, the algorithm will not take into account the limit.");
        DesignUtils.configureLabel(infoLabel);
        infoLabel.setForeground(Color.BLACK);
        infoLabel.setFont(infoLabel.getFont().deriveFont(infoLabel.getFont().getSize() - 2f));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(infoLabel, gbc);

        // Distribution Name Label and TextField
        JLabel distributionNameLabel = new JLabel("Distribution Name:");
        DesignUtils.configureLabel(distributionNameLabel);
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(distributionNameLabel, gbc);

        DesignUtils.configureTextField(distributionNameField);
        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(distributionNameField, gbc);

        // Buttons
        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(new GenerateListener());
        DesignUtils.configureButton(generateButton);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        panel.add(generateButton, gbc);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new CancelListener());
        DesignUtils.configureButton(cancelButton);
        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(cancelButton, gbc);

        // Add panel to frame
        add(panel, BorderLayout.CENTER);
        setVisible(true);

        // Initialize spinners based on the selected algorithm
        updateSpinners();
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
     * Retrieves the shelf details for the specified shelf ID.
     *
     * @param idShelf the ID of the shelf
     * @return a TupleType representing the shelf details
     */
    private TupleType getShelf(int idShelf) {
        TupleType shelf = null;
        try {
            PresentationController presentationController = PresentationController.getInstance();
            shelf = presentationController.getShelf(idShelf);
        } catch (ShelfException e) {
            errorMessageView(e.getMessage());
        }
        return shelf;
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
     * Displays a success message dialog with the given message.
     *
     * @param message the success message to display
     */
    public void successMessageView(String message) {
        JDialog dialog = new JDialog();
        DesignUtils.configureDialog(dialog);
        JLabel successMessage = new JLabel(message);
        DesignUtils.configureSuccessMessage(successMessage);
        JOptionPane.showMessageDialog(dialog, successMessage, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Updates the enabled state of the spinners based on the selected algorithm.
     */
    private void updateSpinners() {
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        if (algorithm.equals("Brute Force")) {
            combinationsSpinner.setEnabled(true);
            neighboursSpinner.setEnabled(false);
        } else {
            combinationsSpinner.setEnabled(false);
            neighboursSpinner.setEnabled(true);
        }
    }

    /**
     * Action listener for the algorithm combo box.
     */
    private class AlgorithmListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateSpinners();
        }
    }

    /**
     * Action listener for generating a new distribution.
     */
    private class GenerateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JDialog dialog = new JDialog();
            DesignUtils.configureDialog(dialog);
            int response = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to generate the distribution with these parameters?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                PresentationController presentationController = PresentationController.getInstance();
                int limit;
                if (algorithmComboBox.getSelectedItem().equals("Brute Force")) limit = (int) combinationsSpinner.getValue();
                else limit = (int) neighboursSpinner.getValue();
                String distributionName = distributionNameField.getText();

                try {
                    presentationController.generateDistribution((int) shelf.get(0), (String) algorithmComboBox.getSelectedItem(), limit, distributionName);
                    successMessageView("Distribution generated successfully.");
                    presentationController.manageDistributionsView((int) shelf.get(0));
                    hideView();
                } catch (ShelfException | DistributionException ex) {
                    errorMessageView(ex.getMessage());
                }
            }
        }
    }

    /**
     * Action listener for canceling the distribution generation.
     */
    private class CancelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            PresentationController presentationController = PresentationController.getInstance();
            hideView();
            presentationController.manageDistributionsView((int) shelf.get(0));
        }
    }
}