package ca.cmpt213.a4.client;

import ca.cmpt213.a4.client.view.SwingUI;

import javax.swing.*;

/**
 * The ConsumableTracker class is the entry point of the system.
 * Its only responsibility is to call main to initiate the system.
 */
public class ConsumableTracker {
    /**
     * Creates and calls the SwingUI and terminates system when complete.
     * This is performed inside invokeLater to utilize the event-dispatching thread.
     * @param args default argument given by Java
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingUI app = new SwingUI();
            app.displayMenu();
        });
    }
}
