package ca.cmpt213.a4.client.view;

import ca.cmpt213.a4.client.control.ConsumableManager;
import ca.cmpt213.a4.client.model.Consumable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

/**
 * The main UI class
 * The user selects which of 4 lists to view with the top buttons
 * Display of one of 4 lists takes place on the central pane.
 * The user can add or remove Consumables with the bottom buttons.
 */
public class SwingUI implements ActionListener {
    JFrame applicationFrame;
    JTextPane displayPane;
    JScrollPane consumableListView;
    JLabel categoryLabel;

    private final ConsumableManager consumableManager = ConsumableManager.getInstance();
    private int DISPLAY_OPTION = 0;

    /**
     * Sets up and displays the main menu
     */
    public void displayMenu() {
        consumableManager.loadFile();

        applicationFrame = new JFrame("Consumable Tracker");
        applicationFrame.setSize(800, 800);
        applicationFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        applicationFrame.setLayout(new BoxLayout(applicationFrame.getContentPane(), BoxLayout.Y_AXIS));
        applicationFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                consumableManager.writeFile();
                super.windowClosing(e);
                applicationFrame.dispose();
            }
        });

        setupTopButtons();
        setupCategoryLabel();
        setupListView();
        setupAddRemoveButton();

        updateView();

        applicationFrame.setSize(700, 700);
        applicationFrame.pack();
        applicationFrame.setVisible(true);
    }

    /**
     * Sets up the four buttons on the top
     */
    private void setupTopButtons() {
        JButton showAllButton = new JButton("All");
        JButton showExpiredButton = new JButton("Expired");
        JButton showNotExpiredButton = new JButton("Not Expired");
        JButton showExpiringSevenButton = new JButton("Expiring in 7 Days");

        showAllButton.addActionListener(this);
        showExpiredButton.addActionListener(this);
        showNotExpiredButton.addActionListener(this);
        showExpiringSevenButton.addActionListener(this);

        JPanel listTabsPanel = new JPanel();
        listTabsPanel.setLayout(new BoxLayout(listTabsPanel, BoxLayout.X_AXIS));

        listTabsPanel.add(showAllButton);
        listTabsPanel.add(showExpiredButton);
        listTabsPanel.add(showNotExpiredButton);
        listTabsPanel.add(showExpiringSevenButton);

        listTabsPanel.setPreferredSize(new Dimension(800, 90));
        addPanel(listTabsPanel, applicationFrame);
    }

    /**
     * Sets up the category label above the main pane
     */
    private void setupCategoryLabel() {
        categoryLabel = new JLabel("All Consumables");
        JPanel categoryPanel = new JPanel();
        categoryPanel.add(categoryLabel);
        categoryPanel.setPreferredSize(new Dimension(800, 20));
        addPanel(categoryPanel, applicationFrame);
    }

    /**
     * Sets up the central pane containing Consumable objects
     */
    private void setupListView() {
        displayPane = new JTextPane();
        displayPane.setEditable(false);

        consumableListView = new JScrollPane(displayPane);
        consumableListView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        consumableListView.setPreferredSize(new Dimension(800, 500));
        consumableListView.setAlignmentX(Component.CENTER_ALIGNMENT);
        applicationFrame.add(consumableListView);
    }

    /**
     * Sets up the bottom buttons which are used to add and remove items
     */
    private void setupAddRemoveButton() {
        JButton addNewButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");

        addNewButton.addActionListener(this);
        removeButton.addActionListener(this);

        JPanel addRemovePanel = new JPanel();
        addRemovePanel.setLayout(new BoxLayout(addRemovePanel, BoxLayout.X_AXIS));

        addRemovePanel.add(addNewButton);
        addRemovePanel.add(removeButton);
        addRemovePanel.setPreferredSize(new Dimension(800, 90));
        addPanel(addRemovePanel, applicationFrame);
    }

    /**
     * Adds a panel to some container; in this case, the main application frame
     * @param jpanel the panel to be added
     * @param container the container which the panel is to be added to
     */
    private static void addPanel(JPanel jpanel, Container container) {
        jpanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(jpanel);
    }

    /**
     * Displays all items on the central pane
     */
    private void viewAllConsumables() {
        displayPane.setText(consumableManager.getAllConsumablesString());
        displayPane.setCaretPosition(0);
    }

    /**
     * Displays all expired items on the central pane
     */
    private void viewExpired() {
        displayPane.setText(consumableManager.getExpiredString());
        displayPane.setCaretPosition(0);
    }

    /**
     * Displays all unexpired items on the central pane
     */
    private void viewNotExpired() {
        displayPane.setText(consumableManager.getNotExpiredString());
        displayPane.setCaretPosition(0);
    }

    /**
     * Displays all items expiring within seven days on the central pane
     */
    private void viewExpiringSevenDays() {
        displayPane.setText(consumableManager.getExpiringSevenDaysString());
        displayPane.setCaretPosition(0);
    }

    /**
     * Refreshes the central pane whenever necessary
     * For example, when switching views or after adding an item
     */
    private void updateView() {
        if (DISPLAY_OPTION == 0) {
            viewAllConsumables();
            categoryLabel.setText("All Consumables");
        } else if (DISPLAY_OPTION == 1) {
            viewExpired();
            categoryLabel.setText("Expired Consumables");
        } else if (DISPLAY_OPTION == 2) {
            viewNotExpired();
            categoryLabel.setText("Consumables which are not Expired");
        } else if (DISPLAY_OPTION == 3) {
            viewExpiringSevenDays();
            categoryLabel.setText("Consumables Expiring within Seven Days");
        }
    }

    /**
     * Prompts the user for the index of the item to delete
     * @return the index given by the user
     */
    private int getDeletionIndex() {
        try {
            String input = JOptionPane.showInputDialog(applicationFrame,
                    "Which consumable would you like to delete?",
                    "Enter an index",
                    JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return -1;
            }
            return Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            //do nothing
        }
        return -1;
    }

    /**
     * Creates the dialog prompt which asks users to enter the relevant information.
     */
    private void addConsumable() {
        new AddConsumableDialog(applicationFrame);
        updateView();
    }

    /**
     * Validates the index given by the user, then removed the associated Consumable from the list
     */
    private void removeConsumable() {
        if (consumableManager.getSize() == 0) {
            JOptionPane.showMessageDialog(applicationFrame, "The list is empty.");
            return;
        }
        int toDelete = getDeletionIndex();
        if (toDelete < 1 || toDelete > consumableManager.getSize()) {
            JOptionPane.showMessageDialog(applicationFrame,
                    "Please give a number from 1 to " + consumableManager.getSize() + ".",
                    "Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        consumableManager.removeConsumable(toDelete-1);
        updateView();
        JOptionPane.showMessageDialog(applicationFrame,
                "Item #" + toDelete + " has been removed!",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Defines behaviour when a button is pressed
     * The first four buttons update the view, while the latter two modify the list
     * @param e the action being received
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (Objects.equals(e.getActionCommand(), "All")) {
            DISPLAY_OPTION = 0;
            updateView();
        } else if (Objects.equals(e.getActionCommand(), "Expired")) {
            DISPLAY_OPTION = 1;
            updateView();
        } else if (Objects.equals(e.getActionCommand(), "Not Expired")) {
            DISPLAY_OPTION = 2;
            updateView();
        } else if (Objects.equals(e.getActionCommand(), "Expiring in 7 Days")) {
            DISPLAY_OPTION = 3;
            updateView();
        } else if (Objects.equals(e.getActionCommand(), "Add")) {
            addConsumable();
            dummy();
        } else if (Objects.equals(e.getActionCommand(), "Remove")) {
            //removeConsumable();
            JOptionPane.showMessageDialog(applicationFrame, "CMPT213 A3 Consumables Tracker", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void dummy() {
        Consumable dummy = ConsumableManager.deserializeConsumable("{\"weight\":1.0,\"name\":\"Food\",\"notes\":\"This is a food!\",\"price\":1.0,\"expDate\":\"2021-11-25T01:30\",\"daysUntilExp\":-1,\"isExpired\":true,\"type\":\"food\",\"uuid\":\"93896c59-e419-4bf0-aa7a-73be212160f7\"}");
        consumableManager.addConsumable(dummy);
    }
}
