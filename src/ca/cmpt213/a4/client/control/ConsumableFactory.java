package ca.cmpt213.a4.client.control;

import ca.cmpt213.a4.client.model.Consumable;
import ca.cmpt213.a4.client.model.DrinkItem;
import ca.cmpt213.a4.client.model.FoodItem;

import java.time.LocalDateTime;

/**
 * ConsumableFactory creates a new FoodItem or DrinkItem and returns it.
 */
public class ConsumableFactory {
    /**
     * The method to get a FoodItem or DrinkItem
     *
     * @param isFood         true to create a FoodItem, false to create a DrinkItem
     * @param name           the name of the item
     * @param notes          any notes about the item
     * @param price          the price of the item
     * @param weightOrVolume the weight (if food) or volume (if drink) of the item
     * @param expDate        the expiration date of the item
     * @return the constructed FoodItem or DrinkItem
     */
    public static Consumable getInstance(boolean isFood, String name, String notes, double price,
                                         double weightOrVolume, LocalDateTime expDate) {
        if (isFood) {
            return new FoodItem(name, notes, price, weightOrVolume, expDate);
        } else {
            return new DrinkItem(name, notes, price, weightOrVolume, expDate);
        }
    }
}
