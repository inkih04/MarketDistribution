package org.presentation.views;

import org.domain.types.TupleType;
import org.presentation.controllers.PresentationController;
import org.presentation.utils.DesignUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The CreateShelfView class represents the GUI for creating a new shelf.
 * It allows users to input the shelf details and create a new shelf.
 * @version 1.0
 */
public class CreateShelfView extends JFrame {

    private final JTextField idField;
    private final JSpinner xsizeSpinner;
    private final JSpinner ysizeSpinner;
    private JComboBox<String> associatedListDropdown;
    ArrayList<TupleType> lists;

    /**
     * Constructs a new CreateShelfView.
     */
    public CreateShelfView() {
        // Set the frame properties
        setTitle("Create Shelf");
        setSize(400, 310);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on the screen
        setLayout(new BorderLayout());
        setResizable(false);

        // Title at the top
        JLabel titleLabel = new JLabel("Create Shelf", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Id (integer)
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("Id (integer):");
        DesignUtils.configureLabel(idLabel);
        contentPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        idField = new JTextField();
        DesignUtils.configureTextField(idField);
        contentPanel.add(idField, gbc);

        // Row 2: X size (spinner)
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel xsizeLabel = new JLabel("X size:");
        DesignUtils.configureLabel(xsizeLabel);
        contentPanel.add(xsizeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        xsizeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        DesignUtils.configureSpinner(xsizeSpinner);
        contentPanel.add(xsizeSpinner, gbc);

        // Row 3: Y size (spinner)
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel ysizeLabel = new JLabel("Y size:");
        DesignUtils.configureLabel(ysizeLabel);
        contentPanel.add(ysizeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        ysizeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        DesignUtils.configureSpinner(ysizeSpinner);
        contentPanel.add(ysizeSpinner, gbc);

        // Row 4: Associated ProductList
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel associatedListLabel = new JLabel("Associated ProductList:");
        DesignUtils.configureLabel(associatedListLabel);
        contentPanel.add(associatedListLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;

        lists = getProductLists();

        if (lists != null) {
            String[] listNames = new String[lists.size()];
            for (int i = 0; i < lists.size(); i++) {
                TupleType list = lists.get(i);
                listNames[i] = (String) list.get(0);
            }
            associatedListDropdown = new JComboBox<>(listNames);
            DesignUtils.configureComboBox(associatedListDropdown);
            contentPanel.add(associatedListDropdown, gbc);
        }

        add(contentPanel, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton createButton = new JButton("Create");
        JButton backButton = new JButton("Back");
        DesignUtils.configureButton(createButton);
        DesignUtils.configureButton(backButton);
        createButton.addActionListener(new CreateListener());
        backButton.addActionListener(new TurnBackListener());
        bottomPanel.add(createButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Retrieves the list of product lists from the PresentationController.
     *
     * <p>This method fetches all product lists available in the system by calling the
     * {@link PresentationController#getAllLists()} method. If an exception occurs during
     * the retrieval, an error message is displayed and the view is redirected to the shelf view.</p>
     *
     * @return an ArrayList of TupleType representing the product lists, or null if an error occurs.
     */
    private ArrayList<TupleType> getProductLists() {
        PresentationController presentationController = PresentationController.getInstance();
        ArrayList<TupleType> lists = null;
        try {
            lists = new ArrayList<>(presentationController.getAllLists());
        } catch (Exception e) {
            errorMessageView("There aren't any lists on the system.");
            presentationController.shelfView();
            hideView();
        }
        return lists;
    }

    /**
     * Displays the view.
     */
    public void showView() {
        if (!(lists == null)) {
            setVisible(true);
        }
    }

    /**
     * Hides the view and disposes of its resources.
     */
    public void hideView() {
        setVisible(false);
        dispose();
    }

    /**
     * Displays an error message dialog with the given message.
     *
     * @param message the error message to display
     */
    public void errorMessageView(String message) {
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
     * Action listener for creating a new shelf.
     */
    private class CreateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            PresentationController presentationController = PresentationController.getInstance();
            if (idField.getText().isEmpty()) {
                errorMessageView("An id must be provided.");
            }
            try {
                presentationController.createShelf(Integer.parseInt(idField.getText()), (int) xsizeSpinner.getValue(), (int) ysizeSpinner.getValue(), (String) associatedListDropdown.getSelectedItem());
                successMessageView("Shelf created successfully.");
                hideView();
                presentationController.shelfView();
            } catch (NumberFormatException ex) {
                errorMessageView("Id must be an integer number.");
            } catch (Exception ex) {
                errorMessageView(ex.getMessage());
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
            presentationController.shelfView();
            hideView();
        }
    }
}