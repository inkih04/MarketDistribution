package org.presentation.views;

import org.presentation.controllers.PresentationController;
import org.presentation.utils.DesignUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

/**
 * The `LoginView` class represents the graphical user interface for the login screen of the application.
 *
 * <p>This class extends the `JFrame` class to create a window that allows users to log in, register, or exit the application.
 * The window contains buttons for each action, which trigger corresponding events when clicked. The login view is the initial
 * screen that users see when starting the application.</p>
 */
public class LoginView extends JFrame {
    private final JPanel buttonPanel = new JPanel();
    private final JButton btnLogin = new JButton("Login");
    private final JButton btnRegister = new JButton("Register");
    private final JButton btnExit = new JButton("Exit");

    /**
     * Constructor that sets up the GUI components for the Login view of the application.
     *
     * This constructor initializes the main JFrame and its components, including:
     * <ul>
     *     <li>The window title, size, and layout configuration.</li>
     *     <li>The background color of the frame.</li>
     *     <li>A welcome title label with custom font settings.</li>
     *     <li>Buttons for login, registration, and exit with associated listeners for each button.</li>
     *     <li>A panel to hold the buttons and apply a grid layout.</li>
     * </ul>
     *
     * It uses {@link DesignUtils} to configure the buttons with consistent styling and attaches listeners to each button to handle their respective actions.
     *
     * @see DesignUtils
     */
    public LoginView() {
        // Configuración del JFrame
        setBounds(600, 400, 550, 400);
        setResizable(false);
        setTitle("Supermarket Distribution Generator - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Cambiar el fondo del JFrame
        Color BACKGROUND_COLOR = new Color(12, 183, 242);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Crear el título
        JLabel labelTitulo = new JLabel("¡Welcome!", JLabel.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        labelTitulo.setForeground(Color.WHITE); // Color del texto en blanco
        labelTitulo.setOpaque(false); // Hacer transparente para mostrar fondo
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Margen superior
        add(labelTitulo, BorderLayout.NORTH);

        // Configurar los botones usando DesignUtils
        DesignUtils.configureButton(btnLogin);
        btnLogin.addActionListener(new ListenerButtonLogin());
        DesignUtils.configureButton(btnRegister);
        btnRegister.addActionListener(new ListenerButtonRegister());
        DesignUtils.configureButton(btnExit);
        btnExit.addActionListener(new ListenerButtonExit());

        // Configurar el panel de botones
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnExit);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        buttonPanel.setOpaque(false); // Fondo transparente
        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * This method is triggered when the login button is pressed.
     * It creates and displays a login dialog where the user can input their username (or email) and password.
     *
     * The method performs the following steps:
     * <ol>
     *     <li>Creates a JPanel with a GridLayout to hold two input fields: one for the username/email and one for the password.</li>
     *     <li>Configures the labels and text fields using the DesignUtils class, which may apply custom styling or settings.</li>
     *     <li>Shows the login dialog to the user using a JOptionPane, which blocks until the user clicks either the "OK" or "Cancel" button.</li>
     *     <li>If the user clicks "OK", retrieves the entered username/email and password from the input fields.</li>
     *     <li>Calls the {@link PresentationController#handleLogin(String, String)} method, passing the username/email and password to handle the login process.</li>
     * </ol>
     *
     */
    private class ListenerButtonLogin implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Crear los campos para el usuario y la contraseña
            JPanel panel = new JPanel(new GridLayout(2, 2));
            JLabel lblUsername = new JLabel("Username or email: ");
            JLabel lblPassword = new JLabel("Password: ");
            JTextField txtUsername = new JTextField();
            JPasswordField txtPassword = new JPasswordField();

            // Configurar los campos y etiquetas usando DesignUtils
            DesignUtils.configureLabel(lblUsername);
            DesignUtils.configureLabel(lblPassword);
            DesignUtils.configureTextField(txtUsername);
            DesignUtils.configureTextField(txtPassword);

            // Agregar los componentes al panel
            panel.add(lblUsername);
            panel.add(txtUsername);
            panel.add(lblPassword);
            panel.add(txtPassword);

            int option = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "Login",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                String userInput = txtUsername.getText();
                String password = new String(txtPassword.getPassword());

                PresentationController presentationController = PresentationController.getInstance();
                presentationController.handleLogin(userInput, password);
            }
        }
    }

    /**
     * Listener for handling the register button action.
     * <p>This inner class listens for the click event on the register button. Upon triggering the event, it presents a
     * dialog window for the user to input their username, email, and password for registration. The data entered is
     * then processed and passed to the {@link PresentationController#handleRegister(String, String, String)} method for further handling.</p>
     *
     * <p>The dialog allows the user to confirm their registration details or cancel the action. If the user confirms,
     * their input is retrieved from the corresponding fields, and the registration is processed by the controller.</p>
     */
    private class ListenerButtonRegister implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JPanel panel = new JPanel(new GridLayout(4, 2));
            JLabel lblUsername = new JLabel("Username: ");
            JLabel lblEmail = new JLabel("Email: ");
            JTextField txtEmail = new JTextField();
            JLabel lblPassword = new JLabel("Password: ");
            JTextField txtUsername = new JTextField();
            JPasswordField txtPassword = new JPasswordField();
            JLabel lblPassword2 = new JLabel("Confirm Password: ");
            JPasswordField txtPassword2 = new JPasswordField();

            // Configurar los campos y etiquetas usando DesignUtils
            DesignUtils.configureLabel(lblUsername);
            DesignUtils.configureLabel(lblEmail);
            DesignUtils.configureLabel(lblPassword);
            DesignUtils.configureLabel(lblPassword2);
            DesignUtils.configureTextField(txtUsername);
            DesignUtils.configureTextField(txtEmail);
            DesignUtils.configureTextField(txtPassword);
            DesignUtils.configureTextField(txtPassword2);

            // Agregar los componentes al panel
            panel.add(lblUsername);
            panel.add(txtUsername);
            panel.add(lblEmail);
            panel.add(txtEmail);
            panel.add(lblPassword);
            panel.add(txtPassword);
            panel.add(lblPassword2);
            panel.add(txtPassword2);

            int option = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "Register",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                String username = txtUsername.getText();
                String email = txtEmail.getText();
                String password = new String(txtPassword.getPassword());

                // Call the controller to handle the login
                PresentationController presentationController = PresentationController.getInstance();
                presentationController.handleRegister(username, email, password);
            }
        }
    }

    /**
     * Listener for handling the exit button action.
     *
     * <p>This inner class listens for the click event on the exit button and triggers the action to terminate the application
     * by calling {@link System#exit(int)} with a status code of 0, indicating a normal termination of the program.</p>
     */
    private class ListenerButtonExit implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    /**
     * Displays the current view by setting its visibility to true.
     *
     * <p>This method is used to show the current window or view. It calls {@link javax.swing.JFrame#setVisible(boolean)}
     * with the argument {@code true}, making the window visible to the user.</p>
     */
    public void showView() {
        setVisible(true);
    }

    /**
     * Hides the current view by setting its visibility to false.
     *
     * <p>This method is used to hide the current window or view. It calls {@link javax.swing.JFrame#setVisible(boolean)}
     * with the argument {@code false}, effectively making the window invisible to the user.</p>
     */
    public void hideView() {
        setVisible(false);
    }
}
