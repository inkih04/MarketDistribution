package org.presentation.drivers;

import org.presentation.controllers.PresentationController;

/**
 * The `DriverPresentation` class serves as the entry point for the presentation layer of the application.
 * It initializes and starts the `PresentationController`, which manages the presentation logic and user interface interactions.
 *
 * <p>This class is responsible for bootstrapping the presentation layer by obtaining an instance of the `PresentationController`
 * and invoking its `start` method. The `PresentationController` is a singleton, ensuring that only one instance of the controller
 * exists throughout the application lifecycle.</p>
 */
public class DriverPresentation {

    /**
     * The main method serves as the entry point for the application.
     * It initializes the `PresentationController` and starts the presentation layer.
     *
     * @param args command-line arguments passed to the application (not used)
     */
    public static void main(String[] args) {
        PresentationController presentationController = PresentationController.getInstance();
        presentationController.start();
    }
}