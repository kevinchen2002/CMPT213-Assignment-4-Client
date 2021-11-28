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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ConsumableManager handles operations in regard to the Consumable list.
 * It uses a Singleton to distribute the same instance across the main UI and the dialog.
 */
public class ConsumableManager {
    private static ArrayList<Consumable> consumableList = new ArrayList<>();
    private static final String filename = "data.json";
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

    /**
     * Method to add a fully-formed consumable object to the list
     * @param consumable the given consumable
     */
    public void addConsumable(Consumable consumable) {
        consumableList.add(consumable);
        Collections.sort(consumableList);
    }

    public ArrayList<Consumable> getAllConsumables() {
        return consumableList;
    }

    /**
     * Returns the size of the underlying Consumable list
     * @return the size as an integer
     */
    public int getSize() {
        return consumableList.size();
    }

    /**
     * Removes the item at the given index
     * @param index the index to be removed
     */
    public void removeConsumable(int index) {
        consumableList.remove(index);
    }

    /**
     * Returns a string representing all Consumables
     * @return said string
     */
    public String getAllConsumablesString() {
        if (consumableList.isEmpty()) {
            return "There are no consumable items!";
        }
        StringBuilder bigString = new StringBuilder();
        for (int i = 0; i < consumableList.size(); i++) {
            String consumableString = "No. " + (i+1) + "\n" + consumableList.get(i);
            bigString.append(consumableString).append("\n\n");
        }
        return bigString.toString();
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
     * Returns a string representing all expired Consumables
     * @return said string
     */
    public String getExpiredString() {
        if (consumableList.isEmpty()) {
            return "There are no consumable items!";
        }
        StringBuilder bigString = new StringBuilder();
        for (int i = 0; i < consumableList.size(); i++) {
            if (consumableList.get(i).isExpired()) {
                String consumableString = "No. " + (i + 1) + "\n" + consumableList.get(i);
                bigString.append(consumableString).append("\n\n");
            }
        }
        if (!bigString.toString().equals("")) {
            return bigString.toString();
        } else {
            return "There are no expired consumable items!";
        }
    }

    /**
     * Returns a string representing all unexpired Consumables
     * @return said string
     */
    public String getNotExpiredString() {
        if (consumableList.isEmpty()) {
            return "There are no consumable items!";
        }
        StringBuilder bigString = new StringBuilder();
        for (int i = 0; i < consumableList.size(); i++) {
            if (!consumableList.get(i).isExpired()) {
                String consumableString = "No. " + (i + 1) + "\n" + consumableList.get(i);
                bigString.append(consumableString).append("\n\n");
            }
        }
        if (!bigString.toString().equals("")) {
            return bigString.toString();
        } else {
            return "All consumable items are expired!";
        }
    }

    /**
     * Returns a string representing all Consumables expiring within seven days
     * @return said string
     */
    public String getExpiringSevenDaysString() {
        if (consumableList.isEmpty()) {
            return "There are no consumable items!";
        }
        StringBuilder bigString = new StringBuilder();
        for (int i = 0; i < consumableList.size(); i++) {
            if (consumableList.get(i).getDaysUntilExp() <= 7 && !consumableList.get(i).isExpired()) {
                String consumableString = "No. " + (i + 1) + "\n" + consumableList.get(i);
                bigString.append(consumableString).append("\n\n");
            }
        }
        if (!bigString.toString().equals("")) {
            return bigString.toString();
        } else {
            return "There are no consumable items expiring within 7 days!";
        }
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

    public static Consumable deserializeConsumable(String gsonString) {
        Consumable deserialized = myGson.fromJson(gsonString, new TypeToken<Consumable>() {
        }.getType());
        if (deserialized instanceof FoodItem) {
            deserialized.setType("food");
        } else if (deserialized instanceof DrinkItem) {
            deserialized.setType("drink");
        }
        return deserialized;
    }

    public static String serializeConsumableList(ArrayList<Consumable> consumableList) {
        return myGson.toJson(consumableList);
    }

    public static ArrayList<Consumable> deserializeConsumableList(String gsonString) {
        consumableList = myGson.fromJson(gsonString, new TypeToken<ArrayList<Consumable>>() {}.getType());
        for (Consumable consumable : consumableList) {
            if (consumable instanceof FoodItem) {
                consumable.setType("food");
            } else if (consumable instanceof DrinkItem) {
                consumable.setType("drink");
            }
        }
        return consumableList;
    }

    /**
     * Creates a new data.json file if needed; derived from https://www.w3schools.com/java/java_files_create.asp
     */
    private static void createFile() {
        try {
            File foodStorage = new File(filename);
            if (foodStorage.createNewFile()) {
                System.out.println("File data.json created!");
            }
        } catch (IOException e) {
            System.out.println("Error while creating file");
            e.printStackTrace();
        }
    }

    /**
     * loads data.json file if it exists; derived from https://attacomsian.com/blog/gson-read-json-file
     */
    public void loadFile() {

        try {
            Reader reader = Files.newBufferedReader(Paths.get(filename));
            consumableList = myGson.fromJson(reader, new TypeToken<List<Consumable>>() {
            }.getType());
            for (Consumable consumable : consumableList) {
                if (consumable instanceof FoodItem) {
                    consumable.setType("food");
                } else if (consumable instanceof DrinkItem) {
                    consumable.setType("drink");
                }
            }
            reader.close();
        } catch (NoSuchFileException e) {
            //if the file is not there, create it
            createFile();
            consumableList.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * writes to data.json upon shutdown; derived from https://attacomsian.com/blog/gson-write-json-file
     */
    public void writeFile() {

        try {
            Writer writer = Files.newBufferedWriter(Paths.get(filename));
            myGson.toJson(consumableList, writer);
            writer.close();

        } catch (NoSuchFileException e) {
            System.out.println("File not found!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
