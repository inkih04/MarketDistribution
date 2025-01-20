package org.presentation.utils;

import javax.swing.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Font;

/**
 * The DesignUtils class provides utility methods for configuring the design and appearance of various Swing components.
 * It includes methods for setting backgrounds, configuring buttons, labels, text fields, combo boxes, tables, scroll panes, dialogs, and messages.
 */
public class DesignUtils {

    /**
     * Draws random circles on the given Graphics object.
     * This method is used to create a decorative background with circles.
     *
     * @param g the Graphics object to draw on
     * @param width the width of the area to draw in
     * @param height the height of the area to draw in
     */
    public static void drawCircles(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;

        // Set the color for the circles (slightly darker blue)
        g2d.setColor(new Color(0, 150, 200));

        // Draw 30 random circles
        for (int i = 0; i < 30; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            int size = (int) (Math.random() * 15 + 15); // Circle size between 15 and 30 pixels

            g2d.fillOval(x, y, size, size); // Draw the circle
        }
    }

    /**
     * Sets the background of the given JFrame with circles.
     * This method uses a JPanel to draw circles on the background.
     *
     * @param frame the JFrame to set the background for
     */
    public static void setBackgroundWithCircles(JFrame frame) {
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCircles(g, getWidth(), getHeight());
            }
        };

        // Set the main background color
        backgroundPanel.setBackground(Color.decode("#0CB7F2"));
        frame.setContentPane(backgroundPanel); // Set the background panel as the main content
        frame.revalidate(); // Ensure the content is revalidated and drawn correctly
    }

    /**
     * Configures the appearance of the given JButton.
     * This method sets the font, background color, text color, focus, border, padding, size, and alignment of the button.
     *
     * @param button the JButton to configure
     */
    public static void configureButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Large and bold font
        button.setBackground(new Color(0, 150, 200)); // Light blue color
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false); // Remove click effect
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 150), 2)); // Darker blue border
        button.setMargin(new Insets(10, 20, 10, 20)); // Adjusted padding
        button.setPreferredSize(new Dimension(150, 30)); // Increased button size
        button.setHorizontalAlignment(SwingConstants.CENTER); // Center the text horizontally
        button.setVerticalAlignment(SwingConstants.CENTER); // Center the text vertically
    }

    /**
     * Configures the appearance of the given JLabel.
     * This method sets the font and text color of the label.
     *
     * @param label the JLabel to configure
     */
    public static void configureLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 16)); // Large and bold font
        label.setForeground(new Color(0, 150, 200)); // Light blue color
    }

    /**
     * Configures the appearance of the given JTextField.
     * This method sets the font, background color, and border of the text field.
     *
     * @param textField the JTextField to configure
     */
    public static void configureTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14)); // Regular font
        textField.setBackground(new Color(240, 240, 240)); // Light gray background
        textField.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 200), 2)); // Light blue border
    }

    /**
     * Configures the appearance of the given JComboBox.
     * This method sets the font, background color, and border of the combo box.
     *
     * @param comboBox the JComboBox to configure
     */
    public static void configureComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14)); // Regular font
        comboBox.setBackground(new Color(240, 240, 240)); // Light gray background
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 200), 2)); // Light blue border
    }

    /**
     * Configures the appearance of the given JTable.
     * This method sets the font, header font, header background color, header text color, selection background color, selection text color, and grid color of the table.
     *
     * @param table the JTable to configure
     */
    public static void configureTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14)); // Regular font
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14)); // Bold font for headers
        table.getTableHeader().setBackground(new Color(0, 150, 200)); // Light blue background for headers
        table.getTableHeader().setForeground(Color.WHITE); // White text for headers
        table.setSelectionBackground(new Color(0, 150, 200)); // Light blue selection background
        table.setSelectionForeground(Color.WHITE); // White selection text
        table.setGridColor(new Color(0, 150, 200)); // Light blue grid lines
    }

    /**
     * Configures the appearance of the given JScrollPane.
     * This method sets the border of the scroll pane.
     *
     * @param scrollPane the JScrollPane to configure
     */
    public static void configureScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 200), 2)); // Light blue border
    }

    /**
     * Configures the appearance of the given JDialog.
     * This method sets the background color of the dialog.
     *
     * @param dialog the JDialog to configure
     */
    public static void configureDialog(JDialog dialog) {
        dialog.getContentPane().setBackground(Color.WHITE); // White background
    }

    /**
     * Configures the appearance of the given JLabel for error messages.
     * This method sets the font and text color of the error label.
     *
     * @param errorLabel the JLabel to configure
     */
    public static void configureErrorMessage(JLabel errorLabel) {
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font
        errorLabel.setForeground(Color.RED); // Red text
    }

    /**
     * Configures the appearance of the given JLabel for success messages.
     * This method sets the font and text color of the success label.
     *
     * @param successLabel the JLabel to configure
     */
    public static void configureSuccessMessage(JLabel successLabel) {
        successLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font
        successLabel.setForeground(new Color(0, 150, 200)); // Light blue text
    }

    /**
     * Configures the appearance of the given JLabel for titles.
     * This method sets the font and text color of the title label.
     *
     * @param titleLabel the JLabel to configure
     */
    public static void configureTitle(JLabel titleLabel) {
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Large and bold font
        titleLabel.setForeground(Color.WHITE); // White text
    }

    /**
     * Configures the appearance of the given JLabel for subtitles.
     * This method sets the font and text color of the subtitle label.
     *
     * @param subtitleLabel the JLabel to configure
     */
    public static void configureSubtitle(JLabel subtitleLabel) {
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Large and bold font
        subtitleLabel.setForeground(Color.WHITE); // White text
    }

    /**
     * Configures the appearance of the given JTextArea.
     * This method sets the font, background color, and border of the text area.
     *
     * @param textArea the JTextArea to configure
     */
    public static void configureTextArea(JTextArea textArea) {
        textArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Regular font
        textArea.setBackground(new Color(240, 240, 240)); // Light gray background
        textArea.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 200), 2)); // Light blue border
    }

    /**
     * Configures the appearance of the given JSpinner.
     * This method sets the font, background color, and border of the spinner.
     *
     * @param xsizeSpinner the JSpinner to configure
     */
    public static void configureSpinner(JSpinner xsizeSpinner) {
        xsizeSpinner.setFont(new Font("Arial", Font.PLAIN, 14)); // Regular font
        xsizeSpinner.setBackground(new Color(240, 240, 240)); // Light gray background
        xsizeSpinner.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 200), 2)); // Light blue border
    }
}