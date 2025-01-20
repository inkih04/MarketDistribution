package org.presentation.views;

import org.presentation.controllers.PresentationController;
import org.presentation.utils.DesignUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The MenuView class represents the main menu window of the application.
 * It provides buttons for managing products, product lists, shelves, viewing operations, logging out, and exiting the application.
 *
 * <p>This class sets up the main menu window, initializing buttons for managing products, product lists, shelves, viewing operations,
 * logging out, and exiting the application. The layout and appearance of the window are configured, including background color, button styles,
 * and event listeners for handling user interactions.</p>
 */
public class MenuView extends JFrame {
    private PresentationController presentationController;
    private JButton exitButton;
    private JButton productManagerButton;
    private JButton productListManagerButton;
    private JButton shelfManagerButton;
    private JButton registersButton;
    private JButton logOutButton;

    /**
     * Constructs a MenuView window with buttons for various management options.
     *
     * <p>This constructor sets up the main options menu window, initializing buttons for
     * managing products, product lists, shelves, viewing operations, logging out, and exiting the application.
     * The layout and appearance of the window are configured, including background color, button styles, and event listeners.</p>
     *
     * @see DesignUtils#configureButton(JButton) for button styling.
     * @see ListenerProductListManagerButton for handling product list management.
     * @see ListenerRegistersButton for handling viewing operations.
     * @see ListenerLogOutButton for handling the logout functionality.
     * @see ListenerExitButton for handling the exit functionality.
     */
    public MenuView() {
        presentationController = PresentationController.getInstance();

        // Configuración de la ventana principal
        setTitle("Options Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        Color BACKGROUND_COLOR = new Color(12, 183, 242);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Panel central con los botones principales
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Inicializar botones y aplicar estilos
        productManagerButton = new JButton("Manage products");
        DesignUtils.configureButton(productManagerButton);
        productManagerButton.addActionListener(new ListenerProductManagerButton());

        productListManagerButton = new JButton("Manage lists");
        DesignUtils.configureButton(productListManagerButton);
        productListManagerButton.addActionListener(new ListenerProductListManagerButton());

        shelfManagerButton = new JButton("Manage shelves");
        DesignUtils.configureButton(shelfManagerButton);
        shelfManagerButton.addActionListener(new ListenerShelfManagerButton());

        registersButton = new JButton("View Operations");
        DesignUtils.configureButton(registersButton);
        registersButton.addActionListener(new ListenerRegistersButton());

        centerPanel.add(productManagerButton);
        centerPanel.add(productListManagerButton);
        centerPanel.add(shelfManagerButton);
        centerPanel.add(registersButton);
        centerPanel.setBackground(BACKGROUND_COLOR);

        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior con los botones separados
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 2, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        logOutButton = new JButton("Log out");
        DesignUtils.configureButton(logOutButton);
        logOutButton.addActionListener(new ListenerLogOutButton());

        exitButton = new JButton("Exit");
        DesignUtils.configureButton(exitButton);
        exitButton.addActionListener(new ListenerExitButton());

        bottomPanel.add(logOutButton);
        bottomPanel.add(exitButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }


    /**
     * Displays the view by making the window visible.
     *
     * <p>This method sets the visibility of the window to true, showing the window to the user.</p>
     */
    public void showView() {
        setVisible(true);
    }

    /**
     * Hides the view by making the window invisible.
     *
     * <p>This method sets the visibility of the window to false, effectively hiding it from the user.</p>
     */
    public void hideView() {
        setVisible(false);
    }


    /**
     * Listener for handling the log-out button action.
     *
     * <p>This inner class listens for the log-out button click event and triggers the log-out action
     * by calling the {@link PresentationController#handleLogOut()} method.</p>
     *
     * @see PresentationController#handleLogOut() for the action taken when logging out.
     */
    private class ListenerLogOutButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            hideView();
            presentationController.handleLogOut();
        }
    }


    /**
     * Listener for handling the exit button action.
     *
     * <p>This inner class listens for the exit button click event and terminates the application
     * by calling {@link System#exit(int)} with a status code of 0, indicating normal termination.</p>
     */
    private class ListenerExitButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }


    /**
     * Listener for handling the product list manager button action.
     *
     * <p>This inner class listens for the click event on the product list manager button and triggers the action
     * to display the product list management menu by calling {@link PresentationController#handleProductListMenu()}.
     * It also hides the current view after the action is performed.</p>
     *
     * @see PresentationController#handleProductListMenu() for handling the product list management functionality.
     */
    private class ListenerProductListManagerButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            presentationController.handleProductListMenu();
            hideView();
        }
    }

    private class ListenerProductManagerButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            presentationController.handleProductCatalog();
            hideView();
        }
    }

    /**
     * Listener for handling the shelf manager button action.
     *
     * <p>This inner class listens for the click event on the shelf manager button and triggers the action
     * to display the shelf management view by calling {@link PresentationController#shelfView()}.
     * It also hides the current view after the action is performed.</p>
     *
     * @see PresentationController#shelfView() for handling the shelf management functionality.
     */
    private class ListenerShelfManagerButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            presentationController.shelfView();
            hideView();
        }
    }

    /**
     * Listener for handling the registers button action.
     *
     * <p>This inner class listens for the click event on the registers button and triggers the action to display
     * the operations view by calling {@link PresentationController#handleViewOperations()}. It also hides the current
     * view after the action is performed.</p>
     *
     * @see PresentationController#handleViewOperations() for handling the operations view functionality.
     */
    private class ListenerRegistersButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            presentationController.handleViewOperations();
            hideView();
        }
    }
}
