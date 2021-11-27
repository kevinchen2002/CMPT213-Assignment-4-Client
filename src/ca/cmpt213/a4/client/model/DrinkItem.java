package ca.cmpt213.a4.client.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * FoodItem class, a subclass of Consumable.
 * Stores the name, notes, price, weight, and expiration date.
 * Consumables are stored in the MainMenu.
 */
public class DrinkItem extends Consumable {
    double volume;

    /**
     * Constructor for FoodItem
     *
     * @param name    the name of the item, cannot be empty
     * @param notes   notes any other information, can be empty
     * @param price   price of this item
     * @param volume  volume of this item
     * @param expDate expiration date of this item
     */
    public DrinkItem(String name, String notes, double price, double volume, LocalDateTime expDate) {
        super("drink");
        //name cannot be empty; enforce with exception
        if (name.equals("")) {
            throw new IllegalArgumentException("Name of drink cannot be empty.");
        }

        this.name = name;
        this.notes = notes;
        this.price = price;
        this.volume = volume;
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
        String drinkString = "This is a drink item.";
        drinkString += "\nDrink: " + name;
        drinkString += "\nNotes: " + notes;
        drinkString += "\nPrice: $" + price;
        drinkString += "\nVolume: " + volume + "ml";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        drinkString += "\nExpiry date: " + expDate.format(formatter);

        //update time until expiry every time the food is displayed; derived from https://mkyong.com/java8/java-8-difference-between-two-localdate-or-localdatetime/
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isBefore(expDate)) {
            isExpired = false;
            int daysUntilExpiry = (int) ChronoUnit.DAYS.between(currentTime, expDate);
            this.daysUntilExp = daysUntilExpiry;
            if (daysUntilExpiry <= 0) {
                drinkString += "\nThis drink item will expire today.";
            } else {
                drinkString += "\nThis drink will expire in " + daysUntilExpiry + " day(s).";
            }
        } else {
            this.isExpired = true;
            int daysUntilExpiry = (int) ChronoUnit.DAYS.between(currentTime, expDate);
            this.daysUntilExp = daysUntilExpiry;
            daysUntilExpiry = -daysUntilExpiry;
            drinkString += "\nThis drink has been expired for " + daysUntilExpiry + " days!";
        }

        return drinkString;
    }
}