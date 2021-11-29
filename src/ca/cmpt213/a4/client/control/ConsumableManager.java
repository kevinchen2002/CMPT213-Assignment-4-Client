package ca.cmpt213.a4.client.control;

import ca.cmpt213.a4.client.gson.extras.RuntimeTypeAdapterFactory;
import ca.cmpt213.a4.client.model.Consumable;
import ca.cmpt213.a4.client.model.DrinkItem;
import ca.cmpt213.a4.client.model.FoodItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * ConsumableManager handles operations in regard to the Consumable list.
 * It uses a Singleton to distribute the same instance across the main UI and the dialog.
 */
public class ConsumableManager {
    private ArrayList<Consumable> consumableList = new ArrayList<>();
    private static ConsumableManager instance;

    /**
     * Method that returns the singular instance of ConsumableManager
     * @return said instance
     */
    public static ConsumableManager getInstance() {
        if (instance == null) {
            instance = new ConsumableManager();
        }
        return instance;
    }

    public void setConsumableList(ArrayList<Consumable> consumableList) {
        this.consumableList = consumableList;
    }

    /**
     * Returns the size of the underlying Consumable list
     * @return the size as an integer
     */
    public int getSize() {
        return consumableList.size();
    }

    public String getIdAt(int index) {
        return consumableList.get(index).getId();
    }

    public static String listToString(ArrayList<Consumable> list) {
        if (list.isEmpty()) {
            return "There are no consumable items!";
        }
        StringBuilder bigString = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String consumableString = "No. " + (i+1) + "\n" + list.get(i);
            bigString.append(consumableString).append("\n\n");
        }
        return bigString.toString();
    }

    /**
     * Learned about RuntimeTypeAdapterFactory class from:
     * https://jansipke.nl/serialize-and-deserialize-a-list-of-polymorphic-objects-with-gson/
     * Downloaded RuntimeTypeAdapterFactory class from:
     * https://github.com/google/gson/blob/master/extras/src/main/java/com/google/gson/typeadapters/RuntimeTypeAdapterFactory.java
     * This is an extra feature of Gson used for deserializing polymorphic objects.
     * This class is provided by Google on the Gson GitHub page, with the link shown above.
     */
    private static final RuntimeTypeAdapterFactory<Consumable> runTimeTypeAdapterFactory = RuntimeTypeAdapterFactory
            .of(Consumable.class, "type")
            .registerSubtype(FoodItem.class, "food")
            .registerSubtype(DrinkItem.class, "drink");

    private static final Gson myGson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
            new TypeAdapter<LocalDateTime>() {
                @Override
                public void write(JsonWriter jsonWriter,
                                  LocalDateTime localDateTime) throws IOException {
                    jsonWriter.value(localDateTime.toString());
                }

                @Override
                public LocalDateTime read(JsonReader jsonReader) throws IOException {
                    return LocalDateTime.parse(jsonReader.nextString());
                }
            }).registerTypeAdapterFactory(runTimeTypeAdapterFactory).create();

    public static String serializeConsumable(Consumable consumable) {
        return myGson.toJson(consumable);
    }

    public static ArrayList<Consumable> deserializeConsumableList(String gsonString) {
        ArrayList<Consumable> list = myGson.fromJson(gsonString, new TypeToken<ArrayList<Consumable>>() {}.getType());
        for (Consumable consumable : list) {
            if (consumable instanceof FoodItem) {
                consumable.setType("food");
            } else if (consumable instanceof DrinkItem) {
                consumable.setType("drink");
            }
        }
        return list;
    }
}
