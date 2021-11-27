package ca.cmpt213.a4.client.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * FoodItem class, a subclass of Consumable.
 * Stores the name, notes, price, weight, and expiration date.
 * Consumables are stored in the MainMenu.
 */
public class FoodItem extends Consumable {
    double weight;

    /**
     * Constructor for FoodItem
     *
     * @param name    the name of the item, cannot be empty
     * @param notes   notes any other information, can be empty
     * @param price   price of this item
     * @param weight  weight of this item
     * @param expDate expiration date of this item
     */
    public FoodItem(String name, String notes, double price, double weight, LocalDateTime expDate) {
        super("food");
        //name cannot be empty; enforce with exception
        if (name.equals("")) {
            throw new IllegalArgumentException("Name of food cannot be empty.");
        }

        this.name = name;
        this.notes = notes;
        this.price = price;
        this.weight = weight;
        this.expDate = expDate;

        //update time until expiry upon construction; derived from https://mkyong.com/java8/java-8-difference-between-two-localdate-or-localdatetime/
        LocalDateTime currentTime = LocalDateTime.now();
        isExpired = !currentTime.isBefore(expDate);
        this.daysUntilExp = (int) ChronoUnit.DAYS.between(currentTime, expDate);
    }

    /**
     * Constructs a string with all information about this FoodItem
     *
     * @return the constructed string
     */
    @Override
    public String toString() {
        String foodString = "This is a food item.";
        foodString += "\nFood: " + name;
        foodString += "\nNotes: " + notes;
        foodString += "\nPrice: $" + price;
        foodString += "\nWeight: " + weight + "g";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        foodString += "\nExpiry date: " + expDate.format(formatter);

        //update time until expiry every time the food is displayed; derived from https://mkyong.com/java8/java-8-difference-between-two-localdate-or-localdatetime/
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isBefore(expDate)) {
            isExpired = false;
            int daysUntilExpiry = (int) ChronoUnit.DAYS.between(currentTime, expDate);
            this.daysUntilExp = daysUntilExpiry;
            if (daysUntilExpiry <= 0) {
                foodString += "\nThis food item will expire today.";
            } else {
                foodString += "\nThis food will expire in " + daysUntilExpiry + " day(s).";
            }
        } else {
            this.isExpired = true;
            int daysUntilExpiry = (int) ChronoUnit.DAYS.between(currentTime, expDate);
            this.daysUntilExp = daysUntilExpiry;
            daysUntilExpiry = -daysUntilExpiry;
            foodString += "\nThis food has been expired for " + daysUntilExpiry + " days!";
        }

        return foodString;
    }
}