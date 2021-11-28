package ca.cmpt213.a4.client.view;

import ca.cmpt213.a4.client.control.ConsumableFactory;
import ca.cmpt213.a4.client.control.ConsumableManager;
import ca.cmpt213.a4.client.model.Consumable;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.optionalusertools.DateTimeChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateTimeChangeEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Creates and displays a custom dialog which prompts the users to enter information to add a Consumable
 * Once "OK" is pressed, the new item is added to the manager, and the dialog closes
 * This can be cancelled at any time
 * http://www2.hawaii.edu/~takebaya/ics111/jdialog/jdialog.html was used as a reference
 */
public class AddConsumableDialog extends JDialog implements ActionListener, DateTimeChangeListener {
    private boolean isFood = true;
    private LocalDateTime expDate;
    private DateTimePicker dateTimePicker;
    private final ConsumableManager consumableManager = ConsumableManager.getInstance();

    private final JComboBox<String> consumableTypeSelect;
    private JLabel weightOrVolumeLabel;
    private final String[] typeOptions = {"Food", "Drink"};

    JTextField nameField;
    JTextField notesField;
    JTextField priceField;
    JTextField weightOrVolumeField;

    /**
     * Constructs the dialog with all necessary components
     * @param parent the calling frame
     */
    public AddConsumableDialog(Frame parent) {
        super(parent, "Add", true);

        Point location = parent.getLocation();
        setLocation(location.x+100, location.y+100);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        consumableTypeSelect = new JComboBox<>(typeOptions);
        consumableTypeSelect.setPreferredSize(new Dimension(300, 25));
        consumableTypeSelect.addActionListener(this);

        JPanel namePanel = getNamePanel();
        JPanel notesPanel = getNotesPanel();
        JPanel pricePanel = getPricePanel();
        JPanel weightOrVolumePanel = getWeightOrVolumePanel();
        JPanel datePanel = getDatePanel();
        JPanel btnPanel = getBtnPanel();

        panel.add(consumableTypeSelect);
        panel.add(namePanel);
        panel.add(notesPanel);
        panel.add(pricePanel);
        panel.add(weightOrVolumePanel);
        panel.add(datePanel);
        panel.add(btnPanel);

        getContentPane().setSize(500,500);
        getContentPane().add(panel);
        pack();
        this.setVisible(true);
    }

    /**
     * Returns the name panel
     * @return the name panel
     */
    private JPanel getNamePanel() {
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        JLabel nameLabel = new JLabel();
        nameLabel.setText("Name: ");
        nameLabel.setPreferredSize(new Dimension(48, 25));
        nameField = new JTextField();
        nameField.addActionListener(this);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        namePanel.setPreferredSize(new Dimension(300,25));
        return namePanel;
    }

    /**
     * Returns the notes panel
     * @return the notes panel
     */
    private JPanel getNotesPanel() {
        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.X_AXIS));
        JLabel notesLabel = new JLabel();
        notesLabel.setText("Notes: ");
        notesLabel.setPreferredSize(new Dimension(48, 25));
        notesField = new JTextField();
        notesPanel.add(notesLabel);
        notesPanel.add(notesField);
        notesPanel.setPreferredSize(new Dimension(300,25));
        return notesPanel;
    }

    /**
     * Returns the price panel
     * @return the price panel
     */
    private JPanel getPricePanel() {
        JPanel pricePanel = new JPanel();
        pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.X_AXIS));
        JLabel priceLabel = new JLabel();
        priceLabel.setText("Price: ");
        priceLabel.setPreferredSize(new Dimension(48, 25));
        priceField = new JTextField();
        pricePanel.add(priceLabel);
        pricePanel.add(priceField);
        pricePanel.setPreferredSize(new Dimension(300,25));
        return pricePanel;
    }

    /**
     * Returns the weight/volume panel
     * @return the weight/volume panel
     */
    private JPanel getWeightOrVolumePanel() {
        JPanel weightOrVolumePanel = new JPanel();
        weightOrVolumePanel.setLayout(new BoxLayout(weightOrVolumePanel, BoxLayout.X_AXIS));
        weightOrVolumeLabel = new JLabel();
        weightOrVolumeLabel.setText("Weight: ");
        weightOrVolumeLabel.setPreferredSize(new Dimension(48, 25));
        weightOrVolumeField = new JTextField();
        weightOrVolumePanel.add(weightOrVolumeLabel);
        weightOrVolumePanel.add(weightOrVolumeField);
        weightOrVolumePanel.setPreferredSize(new Dimension(300,25));
        return weightOrVolumePanel;
    }

    /**
     * Returns the date panel
     * @return the date panel
     */
    private JPanel getDatePanel() {
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));
        JLabel dateLabel = new JLabel();
        dateLabel.setText("Date: ");
        dateLabel.setPreferredSize(new Dimension(48, 25));
        dateTimePicker = new DateTimePicker();
        dateTimePicker.addDateTimeChangeListener(this);
        datePanel.add(dateLabel);
        datePanel.add(dateTimePicker);
        datePanel.setPreferredSize(new Dimension (300, 25));
        return datePanel;
    }

    /**
     * Returns the button panel
     * @return the button panel
     */
    private JPanel getBtnPanel() {
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");
        okBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        return btnPanel;
    }

    /**
     * Validates the content of the text fields and creates a corresponding Consumable
     * If any content is invalid, a dialog will inform the user of this, and the process will end
     * If successful, this dialog closes
     */
    private void addConsumable() {
        try {
            String name = parseName();
            String notes = parseNotes();
            double price = parsePrice();
            double weightOrVolume = parseWeightOrVolume();

            if (name.equals("")) {
                JOptionPane.showMessageDialog(this,
                        "Error: name cannot be empty",
                        "Invalid input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (price < 0) {
                JOptionPane.showMessageDialog(this,
                        "Error: price cannot be less than 0",
                        "Invalid input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (weightOrVolume < 0) {
                if (isFood) {
                    JOptionPane.showMessageDialog(this,
                            "Error: weight cannot be less than 0",
                            "Invalid input",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error: volume cannot be less than 0",
                            "Invalid input",
                            JOptionPane.WARNING_MESSAGE);
                }
                return;
            }
            Consumable newConsumable = ConsumableFactory.getInstance(isFood, name, notes, price, weightOrVolume, expDate);
            curlWithBody("POST", "/addConsumable", newConsumable);
            consumableManager.addConsumable(newConsumable);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: invalid input",
                    "Invalid input",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Parses and returns the name of the new item
     * @return the name of the new item
     */
    private String parseName() {
        if (nameField.getText() == null) {
            return "";
        }
        return nameField.getText();
    }

    /**
     * Parses and returns the notes regarding the new item
     * @return the notes regarding the new item
     */
    private String parseNotes() {
        if (notesField.getText() == null) {
            return "";
        }
        return notesField.getText();
    }

    /**
     * Parses and returns the price of the new item
     * @return the price of the new item
     */
    private double parsePrice() {
        if (priceField.getText() == null) {
            return -1;
        }
        try {
            return Double.parseDouble(priceField.getText());
        } catch (NumberFormatException nfe) {
            //do nothing
        }
        return -1;
    }

    /**
     * Parses and returns the weight/volume of the new item
     * @return the weight/volume of the new item
     */
    private double parseWeightOrVolume() {
        if (weightOrVolumeField.getText() == null) {
            return -1;
        }
        try {
            return Double.parseDouble(weightOrVolumeField.getText());
        } catch (NumberFormatException nfe) {
            //do nothing
        }
        return -1;
    }

    /**
     * Defines behaviour when Food/Drink is selected or a button is pressed
     * @param e the action being recieved
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (Objects.equals(consumableTypeSelect.getSelectedItem(), "Food")) {
            weightOrVolumeLabel.setText("Weight: ");
            isFood = true;
        } else if (Objects.equals(consumableTypeSelect.getSelectedItem(), "Drink")) {
            weightOrVolumeLabel.setText("Volume:");
            isFood = false;
        }

        if (Objects.equals(e.getActionCommand(), "OK")) {
            addConsumable();
        } else if (Objects.equals(e.getActionCommand(), "Cancel")) {
            this.dispose();
        }
    }

    /**
     * Updates the date field if the date has been changed
     * @param event the event being received
     */
    @Override
    public void dateOrTimeChanged(DateTimeChangeEvent event) {
        expDate = dateTimePicker.getDateTimePermissive();
    }

    private String curlWithBody(String method, String operation, Consumable consumable) {
        String consumableString = ConsumableManager.serializeConsumable(consumable);
        consumableString = consumableString.replaceAll("\"", "\\\\"+"\"");
        String command = "curl -i -H \"Content-Type: application/json\" -X " + method + " -d \"" + consumableString + "\" localhost:8080" + operation;
        System.out.println(command);
        return executeCommand(command);
    }

    private String executeCommand(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = process.getInputStream();
        return getStringFromInputStream(inputStream);
    }

    //Used https://www.baeldung.com/convert-input-stream-to-string as reference
    public String getStringFromInputStream(InputStream is) {
        return new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
